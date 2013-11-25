package com.virtual_affairs.yammer_quiz;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;
import android.view.Display;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Random;

/**
 * Created by emonidi on 13-11-15.
 */
public class Helper {

    public int makeRandomNum(int arraySize){
        Random random = new Random();
        int r = random.nextInt(arraySize);
        return r;
    }

    public Class startNextQuestion(Context context,String cl){
        String[] activities = {"WhoQuestion","WhoNamesQuestionActivity","PositionQuestionActivity"};
        Class<?> activity = null;
        
        if(cl.equals("splash")){
        	activity = WhoQuestion.class;
        	return activity;
        }
        
        for(int i = 0; i < activities.length;i++){
            if(i == activities.length){
                activity =  WhoQuestion.class;
            }
            else{
                if(cl.equals("WhoQuestion")){
                    activity = WhoQuestion.class;
                    return activity;
                }else if(cl.equals("WhoNamesQuestionActivity")){
                	  activity = WhoNamesQuestionActivity.class;
                      return activity;
                }else if(cl.equals("PositionQuestionActivity")){
                      activity = PositionQuestionActivity.class;
                      return  activity;
                }
            }
        }
        return null;
    };


    public JSONObject getPersonWithImage(JSONArray people) {
            Random r =  new Random();
            int peopleLength = people.length();
            int rand = r.nextInt(peopleLength);
            JSONObject person = null;
        try {
            person = people.getJSONObject(rand);
            String imgString = person.getString("mugshot_url_template");
            while (imgString.contains("no_photo")){
                rand = r.nextInt(peopleLength);
                person = people.getJSONObject(rand);
                imgString = person.getString("mugshot_url_template");

            }


        } catch (JSONException e) {
            e.printStackTrace();
        }
        return person;
    }



    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    public Point getScreenSize(Display display){
        Point size = new Point();

        display.getSize(size);
        return size;
    }
}

