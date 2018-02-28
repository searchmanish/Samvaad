package com.samvaad;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ProfileActivity extends AppCompatActivity {

    private ImageView mProfileImage;
    private TextView mProfileName, mProfileStatus, mProfileFriendsCount;
    private Button mProfileSendReqBtn;

    private DatabaseReference mUserDatabase;
    private DatabaseReference mFriendReqDatabase;
    private FirebaseUser mCurrent_user;
    private String mCurrent_state;

    ProgressDialog mProgressDialog;

    private TextView mDisplayId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // mDisplayId = findViewById(R.id.profile_displayName);
        final String user_id = getIntent().getStringExtra("user_id");
        //mDisplayId.setText(user_id);

        mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(user_id);
        mFriendReqDatabase = FirebaseDatabase.getInstance().getReference().child("Friend_req");
        mCurrent_user = FirebaseAuth.getInstance().getCurrentUser();

        mProfileImage = findViewById(R.id.profile_image);
        mProfileName = findViewById(R.id.profile_displayName);
        mProfileStatus = findViewById(R.id.profile_status);
        mProfileFriendsCount = findViewById(R.id.profile_totalFriends);
        mProfileSendReqBtn = findViewById(R.id.profile_send_req_btn);

        //setting the data of uaer profile selected
        mCurrent_state = "not_friends";
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setTitle("Loading UserData");
        mProgressDialog.setMessage("Please Wait While Data is Loading....");
        mProgressDialog.setCanceledOnTouchOutside(false);
        mProgressDialog.show();

        mUserDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String display_name = dataSnapshot.child("name").getValue().toString();
                String status = dataSnapshot.child("status").getValue().toString();
                String image = dataSnapshot.child("image").getValue().toString();

                mProfileName.setText(display_name);
                mProfileStatus.setText(status);
                Glide.with(ProfileActivity.this).load(image).placeholder(R.drawable.avtar).into(mProfileImage);

                mProgressDialog.dismiss();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mProfileSendReqBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mProfileSendReqBtn.setEnabled(false);
                if (mCurrent_state.equals("not_friends")) {
                    mFriendReqDatabase.child(mCurrent_user.getUid()).child(user_id).child("request_type").setValue("sent")
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {

                                    if (task.isSuccessful()) {
                                        mFriendReqDatabase.child(user_id).child(mCurrent_user.getUid()).child("request_type")
                                                .setValue("received").addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                mProfileSendReqBtn.setEnabled(true);
                                                mProfileSendReqBtn.setText("CANCEL FRIEND REQUEST");
                                                mCurrent_state = "req_sent";
                                                Toast.makeText(ProfileActivity.this, "Request sent sucessfully", Toast.LENGTH_SHORT).show();

                                            }
                                        });

                                    } else {
                                        Toast.makeText(ProfileActivity.this, "Request sent failed", Toast.LENGTH_SHORT).show();

                                    }

                                }
                            });
                }

            }
        });

    }
}
