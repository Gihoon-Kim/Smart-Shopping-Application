package com.example.marketapp;

import android.content.ContentValues;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public class RequestHttpURLConnection {

    public String request(String _url, ContentValues _params) {

        // HttpURLConnection Reference variable
        HttpURLConnection urlConnection = null;
        // Parameter to be sent after URL
        StringBuffer sbParams = new StringBuffer();

        /*
        1. Connect parameter to StringBuffer
         */
        // if data is null, parameter is empty
        if (_params == null) {

            sbParams.append("");
        } else {

            // if there are more than two parameter, create a variable to switch because & is required for parameter connection
            boolean isAnd = false;
            // Value and key of Parameter
            String key;
            String value;

            for (Map.Entry<String, Object> parameter : _params.valueSet()) {

                key = parameter.getKey();
                value = parameter.getValue().toString();

                // when there are more than two parameter, put & between parameters
                if (isAnd) {

                    sbParams.append("&");
                }

                sbParams.append(key).append("=").append(value);

                // if there are more than two parameter, change isAnd to true and add & from the next loop
                if (!isAnd) {

                    if (_params.size() >= 2) {

                        isAnd = true;
                    }
                }
            }
        }

        /*
        2. Through HttpUrlConnection, Get data of the WEB
         */
        try {

            URL url = new URL(_url);
            urlConnection = (HttpURLConnection) url.openConnection();

            // [2-1]. urlConnection Set up
            urlConnection.setRequestMethod("POST"); // Setting method up for URL request : POST
            urlConnection.setRequestProperty("Accept-Charset", "UTF-8");    // Setting Accept-Charset up
            urlConnection.setRequestProperty("Context_Type", "application/x-www-form-urlencoded;cahrset=UTF-8");

            // [2-2]. Sending parameter and read data
            String strParams = sbParams.toString(); // Store organized parameters in sbParams as String // ex) id=id1&pw=123;
            OutputStream os = urlConnection.getOutputStream();
            os.write(strParams.getBytes(StandardCharsets.UTF_8));  // write to output stream
            os.flush(); // Flushes the output stream and forced execute all buffered output bytes
            os.close(); // Closes output stream and releases all system resources

            // [2-3]. Checking connection request
            // if it is fail, return null and exit the method
            if (urlConnection.getResponseCode() != HttpURLConnection.HTTP_OK) {

                return null;
            }

            // [2-4]. Return the readout
            // Receive the requested URL output by BufferedReader
            BufferedReader reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream(), StandardCharsets.UTF_8));

            // Variables line of output and sum
            String line;
            String page = "";

            // Take the line and combine it
            while ((line = reader.readLine()) != null) {

                page += line;
            }

            return page;
        } catch (MalformedURLException e) { // for URL.

            e.printStackTrace();
        } catch (IOException e) {   // for openConnection();

            e.printStackTrace();
        } finally {

            if (urlConnection != null) {

                urlConnection.disconnect();
            }
        }

        return null;
    }
}
