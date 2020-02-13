package com.example.gnbtransactions.viewmodel;

import android.app.Application;

import com.example.gnbtransactions.App;
import com.example.gnbtransactions.model.Rate;
import com.example.gnbtransactions.model.Transaction;
import com.example.gnbtransactions.repo.TransactionRepository;

import java.util.HashMap;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

public class TransactionViewModel extends AndroidViewModel {

    private static TransactionRepository transactionRepository;

    private final LiveData<List<Rate>> rates;
    private final LiveData<List<String>> skus;
    private final HashMap<String, List<Transaction>> products;
    private final HashMap<String, Double> directRates;

    public TransactionViewModel(@NonNull Application application) {
        super(application);

        transactionRepository = this.<App>getApplication().getTransactionRepository();
        rates = transactionRepository.getRatesLiveData();
        skus = transactionRepository.getSkusLiveData();
        products = transactionRepository.getProductsMap();
        directRates = transactionRepository.getDirectRatesMap();
    }

    public void downloadRates() {

        transactionRepository.downloadRates();
    }

    public void downdloadTransactions() {

        transactionRepository.downloadTransactions();
    }

    public LiveData<List<Rate>> getRatesObservable() {
        return rates;
    }

    public LiveData<List<String>> getSkusObservable() { return skus; }

    public HashMap<String, List<Transaction>> getTransactionsMap() {
        return products;
    }

    public HashMap<String, Double> getDirectRatesMap() { return directRates; }
}
