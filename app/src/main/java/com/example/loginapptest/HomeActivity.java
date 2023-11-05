package com.example.loginapptest;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class HomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home);

        //Setup
        Button login = (Button)findViewById(R.id.login_home_btn);
        Button signup = (Button)findViewById(R.id.signup_home_btn);

        //When click button login
        /*
        login.setOnClickListener(view -> {
            //Intent Logins = new Intent(HomeActivity.this, LoginActivity.class);
            Intent Logins = new Intent("LOGIN");
            startActivity(Logins);
        });
*/
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent Logins = new Intent("LOGIN2");
                startActivity(Logins);
            }
        });
        //When click button sign up
        signup.setOnClickListener(view -> {
            Intent Signings = new Intent(HomeActivity.this, SignupActivity.class);
            startActivity(Signings);
        });
    }
}