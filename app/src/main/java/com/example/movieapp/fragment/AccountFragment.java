package com.example.movieapp.fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.bumptech.glide.Glide;
import com.example.movieapp.R;
import com.example.movieapp.activity.SignInActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class AccountFragment extends Fragment {

    private ImageView imgUser;
    private TextView nameUser,emailUser,phoneUser,edt_edit_profile;
    private Button btnSignOut;

  public AccountFragment() {

  }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_account, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView(view);
        showUserInformation();
    }

    private void initView(View view) {
        imgUser = view.findViewById(R.id.imgUser);
        nameUser = view.findViewById(R.id.nameUser);
        emailUser = view.findViewById(R.id.emailUser);
     //   phoneUser = view.findViewById(R.id.phoneUser);
        edt_edit_profile = view.findViewById(R.id.edt_edit_profile);
        btnSignOut = view.findViewById(R.id.btnSignOut);

        btnSignOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(getActivity(), SignInActivity.class);
                startActivity(intent);
                getActivity().finish();
            }
        });
        edt_edit_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
                editProfileFragment = EditProfileFragment.newInstance();
                editProfileFragment.setEditProfileListener(iEditProfileListener);
                fragmentTransaction.add(R.id.container, editProfileFragment);
                fragmentTransaction.commit();
            }
        });

    }

    private EditProfileFragment editProfileFragment;
    private final EditProfileFragment.IEditProfileListener iEditProfileListener = new EditProfileFragment.IEditProfileListener() {
        @Override
        public void onEditSuccess() {
            getActivity().getSupportFragmentManager().beginTransaction().remove(editProfileFragment).commit();
            showUserInformation();
        }

        @Override
        public void onEditError(String message) {
            Log.d("TAG", "onEditError: " + message);
        }
    };

    public void showUserInformation() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            return;
        }
        String name = user.getDisplayName();
        String email = user.getEmail();
        Uri photourl = user.getPhotoUrl();
        //     String phoneNumber = user.getPhoneNumber();


         if(name == null){
             nameUser.setVisibility(View.GONE);
         }else {
             nameUser.setVisibility(View.VISIBLE);
             nameUser.setText(name);
         }

        emailUser.setText(email);
 //       phoneUser.setText(phoneNumber);
        Glide.with(this).load(photourl).error(R.drawable._0).into(imgUser);
    }


}