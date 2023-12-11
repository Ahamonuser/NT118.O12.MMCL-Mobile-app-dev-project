package com.example.loginapptest;

import static java.lang.String.valueOf;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

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

    float rainfall;
    float temperature_default;
    int humidity_default;
    String place;
    float windSpeed;
    float windDirection;
    int AQI;
    int AQI_Predict;
    int CO2;
    float humidity_Asset;
    int PM10;
    int PM25;
    float temperature_DHT11;
    int humidity_DHT11;

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
        TextView MoreAsset = (TextView) v.findViewById(R.id.MoreAsset);
        TextView MoreDefault = (TextView) v.findViewById(R.id.MoreDefault);
        ImageView logout = (ImageView) v.findViewById(R.id.Logout_dash);
        // Lấy token từ bundle
        token = getArguments().getString("token");
        Log.d("Dashboard", token);
        Get_Id_API_Weather();
        Call_API_Weather();
        MoreAsset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dialog dialog = new Dialog(getContext());
                dialog.requestWindowFeature(dialog.getWindow().FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.custom_dialog_box_asset);
                Window window = dialog.getWindow();
                if (window == null) {
                    return;
                }
                window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                window.setBackgroundDrawableResource(android.R.color.transparent);
                WindowManager.LayoutParams layoutParams = window.getAttributes();
                layoutParams.gravity = Gravity.CENTER;
                window.setAttributes(layoutParams);
                dialog.setCancelable(true);
                TextView txt_CO2 = (TextView) dialog.findViewById(R.id.CO2_Asset);
                TextView txt_AQI = (TextView) dialog.findViewById(R.id.AQI_Asset);
                TextView txt_PM10 = (TextView) dialog.findViewById(R.id.PM10_Asset);
                TextView txt_PM25 = (TextView) dialog.findViewById(R.id.PM25_Asset);
                TextView txt_humidity = (TextView) dialog.findViewById(R.id.Humidity_Asset);
                TextView txt_AQI_Predict = (TextView) dialog.findViewById(R.id.AQI_pre_Asset);
                txt_CO2.setText(valueOf(CO2));
                txt_AQI.setText(valueOf(AQI));
                txt_PM10.setText(valueOf(PM10));
                txt_PM25.setText(valueOf(PM25));
                txt_humidity.setText(valueOf(humidity_Asset));
                txt_AQI_Predict.setText(valueOf(AQI_Predict));
                dialog.show();
            }
        });
        MoreDefault.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dialog dialog = new Dialog(getContext());
                dialog.requestWindowFeature(dialog.getWindow().FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.custom_dialog_box_default);
                Window window = dialog.getWindow();
                if (window == null) {
                    return;
                }
                window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                window.setBackgroundDrawableResource(android.R.color.transparent);
                WindowManager.LayoutParams layoutParams = window.getAttributes();
                layoutParams.gravity = Gravity.CENTER;
                window.setAttributes(layoutParams);
                dialog.setCancelable(true);
                TextView txt_rainfall = (TextView) dialog.findViewById(R.id.Rainfall_Default);
                TextView txt_temperature = (TextView) dialog.findViewById(R.id.Temperature_Default);
                TextView txt_humidity = (TextView) dialog.findViewById(R.id.Humidity_Default);
                TextView txt_place = (TextView) dialog.findViewById(R.id.Place_Default);
                TextView txt_windSpeed = (TextView) dialog.findViewById(R.id.Wind_speed_Default);
                TextView txt_windDirection = (TextView) dialog.findViewById(R.id.Wind_dir_Default);
                txt_rainfall.setText(valueOf(rainfall));
                txt_temperature.setText(valueOf(temperature_default));
                txt_humidity.setText(valueOf(humidity_default));
                txt_place.setText(place);
                txt_windSpeed.setText(valueOf(windSpeed));
                txt_windDirection.setText(valueOf(windDirection));
                dialog.show();
            }
        });
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().finish();
            }
        });
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
            rainfall = rainfall_att.get("value").getAsFloat();
            temperature_default = temperature_att.get("value").getAsFloat();
            humidity_default = humidity_att.get("value").getAsInt();
            place = place_att.get("value").getAsString();
            windSpeed = windSpeed_att.get("value").getAsFloat();
            windDirection = windDirection_att.get("value").getAsFloat();
            TextView txt_place = (TextView) v.findViewById(R.id.Place_dash);
            TextView txt_windDirection = (TextView) v.findViewById(R.id.Wind_dir_dash);
            txt_place.setText(place);
            txt_windDirection.setText(valueOf(windDirection));
        }
        else if (object.get("name").getAsString().equals("Weather Asset"))
        {
            JsonObject AQI_att = attribute.get("AQI").getAsJsonObject();
            JsonObject AQI_Predict_att = attribute.get("AQI_Predict").getAsJsonObject();
            JsonObject CO2_att = attribute.get("CO2").getAsJsonObject();
            JsonObject humidity_att = attribute.get("humidity").getAsJsonObject();
            JsonObject PM10_att = attribute.get("PM10").getAsJsonObject();
            JsonObject PM25_att = attribute.get("PM25").getAsJsonObject();
            AQI = AQI_att.get("value").getAsInt();
            AQI_Predict = AQI_Predict_att.get("value").getAsInt();
            CO2 = CO2_att.get("value").getAsInt();
            humidity_Asset = humidity_att.get("value").getAsFloat();
            PM10 = PM10_att.get("value").getAsInt();
            PM25 = PM25_att.get("value").getAsInt();
            TextView txt_AQI = (TextView) v.findViewById(R.id.AQI_dash);
            TextView txt_CO2 = (TextView) v.findViewById(R.id.CO2_dash);
            txt_AQI.setText(valueOf(AQI));
            txt_CO2.setText(valueOf(CO2));
        }
        else if (object.get("name").getAsString().equals("DHT11 Asset")) //DHT11 Asset
        {
            JsonObject temperature_att = attribute.get("temperature").getAsJsonObject();
            JsonObject humidity_att = attribute.get("humidity").getAsJsonObject();
            temperature_DHT11 = temperature_att.get("value").getAsFloat();
            humidity_DHT11 = humidity_att.get("value").getAsInt();
            TextView txt_temperature = (TextView) v.findViewById(R.id.Temperature_DHT11_dash);
            TextView txt_humidity = (TextView) v.findViewById(R.id.Humidity_DHT11_dash);
            txt_temperature.setText(valueOf(temperature_DHT11));
            txt_humidity.setText(valueOf(humidity_DHT11));
        }
    }
}