package com.example.weatherapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class CurrentWeather extends AppCompatActivity {

    private String LATITUDE,LONGITUDE,API_KEY,JSON_RESPONSE;
    private String BASE_URL = "http://api.openweathermap.org/data/2.5/weather?";
    TextView coordinates;
    Bundle extras;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_current_weather);
        extras = getIntent().getExtras();
        LATITUDE = extras.getString("LAT");
        LONGITUDE = extras.getString("LONG");
        API_KEY = extras.getString("API_KEY");
        coordinates = findViewById(R.id.Coordinates);
        BASE_URL = BASE_URL + "lat=" + LATITUDE + "&lon=" + LONGITUDE + "&appid=" + API_KEY;

        CurrentWeatherAsyncTask currentWeatherAsyncTask = new CurrentWeatherAsyncTask();
        currentWeatherAsyncTask.execute(BASE_URL);
    }

    public class CurrentWeatherAsyncTask extends AsyncTask<String,Void,Weather>{
        URL url;
        String BASE_URL;

        @Override
        protected Weather doInBackground(String... strings){
            BASE_URL = strings[0];
            Log.d("UGUR",BASE_URL);
            url = createUrl(BASE_URL);
            try{
                JSON_RESPONSE = makeHttpRequest(url);
                Log.d("UGUR",JSON_RESPONSE);
            }catch (IOException e){
                e.printStackTrace();
            }
            return extractWeatherInfo(JSON_RESPONSE);
        }

        @Override
        protected void onPostExecute(Weather weather){
            updateUI(weather);
        }
    }

    public URL createUrl(String URLString){
        URL url;
        try{
            url = new URL(URLString);
        }catch (MalformedURLException e){
            e.printStackTrace();
            return null;
        }
        return url;
    }

    public String makeHttpRequest(URL url) throws IOException{
        String jsonResponse = "";
        HttpURLConnection httpURLConnection = null;
        InputStream inputStream = null;
        Log.d("UGUR",jsonResponse);
        try {
            httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setReadTimeout(10000);
            httpURLConnection.setConnectTimeout(15000);
            httpURLConnection.setRequestMethod("GET");
            httpURLConnection.connect();
            // TODO check if connection is successful
            inputStream = httpURLConnection.getInputStream();
            jsonResponse = convertStreamToString(inputStream);
        }catch (IOException e){
            e.printStackTrace();
        }finally {
            if(httpURLConnection!=null){
                httpURLConnection.disconnect();
            }
            if (inputStream!=null){
                inputStream.close();
            }
        }
        return jsonResponse;
    }

    public String convertStreamToString(InputStream inputStream) throws IOException{
        StringBuilder output = new StringBuilder();
        if(inputStream!=null){
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream,"UTF-8");
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while (line!=null){
                output.append(line);
                line = reader.readLine();
            }
        }
        return output.toString();
    }

    public Weather extractWeatherInfo(String JSON_RESPONSE){
        Weather weather = new Weather();
        try {
            JSONObject root = new JSONObject(JSON_RESPONSE);
            JSONArray weatherArray = root.optJSONArray("weather");
            JSONObject weatherObject = weatherArray.optJSONObject(0);
            weather.setSituation(weatherObject.optString("main"));
            weather.setDescription(weatherObject.optString("description"));
            weather.setIconId(weatherObject.optString("icon"));
            weather.setLocationName(root.optString("name"));
            weather.setResponseCode(root.optString("cod"));
            weather.setTemperature(weatherObject.optString("temp"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return weather;
    }

    public void updateUI(Weather weather){
        coordinates.setText(weather.getLocationName()+" / " +weather.getSituation());
    }

}
