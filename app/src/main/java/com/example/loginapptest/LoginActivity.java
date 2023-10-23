package com.example.loginapptest;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class LoginActivity extends AppCompatActivity {
    List<User> users = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Setup
        Button login = (Button)findViewById(R.id.login_btn);
        TextView signup = (TextView)findViewById(R.id.signup_login_btn);
        EditText username = (EditText)findViewById(R.id.username_login_txt);
        EditText password = (EditText)findViewById(R.id.password_login_txt);

        //When click button sign up
        signup.setOnClickListener(view -> {
            Intent Signings = new Intent(LoginActivity.this, SignupActivity.class);
            users.clear();
            startActivity(Signings);
        });

        //Check if get user list successfully -------------- NEED MAINTENANCE

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

        //When click back button
        Button back = (Button)findViewById(R.id.back_login_btn);
        back.setOnClickListener(view -> {
            Intent Back = new Intent(LoginActivity.this, HomeActivity.class);
            users.clear();
            startActivity(Back);
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
                        Intent Logging = new Intent(LoginActivity.this, SuccessActivityResult.class);
                        users.clear();
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