package macbeth;

import com.google.gson.Gson;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

// https://earthquake.usgs.gov/earthquakes/feed/v1.0/geojson.php

/**
 * Create an EarthquakeList object based on JSON data from the USGS earthquake website.
 */
public class EarthquakeLoader {

    EarthquakeList earthquakeList;

    /**
     * Create EarthquakeList objects from the JSON data
     */
    public void loadEarthquakes() {
        try {
            // Create a stream to the URL
            // Here is where I found the JSON streams: https://earthquake.usgs.gov/earthquakes/feed/v1.0/geojson.php
            URL url = new URL("https://earthquake.usgs.gov/earthquakes/feed/v1.0/summary/all_day.geojson");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));

            // Read all data from the website into a single string
            String line = "";
            String allLines = "";
            do {
                line = reader.readLine();
                if (line != null) {
                    allLines += line;
                }
            }
            while (line != null);

            // Create the EarthquakeList object from the JSON data
            Gson gson = new Gson();
            earthquakeList = gson.fromJson(allLines, EarthquakeList.class);
        }
        catch (MalformedURLException murle) {
            System.out.println(murle.getMessage());
        }
        catch (IOException ioe) {
            System.out.println(ioe.getMessage());
        }
    }

    /**
     * Return the earthquakeList
     *
     * @return EarthquakeList
     */
    public EarthquakeList getEarthquakeList() {
        return earthquakeList;
    }
}
