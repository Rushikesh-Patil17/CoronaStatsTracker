package com.example.coronastatstracker;

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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

public final class QueryUtils {

    public static String lastUpdated = null;

    private QueryUtils() {
    }

    private static URL createUrl(String stringUrl) {
        URL url = null;

        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return url;
    }

    private static String makeHttpRequest(URL url) throws IOException {
        String jsonResponse = "";

        if (url == null) {
            return jsonResponse;
        }

        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(10000);
            urlConnection.setConnectTimeout(15000);
            urlConnection.setRequestMethod("GET");
            urlConnection.addRequestProperty("User-Agent", "");
            urlConnection.connect();

            if (urlConnection.getResponseCode() == 200) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            }
        } catch (IOException e) {
            e.printStackTrace();

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

    public static List<Cases> fetchEarthquakeData(String requestUrl) {
        // Create URL object
        URL url = createUrl(requestUrl);
        List<Cases> cases = new ArrayList<>();

        // Perform HTTP request to the URL and receive a JSON response back
        String jsonResponse = null;
        try {
            jsonResponse = makeHttpRequest(url);
            cases = extractCases(jsonResponse);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return cases;
    }

    private static List<Cases> extractCases(String jsonResponse) {
        List<Cases> cases = new ArrayList<>();
        try {
            JSONObject jsonObject = new JSONObject(jsonResponse);
            JSONArray jsonArray = jsonObject.optJSONArray("regionData");

            try {
                String dateStr = jsonObject.optString("lastUpdatedAtApify");

                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
                TimeZone tz = TimeZone.getTimeZone("UTC");
                formatter.setTimeZone(tz);

                Date date = (Date) formatter.parse(dateStr);

                lastUpdated = "Last Updated at " + formatDate(date) + ", " + formatTime(date);

            } catch (ParseException e) {
                e.printStackTrace();
            }


            long confirmed = jsonObject.optLong("totalCases");
            long recovered = jsonObject.optLong("recovered");
            long deceased = jsonObject.optLong("deaths");

            cases.add(new Cases("India", confirmed, recovered, deceased));

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject object = jsonArray.optJSONObject(i);

                String state = object.optString("region");
                long con = object.optLong("totalCases");
                long rec = object.optLong("recovered");
                long dec = object.optLong("deceased");

                if (state.equals("Dadar Nagar Haveli"))
                    state = "Dadra and Nagar Haveli";

                if (state.equals("Diu and Daman"))
                    state = "Daman and Diu";

                else if (state.equals("Daman & Diu"))
                    state = "Daman and Diu";

                else if (state.equals("Diu & Daman"))
                    state = "Daman and Diu";

                else if (state.equals("Diu Daman") || state.equals("Daman Diu"))
                    state = "Daman and Diu";

                cases.add(new Cases(state, con, rec, dec));
            }

        } catch (JSONException ex) {
            ex.printStackTrace();
        }

        return cases;
    }

    private static String formatDate(Date dateObject) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("LLL dd, yyyy");
        return dateFormat.format(dateObject);
    }

    private static String formatTime(Date dateObject) {
        SimpleDateFormat timeFormat = new SimpleDateFormat("h:mm a");
        return timeFormat.format(dateObject);
    }

}