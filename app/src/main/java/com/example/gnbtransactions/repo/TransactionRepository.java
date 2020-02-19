package com.example.gnbtransactions.repo;
import android.app.Application;

import com.example.gnbtransactions.model.Rate;
import com.example.gnbtransactions.model.Transaction;
import com.example.gnbtransactions.webservice.TransactionWebService;

import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import androidx.lifecycle.MutableLiveData;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

public class TransactionRepository {

    private static TransactionRepository instance = null;

    private TransactionWebService transactionWebService;

    private MutableLiveData<List<Rate>> rates;
    private MutableLiveData<List<String>> skus;

    private HashMap<String, List<Transaction>> productsMap;
    private HashMap<String, Double> directRatesMap;

    private TransactionRepository(Application application) {

        transactionWebService = new TransactionWebService();
        productsMap = new HashMap<>();
        directRatesMap = new HashMap<>();

        rates = new MutableLiveData<>();
        skus = new MutableLiveData<>();
    }

    public static TransactionRepository getInstance(Application application) {

        if(instance == null) {

            instance = new TransactionRepository(application);
        }
        return instance;
    }

    public void downloadRates() {

        transactionWebService.queryRates().enqueue(new Callback<List<Rate>>() {
            @Override
            public void onResponse(@NotNull Call<List<Rate>> call, @NotNull Response<List<Rate>> response) {

                Timber.d("SUCC");

                if(response.body() != null) {

                    directRatesMap = calculateDirectRatesMap(response.body());
                    rates.postValue(response.body());
                }
            }

            @Override
            public void onFailure(@NotNull Call<List<Rate>> call, @NotNull Throwable t) {

                Timber.d("FAIL");

                Timber.d(t.toString());
            }
        });
    }

    public void downloadTransactions() {

        transactionWebService.queryTransactions().enqueue(new Callback<List<Transaction>>() {
            @Override
            public void onResponse(@NotNull Call<List<Transaction>> call, @NotNull Response<List<Transaction>> response) {

                Timber.d("SUCC");

                if(response.body() != null) {

                    for(Transaction transaction : response.body()) {

                        if(productsMap.containsKey(transaction.getSku())) {

                            productsMap.get(transaction.getSku()).add(transaction);
                        } else {

                            ArrayList<Transaction> subtransactions = new ArrayList<>();
                            subtransactions.add(transaction);
                            productsMap.put(transaction.getSku(), subtransactions);
                        }
                    }

                    ArrayList<String> tr = new ArrayList<>(productsMap.keySet());
                    skus.postValue(tr);
                }
            }

            @Override
            public void onFailure(@NotNull Call<List<Transaction>> call, @NotNull Throwable t) {

                Timber.d("FAIL");

                Timber.d(t.toString());
            }
        });
    }

    /**
     * This method calculates the conversion of any currency directly to EUR.
     * We consider each given pair of currencies as being an edge in an
     * undirected graph. Thus we calculate the adjacency list of each node in
     * the graph and then we traverse the graph depth-first, stopping when we
     * reach the destination node (EUR node). In each step of the traversal
     * we multiply the current conversion rate with the conversion rate of the
     * edge between current and next vertex (initial conversion rate is 1 for each
     * vertex).
     *
     * @param rates - given list of rates between 2 different currencies, considered
     *              as being a list of edges in a graph
     * @return      - map of each vertex and its conversion rate to EUR after
     *              traversing the graph
     */

    private HashMap<String, Double> calculateDirectRatesMap(List<Rate> rates) {

        HashMap<String, HashMap<String, String>> ratesMap = new HashMap<>();
        HashMap<String, Double> directRatesMap = new HashMap<>();

        // Calculate adjacency list ratesMap
        if(rates != null) {

            for (Rate rate : rates) {

                if (ratesMap.containsKey(rate.getFrom())) {

                    ratesMap.get(rate.getFrom()).put(rate.getTo(), rate.getRate());
                } else {

                    HashMap<String, String> neigbourRates = new HashMap<>();
                    neigbourRates.put(rate.getTo(), rate.getRate());
                    ratesMap.put(rate.getFrom(), neigbourRates);
                }
            }

            //DFS traversal for each node
            for (String currency : ratesMap.keySet()) {

                HashMap<String, Boolean> visitedRatesMap = new HashMap<>();

                for (String node : ratesMap.keySet()) {

                    visitedRatesMap.put(node, false);
                }

                double conversionRate = 1;

                String currentCurrency = currency;

                while (true) {

                    if (ratesMap.get(currentCurrency).containsKey("EUR")) {

                        conversionRate *= Double.valueOf(ratesMap.get(currentCurrency).get("EUR"));

                        directRatesMap.put(currency, conversionRate);
                        break;
                    } else {

                        ArrayList<String> neighboursCurrency = new ArrayList<>(ratesMap.get(currentCurrency).keySet());

                        String lastCurrency = currentCurrency;

                        currentCurrency = null;

                        for (String curr : neighboursCurrency) {

                            if (!visitedRatesMap.get(curr)) {

                                currentCurrency = curr;
                                break;
                            }
                        }
                        if (currentCurrency == null) {

                            currentCurrency = neighboursCurrency.get(0);
                        }

                        visitedRatesMap.put(lastCurrency, true);

                        conversionRate *= Double.valueOf(ratesMap.get(lastCurrency).get(currentCurrency));
                    }
                }
            }
        }

        return directRatesMap;
    }

    /**
     * This method calculates the sum of all the transactions of type sku,
     * converting all currencies into EUR using the given rates. After each
     * conversion, the converted value is rounded using banker's rounding
     * (round half even)
     *
     * @param transactions  - all transactions of type sku
     * @param rates         - rates of any available currencies to EUR
     * @param sku           - selected sku
     * @return              - total amount value in EUR currency
     */
    private BigDecimal calculateTotalAmount(HashMap<String, List<Transaction>> transactions,
                                            HashMap<String, Double> rates, String sku) {

        BigDecimal totalAmount = new BigDecimal("0.00");

        for(Transaction transaction : transactions.get(sku)) {

            BigDecimal conversionRate = new BigDecimal(rates.get(transaction.getCurrency()));
            BigDecimal amount = new BigDecimal(transaction.getAmount());

            BigDecimal product = amount.multiply(conversionRate);

            product = product.setScale(2, BigDecimal.ROUND_HALF_EVEN);

            totalAmount = totalAmount.add(product);
        }

        return totalAmount;
    }

    public List<String> getTransactionsList(String sku) {

        BigDecimal totalAmount = calculateTotalAmount(productsMap, directRatesMap, sku);

        ArrayList<String> transactionsList = new ArrayList<>();

        transactionsList.add("TOTAL : " + totalAmount + " EUR");

        for(Transaction transaction : productsMap.get(sku)) {

            BigDecimal amount = new BigDecimal(transaction.getAmount());
            amount = amount.setScale(2, BigDecimal.ROUND_HALF_EVEN);
            transactionsList.add(amount + " " + transaction.getCurrency());
        }

        return transactionsList;
    }

    public MutableLiveData<List<Rate>> getRatesLiveData() {
        return rates;
    }

    public MutableLiveData<List<String>> getSkusLiveData() {
        return skus;
    }


}
