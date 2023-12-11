package com.example.movieapp.fragment;

import static androidx.databinding.adapters.ImageViewBindingAdapter.setImageDrawable;

import static com.example.movieapp.activity.MainActivity.MY_REQUEST_CODE;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.Environment;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.movieapp.R;
import com.example.movieapp.activity.MainActivity;
import com.example.movieapp.utils.Constants;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

import java.io.File;


public class EditProfileFragment extends Fragment {

    private Context mContext;

    private AccountFragment accountFragment = new AccountFragment();
    private ImageView imageView;


    private EditText edtFullName,edtEmail;
    private Button btnUpdateProfile;

    private BitmapDrawable mRecycleableBitmapDrawable = null;

    private MainActivity mainActivity;

    private ProgressDialog progressDialog;

    private Uri mUri;

    private View mView;

    public void setmUri(Uri mUri) {
        this.mUri = mUri;
    }

    public EditProfileFragment() {
    }
   @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mView =  inflater.inflate(R.layout.fragment_edit_profile, container, false);
        return mView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView();
        mainActivity = (MainActivity) getActivity();
        progressDialog = new ProgressDialog(getActivity());
        setUserInformation();
        initListener();
    }

    private void initListener() {
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickRequestPermission();
            }
        });

        btnUpdateProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickUpdateProfile();
            }
        });

    }

    private void onClickUpdateProfile() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user == null){
            return;
        }
        progressDialog.show();
        String strFullName = edtFullName.getText().toString().trim();
        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setDisplayName(strFullName)
                .setPhotoUri(mUri)
                .build();

        user.updateProfile(profileUpdates)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        progressDialog.dismiss();
                        if (task.isSuccessful()) {
                            Toast.makeText(getActivity(), "Update profile Success!!", Toast.LENGTH_SHORT).show();

                            accountFragment.showUserInformation();
                        }
                    }
                });
    }

    private void onClickRequestPermission() {
   //      = (MainActivity) getActivity();
        if(mainActivity == null){
            return;
        }
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.M){
            mainActivity.openGallery();
            return;
        }
        if(getActivity().checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){
            mainActivity.openGallery();
        }else{
            String [] permissions = {Manifest.permission.READ_EXTERNAL_STORAGE};
            getActivity().requestPermissions(permissions, MY_REQUEST_CODE);
        }
    }

    private void setUserInformation() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user == null){
            return;
        }else{
            edtFullName.setText(user.getDisplayName());
            edtEmail.setText(user.getEmail());
            Glide.with(getActivity()).load(user.getPhotoUrl()).error(R.drawable._0).into(imageView);
        }
    }


    private void initView() {
        //  <!-- TODO: ImageView null object -->
        imageView = mView.findViewById(R.id.img_avatarEdit);
        edtFullName = mView.findViewById(R.id.edt_full_name);
        edtEmail = mView.findViewById(R.id.edt_email);
        btnUpdateProfile = mView.findViewById(R.id.btnUpdate_profile);
    }

    public void setBitmapImageView(Bitmap bitmapImageView) {

        //  <!-- TODO: ImageView null object -->
        if (imageView == null) {
        } else {
            imageView.setImageBitmap(bitmapImageView);
        }
    }




}