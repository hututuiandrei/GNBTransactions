package com.example.gnbtransactions.viewmodel;

import android.app.Application;

import com.example.gnbtransactions.model.Rate;
import com.example.gnbtransactions.repo.TransactionRepository;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

public class TransactionViewModel extends AndroidViewModel {

    private static TransactionRepository transactionRepository;

    private final LiveData<List<Rate>> rates;
    private final LiveData<List<String>> skus;

    public TransactionViewModel(@NonNull Application application) {
        super(application);

        transactionRepository = TransactionRepository.getInstance(application);
        rates = transactionRepository.getRatesLiveData();
        skus = transactionRepository.getSkusLiveData();
    }

    public void downloadRates() {

        transactionRepository.downloadRates();
    }

    public void downloadTransactions() {

        transactionRepository.downloadTransactions();
    }

    public LiveData<List<Rate>> getRatesObservable() {
        return rates;
    }

    public LiveData<List<String>> getSkusObservable() { return skus; }

    public List<String> getTransactionsList(String sku) {return transactionRepository.getTransactionsList(sku); }
}
