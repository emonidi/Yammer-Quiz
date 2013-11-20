package com.virtual_affairs.yammer_quiz;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Shader;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Display;
import android.widget.ImageView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Random;

/**
 * Created by emonidi on 13-11-19.
 */
public class PositionQuestionActivity extends Activity {
    Person person; // Person object with picture(bitmap), Name string
    Helper helper;// Helper class with some methods
    InfoManager infoManager; //gets/save the raw text in local files
    ArrayList<Person> selectedPeople;
    Random random = new Random();
    int answer  = random.nextInt(3);
    Bitmap imageBitmap;
    Display display ;
    int screenWidth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.who_names_question);
        helper = new Helper();
        display = getWindowManager().getDefaultDisplay();
        screenWidth = helper.getScreenSize(display).x;
        selectedPeople = getSelectedPeople();
        GetImage imageGetter = new GetImage();
        imageGetter.execute(selectedPeople.get(answer).getImageUrl());


    }

    private class GetImage extends AsyncTask<String,Void,Bitmap>{

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
            ImageView imageView = (ImageView) findViewById(R.id.mainImageView);
            Bitmap circleBitmap = Bitmap.createBitmap(bitmap.getWidth(),bitmap.getHeight(), Bitmap.Config.ARGB_8888);
            BitmapShader shader = new BitmapShader(bitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
            Paint paint = new Paint();
            paint.setShader(shader);



            Canvas c= new Canvas(circleBitmap);
            c.drawCircle(bitmap.getWidth()/2,bitmap.getHeight()/2,bitmap.getWidth()/2,paint);
            imageView.setImageBitmap(circleBitmap);
        }

        @Override
        protected Bitmap doInBackground(String... strings) {
            Bitmap b = null;
            try {
                URL url = new URL(strings[0]);
                 b = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                return b;
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return b;
        }

    }

    private ArrayList<Person> getSelectedPeople(){
        //get the helper
        //helper = new Helper();

        //get the infoManager
        infoManager = new InfoManager(getBaseContext(),"people");
        String raw  = infoManager.Read();
        try {
            JSONArray people = new JSONArray(raw);
            selectedPeople = SelectedPeople(people);
            return selectedPeople;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    };

    private ArrayList<Person> SelectedPeople(JSONArray people){
        Person p = null;
        ArrayList selectedPeople = new ArrayList<Person>();
        for(int i = 0 ;  i < 3; i++){
            JSONObject person=  null;
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
                p.setImageUrl(person.getString("mugshot_url_template").replace("{width}", "300").replace("{height}", "300"));
                p.setFull_name(person.getString("full_name"));
                p.setJobTitle(person.getString("job_title"));
                selectedPeople.add(p);
            } catch (JSONException e) {
                e.printStackTrace();
            }


        }
        return selectedPeople;
    }
}
