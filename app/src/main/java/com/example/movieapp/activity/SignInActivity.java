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

import com.example.movieapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class SignInActivity extends AppCompatActivity {


    private TextView tv_SignUp;
    private EditText edtEmail_login,edtPassword_login;
    private Button btnLogin;

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        initView();
    }

    private void initView() {
        progressDialog = new ProgressDialog(this);
        tv_SignUp = findViewById(R.id.tv_SignUp);
        edtEmail_login = findViewById(R.id.edtEmail_login);
        edtPassword_login = findViewById(R.id.edtPassword_login);
        btnLogin = findViewById(R.id.btnLogin);

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
}