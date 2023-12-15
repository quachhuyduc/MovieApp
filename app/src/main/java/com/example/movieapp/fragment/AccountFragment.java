package com.example.movieapp.fragment;

import static android.app.Activity.RESULT_OK;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.movieapp.R;

import com.example.movieapp.activity.SignInActivity;
import com.example.movieapp.utils.Constants;
import com.example.movieapp.utils.SharedPreferencesUtil;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.io.IOException;


public class AccountFragment extends Fragment {

    private ImageView imgUser;
    private TextView nameUser,emailUser;

    private TextView edt_edit_profile;
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
               fragmentTransaction.add(R.id.container,new EditProfileFragment(),null).addToBackStack(null);
               fragmentTransaction.commit();
            }
        });

    }

    //  <!-- TODO: Update Name bị crash thoát ra vào lại đã update rồi  -->
    public void showUserInformation(){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String email = user.getEmail();
    //    Uri photourl = user.getPhotoUrl();
        if (user != null) {
            String displayName = user.getDisplayName();
            if (displayName != null) {
                nameUser.setText(displayName);
            } else {
                // Nếu không có giá trị từ FirebaseUser, kiểm tra SharedPreferences
                String savedDisplayName = SharedPreferencesUtil.getDisplayName(requireContext());
                if (savedDisplayName != null) {
                    nameUser.setText(savedDisplayName);
                }
            }
            String imageUrl = SharedPreferencesUtil.getImageUrl(requireContext());
            if (imageUrl != null) {
                // Hiển thị ảnh từ đường dẫn đã lưu trữ
                Glide.with(this).load(imageUrl).into(imgUser);
            }

        }
        emailUser.setText(email);

    }



}