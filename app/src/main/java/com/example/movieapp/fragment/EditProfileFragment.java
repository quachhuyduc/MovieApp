package com.example.movieapp.fragment;

import static com.example.movieapp.utils.Constants.MY_REQUEST_CODE;
import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.movieapp.R;
import com.example.movieapp.activity.MainActivity;
import com.example.movieapp.activity.SignInActivity;
import com.example.movieapp.activity.SignUpActivity;
import com.example.movieapp.utils.SharedPreferencesUtil;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;


public class EditProfileFragment extends Fragment {

    private static final String TAG = "EditProfileFragment";
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
    private ImageView imageView;


    private EditText edtFullName, edtEmail,edtOldPassword, edtNewPassword, edtCheckPassword;
    private Button btnUpdateProfile;
    private Button btnUdapteEmail;
    private Button btnChangePassword;
    private LinearLayout llLoading;


    private MainActivity mainActivity;

    private ProgressDialog progressDialog;

    private Uri mUri;

    private View mView;

    private FirebaseStorage firebaseStorage;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser currentUser;

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
        userRef = FirebaseDatabase.getInstance().getReference("users");
        return mView;
    }

    private void setupFirebase() {
        firebaseAuth = FirebaseAuth.getInstance();
        currentUser = firebaseAuth.getCurrentUser();
        firebaseStorage = FirebaseStorage.getInstance();
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mainActivity = (MainActivity) getActivity();
        progressDialog = new ProgressDialog(getActivity());
        initView();
        setupFirebase();
        setUserInformation();
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
        btnChangePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickChangePassword();
            }
        });

    }

    private void onClickChangePassword() {
        String oldPassword = edtOldPassword.getText().toString().trim();
        String newPassword = edtNewPassword.getText().toString().trim();
        String checkPassword = edtCheckPassword.getText().toString().trim();

        if (oldPassword.isEmpty() || newPassword.isEmpty() || checkPassword.isEmpty()) {
            showToast("Please fill in all fields");
        } else if (!isNewPasswordValid(newPassword, checkPassword)) {
            showToast("New password and confirm password do not match");
        } else {
            // Kiểm tra mật khẩu cũ và cập nhật mật khẩu mới
            FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
            if (currentUser != null) {
                // Tạo một đối tượng AuthCredential để xác thực mật khẩu cũ
                AuthCredential credential = EmailAuthProvider.getCredential(currentUser.getEmail(), oldPassword);

                // Xác thực mật khẩu cũ
                currentUser.reauthenticate(credential)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    // Xác thực thành công, có thể cập nhật mật khẩu mới
                                    updatePassword(newPassword);
                                } else {
                                    showToast("Incorrect old password");
                                }
                            }
                        });
            }
        }
    }
    private void updatePassword(String newPassword) {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            // Cập nhật mật khẩu mới
            currentUser.updatePassword(newPassword)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                showToast("Password updated successfully");
                                Intent intent = new Intent(getActivity(), SignInActivity.class);
                                startActivity(intent);
                                getActivity().finish();
                                // Cập nhật mật khẩu thành công, có thể thực hiện các công việc khác nếu cần
                            } else {
                                showToast("Failed to update password");
                            }
                        }
                    });
        }
    }

    private boolean isNewPasswordValid(String newPassword, String checkPassword) {
        return newPassword.equals(checkPassword);
    }

    private void showToast(String message) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
    }



    private void onClickUpdateEmail() {
        showLoading();
        String strnewEmail = edtEmail.getText().toString().trim();
        currentUser.updateEmail(strnewEmail).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                progressDialog.dismiss();
                if (task.isSuccessful()) {
                    Toast.makeText(getActivity(), "User email address updated.", Toast.LENGTH_SHORT).show();
                    editProfileListener.onEditSuccess();
                }
                hideLoading();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "onFailure: "+e.getMessage());
            }
        });
    }

    private void onClickUpdateProfile() {
        updateLoadToFireStore();

    }

    private void onClickRequestPermission() {
        //      = (MainActivity) getActivity();
        if (mainActivity == null) {
            return;
        }
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            openGallery();
            return;
        }
        if (getActivity().checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            openGallery();
        } else {
            String[] permissions = {Manifest.permission.READ_EXTERNAL_STORAGE};
            getActivity().requestPermissions(permissions, MY_REQUEST_CODE);
        }
    }

    private void setUserInformation() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            edtFullName.setText(user.getDisplayName());
            edtEmail.setText(user.getEmail());
            mUri = user.getPhotoUrl();
            Glide.with(getActivity()).load(mUri != null ? mUri : R.drawable.ic_launcher_background).placeholder(R.drawable.ic_launcher_background).addListener(requestListener).into(imageView);
        }
    }

    private final RequestListener requestListener = new RequestListener<Drawable>() {
        @Override
        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
            hideLoading();
            return false;
        }

        @Override
        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
            hideLoading();
            return false;
        }
    };

    private void showLoading() {
        llLoading.setVisibility(View.VISIBLE);
        changeStateButton(false);
    }

    private void hideLoading() {
        llLoading.setVisibility(View.GONE);
        changeStateButton(true);
    }

    private void changeStateButton(boolean enable) {
        imageView.setEnabled(enable);
        edtEmail.setEnabled(enable);
        edtFullName.setEnabled(enable);
        btnUdapteEmail.setEnabled(enable);
        btnUpdateProfile.setEnabled(enable);
        btnChangePassword.setEnabled(enable);
    }

    private void initView() {
        imageView = mView.findViewById(R.id.img_avatarEdit);
        edtFullName = mView.findViewById(R.id.edt_full_name);
        edtEmail = mView.findViewById(R.id.edt_email);
        edtOldPassword = mView.findViewById(R.id.edtOldPassword);
        edtNewPassword = mView.findViewById(R.id.edtNewPassword);
        edtCheckPassword = mView.findViewById(R.id.edtConfirmPassword);
        btnUdapteEmail = mView.findViewById(R.id.btnUpdate_email);
        btnUpdateProfile = mView.findViewById(R.id.btnUpdate_profile);
        btnChangePassword = mView.findViewById(R.id.btnChangePassword);
        llLoading = mView.findViewById(R.id.llLoading);
        initListener();
    }

    private final ActivityResultLauncher<String> pickImageResultLauncher = registerForActivityResult(new ActivityResultContracts.GetContent(), result -> {
        if (result != null) {
            mUri = result;
            Bitmap bitmap;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), result);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            Glide.with(this).load(bitmap).centerCrop().into(imageView);
        }
    });

    private void updateLoadToFireStore() {

        if (mUri != null) {
            showLoading();
            // Create or update a reference to the image in Firebase Storage
            StorageReference storageReference = firebaseStorage.getReference("profile_pictures" + currentUser.getUid() + ".jpg");
            // Upload the image
            UploadTask uploadTask = storageReference.putFile(mUri);
            uploadTask.continueWithTask(task -> {
                if (task.isSuccessful()) {
                    //Get download url from firebase storage
                    Log.d(TAG, "Url: " + task.getResult().getStorage().getDownloadUrl());
                    return task.getResult().getStorage().getDownloadUrl();
                }
                editProfileListener.onEditError("Can't get download url");
                throw task.getException();
            }).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Uri downloadUri = task.getResult();
                    //Update user profile
                    updateUserProfile(downloadUri);

                }
                hideLoading();
            }).addOnFailureListener(e -> {
                Log.d(TAG, "onFailure: " + e.getMessage());
                editProfileListener.onEditError(e.getMessage());
                hideLoading();
            });
        }
    }
    private void updateUserName() {
        if (isAdded()) {
            FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
            if (currentUser != null) {
                String newName = edtFullName.getText().toString();

                Context context = getContext();
                if (context != null) {
                    UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                            .setDisplayName(newName)
                            .build();

                    currentUser.updateProfile(profileUpdates)
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    // Cập nhật thành công, lưu tên vào SharedPreferences
                                    SharedPreferencesUtil.saveDisplayName(context, newName);


                                } else {
                                    // Xử lý khi cập nhật thất bại
                                }
                            });
                }
            }
        }

    }

    private void updateUserProfile(Uri uri) {

        UserProfileChangeRequest profileChangeRequest = new UserProfileChangeRequest.Builder().setPhotoUri(uri).build();

        currentUser.updateProfile(profileChangeRequest).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Log.d(TAG, "updateUserProfile: update profile success");

            }
            updateUserName();
            editProfileListener.onEditSuccess();

            hideLoading();
        }).addOnFailureListener(e -> {
            Log.d(TAG, "onFailure: " + e.getMessage());
            editProfileListener.onEditError(e.getMessage());
            hideLoading();
        });
    }

    private void openGallery() {
        pickImageResultLauncher.launch("image/*");
    }
}