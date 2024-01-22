package com.example.movieapp.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SignUpActivity extends AppCompatActivity {

    private TextView tvLogin;



    private EditText edtEmail;
    private EditText edtPassword , edtCheckPassword,edtAge;


    private Button btnJoinUs;

    private ProgressDialog progressDialog;

    private FirebaseAuth auth;
    private DatabaseReference databaseReference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        auth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("users");

        initView();
    }

    private void initView() {
        tvLogin = findViewById(R.id.tvLogin);

        edtEmail = findViewById(R.id.edtEmail);
        edtPassword = findViewById(R.id.edtPassword);
        edtCheckPassword = findViewById(R.id.edtCheckPassword);
        btnJoinUs = findViewById(R.id.btnJoinUs);
        edtAge = findViewById(R.id.edtAge);

        progressDialog = new ProgressDialog(this);


        tvLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickSignUp();
            }
        });

        btnJoinUs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(edtEmail.getText().toString().isEmpty()||edtPassword.getText().toString().isEmpty()||edtCheckPassword.getText().toString().isEmpty())
                {
                    Toast.makeText(getApplicationContext(), "Please enter complete information..", Toast.LENGTH_SHORT).show();
                }else{
                    onClickSignUp();
                }

            }
        });
    }

    private void onClickSignUp() {
        String strEmail = edtEmail.getText().toString().trim();
        String strPassword = edtPassword.getText().toString().trim();
        String strAge = edtAge.getText().toString().trim();
        FirebaseAuth auth = FirebaseAuth.getInstance();
        progressDialog.show();
        auth.createUserWithEmailAndPassword(strEmail, strPassword)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        progressDialog.dismiss();
                        if (task.isSuccessful()) {
                            FirebaseUser user = auth.getCurrentUser();
                            String userId = user.getUid();

                            saveAgeToDatabase(userId,strAge);

                            // Sign in success, update UI with the signed-in user's information
                            Intent intent = new Intent(SignUpActivity.this,MainActivity.class);
                           startActivity(intent);
                           finishAffinity();

                        } else {
                            // If sign in fails, display a message to the user.

                            Toast.makeText(SignUpActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();

                        }
                    }
                });
    }

    private void saveAgeToDatabase(String userId, String age) {
        DatabaseReference userReference = databaseReference.child(userId);
        userReference.child("age").setValue(age);
    }
}