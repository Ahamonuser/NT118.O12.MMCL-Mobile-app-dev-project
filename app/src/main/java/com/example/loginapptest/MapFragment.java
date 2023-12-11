package com.example.loginapptest;

import static java.lang.String.valueOf;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;

import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

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
    float windSpeed;
    float windDirection;
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
            Dialog dialog = new Dialog(getContext());
            dialog.requestWindowFeature(dialog.getWindow().FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.custom_dialog_box_default);
            Window window = dialog.getWindow();
            if (window == null) {
                return false;
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
            txt_temperature.setText(valueOf(temperature));
            txt_humidity.setText(valueOf(humidity));
            txt_place.setText(place);
            txt_windSpeed.setText(valueOf(windSpeed));
            txt_windDirection.setText(valueOf(windDirection));
            dialog.show();
            return true;
        });
        marker2.setOnMarkerClickListener((marker, mapView) -> {
            Dialog dialog = new Dialog(getContext());
            dialog.requestWindowFeature(dialog.getWindow().FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.custom_dialog_box_light);
            Window window = dialog.getWindow();
            if (window == null) {
                return false;
            }
            window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            window.setBackgroundDrawableResource(android.R.color.transparent);
            WindowManager.LayoutParams layoutParams = window.getAttributes();
            layoutParams.gravity = Gravity.CENTER;
            window.setAttributes(layoutParams);
            dialog.setCancelable(true);
            TextView txt_brightness = (TextView) dialog.findViewById(R.id.Brightness);
            TextView txt_colourTemperature = (TextView) dialog.findViewById(R.id.Colour_Temperature);
            TextView txt_email = (TextView) dialog.findViewById(R.id.Email_light);
            TextView txt_onOff = (TextView) dialog.findViewById(R.id.On_Off);
            txt_brightness.setText(valueOf(Brightness));
            txt_colourTemperature.setText(valueOf(ColourTemperature));
            txt_email.setText(Email);
            boolean lightstatus = OnOff;
            ImageView img = (ImageView) dialog.findViewById(R.id.On_Off_image);
            if (lightstatus == true)
            {
                txt_onOff.setText("ON");
                img.setImageResource(R.drawable.baseline_light_mode_65);
            }
            else
            {
                txt_onOff.setText("OFF");
                img.setImageResource(R.drawable.baseline_dark_mode_65);
            }

            dialog.show();
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
        JsonObject windSpeed_att = attribute.get("windSpeed").getAsJsonObject();
        JsonObject windDirection_att = attribute.get("windDirection").getAsJsonObject();
        rainfall = rainfall_att.get("value").getAsFloat();
        temperature = temperature_att.get("value").getAsFloat();
        humidity = humidity_att.get("value").getAsInt();
        place = place_att.get("value").getAsString();
        windSpeed = windSpeed_att.get("value").getAsFloat();
        windDirection = windDirection_att.get("value").getAsFloat();
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