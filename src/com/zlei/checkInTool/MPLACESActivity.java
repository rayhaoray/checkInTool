package com.zlei.checkInTool;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.webkit.CookieManager;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class MPLACESActivity extends Activity {

    TextView jsonText;
    String jsonString = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_m);
        jsonText = (TextView) findViewById(R.id.mplaces_venues);
        String url = "https://api.sessionm.com/apps/aba6ba56b63680cad063e987df52a71e620dbc77/mplaces/ads/fetch" +
                "?coordinates[latitude]=42.3493505&coordinates[longitude]=-71.0492305&coordinates[accuracy]=10";
        new getVenuesTask().execute(url);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private class getVenuesTask extends AsyncTask<String, Void, String> {
        protected String doInBackground(String... url) {
            try {
                URL u = new URL(url[0]);
                int timeout = 4000;
                HttpURLConnection c = (HttpURLConnection) u.openConnection();
                c.setRequestMethod("GET");
                c.setRequestProperty("Content-length", "0");
                c.setUseCaches(true);
                String cookieString = CookieManager.getInstance().getCookie(url[0]);
                c.setRequestProperty("Cookie", cookieString);
                c.setAllowUserInteraction(false);
                c.setConnectTimeout(timeout);
                c.setReadTimeout(timeout);
                c.connect();
                int status = c.getResponseCode();
                switch (status) {
                    case 200:
                    case 201:
                        BufferedReader br = new BufferedReader(new InputStreamReader(c.getInputStream()));
                        StringBuilder sb = new StringBuilder();
                        String line;
                        while ((line = br.readLine()) != null) {
                            sb.append(line + "\n");
                        }
                        br.close();
                        return sb.toString();
                }
            } catch (MalformedURLException ex) {
            } catch (IOException ex) {
            }
            return null;
        }

        protected void onPostExecute(String result) {
            jsonString = result;
            jsonText.setText(jsonString);
        }
    }
}


