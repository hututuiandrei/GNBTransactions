package com.example.gnbtransactions.webservice;

import com.example.gnbtransactions.App;
import com.example.gnbtransactions.R;
import com.example.gnbtransactions.model.Rate;
import com.example.gnbtransactions.model.Transaction;

import java.util.List;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;

public class TransactionWebService {

    private TransactionApi ratesApi;

    public TransactionWebService() {

        ratesApi = TransactionApiClient.getClient().create(TransactionApi.class);
    }

    public Call<List<Rate>> queryRates() {

        return ratesApi.queryRates();
    }

    public Call<List<Transaction>> queryTransactions() {

        return ratesApi.queryTransactions();
    }

    private interface TransactionApi {

        @GET("/rates.json")
        Call<List<Rate>> queryRates();

        @GET("/transactions.json")
        Call<List<Transaction>> queryTransactions();
    }

}

class TransactionApiClient {

    private static Retrofit retrofit = null;

    static Retrofit getClient() {

        String BASE_URL = App.getContext().getResources().getString(R.string.rates_url);

        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        return retrofit;
    }
}
