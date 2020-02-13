package com.example.gnbtransactions.model;

import com.google.gson.annotations.SerializedName;

public class Rate {

    @SerializedName("from")
    private String from;

    @SerializedName("to")
    private String to;

    @SerializedName("rate")
    private String rate;

    public String getFrom() {
        return from;
    }

    public String getTo() {
        return to;
    }

    public String getRate() {
        return rate;
    }
}
