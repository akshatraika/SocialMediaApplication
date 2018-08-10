package com.alpha.twostudents.socialmediaapplication;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Date;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {


    private Button buttonRegister;
    private EditText editTextEmail;
    private EditText editTextFirstName;
    private EditText editTextLastName;
    private EditText editTextPassword;
    private EditText editTextConfirmPassword;
    private TextView textViewSignIn;
    private ProgressDialog progressDialog;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;
    private Spinner day_spinner;
    private Spinner month_spinner;
    private Spinner year_spinner;
    private Date date;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Get the Firebase Instance and the Database Reference
        firebaseAuth = firebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();

        // User is already signed in
        if (firebaseAuth.getCurrentUser() != null){
            // Start profile activity
            finish();
            startActivity(new Intent(getApplicationContext(), ProfileActivity.class));
        }

        // Initialize the variables
        editTextFirstName = (EditText) findViewById(R.id.editTextFirstName);
        editTextLastName = (EditText) findViewById(R.id.editTextLastName);
        editTextEmail = (EditText) findViewById(R.id.editTextEmail);
        editTextPassword = (EditText) findViewById(R.id.editTextPassword);
        editTextConfirmPassword = (EditText) findViewById(R.id.editTextConfirmPassword);


        // Still need to fix the contents of the date values. Look at database to see what I mean.
        day_spinner = (Spinner)findViewById(R.id.day_spinner);
        month_spinner = (Spinner)findViewById(R.id.month_spinner);
        year_spinner = (Spinner)findViewById(R.id.year_spinner);
        date = new Date();

        ArrayAdapter<CharSequence> day_adapter = ArrayAdapter.createFromResource(this,
                R.array.dates, android.R.layout.simple_spinner_dropdown_item);
        day_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        day_spinner.setAdapter(day_adapter);

        ArrayAdapter<CharSequence> month_adapter = ArrayAdapter.createFromResource(this,
                R.array.months, android.R.layout.simple_spinner_dropdown_item);
        month_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        month_spinner.setAdapter(month_adapter);

        ArrayAdapter<CharSequence> year_adapter = ArrayAdapter.createFromResource(this,
                R.array.years, android.R.layout.simple_spinner_dropdown_item);
        year_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        year_spinner.setAdapter(year_adapter);

        day_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                date.setDate(Integer.parseInt((String)parent.getItemAtPosition(position)));
                Toast.makeText(MainActivity.this, date.toString(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        month_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                date.setMonth(position+1);
                Toast.makeText(MainActivity.this, date.toString(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        year_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                date.setYear(Integer.parseInt((String)parent.getItemAtPosition(position)));
                Toast.makeText(MainActivity.this, date.toString(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });



        progressDialog = new ProgressDialog(this);
        buttonRegister = (Button) findViewById(R.id.buttonRegister);
        textViewSignIn = (TextView) findViewById(R.id.textViewSignIn);
        buttonRegister.setOnClickListener(this);
        textViewSignIn.setOnClickListener(this);
    }

    /**
     * Checks to see what was clicked
     * @param v the item that was clicked
     */
    @Override
    public void onClick(View v) {
        if (v == buttonRegister) {
            registerUser();
        }
        if (v == textViewSignIn) {
            startActivity(new Intent(this, LoginActivity.class));
        }
    }

    /**
     * Uses the email and password to create a user
     */
    private void registerUser() {
        // Get the email and password
        final String email = editTextEmail.getText().toString().trim();
        final String password = editTextPassword.getText().toString();
        String confirmPassword = editTextConfirmPassword.getText().toString();

        // Get other user information
        final String firstName = editTextFirstName.getText().toString();
        final String lastName = editTextLastName.getText().toString();
        final Date birthDate = date;

        //Checks that need to be met before account can be created
        // Checks fields if empty. Tells user if one was empty and stops from executing further
        if (TextUtils.isEmpty(email)) {
            Toast.makeText(this, "Please enter email", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Please enter password", Toast.LENGTH_SHORT).show();
            return;
        }

        // Confirm password functionality
        if (TextUtils.isEmpty(confirmPassword)) {
            Toast.makeText(this, "Please confirm the password", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!confirmPassword.equals(password)) {
            Toast.makeText(this, "Confirmed password do not match", Toast.LENGTH_SHORT).show();
            return;
        }

        // Checks if user information is filled out
        if (TextUtils.isEmpty(firstName) || TextUtils.isEmpty((lastName))) {
            Toast.makeText(this, "Fill out your complete name", Toast.LENGTH_SHORT).show();
            return;
        }

        // Display for the user to show the account being created
        progressDialog.setMessage("Registering User...");
        progressDialog.show();

        // Creating the account with email and password
        firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                // Adding user information to the account
                User newUser = new User(firstName, lastName, birthDate, email, password);
                databaseReference.child(firebaseAuth.getCurrentUser().getUid()).setValue(newUser);
                if (task.isSuccessful()) {
                    //User is successfully registered and signed in
                    Toast.makeText(MainActivity.this, "Registered Successfully", Toast.LENGTH_SHORT).show();
                    Intent i = new Intent(MainActivity.this, ProfilePicActivity.class);
// set the new task and clear flags
                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(i);

                } else {

                    Toast.makeText(MainActivity.this,"Could not register... Try again", Toast.LENGTH_SHORT).show();
                }
                progressDialog.dismiss();
            }
        });


    }
}
