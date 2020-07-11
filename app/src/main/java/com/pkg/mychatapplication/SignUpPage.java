package com.pkg.mychatapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
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
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;
import com.google.i18n.phonenumbers.Phonenumber.PhoneNumber;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class SignUpPage extends AppCompatActivity {


    private FirebaseAuth mAuth;
    private FirebaseUser mCurrentUser;
    private EditText mCountryCode;
    private EditText mPhoneNumber;
    private Button mSignUpButton;
    private ProgressBar mProgressBar;
    private TextView mMessage;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    private String myNum;
    private DatabaseReference mDatabase ;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_page);
        mCountryCode = findViewById(R.id.country_code_text);
        mPhoneNumber = findViewById(R.id.phone_number_text);
        mSignUpButton = findViewById(R.id.generate_btn);
        mProgressBar = findViewById(R.id.login_progress_bar);
        mMessage = findViewById(R.id.login_form_feedback);

        mAuth = FirebaseAuth.getInstance();
        mCurrentUser = mAuth.getCurrentUser();

        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                //auto login
                signInWithPhoneAuthCredential(phoneAuthCredential);

            }

            @Override
            public void onVerificationFailed(@NonNull FirebaseException e) {
                mMessage.setText(e.getMessage());
               // mMessage.setText("Verification failed.Try Again");
                mMessage.setVisibility(View.VISIBLE);
                mProgressBar.setVisibility(View.INVISIBLE);
                mSignUpButton.setEnabled(true);

            }

            @Override
            public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                super.onCodeSent(s, forceResendingToken);
                Intent Verify = new Intent(SignUpPage.this, VerifyNumber.class);
                Verify.putExtra("VerificationCode", s);       //send the verification code received to the VerifyNumber page
                Verify.putExtra("PhoneNumber",myNum);
                startActivity(Verify);


            }

            @Override
            public void onCodeAutoRetrievalTimeOut(@NonNull String s) {
                super.onCodeAutoRetrievalTimeOut(s);
            }
        };

        mSignUpButton.setOnClickListener(new View.OnClickListener() {   //get the values of both Country Code and the phone number
            @Override
            public void onClick(View view) {
                String CountCode = mCountryCode.getText().toString();
                String PhoneNum = mPhoneNumber.getText().toString();
                String  fullNum = "+"+CountCode+PhoneNum;
                myNum = "+" +CountCode+"-"+PhoneNum;

                if(CountCode.isEmpty() || PhoneNum.isEmpty()){
                    mMessage.setText("Please fill the empty fields");
                    mMessage.setVisibility(View.VISIBLE);
                }

                // I used Google's libphonenumber library to check if entered numbers are valid depending on each country
                //https://github.com/google/libphonenumber



                final PhoneNumberUtil phoneNumberUtil = PhoneNumberUtil.getInstance();
                try {
                    PhoneNumber phoneNumber = phoneNumberUtil.parse(fullNum, Locale.getDefault().getCountry());
                     boolean num= phoneNumberUtil.isValidNumber(phoneNumber);
                     if(!num){       //the number is not valid
                         mMessage.setText("Please Enter a valid number");
                         mMessage.setVisibility(View.VISIBLE);
                     }
                     else{      //Everything is valid


                         mProgressBar.setVisibility(View.VISIBLE);      //ProgressBar is now visible

                         mSignUpButton.setEnabled(false);       //Disabled while signing up is in progress

                         PhoneAuthProvider.getInstance().verifyPhoneNumber(     //Needs both countryCode and PhoneNumber
                                 fullNum,        // Phone number to verify
                                 60,                 // Timeout duration
                                 TimeUnit.SECONDS,   // Unit of timeout
                                 SignUpPage.this,               // Activity (for callback binding)
                                 mCallbacks);        // OnVerificationStateChangedCallbacks
                     }
                } catch (final Exception e) {
                    //USE THIS FOR TESTING

                   // Toast.makeText(SignUpPage.this,e.getMessage(),Toast.LENGTH_LONG).show();
                }



            }
        });



    }


    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(SignUpPage.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            //This is the case where auto verification happens
                            //Create the user in database if it doesn't exist
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
                                        UserMap.put("phoneNumber", myNum);


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

                            goToMainPage();
                            // ...
                        } else {
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                // The verification code entered was invalid
                                mMessage.setVisibility(View.VISIBLE);
                                mMessage.setText("There was an error verifying OTP");
                            }
                        }
                        mProgressBar.setVisibility(View.INVISIBLE);
                        mSignUpButton.setEnabled(true);
                    }
                });
    }




    public void goToMainPage(){
        Intent home = new Intent(SignUpPage.this, MainActivity.class);
        home.putExtra("PhoneNumber",myNum);
        home.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        home.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(home);
        finish();
    }


    @Override
    protected void onStart() {
        super.onStart();
        if(mCurrentUser!=null){         //Go to the Chat Page
           goToMainPage();
        }
    }
}
