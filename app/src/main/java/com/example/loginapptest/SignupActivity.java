package com.example.loginapptest;

import static android.content.ContentValues.TAG;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Locale;

public class SignupActivity extends AppCompatActivity {

    String signun_url = "https://uiot.ixxc.dev";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        loadLocale();

        //Setup
        Button signup = (Button)findViewById(R.id.signup_activity_btn);
        EditText username = (EditText)findViewById(R.id.username_activity_txt);
        EditText password = (EditText)findViewById(R.id.password_activity_txt);
        EditText email = (EditText)findViewById(R.id.email_activity_txt);
        EditText confirm_password = (EditText)findViewById(R.id.confirm_password_activity_txt);
        WebView webview = findViewById(R.id.webview);

        //Get back to login page
        TextView back = (TextView)findViewById(R.id.back_activity_btn);
        back.setOnClickListener(view -> {
            Intent backto = new Intent(SignupActivity.this, LoginActivity.class);
            startActivity(backto);
        });

        //Add user
        signup.setOnClickListener(view -> {
            webview.setVisibility(webview.GONE);
            webview.loadUrl(signun_url);
            webview.getSettings().setJavaScriptEnabled(true);
            webview.setWebViewClient(new WebViewClient() {

                public void onPageFinished(WebView view, String url) {
                    //Paste form to webview
                    Log.d("My Webview", url);
                    view.evaluateJavascript("document.querySelector('a.btn.waves-effect.waves-light').click();", null);
                    if (url.contains("registration"))
                    {
                        Log.d("My Webview", url);
                        Log.d("URL", "get fill form");

                        String user_script = "document.getElementById('username').value = '" + username.getText().toString() + "';";
                        String email_script = "document.getElementById('email').value = '" + email.getText().toString() + "';";
                        String pass_script = "document.getElementById('password').value = '" + password.getText().toString() + "';";
                        String confirm_pass_script = "document.getElementById('password-confirm').value = '" + confirm_password.getText().toString() + "';";
                        view.evaluateJavascript(user_script, null);
                        view.evaluateJavascript(email_script, null);
                        view.evaluateJavascript(pass_script, null);
                        view.evaluateJavascript(confirm_pass_script, null);

                        view.evaluateJavascript("document.getElementById('kc-register-form').submit();", null);

                        webview.setWebViewClient(new WebViewClient() {
                            @Override
                            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                                // Here put your code
                                Log.d("My Webview", url);

                                webview.setVisibility(webview.GONE);
                                Intent backto = new Intent(SignupActivity.this, LoginActivity.class);
                                startActivity(backto);
                                return true;
                            }
                        });
                    }
                }

            });
        });
        //Back to homepage
        TextView backhomepage = (TextView) findViewById(R.id.back_btn);
        backhomepage.setOnClickListener(view -> {
            Intent backtohome = new Intent(SignupActivity.this, HomeActivity.class);
            startActivity(backtohome);
        });
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
}