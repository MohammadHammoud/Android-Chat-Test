package com.pkg.mychatapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TableLayout;
import android.widget.Toast;

import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {
    //This Page is the chatPage but we have to do some authentication so that the user don't end up here without having logged in
    private FirebaseAuth mAuth;

    private Toolbar mToolbar;
    private ViewPager mViewPager;
    private FragmentsAdapter mAdapter;
    private TabLayout mTabLayout;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
         super.onCreateOptionsMenu(menu);
         getMenuInflater().inflate(R.menu.main_menu,menu);
         return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
         super.onOptionsItemSelected(item);
         if(item.getItemId()== R.id.main_logout_btn){       //Clicked on the logout button
             mAuth.signOut();
             goToSignUp();      //update the UI

         }
         if (item.getItemId()==R.id.profile){  //Clicked on the Profile button
             goToProfile();


         }
         return true;

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        mToolbar = findViewById(R.id.main_page_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Chat");

        mViewPager = findViewById(R.id.tab_pager);
        mAdapter = new FragmentsAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(mAdapter);
        mTabLayout = findViewById(R.id.main_tabs);
        mTabLayout.setupWithViewPager(mViewPager);


    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser mCurrentUser = mAuth.getCurrentUser();
        if(mCurrentUser==null) { //Go back to SignUp page
            goToSignUp();
        }

    }
//    public void onBackPressed() {
//        // super.onBackPressed();
//        Toast.makeText(MainActivity.this,"There is no back action",Toast.LENGTH_LONG).show();
//        return;
//    }
    public void goToSignUp(){
            Intent login = new Intent(MainActivity.this, SignUpPage.class);
            login.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            login.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(login);
            finish();

    }
    public void goToProfile(){
        Intent login = new Intent(MainActivity.this, ProfileActivity.class);
        login.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        login.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(login);
        finish();

    }

}
