package com.example.loginapptest;

import static android.content.ContentValues.TAG;

import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class SignupActivity extends AppCompatActivity {

    String signun_url = "https://uiot.ixxc.dev/auth/realms/master/login-actions/registration?client_id=openremote&tab_id=X32j8mzPTho";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        //Setup
        Button signup = (Button)findViewById(R.id.signup_activity_btn);
        EditText username = (EditText)findViewById(R.id.username_activity_txt);
        EditText password = (EditText)findViewById(R.id.password_activity_txt);
        EditText fullname = (EditText)findViewById(R.id.fullname_activity_txt);

        WebView webview = findViewById(R.id.webview);


        //Add user
        signup.setOnClickListener(view -> {

            webview.setVisibility(webview.VISIBLE);
            webview.loadUrl(signun_url);
            webview.getSettings().setJavaScriptEnabled(true);
            webview.setWebViewClient(new WebViewClient() {
                public void onPageFinished(WebView view, String url) {
                    //Paste form to webview
                    if (url.contains("registration"))
                    {
                        Log.d("URL", "get fill form");
                        String user_script = "document.getElementById('username').value = '" + username.getText().toString() + "';";
                        String pass_script = "document.getElementById('password').value = '" + password.getText().toString() + "';";
                        view.evaluateJavascript(user_script, null);
                        view.evaluateJavascript(pass_script, null);
                        view.evaluateJavascript("document.getElementById('kc-form-register').submit();", null);
                    }

                }
            });
        });

        //Get back to login page
        TextView back = (TextView)findViewById(R.id.back_activity_btn);
        back.setOnClickListener(view -> {
            Intent backto = new Intent(SignupActivity.this, LoginActivity.class);
            startActivity(backto);
        });

        //Back to homepage
        TextView backhomepage = (TextView) findViewById(R.id.back);
        backhomepage.setOnClickListener(view -> {
            Intent backtohome = new Intent(SignupActivity.this, HomeActivity.class);
            startActivity(backtohome);
        });
    }
}