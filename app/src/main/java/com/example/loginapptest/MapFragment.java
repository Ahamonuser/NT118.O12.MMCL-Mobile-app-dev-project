package com.example.loginapptest;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import org.osmdroid.api.IMapController;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.CustomZoomButtonsController;
import org.osmdroid.views.MapView;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.infowindow.MarkerInfoWindow;

import java.io.IOException;
import java.sql.Timestamp;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MapFragment extends Fragment {


    MapView mapView;
    Marker marker;
    Marker marker2;
    String token = "";
    float rainfall;
    float temperature;
    int humidity;
    String place;
    int Brightness;
    int ColourTemperature;
    String Email;
    boolean OnOff;
    public MapFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    JsonObject jsonObjectWeather = new JsonObject();
    JsonObject jsonObjectLight = new JsonObject();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_map, container, false);
        // Lấy token từ bundle
        Bundle data = getArguments();
        token = data.getString("token");
        mapView = v.findViewById(R.id.mapView);
        marker = new Marker(mapView);
        marker2 = new Marker(mapView);
        Context ctx = requireActivity().getApplicationContext();
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx));
        mapView.setTileSource(TileSourceFactory.MAPNIK);
        mapView.setMultiTouchControls(true);
        mapView.getZoomController().setVisibility(CustomZoomButtonsController.Visibility.NEVER);
        Call_API_Default_Weather();
        Call_API_Light();
        marker.setOnMarkerClickListener((marker, mapView) -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setMessage("Place: " + place + "\n" + "Rainfall: " + rainfall + "mm\n" + "Temperature: " + temperature + "°C\n" + "Humidity: " + humidity + "%");
            builder.setTitle("Default Weather");
            builder.setCancelable(false);
            builder.setNeutralButton("OK", (DialogInterface.OnClickListener) (dialog, which) -> {
                dialog.cancel();
            });
            AlertDialog alertDialog = builder.create();
            alertDialog.show();
            return true;
        });
        marker2.setOnMarkerClickListener((marker, mapView) -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setMessage("Email: " + Email + "\n" + "Brightness: " + Brightness + "%\n" + "Colour Temperature(K): " + ColourTemperature + "\n" + "On/Off: " + OnOff);
            builder.setTitle("Light");
            builder.setCancelable(false);
            builder.setNeutralButton("OK", (DialogInterface.OnClickListener) (dialog, which) -> {
                dialog.cancel();
            });
            AlertDialog alertDialog = builder.create();
            alertDialog.show();
            return true;
        });
        return v;
    }

    @SuppressLint("StaticFieldLeak")
    private void Call_API_Default_Weather() {
        // Create a new AsyncTask to perform the network operation
        new AsyncTask<Void, Void, Boolean>() {
            @Override
            protected Boolean doInBackground(Void... voids) {
                // Perform the network operation here
                OkHttpClient client = new OkHttpClient().newBuilder()
                        .build();
                Request request = new Request.Builder()
                        .url("https://uiot.ixxc.dev/api/master/asset/5zI6XqkQVSfdgOrZ1MyWEf")
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
                    jsonObjectWeather = gson.fromJson(json, JsonObject.class);
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
                    JsonObject attribute = jsonObjectWeather.get("attributes").getAsJsonObject();
                    JsonObject location = attribute.get("location").getAsJsonObject();
                    JsonObject value = location.get("value").getAsJsonObject();
                    JsonArray coordinates = value.get("coordinates").getAsJsonArray();
                    double lon = coordinates.get(0).getAsDouble();
                    double lat = coordinates.get(1).getAsDouble();
                    Log.d("Weather", lon + " " + lat);
                    AddMarker(lat, lon);
                    Get_Default_Weather_Value(jsonObjectWeather);
                }
            }
        }.execute();
    }

    @SuppressLint("StaticFieldLeak")
    private void Call_API_Light() {
        // Create a new AsyncTask to perform the network operation
        new AsyncTask<Void, Void, Boolean>() {
            @Override
            protected Boolean doInBackground(Void... voids) {
                // Perform the network operation here
                OkHttpClient client = new OkHttpClient().newBuilder()
                        .build();
                Request request = new Request.Builder()
                        .url("https://uiot.ixxc.dev/api/master/asset/6iWtSbgqMQsVq8RPkJJ9vo")
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
                    jsonObjectLight = gson.fromJson(json, JsonObject.class);
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
                    JsonObject attribute = jsonObjectLight.get("attributes").getAsJsonObject();
                    JsonObject location = attribute.get("location").getAsJsonObject();
                    JsonObject value = location.get("value").getAsJsonObject();
                    JsonArray coordinates = value.get("coordinates").getAsJsonArray();
                    double lon = coordinates.get(0).getAsDouble();
                    double lat = coordinates.get(1).getAsDouble();
                    Log.d("Light", lon + " " + lat);
                    AddMarker2(lat, lon);
                    Get_Light_Value(jsonObjectLight);
                }
            }
        }.execute();
    }

    public void AddMarker(double lat, double lon)
    {
        IMapController iMapController = mapView.getController();
        iMapController.setZoom(18.0);
        GeoPoint geoPoint1 = new GeoPoint(lat, lon);
        iMapController.setCenter(geoPoint1);
        marker.setPosition(geoPoint1);
        mapView.getOverlays().add(marker);
    }

    public void AddMarker2(double lat, double lon)
    {
        IMapController iMapController = mapView.getController();
        iMapController.setZoom(18.0);
        GeoPoint geoPoint = new GeoPoint(lat, lon);
        iMapController.setCenter(geoPoint);
        marker2.setPosition(geoPoint);
        marker2.setDefaultIcon();
        mapView.getOverlays().add(marker2);
    }

    private void Get_Default_Weather_Value(JsonObject object)
    {
        JsonObject attribute = object.get("attributes").getAsJsonObject();
        JsonObject rainfall_att = attribute.get("rainfall").getAsJsonObject();
        JsonObject temperature_att = attribute.get("temperature").getAsJsonObject();
        JsonObject humidity_att = attribute.get("humidity").getAsJsonObject();
        JsonObject place_att = attribute.get("place").getAsJsonObject();
        rainfall = rainfall_att.get("value").getAsFloat();
        temperature = temperature_att.get("value").getAsFloat();
        humidity = humidity_att.get("value").getAsInt();
        place = place_att.get("value").getAsString();
    }

    private void Get_Light_Value(JsonObject object)
    {
        JsonObject attribute = object.get("attributes").getAsJsonObject();
        JsonObject brightness_att = attribute.get("brightness").getAsJsonObject();
        JsonObject colourTemperature_att = attribute.get("colourTemperature").getAsJsonObject();
        JsonObject email_att = attribute.get("email").getAsJsonObject();
        JsonObject onOff_att = attribute.get("onOff").getAsJsonObject();
        Brightness = brightness_att.get("value").getAsInt();
        ColourTemperature = colourTemperature_att.get("value").getAsInt();
        Email = email_att.get("value").getAsString();
        OnOff = onOff_att.get("value").getAsBoolean();
    }
}