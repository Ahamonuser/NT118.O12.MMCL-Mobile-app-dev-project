package com.example.loginapptest;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import com.google.android.material.textfield.TextInputEditText;

public class UserFragment extends Fragment {

    String token = "";

    JsonObject jsonObjectUser = new JsonObject();

    public UserFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v =  inflater.inflate(R.layout.fragment_user, container, false);
        // Lấy token từ bundle
        token = getArguments().getString("token");
        Log.d("User", token);

        Call_API_User();
        return v;
    }

    @SuppressLint("StaticFieldLeak")
    private void Call_API_User() {
        // Create a new AsyncTask to perform the network operation
        new AsyncTask<Void, Void, Boolean>() {
            @Override
            protected Boolean doInBackground(Void... voids) {
                // Perform the network operation here
                OkHttpClient client = new OkHttpClient().newBuilder()
                        .build();
                MediaType mediaType = MediaType.parse("application/json");
                Request request = new Request.Builder()
                        .url("https://uiot.ixxc.dev/api/master/user/user")
                        .method("GET", null)
                        .header("Authorization", "Bearer " + token)
                        .addHeader("Content-Type", "application/json")
                        .build();
                try {
                    Response response = client.newCall(request).execute();
                    if (!response.isSuccessful()) {
                        return false;
                    }
                    String json = response.body().string();
                    Gson gson = new Gson();
                    jsonObjectUser = gson.fromJson(json, JsonObject.class);

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
                    Get_API_User_Value(jsonObjectUser);
                }
            }
        }.execute();
    }

    private void Get_API_User_Value(JsonObject object)
    {
        String username = object.get("username").getAsString();
        String email = object.get("email").getAsString();
        String firstName = object.get("firstName").getAsString();
        String lastName = object.get("lastName").getAsString();
        TextInputEditText txt_Username = (TextInputEditText) getView().findViewById(R.id.name_edit_text);
        TextInputEditText txt_Email = (TextInputEditText) getView().findViewById(R.id.email_edit_text);
        TextInputEditText txt_FirstName = (TextInputEditText) getView().findViewById(R.id.FirstN_edit_text);
        TextInputEditText txt_LastName = (TextInputEditText) getView().findViewById(R.id.LastN_edit_text);
        txt_Username.setText(username);
        txt_Email.setText(email);
        txt_FirstName.setText(firstName);
        txt_LastName.setText(lastName);
        Log.d("User", "username: " + username);
        Log.d("User", "email: " + email);
        Log.d("User", "firstName: " + firstName);
        Log.d("User", "LastName: " + lastName);
    }
}