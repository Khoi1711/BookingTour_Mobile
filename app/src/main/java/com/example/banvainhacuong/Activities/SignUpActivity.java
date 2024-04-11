package com.example.banvainhacuong.Activities;

import static androidx.constraintlayout.widget.ConstraintLayoutStates.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.banvainhacuong.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class SignUpActivity extends AppCompatActivity {

    private EditText editTextName;
    private EditText editTextUsername;
    private EditText editTextPassword;
    private Button buttonSignUp;
    private TextView textViewSignIn;

    String url = "https://bookingtour-cfe14-default-rtdb.asia-southeast1.firebasedatabase.app";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        FirebaseDatabase database = FirebaseDatabase.getInstance(url);
        DatabaseReference myRef = database.getReference("admin");

        editTextName = findViewById(R.id.editTextName);
        editTextUsername = findViewById(R.id.editTextUsername);
        editTextPassword = findViewById(R.id.editTextPassword);
        buttonSignUp = findViewById(R.id.buttonSignUp);
        textViewSignIn = findViewById(R.id.textViewSignIn);


//        textViewSignIn.setOnClickListener(v -> {
//            // Tạo một đối tượng Intent mới để mở SignUpActivity
//            Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
//            // Bắt đầu một hoạt động mới
//            startActivity(intent);
//        });
        buttonSignUp.setOnClickListener(v -> {
            String name = editTextName.getText().toString();
            String username = editTextUsername.getText().toString();
            String password = editTextPassword.getText().toString();

            // Kiểm tra xem tên đăng nhập đã tồn tại chưa
            Query query = myRef.orderByChild("username").equalTo(username);
            query.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        Toast.makeText(SignUpActivity.this, "Tên đăng nhập đã tồn tại", Toast.LENGTH_SHORT).show();
                    } else {
                        String admin_id = myRef.push().getKey();
                        Map<String, Object> values = new HashMap<>();
                        values.put("ad_name", name);
                        values.put("username", username);
                        values.put("password", password);
                        myRef.child(admin_id).setValue(values);

                        Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
                        startActivity(intent);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.w(TAG, "sai thong tin", error.toException());
                }
            });
        });

        textViewSignIn.setOnClickListener(v -> {
            Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
            startActivity(intent);
        });
    }
}
