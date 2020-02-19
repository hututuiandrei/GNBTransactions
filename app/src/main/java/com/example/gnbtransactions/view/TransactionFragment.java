package com.example.gnbtransactions.view;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.gnbtransactions.R;
import com.example.gnbtransactions.viewmodel.TransactionViewModel;

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

        return inflater.inflate(R.layout.fragment_transaction, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        transactionViewModel = new TransactionViewModel(getActivity().getApplication());

        initView();

        Bundle bundle = this.getArguments();
        if(bundle != null) {

            String sku = bundle.get("sku").toString();

            ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle(sku);

            ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(getContext(),
                    android.R.layout.simple_list_item_1, transactionViewModel.getTransactionsList(sku));

            listView.setAdapter(arrayAdapter);
        }
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
