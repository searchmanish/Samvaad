package com.samvaad;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import android.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import java.security.PrivateKey;

public class LoginActivity extends AppCompatActivity {

    private TextInputEditText mLoginEmail;
    private TextInputEditText mLoginPassword;
    private Button mLogin_btn;
    private Toolbar mLoginToolbar;

    private ProgressDialog mLoginProgress;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mAuth = FirebaseAuth.getInstance();

        mLoginEmail=findViewById(R.id.login_email);
        mLoginPassword=findViewById(R.id.login_password);
      //  mLoginToolbar=findViewById(R.id.login_toolbar);
        mLoginProgress=new ProgressDialog(this);

        mLogin_btn=findViewById(R.id.login_btn);

        mLogin_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               String email=mLoginEmail.getEditableText().toString();
               String password=mLoginPassword.getEditableText().toString();

               if(!TextUtils.isEmpty(email)||!TextUtils.isEmpty(password)){
                   mLoginProgress.setTitle("Login");
                   mLoginProgress.setMessage("Please wait while login..");
                   mLoginProgress.setCanceledOnTouchOutside(false);
                   mLoginProgress.show();
                  loginUser(email,password);
               }
            }
        });
    }

    private void loginUser(String email, String password) {
        mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    mLoginProgress.dismiss();
                    Intent mainIntent =new Intent(LoginActivity.this,MainActivity.class);
                    mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(mainIntent);
                    finish();
                }else
                {
                   mLoginProgress.hide();
                    Toast.makeText(LoginActivity.this,"invalid credintials",Toast.LENGTH_SHORT).show();
                }

            }
        });

    }
}
