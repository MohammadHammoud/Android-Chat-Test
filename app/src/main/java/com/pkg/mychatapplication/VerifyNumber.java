package com.pkg.mychatapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class VerifyNumber extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private FirebaseUser mCurrentUser;
    private String mVerifyCode;
    private Button mVerify;
    private EditText mCode;
    private ProgressBar mProgressBar;
    private TextView mMessage;
    private DatabaseReference mDatabase ;
    private String mPhoneNum;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_number);
        mAuth = FirebaseAuth.getInstance();

        mVerifyCode = this.getIntent().getStringExtra("VerificationCode");        //retrieve the verification code received in previous page
        mPhoneNum = this.getIntent().getStringExtra("PhoneNumber");        //retrieve the Phone Number
        mVerify = findViewById(R.id.verify_btn);
        mCode = findViewById(R.id.otp_text_view);
        mProgressBar = findViewById(R.id.otp_progress_bar);
        mMessage = findViewById(R.id.otp_form_feedback);

        mVerify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Check if the user typed code is empty, correct or wrong

                if (mCode.getText().toString().isEmpty()){
                    mMessage.setText("Please fill the empty field");
                    mMessage.setVisibility(View.VISIBLE);
                }
                else{
                    mProgressBar.setVisibility(View.VISIBLE);
                    mVerify.setEnabled(false); //Disable the button when verifying

                    PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerifyCode, mCode.getText().toString());
                    signInWithPhoneAuthCredential(credential);




                }
            }
        });

    }

    public void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information and Go to Main Page

                            mCurrentUser = mAuth.getCurrentUser();



                            // Toast.makeText(VerifyNumber.this, mCurrentUser.getUid().toString()  ,Toast.LENGTH_LONG).show();
                            mDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(mCurrentUser.getUid());
                            mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {


                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if (snapshot.getValue() == null) {
                                        // The User doesn't exist
                                        //Create new User with this ID
                                        HashMap<String,String> UserMap = new HashMap<String, String>();
                                        UserMap.put("phoneNumber", mPhoneNum);

                                        UserMap.put("image","https://firebasestorage.googleapis.com/v0/b/mychatapplication-8f040.appspot.com/o/Default_Picture%2Fuser2.jpg?alt=media&token=f8ea3c03-0363-41ee-ab09-ec8f5625c66a");


                                        mDatabase.setValue(UserMap);
                                    }
                                    else{
                                        //The user already exists, don't add anything to the database


                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });


                            // Write data to the database
//                             mDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(mCurrentUser.getUid());
//
//
//                            HashMap<String,String> UserMap = new HashMap<String, String>();
//                            UserMap.put("phoneNumber", mPhoneNum);
//
//                            UserMap.put("image","default");
//
//                            mDatabase.setValue(UserMap);
                            goToMainPage();

                            // ...
                        } else {
                            // Sign in failed, display a message and update the UI
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                // The verification code entered was invalid
                                mMessage.setText("A verification error has occured");
                                mMessage.setVisibility(View.VISIBLE);
                            }
                        }
                        mProgressBar.setVisibility(View.INVISIBLE);
                        mVerify.setEnabled(true);
                    }
                });
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(mCurrentUser!=null){         //Go to the Chat Page
            goToMainPage();
        }
    }

    public void goToMainPage(){
        Intent home = new Intent(VerifyNumber.this, MainActivity.class);
        home.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        home.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(home);
        finish();
    }


}
