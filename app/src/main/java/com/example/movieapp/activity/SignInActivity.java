package com.example.movieapp.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.movieapp.R;
import com.example.movieapp.utils.SharedPreferencesUtil;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SignInActivity extends AppCompatActivity {


    private TextView tv_SignUp,nameUserSave,forgottenPassword;
    private EditText edtEmail_login,edtPassword_login;
    private Button btnLogin;

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        initView();
        nameUserSave();
    }

    private void initView() {
        progressDialog = new ProgressDialog(this);
        tv_SignUp = findViewById(R.id.tv_SignUp);
        forgottenPassword = findViewById(R.id.forgottenPassword);
        nameUserSave = findViewById(R.id.nameUserSave);
        edtEmail_login = findViewById(R.id.edtEmail_login);
        edtPassword_login = findViewById(R.id.edtPassword_login);
        btnLogin = findViewById(R.id.btnLogin);

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String userEmail = currentUser.getEmail();
            // Điền email vào EditText
            edtEmail_login.setText(userEmail);
        }

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(edtEmail_login.getText().toString().isEmpty() ||edtPassword_login.getText().toString().isEmpty())
                {
                    Toast.makeText(getApplicationContext(), "Please enter complete information..", Toast.LENGTH_SHORT).show();
                }else{
                    onClickSignIn();
                }

            }
        });
        tv_SignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickSignUp();
            }
        });
        forgottenPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openForgotPasswordActivity();
            }
        });
    }

    private void openForgotPasswordActivity() {
        Intent intent = new Intent(SignInActivity.this, ForgottenPassword.class);
        startActivity(intent);
    }

    private void onClickSignUp() {
        Intent intent = new Intent(SignInActivity.this,SignUpActivity.class);
        startActivity(intent);
    }

    private void onClickSignIn() {

        String strEmail =  edtEmail_login.getText().toString().trim();
        String strPassword = edtPassword_login.getText().toString().trim();
        FirebaseAuth auth = FirebaseAuth.getInstance();
        progressDialog.show();
        auth.signInWithEmailAndPassword(strEmail, strPassword)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        progressDialog.dismiss();
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                                 Intent intent = new Intent(SignInActivity.this,MainActivity.class);
                                 startActivity(intent);
                                 finishAffinity();

                        } else {
                            // If sign in fails, display a message to the user.

                            Toast.makeText(SignInActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();

                        }
                    }
                });
    }
    public void nameUserSave(){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null) {
            String displayName = user.getDisplayName();
            if (displayName != null) {
                nameUserSave.setText("Welcome back " + displayName);
            } else {
                // Nếu không có giá trị từ FirebaseUser, kiểm tra SharedPreferences
                String savedDisplayName = SharedPreferencesUtil.getDisplayName(getApplicationContext());
                if (savedDisplayName != null) {
                    nameUserSave.setText("Welcome back "  + savedDisplayName);
                }
            }

        }


    }
}