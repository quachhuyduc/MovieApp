package com.example.movieapp.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.movieapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ForgottenPassword extends AppCompatActivity {

    private EditText edtEmailForgotPassword;
    private Button btnSendResetEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgotten_password);
        edtEmailForgotPassword = findViewById(R.id.edtEmailForgotPassword);
        btnSendResetEmail = findViewById(R.id.btnSendResetEmail);

        btnSendResetEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Lấy địa chỉ email từ EditText
                String emailAddress = edtEmailForgotPassword.getText().toString().trim();

                // Kiểm tra hợp lệ của địa chỉ email
                if (isValidEmail(emailAddress)) {
                    // Gửi yêu cầu khôi phục mật khẩu
                    sendPasswordResetEmail(emailAddress);
                } else {
                    // Hiển thị thông báo lỗi nếu địa chỉ email không hợp lệ
                    Toast.makeText(ForgottenPassword.this, "Invalid email address", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    private boolean isValidEmail(String email) {
        return !TextUtils.isEmpty(email) && Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private void sendPasswordResetEmail(String emailAddress) {
        FirebaseAuth auth = FirebaseAuth.getInstance();

        auth.sendPasswordResetEmail(emailAddress)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            // Gửi email khôi phục thành công
                            Toast.makeText(ForgottenPassword.this, "Password reset email sent.", Toast.LENGTH_SHORT).show();
                            finish();  // Đóng ForgotPasswordActivity sau khi gửi thành công
                        } else {
                            // Gửi email khôi phục thất bại
                            Toast.makeText(ForgottenPassword.this, "Failed to send password reset email.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}