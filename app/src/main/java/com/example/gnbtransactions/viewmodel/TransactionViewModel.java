package com.example.gnbtransactions.viewmodel;

import android.app.Application;

import com.example.gnbtransactions.App;
import com.example.gnbtransactions.model.Rate;
import com.example.gnbtransactions.model.Transaction;
import com.example.gnbtransactions.repo.Repository;

import java.util.HashMap;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

public class TransactionViewModel extends AndroidViewModel {

    private static Repository repository;

    private final LiveData<List<Rate>> rates;
    private final LiveData<List<String>> skus;
    private final HashMap<String, List<Transaction>> products;
    private final HashMap<String, Double> directRates;

    public TransactionViewModel(@NonNull Application application) {
        super(application);

        repository = this.<App>getApplication().getRepository();
        rates = repository.getRatesLiveData();
        skus = repository.getTransactionsLiveData();
        products = repository.getProducts();
        directRates = repository.getDirectRatesMap();
    }

    public void downloadRates() {

        repository.downloadRates();
    }

    public void downdloadTransactions() {

        repository.downloadTransactions();
    }

    public LiveData<List<Rate>> getRatesObservable() {
        return rates;
    }

    public LiveData<List<String>> getSkusObservable() { return skus; }

    public HashMap<String, List<Transaction>> getProducts() {
        return products;
    }

    public HashMap<String, Double> getDirectRates() { return directRates; }
}
