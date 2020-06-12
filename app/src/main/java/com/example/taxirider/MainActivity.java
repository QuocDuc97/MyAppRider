package com.example.taxirider;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

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
import com.rengwuxian.materialedittext.MaterialEditText;

import dmax.dialog.SpotsDialog;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class MainActivity extends AppCompatActivity {


    Button btnSignIn, btnSignUp;
    //intitalize variable
    FirebaseAuth mAuth;
    FirebaseDatabase database;
    DatabaseReference user;
    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate ( savedInstanceState );

        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/Arkhip_font.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build()
        );


        setContentView(R.layout.activity_main);
        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        user = FirebaseDatabase.getInstance().getReference(Common.CustomerInformation);
        //
        btnSignIn = findViewById(R.id.btnSignIn);
        btnSignUp = findViewById(R.id.btnSingup);


        //firebse

        // event signUp
        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDiaglogSignUp();
            }
        });

        //enven signIN
        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDiaglogSignIn();
            }
        });


    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext( CalligraphyContextWrapper.wrap(newBase));
    }
    private void showDiaglogSignIn() {
        final AlertDialog.Builder diaglog = new AlertDialog.Builder(this);
        diaglog.setTitle("Dang Nhap");
        diaglog.setMessage("vui long dien thong tin de dang nhap");
        LayoutInflater inflater = LayoutInflater.from(this);
        View view = inflater.inflate(R.layout.login, null);

        final MaterialEditText email = view.findViewById(R.id.email_login);
        final MaterialEditText password = view.findViewById(R.id.password_login);

        diaglog.setView(view);


        diaglog.setPositiveButton("Dang nhap", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int which) {

                dialogInterface.dismiss();
                //dang ky tai khoan
                //final AlertDialog waiting= new SpotsDialog(MainActivity.this);
                final android.app.AlertDialog waiting = new SpotsDialog.Builder().setContext(MainActivity.this).build();
                waiting.show();
                mAuth.signInWithEmailAndPassword(email.getText().toString(), password.getText().toString())
                        .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                            @Override
                            public void onSuccess(AuthResult authResult) {
                                waiting.dismiss();
                                startActivity(new Intent (MainActivity.this, Home.class));
                                finish();
                            }
                        }).addOnFailureListener(new OnFailureListener () {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(MainActivity.this, "Dang nhap that bai", Toast.LENGTH_SHORT).show();
                        waiting.dismiss();
                    }
                });
            }
        });

        diaglog.setNegativeButton("Huy bo", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int which) {
                dialogInterface.dismiss();
            }
        });


        diaglog.show();
    }

    private void showDiaglogSignUp() {
        final AlertDialog.Builder diaglog = new AlertDialog.Builder(this);
        diaglog.setTitle("Dang ky");
        diaglog.setMessage("vui long dung email de dang ky");
        LayoutInflater inflater = LayoutInflater.from(this);
        View view = inflater.inflate(R.layout.sign_up, null);
        final MaterialEditText email = view.findViewById(R.id.email_id);
        final MaterialEditText name = view.findViewById(R.id.Name_id);
        final MaterialEditText phone = view.findViewById(R.id.phone_id);
        final MaterialEditText pass = view.findViewById(R.id.pass_id);
        diaglog.setView(view);
        //set button
        diaglog.setPositiveButton("Dang ky", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int which) {
                dialogInterface.dismiss();
                if(TextUtils.isEmpty(email.getText().toString())){
                    Toast.makeText(MainActivity.this, "khong dc bo trong", Toast.LENGTH_SHORT).show();
                    return;
                }
                Log.d("test", "onClick: " + email.getText().toString());
                Log.d("test1", "onClick: " + pass.getText().toString());
                Log.d("test2", "onClick: " + phone.getText().toString());
                Log.d("test3", "onClick: " + name.getText().toString());
                Log.d("test4", "onClick: " + email.getText().toString());
                //dang ky tai khoan
                mAuth.createUserWithEmailAndPassword(email.getText().toString(), pass.getText().toString())
                        .addOnCompleteListener(new OnCompleteListener<AuthResult> () {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                Rider ac = new Rider ();
                                ac.setEmail(email.getText().toString());
                                ac.setPass(pass.getText().toString());
                                ac.setPhone(phone.getText().toString());
                                ac.setName(name.getText().toString());
                                String uid= mAuth.getCurrentUser().getUid();
                                user.child(uid).setValue(ac)
                                        .addOnSuccessListener(new OnSuccessListener<Void> () {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Toast.makeText(MainActivity.this, "thanh cong", Toast.LENGTH_SHORT).show();
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(MainActivity.this, "that bai", Toast.LENGTH_SHORT).show();
                                    }
                                })
                                ;

                            }


                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(MainActivity.this, "khong thanh cong", Toast.LENGTH_SHORT).show();
                    }
                });


            }
        });
        diaglog.setNegativeButton("huy bo", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int which) {
                dialogInterface.dismiss();
            }
        });
        diaglog.show();

    }
}
