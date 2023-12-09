package com.example.loginapptest;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;

import com.example.loginapptest.ValueEntry;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class Chart extends AppCompatActivity {

    ArrayList<ValueEntry> entryArrayList = new ArrayList<>();

    ArrayList<String> IdList = new ArrayList<>();

    String token = "";
    String assetName;
    String attributeName;
    String startTime;
    String endTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chart);
        token = getIntent().getStringExtra("token");
        assetName = getIntent().getStringExtra("AssetName");
        attributeName = getIntent().getStringExtra("AttributeName");
        startTime = getIntent().getStringExtra("start");
        endTime = getIntent().getStringExtra("end");
        IdList.add("3xih2945Hes4yxpB15ZyKb"); //Default Weather
        IdList.add("5zI6XqkQVSfdgOrZ1MyWEf"); //DHT11 Asset
        IdList.add("4lt7fyHy3SZMgUsECxiOgQ"); //Weather Asset
        String assetId = ToAssetId(assetName);

        Call_API_Asset_Datapoint(assetId, attributeName, startTime, endTime);
    }

    private String ToAssetId(String assetName) {
        String assetId = "";
        switch (assetName) {
            case "Default Weather":
                assetId = IdList.get(0);
                break;
            case "DHT11 Asset":
                assetId = IdList.get(1);
                break;
            case "Weather Asset":
                assetId = IdList.get(2);
                break;
        }
        return assetId;
    }

    private void CreateChart()
    {
        LineChart chart;
        chart = (LineChart) findViewById(R.id.Line);
        chart.setDragEnabled(true);
        chart.setScaleEnabled(true);
        chart.setPinchZoom(true);
        chart.getDescription().setEnabled(false); // bỏ chữ mô tả
        chart.getAxisRight().setEnabled(false); // bỏ trục tung bên phải
        chart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM); //đặt trục hoành ở dưới
        //Tạo các chấm trên chart
        ArrayList<Entry> Values = new ArrayList<>();
        for (int i = 0; i < entryArrayList.size(); i++) {
            long timestamp = entryArrayList.get(i).x;
            String x = ConverteMiliSecondstoTime(timestamp);
            float y = entryArrayList.get(i).y_float;
            Log.d("Entry", x + " " + y);
            Values.add(new Entry(i, y)); // trục hoành, trục tung
        }
        LineDataSet set1 = new LineDataSet(Values, "Data Set 1");
        set1.setLineWidth(5f);
        set1.setColor(0xFFFF0000);
        set1.setFillAlpha(110);
        ArrayList<ILineDataSet> dataSets = new ArrayList<>();
        dataSets.add(set1);
        LineData data = new LineData(dataSets);
        chart.setData(data);
    }


    @SuppressLint("StaticFieldLeak")
    private void Call_API_Asset_Datapoint(String assetId, String attributeName, String startTime, String endTime) {
        // Create a new AsyncTask to perform the network operation
        new AsyncTask<Void, Void, Boolean>() {
            @Override
            protected Boolean doInBackground(Void... voids) {
                // Perform the network operation here
                OkHttpClient client = new OkHttpClient().newBuilder()
                        .build();
                MediaType mediaType = MediaType.parse("application/json");
                RequestBody body = RequestBody.create(mediaType, "{\"fromTimestamp\": 0,\"toTimestamp\": 0,\"fromTime\": \"" + startTime + "\",\"toTime\": \"" + endTime + "\",\"type\": \"string\"}");
                Request request = new Request.Builder()
                        .url("https://uiot.ixxc.dev/api/master/asset/datapoint/" + assetId + "/attribute/" + attributeName)
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
                        ValueEntry valueEntry = new ValueEntry(jsonObject.get("x").getAsLong(), jsonObject.get("y").getAsFloat());
                        entryArrayList.add(valueEntry);
                        Log.d("Entry", i + " " + valueEntry.x + " " + valueEntry.y_float);
                    }
                    CreateChart();

                } catch (IOException e) {
                    e.printStackTrace();
                    return false;
                }
                // Return the result of the network operation
                return true;
            }
        }.execute();
    }

    //Chuyển đổi timestamp miligiây sang giờ theo UTC+7
    private String ConverteMiliSecondstoTime(long miliseconds)
    {
        // Tạo đối tượng Timestamp từ giây và đặt múi giờ là UTC+7
        String timestamp = Instant.ofEpochMilli(miliseconds).plusSeconds(TimeUnit.HOURS.toHours(7)).toString();
        return timestamp;
    }
}