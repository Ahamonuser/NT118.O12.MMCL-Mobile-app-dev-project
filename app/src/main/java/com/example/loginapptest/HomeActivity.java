package com.example.loginapptest;


import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;

import java.io.File;
import java.util.Locale;

public class HomeActivity extends AppCompatActivity {

    String[] LanguageList = {"English", "Tiếng Việt", "Français"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home);
        loadLocale();
        //Setup
        Button login = (Button)findViewById(R.id.login_home_btn);
        Button signup = (Button)findViewById(R.id.signup_home_btn);
        ImageView lang = (ImageView)findViewById(R.id.lang);
        SharedPreferences prefs = this.getSharedPreferences("Settings", MODE_PRIVATE);
        String language = prefs.getString("My_Lang", "");
        if(language.equals("en")){
            lang.setImageResource(R.drawable.flag_of_the_united_states);
            setLocated("en");
        }
        else if(language.equals("vi")){
            lang.setImageResource(R.drawable.flag_of_vietnam);
            setLocated("vi");
        }
        else if(language.equals("fr")){
            lang.setImageResource(R.drawable.flag_of_france);
            setLocated("fr");
        }
        lang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(HomeActivity.this);
                builder.setTitle(R.string.Choose_lang);
                builder.setSingleChoiceItems(LanguageList, -1, (dialogInterface, i) -> {
                    if(i == 0){
                        lang.setImageResource(R.drawable.flag_of_the_united_states);
                        setLocated("en");
                        recreate();
                    }
                    else if(i == 1){
                        lang.setImageResource(R.drawable.flag_of_vietnam);
                        setLocated("vi");
                        recreate();
                    }
                    else if(i == 2){
                        lang.setImageResource(R.drawable.flag_of_france);
                        setLocated("fr");
                        recreate();
                    }
                    dialogInterface.dismiss();
                });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
        });

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

    private void loadLocale(){
        SharedPreferences prefs = this.getSharedPreferences("Settings", MODE_PRIVATE);
        String language = prefs.getString("My_Lang", "");
        setLocated(language);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        deleteCache(this);
    }

    public static void deleteCache(Context context) {
        try {
            File dir = context.getCacheDir();
            deleteDir(dir);
        } catch (Exception e) { e.printStackTrace();}
    }

    public static boolean deleteDir(File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
            return dir.delete();
        } else if(dir.isFile()) {
            return dir.delete();
        } else {
            return false;
        }
    }
}