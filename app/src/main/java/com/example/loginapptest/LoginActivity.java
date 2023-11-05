package com.example.loginapptest;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class LoginActivity extends AppCompatActivity {
    private static final String BASE_URL = "https://uiot.ixxc.dev/auth/realms/master/protocol/openid-connect/token";
    String token = "";


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
            startActivity(Signings);
        });

        //Check if get user list successfully -------------- NEED MAINTENANCE


        //When click back button
        TextView back = (TextView) findViewById(R.id.back_login);
        back.setOnClickListener(view -> { 
            Intent Back = new Intent(LoginActivity.this, HomeActivity.class);
            startActivity(Back);
        });

        //When click button login
        login.setOnClickListener(view -> {
                    String user = username.getText().toString();
                    String pass = password.getText().toString();
                    try {
                        if (loginCheck(user, pass)) {
                            Intent Login = new Intent(LoginActivity.this, HomeActivity.class);
                            startActivity(Login);
                        } else {
                            Toast.makeText(LoginActivity.this, "Login failed", Toast.LENGTH_SHORT).show();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
        );

    }
    private boolean loginCheck (String username, String password) throws IOException {
        OkHttpClient client = new OkHttpClient();

        // Create a new RequestBody instance with the request parameters.
        RequestBody body = RequestBody.create(
                MediaType.parse("application/x-www-form-urlencoded"),
                "grant_type=password&username=" + username + "&password=" + password + "&client_id=openremote"
        );

        // Create a new Request instance.
        Request request = new Request.Builder()
                .url(BASE_URL)
                .post(body)
                .build();

        // Execute the request and get the response.
        Response response = client.newCall(request).execute();

        // Check if the response was successful.
        if (response.isSuccessful()) {
            // Get the response body as a JSON object.
            JsonObject json = new Gson().fromJson(response.body().string(), JsonObject.class);
            // Get the access token from the JSON object.
            token = json.get("access_token").getAsString();
            Log.d("token", token);
            return true;

        } else {
            // If something went wrong, show error message.
            Toast.makeText(this, "Error: " + response.code() + " " + response.message(), Toast.LENGTH_SHORT).show();
            return false;
        }
    }
}