package com.virtual_affairs.yammer_quiz;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;


public class PeopleGetter extends AsyncTask<String, Void, ArrayList<Person>> {
	InputStream is = null;
	String result  = "";
	JSONArray jsonArray = null;
	Context context;
	
	public PeopleGetter(Context context) {
		this.context = context;
	}
	
	@Override
	protected ArrayList<Person> doInBackground(String... params) {
		String token = params[0];
		
		HttpClient httpClient = new DefaultHttpClient();
		HttpGet httpGet = new HttpGet("https://www.yammer.com/api/v1/users.json");
		httpGet.setHeader("Authorization", "Bearer "+token);
		try {
			HttpResponse response = httpClient.execute(httpGet);
			HttpEntity entity = response.getEntity();
		
			is = entity.getContent();
			BufferedReader reader  = new BufferedReader(new InputStreamReader(is,"utf-8"),8);
			StringBuilder sb = new StringBuilder();
			String line = null;
			while((line = reader.readLine()) != null){
				sb.append(line);
			}
			is.close();
			result = sb.toString();	   
			InfoManager infoManager = new InfoManager(context, "people");
			infoManager.Save(result);
			jsonArray = new JSONArray(result);
			
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
	}

}
