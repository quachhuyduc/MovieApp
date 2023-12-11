package com.example.movieapp.adapters;

import com.example.movieapp.models.UserInfo;

public interface IRegisterListener {

    void onRegisterSuccess(UserInfo userInfo);

    void onRegisterCancel();
}
