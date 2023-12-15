package com.example.movieapp.fragment;

import static androidx.databinding.adapters.ImageViewBindingAdapter.setImageDrawable;



import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

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
import com.example.movieapp.utils.SharedPreferencesUtil;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;


public class EditProfileFragment extends Fragment {

    private static final int PICK_IMAGE_REQUEST = 1;



    private Uri imageUri;

    private String selectedImagePath;
    private DatabaseReference userRef;
    private StorageReference storageReference;

    final private AccountFragment accountFragment = new AccountFragment();
    private ImageView imageView;


    private EditText edtFullName,edtEmail;
    private Button btnUpdateProfile, btnUdapteEmail;



    private MainActivity mainActivity;

    private ProgressDialog progressDialog;



    public EditProfileFragment() {
    }
   @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view =  inflater.inflate(R.layout.fragment_edit_profile, container, false);

        userRef = FirebaseDatabase.getInstance().getReference("users");

        // Initialize Firebase Storage reference
        storageReference = FirebaseStorage.getInstance().getReference();
        return  view;

    }



    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView(view);
        mainActivity = (MainActivity) getActivity();
        progressDialog = new ProgressDialog(getActivity());
        setUserInformation();
        initListener();

    }

    private void initListener() {
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();
            }
        });

        btnUpdateProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickUpdateProfile();
            }
        });
        btnUdapteEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickUpdateEmail();
            }
        });

    }



    private void openGallery() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();

            try {
                imageView.setImageURI(imageUri);
                uploadImageToFirebase();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    private void uploadImageToFirebase() throws IOException {
        if (imageUri != null) {
            // Lấy UID của người dùng hiện tại
            FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
            if (currentUser != null) {
                String userId = currentUser.getUid();

                // Tạo đường dẫn lưu trữ trên Firebase Storage
                StorageReference imageRef = storageReference.child("profile_images").child(userId);

                // Upload ảnh lên Firebase Storage
                imageRef.putFile(imageUri)
                        .addOnSuccessListener(taskSnapshot -> {
                            // Lấy đường dẫn tới ảnh sau khi upload thành công
                            imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                                String imageUrl = uri.toString();

                                // Cập nhật đường dẫn ảnh vào Firebase Realtime Database
                                userRef.child(userId).child("imageUrl").setValue(imageUrl);

                                // Lưu đường dẫn ảnh vào SharedPreferences
                                SharedPreferencesUtil.saveImageUrl(requireContext(), imageUrl);
                            });
                        })
                        .addOnFailureListener(e -> {
                            // Xử lý khi upload thất bại
                        });

        }
    }
    }

    private void onClickUpdateEmail() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        progressDialog.show();
        String strnewEmail = edtEmail.getText().toString().trim();
        user.updateEmail(strnewEmail)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        progressDialog.dismiss();
                        if (task.isSuccessful()) {
                            Toast.makeText(getActivity(), "User email address updated.", Toast.LENGTH_SHORT).show();
                            accountFragment.showUserInformation();
                        }
                    }
                });
    }

    private void onClickUpdateProfile() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        progressDialog.show();
        if (currentUser != null) {
            String newName = edtFullName.getText().toString();

            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                    .setDisplayName(newName)
                    .build();

            currentUser.updateProfile(profileUpdates)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            progressDialog.dismiss();
                            if (task.isSuccessful()) {
                                // Cập nhật thành công, gửi tên mới tới AccountFragment
                                SharedPreferencesUtil.saveDisplayName(requireContext(), newName);

                                navigateToAccountFragment();
                            } else {
                                // Xử lý khi cập nhật thất bại
                            }
                        }
                    });
        }
    }

    private void navigateToAccountFragment() {
        getParentFragmentManager().beginTransaction()
                .replace(R.id.container, new AccountFragment())
                .addToBackStack(null)
                .commit();
    }


    private void setUserInformation() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user == null){
            return;
        }else{
            edtFullName.setText(user.getDisplayName());
            edtEmail.setText(user.getEmail());
            Uri uri = user.getPhotoUrl();
            Glide.with(getActivity())
                    .load(uri != null ? uri : R.drawable.ic_launcher_background)
                    .placeholder(R.drawable.ic_launcher_background)
                    .into(imageView);

        }
    }



    private void initView(View view) {
        //  <!-- TODO: ImageView null object -->
        imageView = view.findViewById(R.id.img_avatarEdit);
        edtFullName = view.findViewById(R.id.edt_full_name);
        edtEmail = view.findViewById(R.id.edt_email);
        btnUpdateProfile = view.findViewById(R.id.btnUpdate_profile);
        btnUdapteEmail = view.findViewById(R.id.btnUpdate_email);
    }


}