package com.example.gnbtransactions.view;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import timber.log.Timber;

import android.os.Bundle;

import com.example.gnbtransactions.R;
import com.example.gnbtransactions.viewmodel.TransactionViewModel;
import com.example.gnbtransactions.adapters.RecyclerViewAdapter;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RecyclerViewAdapter adapter;
    private TransactionViewModel transactionViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        transactionViewModel = new TransactionViewModel(getApplication());
        transactionViewModel.downloadRates();
        transactionViewModel.downdloadTransactions();

        initView();

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new RecyclerViewAdapter(new ArrayList<>());
        recyclerView.setAdapter(adapter);

        transactionViewModel.getSkusObservable().observe(this, skus -> {

            Timber.d("CHANGED");

            if(!skus.isEmpty()) {
                recyclerView.post(() -> adapter.addskus(skus));
            }
        });
    }

    private void initView() {

        recyclerView = findViewById(R.id.recycler_view_transactions);
    }
}
