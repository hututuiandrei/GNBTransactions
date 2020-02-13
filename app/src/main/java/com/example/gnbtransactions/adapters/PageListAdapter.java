package com.example.gnbtransactions.adapters;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.gnbtransactions.R;
import com.example.gnbtransactions.model.Transaction;
import com.example.gnbtransactions.view.TransactionFragment;

import java.util.HashMap;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

public class PageListAdapter extends
        RecyclerView.Adapter<PageListAdapter.PageViewHolder>{

    class PageViewHolder extends RecyclerView.ViewHolder {

        private final TextView skuTextView;

        private PageViewHolder(View itemView) {
            super(itemView);

            skuTextView = itemView.findViewById(R.id.transaction_sku);

            itemView.setOnClickListener(view -> {

                String sku = skus.get(getAdapterPosition());

                List<Transaction> transactions = products.get(sku);

//                for(Transaction transaction : products.get(sku)) {
//
//                    Timber.d(transaction.getSku() + " " + transaction.getAmount() + " " + transaction.getCurrency());
//                }
                Bundle bundle = new Bundle();
                bundle.putString("sku", sku);

                TransactionFragment transactionFragment = new TransactionFragment();
                transactionFragment.setArguments(bundle);

                AppCompatActivity activity = (AppCompatActivity) itemView.getContext();
                activity.getSupportFragmentManager().beginTransaction().
                        replace(R.id.frame_layout, transactionFragment).addToBackStack(null).commit();
            });
        }
    }

    private List<String> skus;
    HashMap<String, List<Transaction>> products;

    public PageListAdapter(List<String> skus, HashMap<String, List<Transaction>> products) {
        this.skus = skus;
        this.products = products;

    }

    @Override
    public PageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(parent.getContext()).
                inflate(R.layout.recyclerview_transaction, parent, false);
        return new PageViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(PageViewHolder holder, int position) {

        String current = skus.get(position);

        holder.skuTextView.setText(current);
    }

    public void addskus(List<String> trans){

        skus.addAll(trans);
        notifyDataSetChanged();

    }

    @Override
    public int getItemCount() {

        if (skus != null)
            return skus.size();
        else return 0;
    }

    public void setProducts(HashMap<String, List<Transaction>> products) {
        this.products = products;
    }
}