package com.pkg.mychatapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity {
    private DatabaseReference mUserReference;
    private FirebaseUser mCurrentUser;
    private CircleImageView mImgView;
    private TextView mTextView;
    private String mImage;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        mImgView = findViewById(R.id.profilePic);
        mTextView = findViewById(R.id.Display_number);

        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();
        String myUserID = mCurrentUser.getUid();
        mUserReference = FirebaseDatabase.getInstance().getReference().child("Users").child(myUserID); //Current User
        mUserReference.keepSynced(true);  //offline capabilities
        //Retrieve this user data
        mUserReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //We could have simply passed the phoneNumber through intent from MainActivity to this activity
                //But for the sake of learning, I retrieved it from the database
                String mPhoneNumber = snapshot.child("phoneNumber").getValue().toString();

                 mImage = snapshot.child("image").getValue().toString();  //Should be a link to the image place in storage
                mTextView.setText(mPhoneNumber);
                Picasso.get().load(mImage).networkPolicy(NetworkPolicy.OFFLINE).placeholder(R.drawable.user2).into(mImgView, new Callback() {
                    @Override
                    public void onSuccess() {
                        //do nothing. Image successfully loaded while offline
                    }

                    @Override
                    public void onError(Exception e) {
                        //if failed go take it from firebase
                        Picasso.get().load(mImage).placeholder(R.drawable.user2).into(mImgView);


                        }
                });



            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    @Override
    public void onBackPressed() {
        Intent home = new Intent(ProfileActivity.this, MainActivity.class);
        home.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        home.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(home);
        finish();
    }
}
