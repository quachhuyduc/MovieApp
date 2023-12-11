package com.example.movieapp.object;

import com.example.movieapp.models.Result;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class KnownForResponse {

    @SerializedName("cast")
    @Expose
    private List<Result> results;

    public List<Result> getResults() {
        return results;
    }

    public void setResults(List<Result> results) {
        this.results = results;
    }

}

