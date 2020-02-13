package com.example.gnbtransactions.repo;
import android.app.Application;
import android.util.Pair;
import android.widget.ArrayAdapter;

import com.example.gnbtransactions.model.Rate;
import com.example.gnbtransactions.model.Transaction;
import com.example.gnbtransactions.webservice.RatesWebService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import androidx.lifecycle.MutableLiveData;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

public class Repository {

    RatesWebService ratesWebService;

    private MutableLiveData<List<Rate>> rates;
    private MutableLiveData<List<String>> skus;

    private HashMap<String, List<Transaction>> productsMap;
    HashMap<String, Double> directRatesMap;

    public Repository(Application application) {

        ratesWebService = new RatesWebService();
        productsMap = new HashMap<>();
        directRatesMap = new HashMap<>();

        rates = new MutableLiveData<>();
        skus = new MutableLiveData<>();
    }

    public void downloadRates() {

        ratesWebService.queryRates().enqueue(new Callback<List<Rate>>() {
            @Override
            public void onResponse(Call<List<Rate>> call, Response<List<Rate>> response) {

                Timber.d("SUCC");

                HashMap<String, HashMap<String, String>> ratesMap = new HashMap<>();

                if(response.body() != null) {

                    for(Rate rate : response.body()) {

                        if(ratesMap.containsKey(rate.getFrom())) {

                            ratesMap.get(rate.getFrom()).put(rate.getTo(), rate.getRate());
                        } else {

                            HashMap<String, String> neigbourRates = new HashMap<>();
                            neigbourRates.put(rate.getTo(), rate.getRate());
                            ratesMap.put(rate.getFrom(), neigbourRates);
                        }
                    }

                    for(String currency : ratesMap.keySet()) {

                        HashMap<String, Boolean> visitedRatesMap = new HashMap<>();

                        for(String node : ratesMap.keySet()) {

                            visitedRatesMap.put(node, false);
                        }

                        double conversionRate = 1;

                        String currentCurrency = currency;

                        while(true) {

                            if(ratesMap.get(currentCurrency).containsKey("EUR")) {

                                conversionRate *= Double.valueOf(ratesMap.get(currentCurrency).get("EUR"));

                                directRatesMap.put(currency, conversionRate);
                                break;
                            } else {

                                ArrayList<String> neighboursCurrency = new ArrayList<>(ratesMap.get(currentCurrency).keySet());

                                String lastCurrency = currentCurrency;

                                currentCurrency = null;

                                for (String curr : neighboursCurrency) {

                                    if(!visitedRatesMap.get(curr)) {

                                        currentCurrency = curr;
                                        break;
                                    }
                                }
                                if(currentCurrency == null) {

                                    currentCurrency = neighboursCurrency.get(0);
                                }

                                visitedRatesMap.put(lastCurrency, true);

                                conversionRate *= Double.valueOf(ratesMap.get(lastCurrency).get(currentCurrency));
                            }

                        }
                    }
                    Timber.d(directRatesMap.toString());

                    rates.postValue(response.body());
                }
            }

            @Override
            public void onFailure(Call<List<Rate>> call, Throwable t) {

                Timber.d("FAIL");

                Timber.d(t.toString());
            }
        });
    }

    public void downloadTransactions() {

        ratesWebService.queryTransactions().enqueue(new Callback<List<Transaction>>() {
            @Override
            public void onResponse(Call<List<Transaction>> call, Response<List<Transaction>> response) {

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
            public void onFailure(Call<List<Transaction>> call, Throwable t) {

                Timber.d("FAIL");

                Timber.d(t.toString());
            }
        });
    }

    public MutableLiveData<List<Rate>> getRatesLiveData() {
        return rates;
    }

    public MutableLiveData<List<String>> getTransactionsLiveData() {
        return skus;
    }

    public HashMap<String, List<Transaction>> getProducts() {
        return productsMap;
    }

    public HashMap<String, Double> getDirectRatesMap() {
        return directRatesMap;
    }
}
