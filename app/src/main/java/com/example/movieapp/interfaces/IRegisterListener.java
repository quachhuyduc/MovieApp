package com.example.movieapp.interfaces;

import com.example.movieapp.models.UserInfo;

public interface IRegisterListener {

    void onRegisterSuccess(UserInfo userInfo);

    void onRegisterCancel();
}
