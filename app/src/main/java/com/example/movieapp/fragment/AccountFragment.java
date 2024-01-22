package com.example.movieapp.fragment;

import android.content.Context;
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
import com.example.movieapp.activity.WatchHistoryActivity;
import com.example.movieapp.utils.SharedPreferencesUtil;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class AccountFragment extends Fragment {

    private ImageView imgUser;
    private TextView nameUser,emailUser,edt_edit_profile,tv_history,ageUser;
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
        ageUser = view.findViewById(R.id.ageUser);

        edt_edit_profile = view.findViewById(R.id.edt_edit_profile);
        tv_history = view.findViewById(R.id.tv_history);
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

        tv_history.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), WatchHistoryActivity.class);

                // Bắt đầu WatchHistoryActivity
                startActivity(intent);
            }
        });

    }

    private EditProfileFragment editProfileFragment;
    private final EditProfileFragment.IEditProfileListener iEditProfileListener = new EditProfileFragment.IEditProfileListener() {
        @Override
        public void onEditSuccess() {
            removeEditFragment();
          showUserInformation();
        }

        @Override
        public void onEditError(String message) {
            Log.d("TAG", "onEditError: " + message);
            removeEditFragment();
        }
    };

    private void removeEditFragment() {
        getActivity().getSupportFragmentManager().beginTransaction().remove(editProfileFragment).commit();
        updateUserInfoAfterEditProfile();
    }

    public void updateUserInfoAfterEditProfile() {
        showUserInformation();
    }



    public void showUserInformation() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            return;
        }

        String email = user.getEmail();
        Uri photourl = user.getPhotoUrl();

        String displayName = user.getDisplayName();
        if (displayName != null) {
            nameUser.setText("Name: " + displayName);
        } else {
            Context context = getContext();
            // Nếu không có giá trị từ FirebaseUser, kiểm tra SharedPreferences
            String savedDisplayName = SharedPreferencesUtil.getDisplayName(requireContext());
            if (savedDisplayName != null) {
                nameUser.setText("Name: " + savedDisplayName);
            }
        }

        // Lấy tuổi từ Firebase Realtime Database hoặc Firestore
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("users").child(user.getUid());
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String ageString = dataSnapshot.child("age").getValue(String.class);

                    if (ageString != null && !ageString.isEmpty()) {
                        // Convert ageString to Long if needed
                        try {
                            Long age = Long.parseLong(ageString);

                            // Display age in your UI (replace TextView with your actual view)

                            ageUser.setText("Age: " + age);
                        } catch (NumberFormatException e) {
                            Log.e("TAG", "Failed to convert ageString to Long: " + e.getMessage());
                        }
                    }
                }
            }


            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d("TAG", "onCancelled: " + databaseError.getMessage());
            }
        });

        emailUser.setText("Email: " + email);

        Glide.with(this).load(photourl).error(R.drawable._0).into(imgUser);
    }

    public void onResume() {
        super.onResume();
        showUserInformation();
    }



}