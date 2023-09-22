package com.example.loginapptest;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    List<User> users = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Setup
        Button login = (Button)findViewById(R.id.login_btn);
        Button signup = (Button)findViewById(R.id.signup_main_btn);
        EditText username = (EditText)findViewById(R.id.username_main_txt);
        EditText password = (EditText)findViewById(R.id.password_main_txt);

        //When click button sign up
        signup.setOnClickListener(view -> {
            Intent Signings = new Intent(MainActivity.this, SignupActivity.class);
            startActivity(Signings);
        });

        //Check if get user list successfully
        DatabaseReference userlistref;
        userlistref = FirebaseDatabase.getInstance().getReference();
        userlistref.child("User-list").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (DataSnapshot userref:task.getResult().getChildren())
                {
                    Log.d("firebase", Objects.requireNonNull(userref.getValue()).toString());
                    User user = new User();
                    //user = userref.getValue(User.class);
                    //users.add(user);
                    Log.d("firebase", Objects.requireNonNull(userref.child("username").getValue()).toString());
                    user.username = Objects.requireNonNull(userref.child("username").getValue()).toString();
                    Log.d("firebase", Objects.requireNonNull(userref.child("password").getValue()).toString());
                    user.password = Objects.requireNonNull(userref.child("password").getValue()).toString();
                    users.add(user);
                }
                Toast.makeText(getApplicationContext(), "Getting data completed.", Toast.LENGTH_SHORT).show();
            }
            else {
                Toast.makeText(getApplicationContext(), "Error getting data.", Toast.LENGTH_SHORT).show();
            }
        });

        //When click button login
        login.setOnClickListener(view -> {
            if(username.getText().toString().equals("") || password.getText().toString().equals(""))
            {
                Toast.makeText(getApplicationContext(), "Username and password must be filled",Toast.LENGTH_SHORT).show();
            }
            else {
                for (int turn = 0; turn < users.size(); turn++)
                {
                    User user = users.get(turn);
                    if(username.getText().toString().equals(user.username) && password.getText().toString().equals(user.password))
                    {
                        Intent Logging = new Intent(MainActivity.this, SuccessActivityResult.class);
                        startActivity(Logging);
                        break;
                    }
                    else
                    {
                        if(turn == users.size() - 1)
                        {
                            Toast.makeText(getApplicationContext(), "Wrong Username or Password, please try again.",Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }
        });
    }
}