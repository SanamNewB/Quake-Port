// To Parse JSON data from USGS in the App
// Helper methods related to requesting and receiving earthquake data from USGS.
// SAMPLE_JSON_RESPONSE is a constant value.

package com.sanamshikalgar.quakeport;
import android.text.TextUtils;
import android.util.Log;

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
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

public final class JSONdata {

    private static final String LOG_TAG = JSONdata.class.getSimpleName(); // Tag for the log messages

    private JSONdata() {
    } // Create a private constructor because no one should ever create a JSONdata object

    public static List<EachQuakeInfo> extractEarthquakes(String requestUrl) { // Return a list of DT EachQuakeInfo objects that has been built up from parsing a JSON response.

        URL url = createUrl(requestUrl); // Create URL object

        String jsonResponse = null; // Perform HTTP request to the URL and receive a JSON response back
        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error closing input stream", e);
        }

        List<EachQuakeInfo> eachQuake = extractFeatureFromJson(jsonResponse); // Extract relevant fields from the JSON response and create an {@link Event} object

        return eachQuake; // Return the list of {@link eachQuake}s
    }

    //Returns new URL object from the given string URL.
    private static URL createUrl(String stringUrl) {
        URL url = null;
        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Error with creating URL ", e);
        }
        return url;
    }

    //Make an HTTP request to the given URL and return a String as the response.
    private static String makeHttpRequest(URL url) throws IOException {
        String jsonResponse = "";

        // If the URL is null, then return early.
        if (url == null) {
            return jsonResponse;
        }


        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(10000 /* milliseconds */);
            urlConnection.setConnectTimeout(15000 /* milliseconds */);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // If the request was successful (response code 200),
            // then read the input stream and parse the response.
            if (urlConnection.getResponseCode() == 200) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream); // define readFromStream()
            } else {
                Log.e(LOG_TAG, "Error response code: " + urlConnection.getResponseCode());
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem retrieving the earthquake JSON results.", e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                inputStream.close();
            }
        }
        return jsonResponse;
    }

    // readFromStream(): Convert the InputStream to a String which contains whole JSON response from the server
    private static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while (line != null) {
                output.append(line);
                line = reader.readLine();
            }
        }
        return output.toString();
    }

    private static List<EachQuakeInfo> extractFeatureFromJson(String earthquakeJSON) {

        if (TextUtils.isEmpty(earthquakeJSON)) { // If the JSON string is empty or null, then return early.
            return null;
        }
        List<EachQuakeInfo> eachQuake = new ArrayList<>();
        // Try to parse the JSON response string. If there's a problem with the way the JSON
        // is formatted, a JSONException exception object will be thrown.
        // Catch the exception so the app doesn't crash, and print the error message to the logs.
        try {

            JSONObject baseJsonResponse = new JSONObject(earthquakeJSON); // Create a JSONObject from the JSON response string

            JSONArray featuresArray= baseJsonResponse.getJSONArray("features"); //Extract the JSONArray associated with the key called "features" which represents a list of features (or earthquakes).

            for (int i = 0; i < featuresArray.length(); i++) { // For each earthquake in the earthquakeArray, create an {@link Earthquake} object

                JSONObject currentQuake = featuresArray.getJSONObject(i);// Get a single earthquake at position i within the list of earthquakes
                JSONObject properties = currentQuake.getJSONObject("properties"); // For a given earthquake, extract the JSONObject associated with the key called "properties", which represents a list of all properties for that earthquake.
                double magnitude = properties.getDouble("mag"); // Extract the value for the key called "mag"
                String city = properties.getString("place"); // Extract the value for the key called "place"
                long time = properties.getLong("time"); // Extract the value for the key called "time"
                String url = properties.getString("url"); // Extract the value for the key called "url"
                EachQuakeInfo newQuakeInfo = new EachQuakeInfo(magnitude,city,time,url);
                eachQuake.add(newQuakeInfo);
            }

        } catch (JSONException e) {
            // If an error is thrown when executing any of the above statements in the "try" block,
            // catch the exception here, so the app doesn't crash. Print a log message
            // with the message from the exception.
            Log.e("mydata", "Problem parsing the earthquake JSON results", e);
        }
        return eachQuake; // Return the list of earthquakes
    }
}
