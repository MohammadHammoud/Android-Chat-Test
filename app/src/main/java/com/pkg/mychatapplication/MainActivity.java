package com.pkg.mychatapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {
    //This Page is the chatPage but we have to do some authentication so that the user don't end up here without having logged in
    private FirebaseAuth mAuth;
    private FirebaseUser mCurrentUser;
    private Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        mCurrentUser = mAuth.getCurrentUser();
        mToolbar = findViewById(R.id.main_page_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Chat");
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(mCurrentUser==null){ //Go back to SignUp page
            Intent login = new Intent(MainActivity.this, SignUpPage.class);
            login.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            login.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(login);
            finish();
        }
    }
}
