package com.samvaad;

import android.app.ProgressDialog;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class StatusActivity extends AppCompatActivity {
 private Toolbar mToolbar;
 private TextInputLayout mStatus;
 private Button mSaveBtn;

 //Progress Dialog
    private ProgressDialog mProgress;

 private DatabaseReference mStatusDatabase;
 private FirebaseUser mCurrentUser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_status);
        mToolbar = findViewById(R.id.status_appBar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("STATUS");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //Firebase
        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();
        String current_uid = mCurrentUser.getUid();

        mStatusDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(current_uid);
         String statusValue = getIntent().getStringExtra("statusValue");
        mStatus=findViewById(R.id.status_input);
        mSaveBtn=findViewById(R.id.status_save_btn);

        mStatus.getEditText().setText(statusValue);

        mSaveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //progress dialog
                mProgress = new ProgressDialog(StatusActivity.this);
                mProgress.setTitle("Status Update");
                mProgress.setMessage("Please wait while we save your new status..");
                mProgress.show();

                String status = mStatus.getEditText().getText().toString().trim();

                mStatusDatabase.child("status").setValue(status).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful())
                        {
                            mProgress.dismiss();
                        }else{
                            Toast.makeText(StatusActivity.this,"There is some error..",Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }
}
