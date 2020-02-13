package com.example.gnbtransactions.view;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import timber.log.Timber;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toolbar;

import com.example.gnbtransactions.R;
import com.example.gnbtransactions.model.Transaction;
import com.example.gnbtransactions.viewmodel.TransactionViewModel;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class TransactionFragment extends Fragment {

    private TransactionViewModel transactionViewModel;
    private ListView listView;

    public TransactionFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        return inflater.inflate(R.layout.fragment_transaction, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        transactionViewModel = new TransactionViewModel(getActivity().getApplication());

        initView();

        String sku = null;

        Bundle bundle = this.getArguments();
        if(bundle != null) {
            sku = bundle.get("sku").toString();
        }
        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle(sku);

        HashMap<String, List<Transaction>> products = transactionViewModel.getProducts();
        HashMap<String, Double> rates = transactionViewModel.getDirectRates();
        ArrayList<String> list_row = new ArrayList<>();

        BigDecimal totalAmount = calculateTotalAmount(products, rates, sku);

        list_row.add("TOTAL : " + totalAmount + " EUR");

        for(Transaction transaction : products.get(sku)) {

            BigDecimal amount = new BigDecimal(transaction.getAmount());
            amount = amount.setScale(2, BigDecimal.ROUND_HALF_EVEN);
            list_row.add(amount + " " + transaction.getCurrency());
        }

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(getContext(),
                android.R.layout.simple_list_item_1, list_row);

        listView.setAdapter(arrayAdapter);
    }

    private BigDecimal calculateTotalAmount(HashMap<String, List<Transaction>> products,
                                            HashMap<String, Double> rates, String sku) {

        BigDecimal totalAmount = new BigDecimal("0.00");

        for(Transaction transaction : products.get(sku)) {

            BigDecimal conversionRate = new BigDecimal(rates.get(transaction.getCurrency()));
            BigDecimal amount = new BigDecimal(transaction.getAmount());

            BigDecimal product = amount.multiply(conversionRate);

            product = product.setScale(2, BigDecimal.ROUND_HALF_EVEN);

            totalAmount = totalAmount.add(product);
        }
            return totalAmount;
    }

    private void initView(){

        listView = getView().findViewById(R.id.list_view);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle(R.string.app_name);
    }
}
