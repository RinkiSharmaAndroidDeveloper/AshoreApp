package com.trutek.looped.ui.activity.create;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ListView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.trutek.looped.data.contracts.models.ActivityModel;
import com.trutek.looped.data.contracts.models.LocationModel;
import com.trutek.looped.msas.common.Utils.Constants;
import com.trutek.looped.msas.common.contracts.AsyncResult;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Rinki on 4/10/2017.
 */
public class AddressUtil {

    private static final String TAG = AddressUtil.class.getSimpleName();
    private static String basUrl = "http://maps.google.com/maps/api/geocode/json?sensor=false&address=";
    private static String url;
    String locationAdress;
    LocationModel location =new LocationModel();
    AsyncResult<String> mAsyncResult;

    public void urlConcat(String address, AsyncResult<String> asyncResultLocation)
    {
        String actualAddress;
        mAsyncResult=asyncResultLocation;
        actualAddress = address.replaceAll(" ", "%20");
        url = basUrl + actualAddress;
        new GetAddresses().execute();
    }
    private class GetAddresses extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            HttpHandler sh = new HttpHandler();

            // Making a request to url and getting response
            String jsonStr = sh.makeServiceCall(url);

            Log.e(TAG, "Response from url: " + jsonStr);

            if (jsonStr != null) {
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);
                    JSONArray contacts = jsonObj.getJSONArray("results");
                    for (int i = 0; i < contacts.length(); i++) {
                        JSONObject c = contacts.getJSONObject(i);
                        location.name= c.getString("formatted_address");
                    }
                    mAsyncResult.success(location.name);

                } catch (final JSONException e) {
                    Log.e(TAG, "Json parsing error: " + e.getMessage());
                }
            } else {
                Log.e(TAG, "Couldn't get json from server.");


            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
        }

    }
    public LocationModel locationName(ActivityModel activityModel){
        activityModel.location = new LocationModel();
        activityModel.location.name =location.name;
        activityModel.location.description =location.name;
        return activityModel.location;
    }
    public static class HttpHandler {

        private static final String TAG = HttpHandler.class.getSimpleName();

        public HttpHandler() {
        }

        public String makeServiceCall(String reqUrl) {
            String response = null;

            try {
                URL url = new URL(reqUrl);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setConnectTimeout(100000);
                conn.setReadTimeout(100000);
                conn.connect();
                int responseCode = conn.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    InputStream in = new BufferedInputStream(conn.getInputStream());
                    response = convertStreamToString(in);
                }

            } catch (MalformedURLException e) {
                Log.e(TAG, "MalformedURLException: " + e.getMessage());
            } catch (ProtocolException e) {
                Log.e(TAG, "ProtocolException: " + e.getMessage());
            } catch (IOException e) {
                Log.e(TAG, "IOException: " + e.getMessage());
            } catch (Exception e) {
                Log.e(TAG, "Exception: " + e.getMessage());
            }
            return response;
        }

        private String convertStreamToString(InputStream is) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            StringBuilder sb = new StringBuilder();

            String line;
            try {
                while ((line = reader.readLine()) != null) {
                    sb.append(line).append('\n');
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return sb.toString();
        }
    }
}
