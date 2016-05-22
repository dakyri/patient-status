package com.mayaswell.patientstatus;

import android.os.AsyncTask;

import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

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
	protected int responseCode;
	protected String responseBody = null;
	Response response = null;
	OkHttpClient okclient = null;

	@Override
	protected JSONObject  doInBackground(String... urls)
	{
//		BufferedReader reader = null;
		responseBody = null;
		response = null;

		lastErrorMsg = "No error";

		responseCode = 0;

		if (urls.length < 1) {
			lastErrorMsg = "No request url passed";
			return null;
		}
		String url = urls[0];
		try {
			okclient  = new OkHttpClient();
			Request okrequest = new Request.Builder().url(url).build();
			response = okclient.newCall(okrequest).execute();
			responseBody = response.body().string();
			responseCode = response.code();

			/*
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
			*/
			JSONObject jsono = new JSONObject(responseBody);
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
			try {
				if (response != null) {
					response.body().close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			/*
			if (httpConnection != null) {
				httpConnection.disconnect();
			}
			if (reader != null) {
				try {
					reader.close();
				} catch (final IOException e) {
				}
			}
			*/
		}
	}
}