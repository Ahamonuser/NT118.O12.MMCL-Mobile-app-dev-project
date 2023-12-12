package com.example.loginapptest;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import java.io.IOException;
import java.util.Locale;

public class LoginActivity extends AppCompatActivity {
    private static final String BASE_URL = "https://uiot.ixxc.dev/auth/realms/master/protocol/openid-connect/token";
    String token = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        loadLocale();

        // Setup
        Button login = findViewById(R.id.login_btn);
        TextView signup = findViewById(R.id.signup_login_btn);
        EditText username = findViewById(R.id.username_login_txt);
        EditText password = findViewById(R.id.password_login_txt);

        // When click button sign up
        signup.setOnClickListener(view -> {
            Intent Signings = new Intent(LoginActivity.this, SignupActivity.class);
            startActivity(Signings);
        });

        // When click back button
        TextView back = findViewById(R.id.back_btn);
        back.setOnClickListener(view -> {
            Intent Back = new Intent(LoginActivity.this, HomeActivity.class);
            startActivity(Back);
        });

        // When click button login
        login.setOnClickListener(view -> loginCheck(username.getText().toString(), password.getText().toString()));
    }

    private void loadLocale(){
        SharedPreferences prefs = this.getSharedPreferences("Settings", MODE_PRIVATE);
        String language = prefs.getString("My_Lang", "");
        setLocated(language);
    }

    private void setLocated(String lang){
        Resources resources = this.getBaseContext().getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        Configuration config = resources.getConfiguration();
        Locale locale = new Locale(lang);
        config.setLocale(locale);
        Locale.setDefault(locale);
        resources.updateConfiguration(config, metrics);
        SharedPreferences.Editor editor = this.getSharedPreferences("Settings", MODE_PRIVATE).edit();
        editor.putString("My_Lang", lang);
        editor.apply();
    }

    @SuppressLint("StaticFieldLeak")
    private void loginCheck( String username, String password) {
        // Create a new AsyncTask to perform the network operation
        new AsyncTask<Void, Void, Boolean>() {
            @Override
            protected Boolean doInBackground(Void... voids) {
                // Perform the network operation here
                OkHttpClient client = new OkHttpClient().newBuilder()
                        .build();
                MediaType mediaType = MediaType.parse("application/x-www-form-urlencoded");
                RequestBody body = RequestBody.create(mediaType, "grant_type=password&client_id=openremote&username=" + username + "&password=" + password);
                Request request = new Request.Builder()
                        .url("https://uiot.ixxc.dev/auth/realms/master/protocol/openid-connect/token")
                        .method("POST", body)
                        .addHeader("Content-Type", "application/x-www-form-urlencoded")
                        .build();
                try {
                    Response response = client.newCall(request).execute();
                    if (!response.isSuccessful()) {
                        return false;
                    }
                    String json = response.body().string();
                    Gson gson = new Gson();
                    JsonObject jsonObject = gson.fromJson(json, JsonObject.class);
                    token = jsonObject.get("access_token").getAsString();

                } catch (IOException e) {
                    e.printStackTrace();
                    return false;
                }
                // Return the result of the network operation
                return true;
            }

            @Override
            protected void onPostExecute(Boolean result) {
                // Handle the result of the network operation here
                if (result) {
                    // The user logged in successfully
                    Toast.makeText(LoginActivity.this, R.string.LoginSuccess, Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(LoginActivity.this, SuccessActivityResult.class);
                    intent.putExtra("token",token);
                    startActivity(intent);
                } else {
                    // The user failed to log in
                    Toast.makeText(LoginActivity.this, R.string.LoginFail, Toast.LENGTH_SHORT).show();
                }
            }
        }.execute();
    }

}
