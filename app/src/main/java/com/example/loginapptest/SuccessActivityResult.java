package com.example.loginapptest;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.view.MenuItem;
import android.widget.TextView;


import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.formatter.IFillFormatter;
import com.github.mikephil.charting.interfaces.dataprovider.LineDataProvider;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class SuccessActivityResult extends AppCompatActivity {

    BottomNavigationView navigation;

    private LineChart chart;
    String Root_Frag = "root_fagment";
    ArrayList<String> IDArrayList = new ArrayList<>();
    String token = "";

    MapFragment mapFragment = new MapFragment();
    GraphFragment graphFragment = new GraphFragment();
    DashboardFragment dashboardFragment = new DashboardFragment();
    UserFragment userFragment = new UserFragment();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_success_result);
        token = getIntent().getStringExtra("token");
        //Tạo thread để lấy dữ liệu từ API khi không mở app
        Thread serviceThread = new Thread(new MyService());
        // Bắt đầu chạy thread
        //serviceThread.start();

        // Tạo thread để luôn mở database để sử dụng app inspector
        // -> chỉ để bật chứng minh có lưu database -> không cần thiết
        // Nhưng tốt nhất đừng xóa kẻo lại ko chứng minh lưu local đc thì toang
        Thread ReadThread = new Thread(new ReadService());
        //ReadThread.start();

        navigation = (BottomNavigationView) findViewById(R.id.NavigationView);
        navigation.setSelectedItemId(R.id.map);
        navigation.setOnItemSelectedListener(this::onNavigationItemSelected);
    }


    public boolean onNavigationItemSelected(@Nullable MenuItem item) {
        boolean result = false;

        Log.d("Navi", R.id.map + "");
        Log.d("Navi", R.id.dashboard + "");
        Log.d("Navi", R.id.graph + "");
        Log.d("Navi", R.id.user + "");
        Log.d("Navi", item.getItemId() + "");
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        Bundle bundle = new Bundle();
        bundle.putString("token", token);
        if (item.getItemId() == R.id.map) {
            MapFragment map = new MapFragment();
            map.setArguments(bundle);
            fragmentTransaction.replace(R.id.FL, map).commit();
            result = true;
        }
        else if (item.getItemId() == R.id.dashboard) {
            DashboardFragment dashboard = new DashboardFragment();
            dashboard.setArguments(bundle);
            fragmentTransaction.replace(R.id.FL, dashboard).commit();
            result = true;
        }
        else if (item.getItemId() == R.id.graph) {
            GraphFragment graph = new GraphFragment();
            graph.setArguments(bundle);
            fragmentTransaction.replace(R.id.FL, graph).commit();
            result = true;
        }
        else if (item.getItemId() == R.id.user) {
            UserFragment user = new UserFragment();
            user.setArguments(bundle);
            fragmentTransaction.replace(R.id.FL, user).commit();
            result = true;
        }
        return result;
    }

    class ReadService implements Runnable {

        @Override
        public void run() {
            while (true)
            {
                try {
                    DatabaseHelper databaseHelper = new DatabaseHelper(SuccessActivityResult.this);
                    Cursor cursor = databaseHelper.readAllDHT11();
                    Thread.sleep(2000);
                } catch (InterruptedException ex) {}
            }
        }
    }

    //service chạy lấy dữ liệu ngầm
    class MyService implements Runnable {

        @Override
        public void run() {
            // Đây là nơi bạn đặt logic của service
            while (true)
            {
                try {
                    //Thực hiện call api và lưu dữ liệu vào database
                    Get_Id_API_Weather();
                    Call_API_Weather_Service();
                    Log.d("Service called", "delay 1 hour to wait for new data");
                    Thread.sleep(1000 * 60 * 60);
                } catch (InterruptedException ex) {}
            }
        }
    }

    //Lấy asset id của API lấy thông số thời tiết
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
    private void Call_API_Weather_Service() {
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
                        Get_API_Weather_Value_Service(jsonObject);
                    } catch (IOException e) {
                        e.printStackTrace();
                        return false;
                    }
                }
                // Return the result of the network operation
                return true;
            }
        }.execute();
    }

    private void Get_API_Weather_Value_Service(JsonObject object)
    {
        JsonObject attribute = object.get("attributes").getAsJsonObject();
        DatabaseHelper databaseHelper = new DatabaseHelper(this);
        if (object.get("name").getAsString().equals("Default Weather"))
        {
            JsonObject rainfall_att = attribute.get("rainfall").getAsJsonObject();
            JsonObject temperature_att = attribute.get("temperature").getAsJsonObject();
            JsonObject humidity_att = attribute.get("humidity").getAsJsonObject();
            JsonObject windSpeed_att = attribute.get("windSpeed").getAsJsonObject();
            JsonObject windDirection_att = attribute.get("windDirection").getAsJsonObject();
            JsonObject place_att = attribute.get("place").getAsJsonObject();
            float rainfall = rainfall_att.get("value").getAsFloat();
            float temperature = temperature_att.get("value").getAsFloat();
            int humidity = humidity_att.get("value").getAsInt();
            float windSpeed = windSpeed_att.get("value").getAsFloat();
            float windDirection = windDirection_att.get("value").getAsFloat();
            String place = place_att.get("value").getAsString();
            long time = rainfall_att.get("timestamp").getAsLong();
            Timestamp timestamp = ConverteSecondstoTime(time);
            databaseHelper.insertDEFAULT_WEATHER(temperature, humidity, rainfall, windDirection, windSpeed,
                    place, timestamp.toString());
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
            long time = AQI_att.get("timestamp").getAsLong();
            Timestamp timestamp = ConverteSecondstoTime(time);
            databaseHelper.insertWEATHER(AQI, AQI_Predict, CO2, humidity, PM10, PM25, timestamp.toString());
        }
        else //DHT11 Asset
        {
            JsonObject temperature_att = attribute.get("temperature").getAsJsonObject();
            JsonObject humidity_att = attribute.get("humidity").getAsJsonObject();
            float temperature = temperature_att.get("value").getAsFloat();
            int humidity = humidity_att.get("value").getAsInt();
            long time = temperature_att.get("timestamp").getAsLong();
            Timestamp timestamp = ConverteSecondstoTime(time);
            databaseHelper.insertDHT11(temperature, humidity, timestamp.toString());
        }
    }

    //Chuyển đổi timestamp giây sang giờ theo UTC+7
    private Timestamp ConverteSecondstoTime(long seconds)
    {
        // Tạo đối tượng Timestamp từ giây và đặt múi giờ là UTC+7
        Timestamp timestamp = new Timestamp(seconds);
        timestamp.setTime(timestamp.getTime() + TimeZone.getTimeZone("UTC+7").getRawOffset());
        return timestamp;
    }


}