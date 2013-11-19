package com.virtual_affairs.yammer_quiz;

import android.app.Activity;
import android.os.Bundle;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Random;

/**
 * Created by emonidi on 13-11-19.
 */
public class PositionQuestionActivity extends Activity {
    Person person; // Person object with picture(bitmap), Name string
    Helper helper; // Helper class with some methods
    InfoManager infoManager; //gets/save the raw text in local files
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.position_question_activity);
        person = getPerson();

    }

    private Person getPerson(){
        //get the helper
        //helper = new Helper();

        //get the infoManager
        infoManager = new InfoManager(getBaseContext(),"people");
        String raw  = infoManager.Read();

        try {
            JSONArray people = new JSONArray(raw);
            getPerson(people);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    };

    private Person getPerson(JSONArray people){
        Random random = new Random();
        Person p = null;
        JSONObject person = null;
        String jobTitle = "";
        String image = "no_photo";
        while (image.contains("no_photo") && jobTitle.length() < 3 ){
            int r  = random.nextInt(people.length());
            try {
                person = people.getJSONObject(r);
                jobTitle = person.getString("job_title");
                image = person.getString("mugshot_url_template");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        p = new Person();
        try {
            p.setFull_name(person.getString("full_name").replace("{width}","300").replace("{height}","300"));
            p.setImageUrl(image);
            p.setJobTitle(person.getString("job_title"));
            return p;
        } catch (JSONException e) {
            e.printStackTrace();
        }


        return null;
    }
}
