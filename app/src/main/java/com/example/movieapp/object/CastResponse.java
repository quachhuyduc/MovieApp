
package com.example.movieapp.object;

import java.util.List;


import com.example.movieapp.models.Cast;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class CastResponse {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("cast")
    @Expose
    private List<Cast> cast;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public List<Cast> getCast() {
        return cast;
    }

    public void setCast(List<Cast> cast) {
        this.cast = cast;
    }

}
