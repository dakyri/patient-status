package com.mayaswell.patientstatus;

import android.os.AsyncTask;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;


/**
 * Created by dak on 4/24/2016.
 */
public class JSONSlurper  extends AsyncTask<String, Integer, JSONObject> {

	protected String lastErrorMsg = "No error";

	@Override
	protected JSONObject  doInBackground(String... urls)
	{
		BufferedReader reader = null;
		String responseString = null;
		HttpURLConnection httpConnection = null;

		lastErrorMsg = null;
		if (urls.length < 1) {
			lastErrorMsg = "No request url passed";
			return null;
		}
		String url = urls[0];
		try {
			URL httpurl = new URL(url);
			httpConnection = (HttpURLConnection) httpurl.openConnection();

			httpConnection.setRequestMethod("GET");
			httpConnection.connect();
			int status;
			if ((status=httpConnection.getResponseCode()) != HttpURLConnection.HTTP_OK) {
				lastErrorMsg = "Invalid response from server: " + Integer.toString(status)+" , url "+url;
				return null;
			}


			// Read the input stream into a String
			InputStream inputStream = httpConnection.getInputStream();
			StringBuffer buffer = new StringBuffer();
			if (inputStream == null) {
				return null;
			}
			reader = new BufferedReader(new InputStreamReader(inputStream));
			String line;
			while ((line = reader.readLine()) != null) {
				buffer.append(line);
			}

			if (buffer.length() == 0) {
				return null;
			}
			responseString = buffer.toString();
			JSONObject jsono = new JSONObject(responseString);
			return jsono;
		} catch (IOException e) {
			e.printStackTrace();
			String msg = e.getMessage();
			if (msg == null) {
				msg = "IO exception for request to url '"+ url+"'";
			}
			lastErrorMsg = msg;
			return null;
		} catch (JSONException e) {
			String msg = e.getMessage();
			if (msg == null) {
				msg = "JSON processing exception for request to url '"+ url+"'";
			}
			lastErrorMsg = msg;
			return null;
		} finally {
			if (httpConnection != null) {
				httpConnection.disconnect();
			}
			if (reader != null) {
				try {
					reader.close();
				} catch (final IOException e) {
				}
			}
		}
	}
}