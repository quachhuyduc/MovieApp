package com.example.movieapp.fragment;

import static android.app.Activity.RESULT_OK;
import static com.example.movieapp.activity.MainActivity.MY_REQUEST_CODE;
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
import android.graphics.drawable.BitmapDrawable;
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

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

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

import java.io.IOException;
import java.io.File;
import java.io.IOException;


public class EditProfileFragment extends Fragment {

    public interface IEditProfileListener {

        void onEditSuccess();

        void onEditError(String message);
    }

    private IEditProfileListener editProfileListener;

    private Context mContext;
    private static final int PICK_IMAGE_REQUEST = 1;



    private Uri imageUri;

    private String selectedImagePath;
    private DatabaseReference userRef;
    private StorageReference storageReference;

    final private AccountFragment accountFragment = new AccountFragment();
    private ImageView imageView;


    private EditText edtFullName, edtEmail;
    private Button btnUpdateProfile;
    private EditText edtFullName,edtEmail;
    private Button btnUpdateProfile, btnUdapteEmail;



    private MainActivity mainActivity;

    private ProgressDialog progressDialog;

    private Uri mUri;

    private View mView;

    public void setmUri(Uri mUri) {
        this.mUri = mUri;
    }

    public EditProfileFragment() {
    }

    public static EditProfileFragment newInstance() {
        Bundle args = new Bundle();
        EditProfileFragment fragment = new EditProfileFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public void setEditProfileListener(IEditProfileListener editProfileListener) {
        this.editProfileListener = editProfileListener;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        mView = inflater.inflate(R.layout.fragment_edit_profile, container, false);
        return mView;
        View view =  inflater.inflate(R.layout.fragment_edit_profile, container, false);

        userRef = FirebaseDatabase.getInstance().getReference("users");

        // Initialize Firebase Storage reference
        storageReference = FirebaseStorage.getInstance().getReference();
        return  view;

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
        btnUdapteEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickUpdateEmail();
            }
        });

    }



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
                            editProfileListener.onEditSuccess();
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
            openGallery();
            return;
        }
        if(getActivity().checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){
            openGallery();
        }else{
            String [] permissions = {Manifest.permission.READ_EXTERNAL_STORAGE};
            getActivity().requestPermissions(permissions, MY_REQUEST_CODE);
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
        if (user != null) {
            edtFullName.setText(user.getDisplayName());
            edtEmail.setText(user.getEmail());
            Uri uri = user.getPhotoUrl();
            Glide.with(getActivity())
                    .load(uri != null ? uri : R.drawable.ic_launcher_background)
                    .placeholder(R.drawable.ic_launcher_background)
                    .into(imageView);

        }
    }


    private void initView() {
        imageView = mView.findViewById(R.id.img_avatarEdit);
        edtFullName = mView.findViewById(R.id.edt_full_name);
        edtEmail = mView.findViewById(R.id.edt_email);
        btnUpdateProfile = mView.findViewById(R.id.btnUpdate_profile);

    }

    public void setBitmapImageView(Bitmap bitmap) {
        imageView.setImageBitmap(null);
        imageView.setImageBitmap(bitmap);
    }

    final private ActivityResultLauncher<Intent> mActivityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult result) {
            if (result.getResultCode() == RESULT_OK) {
                Intent intent = result.getData();
                if (intent == null) {
                    return;
                }
                Uri uri = intent.getData();
                if (uri != null){
                    uploadImageToFirestore();
                }
                mUri = uri;
                Bitmap bitmap = null;
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), uri);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                imageView.setImageBitmap(null);
                imageView.setImageBitmap(bitmap);
            }
        }
    });

    private void uploadImageToFirestore() {
//        StorageReference storageReference = FirebaseStorage.getInstance().getReference("profile_pictures/" + user.getUid() + ".jpg");
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openGallery();
            } else {
                Toast.makeText(getActivity(), "Please access the permisson", Toast.LENGTH_SHORT).show();
            }
        }
    }


    public void openGallery() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        mActivityResultLauncher.launch(Intent.createChooser(intent, "Select Picture"));
    }


}