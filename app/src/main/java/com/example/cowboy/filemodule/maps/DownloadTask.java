package com.example.cowboy.filemodule.maps;

import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.maps.GoogleMap;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Cowboy on 01.03.2018.
 */
public class DownloadTask extends AsyncTask {

    private GoogleMap mMap;

    public DownloadTask(GoogleMap map) {
        this.mMap = map;
    }

    @Override
    protected String doInBackground(Object[] params) {

        String data = "";

        try {
            data = downloadUrl(params[0].toString());
        } catch (Exception e) {
            Log.d("Background Task", e.toString());
        }
        return data;
    }

    @Override
    public void onPostExecute(Object result) {
        super.onPostExecute(result);

        ParserTask parserTask = new ParserTask(mMap);
        String result2 = result.toString();

        parserTask.execute(result2);

    }

    private String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(strUrl);

            urlConnection = (HttpURLConnection) url.openConnection();

            urlConnection.connect();

            iStream = urlConnection.getInputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

            StringBuffer sb = new StringBuffer();

            String line = "";
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

            data = sb.toString();

            br.close();

        } catch (Exception e) {
            Log.d("Exception", e.toString());
        } finally {
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }

}
