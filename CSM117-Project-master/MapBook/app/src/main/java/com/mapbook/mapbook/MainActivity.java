package com.mapbook.mapbook;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private FirebaseAuth mAuth;
    private EditText editEmail;
    private EditText editPassword;
    private Button buttonSignIn;
    private TextView viewSignUp;

    private DatabaseReference mDatabase;
    private RequestAccess requestAccessDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        requestAccessDB = new RequestAccess();

        editEmail = (EditText)findViewById(R.id.editTextEmail);
        editPassword = (EditText)findViewById(R.id.editTextPassword);
        buttonSignIn = (Button)findViewById(R.id.buttonSignIn);
        viewSignUp = (TextView)findViewById(R.id.signUp);
        viewSignUp.setOnClickListener(this);

        buttonSignIn.setOnClickListener(this);
    }

    public void logIn() {
        Intent intent = new Intent(this, MainNavigation.class);
        startActivity(intent);
    }

    public void goToSighUp() {
        Intent intent = new Intent(this, SignUpActivity.class);
        startActivity(intent);
    }


    private void userLogIn(){
        final String email = editEmail.getText().toString().trim();
        String password = editPassword.getText().toString().trim();

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Toast.makeText(MainActivity.this, "Authentication successful.",
                                    Toast.LENGTH_LONG).show();
                            FirebaseUser user = mAuth.getCurrentUser();
//                            requestAccessDB.addBookToUser(mAuth.getCurrentUser().getUid(), email,
//                                    "War and Peace", "Tolsto", "99877",
//                                    "Princeton", "English", "50","BUY");
//                            writeNewUser(mAuth.getCurrentUser().getUid(), email);
                            logIn();
//                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(MainActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
//                            updateUI(null);
                        }

                        // ...
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        Toast.makeText(MainActivity.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                    }});
    }

    @Override
    public void onClick(View v) {
        if(v == buttonSignIn){
            userLogIn();
        }
        if(v == viewSignUp){
            goToSighUp();
        }
    }
}