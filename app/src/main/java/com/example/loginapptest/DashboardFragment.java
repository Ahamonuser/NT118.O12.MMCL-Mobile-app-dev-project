package com.example.loginapptest;

import static java.lang.String.valueOf;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class DashboardFragment extends Fragment {
    String token = "";

    ArrayList<String> IDArrayList = new ArrayList<>();

    ArrayList<JsonObject> ObjectList = new ArrayList<>();

    View v;

    public DashboardFragment() {
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
        v = inflater.inflate(R.layout.fragment_dashboard, container, false);
        // Lấy token từ bundle
        token = getArguments().getString("token");
        Log.d("Dashboard", token);
        Get_Id_API_Weather();
        Call_API_Weather();
        return v;
    }


    @SuppressLint("StaticFieldLeak")
    private void Get_Id_API_Weather() {
        // Create a new AsyncTask to perform the network operation
        new AsyncTask<Void, Void, Boolean>() {
            @Override
            protected Boolean doInBackground(Void... voids) {
                // Perform the network operation here
                OkHttpClient client = new OkHttpClient().newBuilder()
                        .build();
                MediaType mediaType = MediaType.parse("application/json");
                RequestBody body = RequestBody.create(mediaType, "{\"realm\":{\"name\":\"master\"},\"select\":{\"attributes\":[\"location\",\"direction\"]},\"types\":[\"WeatherAsset\"],\"attributes\":{\"items\":[{\"name\":{\"predicateType\":\"string\",\"value\":\"location\"},\"meta\":[{\"name\":{\"predicateType\":\"string\",\"value\":\"showOnDashboard\"},\"negated\":true},{\"name\":{\"predicateType\":\"string\",\"value\":\"showOnDashboard\"},\"value\":{\"predicateType\":\"boolean\",\"value\":true}}]}]}}");
                Request request = new Request.Builder()
                        .url("https://uiot.ixxc.dev/api/master/asset/query")
                        .method("POST", body)
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
                    JsonArray jsonArray = gson.fromJson(json, JsonArray.class);
                    for (int i = 0; i < jsonArray.size(); i++) {
                        JsonObject jsonObject = jsonArray.get(i).getAsJsonObject();
                        IDArrayList.add(jsonObject.get("id").getAsString());
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                    return false;
                }
                // Return the result of the network operation
                return true;
            }
        }.execute();
    }

    @SuppressLint("StaticFieldLeak")
    private void Call_API_Weather() {
        // Create a new AsyncTask to perform the network operation
        new AsyncTask<Void, Void, Boolean>() {
            @Override
            protected Boolean doInBackground(Void... voids) {
                // Perform the network operation here
                for (int i = 0; i < IDArrayList.size(); i++) {
                    OkHttpClient client = new OkHttpClient().newBuilder()
                            .build();
                    Request request = new Request.Builder()
                            .url("https://uiot.ixxc.dev/api/master/asset/" + IDArrayList.get(i))
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
                        JsonObject jsonObject = gson.fromJson(json, JsonObject.class);
                        ObjectList.add(jsonObject);
                    } catch (IOException e) {
                        e.printStackTrace();
                        return false;
                    }
                }
                // Return the result of the network operation
                return true;
            }
            @Override
            protected void onPostExecute(Boolean result) {
                // Handle the result of the network operation here
                if (result) {
                    for (int i = 0; i < ObjectList.size(); i++) {
                        Get_API_Weather_Value(ObjectList.get(i));
                    }
                }
            }
        }.execute();
    }

    private void Get_API_Weather_Value(JsonObject object)
    {
        JsonObject attribute = object.get("attributes").getAsJsonObject();
        if (object.get("name").getAsString().equals("Default Weather"))
        {
            JsonObject rainfall_att = attribute.get("rainfall").getAsJsonObject();
            JsonObject temperature_att = attribute.get("temperature").getAsJsonObject();
            JsonObject humidity_att = attribute.get("humidity").getAsJsonObject();
            JsonObject place_att = attribute.get("place").getAsJsonObject();
            JsonObject windSpeed_att = attribute.get("windSpeed").getAsJsonObject();
            JsonObject windDirection_att = attribute.get("windDirection").getAsJsonObject();
            float rainfall = rainfall_att.get("value").getAsFloat();
            float temperature = temperature_att.get("value").getAsFloat();
            int humidity = humidity_att.get("value").getAsInt();
            String place = place_att.get("value").getAsString();
            float windSpeed = windSpeed_att.get("value").getAsFloat();
            float windDirection = windDirection_att.get("value").getAsFloat();
            TextView txt_rainfall = (TextView) v.findViewById(R.id.Rainfall);
            TextView txt_temperature = (TextView) v.findViewById(R.id.Temperature_Def);
            TextView txt_humidity = (TextView) v.findViewById(R.id.Humidity_Def);
            TextView txt_place = (TextView) v.findViewById(R.id.Place);
            TextView txt_windSpeed = (TextView) v.findViewById(R.id.Windspeed);
            TextView txt_windDirection = (TextView) v.findViewById(R.id.Winddirection);
            txt_rainfall.setText(valueOf(rainfall));
            txt_temperature.setText(valueOf(temperature));
            txt_humidity.setText(valueOf(humidity));
            txt_place.setText(place);
            txt_windSpeed.setText(valueOf(windSpeed));
            txt_windDirection.setText(valueOf(windDirection));
            Log.d("Default Weather", "rainfall: " + rainfall + " mm");
            Log.d("Default Weather", "temperature: " + temperature + " °C");
            Log.d("Default Weather", "humidity: " + humidity + " %");
            Log.d("Default Weather", "place: " + place);
        }
        else if (object.get("name").getAsString().equals("Weather Asset"))
        {
            JsonObject AQI_att = attribute.get("AQI").getAsJsonObject();
            JsonObject AQI_Predict_att = attribute.get("AQI_Predict").getAsJsonObject();
            JsonObject CO2_att = attribute.get("CO2").getAsJsonObject();
            JsonObject humidity_att = attribute.get("humidity").getAsJsonObject();
            JsonObject PM10_att = attribute.get("PM10").getAsJsonObject();
            JsonObject PM25_att = attribute.get("PM25").getAsJsonObject();
            int AQI = AQI_att.get("value").getAsInt();
            int AQI_Predict = AQI_Predict_att.get("value").getAsInt();
            int CO2 = CO2_att.get("value").getAsInt();
            float humidity = humidity_att.get("value").getAsFloat();
            int PM10 = PM10_att.get("value").getAsInt();
            int PM25 = PM25_att.get("value").getAsInt();
            TextView txt_AQI = (TextView) v.findViewById(R.id.AQI);
            TextView txt_AQI_Predict = (TextView) v.findViewById(R.id.AQI_pre);
            TextView txt_CO2 = (TextView) v.findViewById(R.id.CO2);
            TextView txt_humidity = (TextView) v.findViewById(R.id.Humidity_WAsset);
            TextView txt_PM10 = (TextView) v.findViewById(R.id.PM10);
            TextView txt_PM25 = (TextView) v.findViewById(R.id.PM25);
            txt_AQI.setText(valueOf(AQI));
            txt_AQI_Predict.setText(valueOf(AQI_Predict));
            txt_CO2.setText(valueOf(CO2));
            txt_humidity.setText(valueOf(humidity));
            txt_PM10.setText(valueOf(PM10));
            txt_PM25.setText(valueOf(PM25));
            Log.d("Weather Asset", "AQI: " + AQI);
            Log.d("Weather Asset", "AQI_Predict: " + AQI_Predict);
            Log.d("Weather Asset", "CO2: " + CO2);
            Log.d("Weather Asset", "humidity: " + humidity + " %");
            Log.d("Weather Asset", "PM10: " + PM10);
            Log.d("Weather Asset", "PM25: " + PM25);
        }
        else if (object.get("name").getAsString().equals("DHT11 Asset")) //DHT11 Asset
        {
            JsonObject temperature_att = attribute.get("temperature").getAsJsonObject();
            JsonObject humidity_att = attribute.get("humidity").getAsJsonObject();
            float temperature = temperature_att.get("value").getAsFloat();
            int humidity = humidity_att.get("value").getAsInt();
            TextView txt_temperature = (TextView) v.findViewById(R.id.Temperature_DHT11);
            TextView txt_humidity = (TextView) v.findViewById(R.id.Humidity_DHT11);
            txt_temperature.setText(valueOf(temperature));
            txt_humidity.setText(valueOf(humidity));
            Log.d("DHT11 Asset", "temperature: " + temperature + " °C");
            Log.d("DHT11 Asset", "humidity: " + humidity + " %");
        }
    }
}