package com.example.loginapptest;

import android.app.AlertDialog;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SignupActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        //Setup
        Button signup = (Button)findViewById(R.id.signup_activity_btn);
        EditText username = (EditText)findViewById(R.id.username_activity_txt);
        EditText password = (EditText)findViewById(R.id.password_activity_txt);
        EditText fullname = (EditText)findViewById(R.id.fullname_activity_txt);

        //Add user
        signup.setOnClickListener(view -> {
            DatabaseReference mDatabase;
            mDatabase = FirebaseDatabase.getInstance().getReference();
            User user = new User(username.getText().toString(), password.getText().toString(), fullname.getText().toString());
            mDatabase.child("User-list").child(user.username).setValue(user);
            //A notification box appear
            AlertDialog.Builder builder = new AlertDialog.Builder(SignupActivity.this);
            builder.setCancelable(true);
            builder.setTitle("Notification");
            builder.setMessage("Sign up successfully, please return to login page.");
            builder.setPositiveButton("OK", (dialogInterface, i) -> dialogInterface.dismiss());
            builder.show();
        });
    }
}