
package com.example.movieapp.object;

import java.util.List;

import com.example.movieapp.models.Profile;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


public class CastImagesResponse {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("profiles")
    @Expose
    private List<Profile> profiles;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public List<Profile> getProfiles() {
        return profiles;
    }

    public void setProfiles(List<Profile> profiles) {
        this.profiles = profiles;
    }

}
