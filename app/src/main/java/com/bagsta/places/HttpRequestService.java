package com.bagsta.places;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import android.util.Log;

public class HttpRequestService {
	
	
	static InputStream requestInpustStream(String urlString){
		HttpURLConnection connection = null;
		InputStream inputStream = null;
		try {
			URL url = new URL(urlString);
			connection = (HttpURLConnection) url.openConnection();
			
			connection.setReadTimeout(50000);
			connection.setConnectTimeout(55000);
			connection.setRequestMethod("GET");
			connection.setDoInput(true);
			connection.connect();
			
			inputStream = connection.getInputStream();
		} catch (IOException exception) {
			exception.printStackTrace();
		}
		return inputStream;
	}
	
	static byte[] requestBytes(String urlString) {
		InputStream inputStream = null;
		byte[] data = null;
		
		try {
			inputStream = requestInpustStream(urlString);
			if (inputStream == null)
				return data;
			
			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

			Utils.copyStream(inputStream, outputStream);
			data = outputStream.toByteArray();

			inputStream.close();

		} catch (IOException exception) {
			exception.printStackTrace();
		} finally {
			if (inputStream != null)
				try {
					inputStream.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		}
		return data;
	}
	
	static String requestString(String urlString) {
		Log.d("urlString", ""+urlString);
		byte data[] = requestBytes(urlString);
		return data != null ? new String(data) : null;
	}

}

