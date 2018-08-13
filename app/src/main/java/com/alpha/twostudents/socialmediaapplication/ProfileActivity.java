////////////////////////////////////////////////////////////////////////////////////////////////////
//
// Initial activity fot the user. Displays the Home fragment initially.
// Displays after login is successful
//
////////////////////////////////////////////////////////////////////////////////////////////////////
package com.alpha.twostudents.socialmediaapplication;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.alpha.twostudents.socialmediaapplication.CameraFragment;
import com.alpha.twostudents.socialmediaapplication.HomeFragment;
import com.alpha.twostudents.socialmediaapplication.LoginActivity;
import com.alpha.twostudents.socialmediaapplication.ProfileFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.w3c.dom.Text;

public class ProfileActivity extends AppCompatActivity implements View.OnClickListener, HomeFragment.OnFragmentInteractionListener, CameraFragment.OnFragmentInteractionListener, ProfileFragment.OnFragmentInteractionListener {

    private FirebaseAuth firebaseAuth;
    private TextView textViewUserEmail;
    private Button buttonLogout;
    //Bottom navigation view allows user to jump to timeline, camera and profile fragment
    private BottomNavigationView.OnNavigationItemSelectedListener navListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    Fragment selectedFragment = null;

                    switch (item.getItemId()) {
                        case R.id.nav_camera:
                            selectedFragment = new CameraFragment();
                            break;
                        case R.id.nav_profile:
                            selectedFragment = new ProfileFragment();
                            break;
                        case R.id.nav_home:
                            selectedFragment = new HomeFragment();
                            break;
                    }

                    //jumping to the selected fragment
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.fragment_container, selectedFragment).addToBackStack(null).commit();


                    return true;
                }
            };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //getting saved state if any
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);


        //Declaring navigation listeners and initialising fragment for the current state i.e home
        BottomNavigationView bottomNav =  findViewById(R.id.bottom_navigation);
        bottomNav.setOnNavigationItemSelectedListener(navListener);
        try {
            getSupportFragmentManager().beginTransaction().
                    replace(R.id.fragment_container, new HomeFragment()).commit();
        } catch (RuntimeException e){
            Log.e("", "RunTime Exception occurred while initiating fragments" );
        }


        firebaseAuth = FirebaseAuth.getInstance();

        // User is not logged in
        if (firebaseAuth.getCurrentUser() == null) {
            finish();
            startActivity(new Intent(this, LoginActivity.class));
        }
        FirebaseUser user = firebaseAuth.getCurrentUser();

//        //Displaying the user email id on the top
//        //TODO get rid of this once menu bar on the left is finished
        textViewUserEmail = findViewById(R.id.textViewCurrentUser);
        textViewUserEmail.setText("Welcome " + user.getEmail());
        buttonLogout = (Button) findViewById(R.id.buttonLogOut);
        buttonLogout.setText(user.getEmail());
        buttonLogout.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        if (v == buttonLogout) {
            firebaseAuth.signOut();
            finish();
            startActivity(new Intent(this, LoginActivity.class));

        }
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}