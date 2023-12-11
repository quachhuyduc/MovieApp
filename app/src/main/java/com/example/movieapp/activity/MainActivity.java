package com.example.movieapp.activity;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.movieapp.R;
import com.example.movieapp.fragment.EditProfileFragment;
import com.example.movieapp.fragment.HomeFragment;
import com.example.movieapp.models.NowPlayingMovie;
import com.example.movieapp.models.UserInfo;
import com.example.movieapp.utils.Constants;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.IOException;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    public static final int MY_REQUEST_CODE = 10;
   final private EditProfileFragment editProfileFragment = new EditProfileFragment();



    final private ActivityResultLauncher<Intent> mActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult result) {
               if(result.getResultCode() == RESULT_OK){
                   Intent intent = result.getData();
                   if(intent == null){
                       return;
                   }
                       Uri uri = intent.getData();
                        editProfileFragment.setmUri(uri);
                       try {
                           Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(),uri);
                           editProfileFragment.setBitmapImageView(bitmap);

                       } catch (IOException e) {
                           e.printStackTrace();

                   }
               }
        }
    });




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
        }

    private void initView() {

        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment);

        NavController navController = navHostFragment.getNavController();
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);

        NavigationUI.setupWithNavController(bottomNavigationView,navController);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == MY_REQUEST_CODE){
            if(grantResults.length >0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                openGallery();
            }else{
                Toast.makeText(this, "Please access the permisson", Toast.LENGTH_SHORT).show();
            }
        }
    }



    public void openGallery() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        mActivityResultLauncher.launch(Intent.createChooser(intent,"Select Picture"));
    }


}
