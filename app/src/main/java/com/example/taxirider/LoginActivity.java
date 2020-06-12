package com.example.taxirider;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.example.taxirider.common.Common;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import dmax.dialog.SpotsDialog;

public class LoginActivity extends AppCompatActivity {


   private EditText mEmail, mPassword;
   private TextView mSignUp;
   private Button btnLogin;
   private FirebaseAuth mAuth;
   private FirebaseDatabase database;
   private DatabaseReference user;
   private AlertDialog waiting;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getInit();
        login();
        mSignUp= (TextView)findViewById(R.id.tvDangKy);
        mSignUp.setOnClickListener(onClickSignUp());

    }

    private void login() {
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(validate()){
                    waiting= new SpotsDialog.Builder().setContext(LoginActivity.this).build();
                    mAuth.signInWithEmailAndPassword(mEmail.getText().toString(),mPassword.getText().toString()).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                        @Override
                        public void onSuccess(AuthResult authResult) {
                            waiting.dismiss();
                            Toast.makeText(LoginActivity.this, "Dang nhap thanh con", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(getApplicationContext(),Home.class));
                            finish();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(LoginActivity.this, "Dang nhap that bai", Toast.LENGTH_SHORT).show();
                        }
                    });

                }
            }
        });
    }


    public Boolean validate() {
        Boolean value = true;
        if (mEmail.getText().toString().equals("") || !mEmail.getText().toString().matches(Common.EMAIL_PATTERN)) {
            Log.d("TAG", "validate: "+value);
            value = false;
            mEmail.setError("email khong duoc bo trong hoac co dinh dang abc@xyz.com");
        } else {
            mEmail.setError(null);
        }

        if (mPassword.getText().toString().trim().equals("")) {
            value = false;
            mPassword.setError("mat khau khong duoc bo trong");
        } else {
            mPassword.setError(null);
        }
        return value;
    }
    private void getInit() {
        mEmail= findViewById(R.id.edEmailLogin);
        mPassword=findViewById(R.id.edPasswordLogin);
        btnLogin= findViewById(R.id.btnLogin);
        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        user = FirebaseDatabase.getInstance().getReference(Common.CustomerInformation);


    }
    private View.OnClickListener onClickSignUp() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent= new Intent(LoginActivity.this,RegistrationActivity.class);
                LoginActivity.this.startActivity(intent);
            }
        };
    }


}
