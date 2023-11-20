package com.example.imoto;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.imoto.Common.Common;
import com.example.imoto.Helper.Helper;
import com.example.imoto.Model.OpenWeatherMap;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.squareup.picasso.Picasso;

import java.lang.reflect.Type;


public class WeatherActivity extends AppCompatActivity implements LocationListener {

    TextView txt_city, txt_last_update, txt_description, txt_humidity, txt_time, txt_celsius;

    ImageView imageView;

    LocationManager locationManager;
    String provider;
    static double lat, lng;
    OpenWeatherMap openWeatherMap = new OpenWeatherMap();

    int MY_PERMISSION = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);

        //Get Coordinates
        txt_city = findViewById(R.id.txt_city);
        txt_last_update = findViewById(R.id.txt_last_update);
        txt_description = findViewById(R.id.txt_description);
        txt_humidity = findViewById(R.id.txt_humidity);
        txt_time = findViewById(R.id.txt_time);
        txt_celsius = findViewById(R.id.txt_celsius);
        imageView = findViewById(R.id.image_view);

        //Get Coordinate
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        provider = locationManager.getBestProvider(new Criteria(), false);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(WeatherActivity.this, new String[]{
                    Manifest.permission.INTERNET,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_NETWORK_STATE,
                    Manifest.permission.SYSTEM_ALERT_WINDOW,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE

            }, MY_PERMISSION);
        }
        Location location = locationManager.getLastKnownLocation(provider);
        if (location == null)
            Log.e("Tag", "No Location");
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(WeatherActivity.this, new String[]{
                    Manifest.permission.INTERNET,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_NETWORK_STATE,
                    Manifest.permission.SYSTEM_ALERT_WINDOW,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE

            }, MY_PERMISSION);
        }

        locationManager.removeUpdates(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(WeatherActivity.this, new String[]{
                    Manifest.permission.INTERNET,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_NETWORK_STATE,
                    Manifest.permission.SYSTEM_ALERT_WINDOW,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE

            }, MY_PERMISSION);
        }
        locationManager.requestLocationUpdates(provider, 400, 1, this);
    }

    @Override
    public void onLocationChanged(Location location) {
        lat = location.getLatitude();
        lng = location.getLongitude();

        new GetWeather().execute(Common.apiRequest(String.valueOf(lat),String.valueOf(lng)));


    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {


    }

    private class GetWeather extends AsyncTask<String,Void,String>{

        ProgressDialog pd = new ProgressDialog(WeatherActivity.this);

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd.setTitle("Please wait...");
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (s.contains("Error: City Not Found ")){
                pd.dismiss();
                return;
            }
            Gson gson = new Gson();
            Type mType = new TypeToken<OpenWeatherMap>(){}.getType();
            openWeatherMap = gson.fromJson(s,mType);
            pd.dismiss();

            txt_city.setText(String.format("%s,%s",openWeatherMap.getName(),openWeatherMap.getSys().getCountry()));
            txt_last_update.setText(String.format("Last Updated: %s", Common.getDateNow()));
            txt_description.setText(String.format("%s",openWeatherMap.getWeather().get(0).getDescription()));
            txt_humidity.setText(String.format("%d%%",openWeatherMap.getMain().getHumidity()));
            txt_time.setText(String.format("%s/%s",Common.unixTimeStampToDateTime(openWeatherMap.getSys().getSunrise()), Common.unixTimeStampToDateTime(openWeatherMap.getSys().getSunset())));
            txt_celsius.setText(String.format("%.2f °C",openWeatherMap.getMain().getTemp()));
            Picasso.with(WeatherActivity.this)
                    .load(Common.getImage(openWeatherMap.getWeather().get(0).getIcon()))
                    .into(imageView);


        }

        @Override
        protected String doInBackground(String... params) {
            String stream = null;
            String urlString = params[0];

            Helper http = new Helper();
            stream = http.getHTTPData(urlString);
            return stream;


        }
    }
}
