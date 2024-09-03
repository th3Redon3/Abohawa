import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

public class AbohawaApp {
    // Fetch weather data from given location
    public static JSONObject getAbohawaData(String locationName) {
        // Get location coordinates using geolocation API
        JSONArray locationData = getLocationData(locationName);

        // extract latitude and longgitude data
        JSONObject location = (JSONObject) locationData.get(0);
        double latitude = (double) location.get("latitude");
        double longitude = (double) location.get("longitude");

        // build API request URL with location coordinates
        String urlString = "https://api.open-meteo.com/v1/forecast?latitude=" + latitude +
                "&longitude=" + longitude +
                "&hourly=temperature_2m,relative_humidity_2m,weather_code,wind_speed_10m&temperature_unit=fahrenheit&timezone=auto";

        try {
            // call api and get response
            HttpURLConnection conn = fetchApiResponse(urlString);

            // check api response status
            if (conn.getResponseCode() != 200){
                System.out.println("Error: Could not connect to API");
                return null;
            }

            // store resulting json data
            var resultJson = new StringBuilder();
            var scanner = new Scanner(conn.getInputStream());
            while(scanner.hasNext()){
                // read and store into the string builder
                resultJson.append(scanner.nextLine());
            }

            // close scanner
            scanner.close();

            // close url connection
            conn.disconnect();

            // parse through our data
            var parser = new JSONParser();
            JSONObject resultJsonObj = (JSONObject) parser.parse(String.valueOf(resultJson));

            // retrieve hourly data
            JSONObject hourly = (JSONObject) resultJsonObj.get("hourly");
            JSONArray time = (JSONArray) hourly.get("time"); // time of current hour
            int index = findIndexOfCurrentTime(time);

            JSONArray temperatureData = (JSONArray) hourly.get("temperature_2m");
            double temperature = (double) temperatureData.get(index);

            // get weather code
            JSONArray weatherCode = (JSONArray) hourly.get("weather_code");
            String weatherCondition = convertWeatherCode((long) weatherCode.get(index));

            // get humidity
            JSONArray relativeHumidity = (JSONArray) hourly.get("relative_humidity_2m");
            long humidity = (long) relativeHumidity.get(index);

            // get windspeed
            JSONArray windspeedData = (JSONArray) hourly.get("wind_speed_10m");
            double windspeed = (double) windspeedData.get(index);

            // build weather json data object for frontend access
            var weatherData = new JSONObject();
            weatherData.put("temperature", temperature);
            weatherData.put("weather_condition", weatherCondition);
            weatherData.put("humidity", humidity);
            weatherData.put("windspeed", windspeed);

            return weatherData;

        }catch (Exception e){
            e.printStackTrace();
        }

        // Check if location data is null or empty
        if (locationData == null || locationData.isEmpty()) {
            System.out.println("Error: Could not retrieve location data for " + locationName);
            return null;
        }

        // For debugging, print out the location data
        System.out.println("Location data retrieved: " + locationData.toJSONString());

        // Dummy response until weather fetching is implemented
        return new JSONObject(); // Replace with actual weather data fetching logic
    }

    // Retrieves geographic coordinates for a given location name
    private static JSONArray getLocationData(String locationName) {
        // Replace any whitespace in the location name with '+' to adhere to API's request format
        locationName = locationName.replaceAll(" ", "+");

        // Build the API URL with location parameter
        String urlString = "https://geocoding-api.open-meteo.com/v1/search?name="
                + locationName
                + "&count=10&language=en&format=json";

        HttpURLConnection connection = null;
        try {
            // Call API and get a response
            connection = fetchApiResponse(urlString);

            // Check if the connection is null (failure to connect)
            if (connection == null) {
                System.out.println("Error: Connection is null");
                return null;
            }

            // Check response status
            if (connection.getResponseCode() != 200) { // 200 means successful connection
                System.out.println("Error: Could not connect to API, response code: " + connection.getResponseCode());
                return null;
            }

            // Store the API results
            var resultJson = new StringBuilder();
            try (Scanner scanner = new Scanner(connection.getInputStream())) {
                // Read and store the resulting data in our string builder
                while (scanner.hasNext()) {
                    resultJson.append(scanner.nextLine());
                }
            }

            // Parse the JSON string into a JSON object
            JSONParser parser = new JSONParser();
            JSONObject resultsJsonObj = (JSONObject) parser.parse(resultJson.toString());

            // Get the list of location data generated by API from location name
            return (JSONArray) resultsJsonObj.get("results");

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }

        // Could not find location
        return null;
    }

    // Method to fetch the API response
    private static HttpURLConnection fetchApiResponse(String urlString) {
        try {
            // Attempt connection
            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            // Set request method to GET
            connection.setRequestMethod("GET");

            // Set connection and read timeouts
            connection.setConnectTimeout(5000); // 5 seconds
            connection.setReadTimeout(5000);    // 5 seconds

            // Connect to the API
            connection.connect();
            return connection;

        } catch (IOException e) {
            e.printStackTrace();
        }

        // Could not make the connection
        return null;
    }

    public static int findIndexOfCurrentTime(JSONArray timelist){
        String currentTime = getCurrentTime();

        // iteration for matching current time
        for (int i = 0; i < timelist.size(); i++){
            String time = (String) timelist.get(i);
            if (time.equalsIgnoreCase(currentTime)){
                // return the index
                return i;
            }
        }

        return 0;
    }

    public static String getCurrentTime(){
        // get current date and time
        LocalDateTime currentDateTime = LocalDateTime.now();

        //format date
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH':00'");

        // format and print the current date and time
        String formattedDateTime = currentDateTime.format(formatter);

        return formattedDateTime;
    }

    // convert weather code to readable data(decoder)
    private static String convertWeatherCode(long weatherCode){
        String weatherCondition = switch ((int) weatherCode) {
            case 0 -> "Clear";
            case 1, 2, 3 -> "Cloudy";
            case 51, 53, 55 -> "Drizzle";
            case 56, 57 -> "Freezing Drizzle";
            case 61, 63, 65 -> "Rain";
            case 66, 67 -> "Freezing Rain";
            case 71, 73, 75 -> "Snow Fall";
            case 77 -> "Snow Grains";
            case 80, 81, 82 -> "Rain Showers";
            case 85, 86 -> "Snow Showers Slight and Heavy";
            case 95 -> "Thunderstorm";
            case 96, 99 -> "Thunderstorm with Slight and Heavy Hail";
            default -> "Unknown Weather Code";
        };
        return weatherCondition;
    }
}
