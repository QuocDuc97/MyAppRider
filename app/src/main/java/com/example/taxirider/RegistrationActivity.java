package com.example.taxirider;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.example.taxirider.Model.Rider;
import com.example.taxirider.common.Common;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import dmax.dialog.SpotsDialog;

public class RegistrationActivity extends AppCompatActivity {


    private TextView tvLogin;
    private EditText mUsername;
    private Button btnRegistration;
    private EditText mEmail;
    private EditText mPassword;
    private EditText mPhone;
    private FirebaseAuth mAuth;
    private FirebaseDatabase database;
    private DatabaseReference user;
    private AlertDialog waiting;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        getInit();
        tvLogin = (TextView) findViewById(R.id.tvDangNhap);
        tvLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), LoginActivity.class));
            }
        });

        register();


    }

    private void register() {
        btnRegistration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                waiting = new SpotsDialog.Builder().setContext(RegistrationActivity.this).build();
                if(validate()){
                    mAuth.createUserWithEmailAndPassword(mEmail.getText().toString(), mPassword.getText().toString())
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    waiting.dismiss();
                                    Rider account = new Rider();
                                    account.setName(mUsername.getText().toString());
                                    account.setEmail(mEmail.getText().toString());
                                    account.setPass(mPassword.getText().toString());
                                    account.setPhone(mPhone.getText().toString());
                                    user.child(mAuth.getCurrentUser().getUid()).setValue(account).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Toast.makeText(RegistrationActivity.this, "dang ky thanh cong", Toast.LENGTH_SHORT).show();
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(RegistrationActivity.this, "dang ky k thanh cong 1", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                    startActivity(new Intent(RegistrationActivity.this, LoginActivity.class));
                                }
                            });

                }

            }
        });
    }

    private void getInit() {
        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        user = FirebaseDatabase.getInstance().getReference(Common.CustomerInformation);
        mUsername = findViewById(R.id.edUsername);
        mEmail = findViewById(R.id.edEmail);
        mPassword = findViewById(R.id.edPassword);
        mPhone = findViewById(R.id.edPhone);
        btnRegistration = findViewById(R.id.btnRegistration);

    }

    public Boolean validate() {
        Boolean value = true;
        if (mEmail.getText().toString().equals("") || !mEmail.getText().toString().trim().matches(String.valueOf(android.util.Patterns.EMAIL_ADDRESS))) {
            value = false;
            mEmail.setError("email khong duoc bo trong hoac co dinh dang abc@xyz.com");
        } else if (mPassword.getText().toString().trim().equals("")) {
            value = false;
            mPassword.setError("mat khau khong duoc bo trong");
        } else if (mUsername.getText().toString().trim().equals("")) {
            value = false;
            mUsername.setError("ho ten khong duoc bo trong");
        } else if (mPhone.getText().toString().trim().equals("")) {
            value = false;
            mPhone.setError("So dien thoai khong duoc bo trong");
        }
        return value;
    }

}
