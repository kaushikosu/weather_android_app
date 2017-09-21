/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.android.sunshine;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.android.recyclerview.RecyclerViewAdapter;
import com.example.android.sunshine.data.SunshinePreferences;
import com.example.android.sunshine.utilities.NetworkUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    private TextView mWeatherTextView;

    private TextView mErrorMessage;

    private ProgressBar mProgressBar;

    private static int NUM_ITEMS = 0;

    private RecyclerViewAdapter recyclerViewAdapter;

    private RecyclerView mRecyclerView;

    public JSONArray allElements;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forecast);

        /*
         * Using findViewById, we get a reference to our TextView from xml. This allows us to
         * do things like set the text of the TextView.
         */
        mErrorMessage = (TextView) findViewById(R.id.error_message_textView);
        mProgressBar = (ProgressBar) findViewById(R.id.tv_progress_bar);
        mRecyclerView = (RecyclerView) findViewById(R.id.rv_elements);
        loadWeatherData();
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setHasFixedSize(true);


        // TODO (9) Call loadWeatherData to perform the network request to get the weather
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.refresh_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int selectedMenuItemId = item.getItemId();
        if (selectedMenuItemId == R.id.action_refresh){
            loadWeatherData();
        }
        return true;
    }

    // TODO (8) Create a method that will get the user's preferred location and execute your new AsyncTask and call it loadWeatherData

    // TODO (5) Create a class that extends AsyncTask to perform network requests
    // TODO (6) Override the doInBackground method to perform your network requests
    // TODO (7) Override the onPostExecute method to display the results of the network request


    private void showErrorMessage(){
        mRecyclerView.setVisibility(View.INVISIBLE);
        mErrorMessage.setVisibility(View.VISIBLE);
    }

    private void showWeatherData(){
        mRecyclerView.setVisibility(View.VISIBLE);
        mErrorMessage.setVisibility(View.INVISIBLE);
    }

    protected void loadWeatherData(){
        String pref_location = SunshinePreferences.getPreferredWeatherLocation(MainActivity.this);
        URL url = NetworkUtils.buildUrl(pref_location);
        mProgressBar.setVisibility(View.VISIBLE);
        new FetchWeatherDataTask().execute(url);
    }

    public class FetchWeatherDataTask extends AsyncTask<URL,Void,String>{

        @Override
        protected String doInBackground(URL... params) {
            URL url = params[0];
            String response = null;
            try {
                response = NetworkUtils.getResponseFromHttpUrl(url);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return response;
        }

        @Override
        protected void onPostExecute(String s) {
            mProgressBar.setVisibility(View.GONE);
            if (!TextUtils.isEmpty(s)) {
                convertToJsonElements(s);
                recyclerViewAdapter = new RecyclerViewAdapter(allElements.length(),allElements);
                mRecyclerView.setAdapter(recyclerViewAdapter);
            }
            else{
                showErrorMessage();
            }
        }
    }

    private String convertToJsonElements(String s){
        String returnString = "";
        try {
            JSONObject weatherJson = new JSONObject(s);
            allElements = weatherJson.getJSONArray("list");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return returnString;
    }


}