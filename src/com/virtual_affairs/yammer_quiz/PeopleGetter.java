package com.virtual_affairs.yammer_quiz;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Date;

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
import org.json.JSONStringer;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;


public class PeopleGetter extends AsyncTask<String, Void, ArrayList<Person>> {
	InputStream is = null;
	String result  = "";
	JSONArray jsonArray = null;
	Activity activity;
	
	public PeopleGetter(Activity activity) {
		this.activity = activity;
	}

    @Override
    protected void onPostExecute(ArrayList<Person> persons) {
        super.onPostExecute(persons);
        activity.startActivity(new Intent(activity,WhoQuestion.class));
    }

    @Override
	protected ArrayList<Person> doInBackground(String... params) {
		String token = params[0];
        String result = null;
        JSONArray people = new JSONArray();
        StringBuilder sbuilder = new StringBuilder();
        int index = 0;

        while(index < 4){
            StringBuilder sb = null;
            HttpGet httpGet = null;
            HttpClient httpClient = new DefaultHttpClient();
            JSONArray arr = null;
            String line = null;

            String s = "";
            try {
                s = "https://www.yammer.com/api/v1/users.json?page="+String.valueOf(index);

                sb = new StringBuilder();
                httpGet = new HttpGet(s);
                httpGet.setHeader("Authorization", "Bearer "+token);

                HttpResponse response = httpClient.execute(httpGet);
                HttpEntity entity = response.getEntity();
                InputStream is = entity.getContent();
                BufferedReader reader = new BufferedReader(new InputStreamReader(is,"utf-8"),8);
                while ((line = reader.readLine()) != null){
                    sbuilder.delete(0,sbuilder.length());
                    sbuilder.append(line);
                }

                arr = new JSONArray(sbuilder.toString());
                for (int i = 0; i < arr.length(); i++){
                    people.put(arr.getJSONObject(i));
                }

                index++;


            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }


        }



        result = people.toString();

        System.out.println(people.toString());
        InfoManager iMan = new InfoManager(activity,"people");
        InfoManager updated = new InfoManager(activity,"updated");
        updated.Save(String.valueOf(new Date().getTime()));
        iMan.Save(result);
        Log.d("OK","OK");
//
//
//
//		HttpClient httpClient = new DefaultHttpClient();
//		HttpGet httpGet = new HttpGet("https://www.yammer.com/api/v1/users.json");
//		httpGet.setHeader("Authorization", "Bearer "+token);
//		try {
//			HttpResponse response = httpClient.execute(httpGet);
//			HttpEntity entity = response.getEntity();
//
//			is = entity.getContent();
//			BufferedReader reader  = new BufferedReader(new InputStreamReader(is,"utf-8"),8);
//			while((line = reader.readLine()) != null){
//				sb.append(line);
//			}
//			is.close();
//			result = sb.toString();
//			InfoManager infoManager = new InfoManager(activity.getBaseContext(), "people");
//			infoManager.Save(result);
//			jsonArray = new JSONArray(result);
//
//		} catch (ClientProtocolException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (JSONException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//
		return null;
	}

}
