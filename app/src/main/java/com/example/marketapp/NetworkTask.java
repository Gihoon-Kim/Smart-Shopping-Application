package com.example.marketapp;

import android.content.ContentValues;
import android.os.AsyncTask;
import android.widget.TextView;

public class NetworkTask extends AsyncTask<Void, Void, String> {

    private String url;
    private ContentValues values;
    TextView textView;

    NetworkTask(String url, ContentValues values, TextView textView) {

        this.url = url;
        this.values = values;
        this.textView = textView;
    }

    @Override
    protected String doInBackground(Void... voids) {

        String result;  // Variable for result of requesting
        RequestHttpURLConnection requestHttpURLConnection = new RequestHttpURLConnection();
        result = requestHttpURLConnection.request(url, values); // Obtain the result from the URL

        return result;
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);

        // Print s because returned value from doInBackground() is fallen to the parameter of onPostExecute
        textView.setText(s);
    }
}
