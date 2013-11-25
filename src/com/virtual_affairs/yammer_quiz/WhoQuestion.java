package com.virtual_affairs.yammer_quiz;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.LauncherActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Layout;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;

import android.os.Handler;
import android.widget.Toast;

import java.util.List;
import java.util.logging.LogRecord;
import java.util.zip.Inflater;

import static java.lang.Integer.*;

/**
 * Created by emonidi on 13-11-15.
 */
public class WhoQuestion extends Activity {
    JSONArray people = null;
    Helper helper = new Helper();
    ImageView pic1;
    ImageView pic2;
    ImageView pic3;
    TextView questionText;
    LinearLayout imageLayout;
    JSONArray selectedPeople = null;
    Point screenSize;
    Display screen;
    LinearLayout answer_container;
    int answer;

    LinearLayout mainLayout;
    ProgressBar progressBar;
    ListView listView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_who_question);

        answer = helper.makeRandomNum(3);
        answer_container = (LinearLayout) findViewById(R.id.answer_container);
        getPeopleData();
        getPeopleWithImages(3);
        questionText = (TextView) findViewById(R.id.questionText);
        ArrayList<Person> persons = new ArrayList<Person>();
        for(int i = 0; i < selectedPeople.length(); i++){
            Person person = new Person();
            try {
                person.setFull_name(selectedPeople.getJSONObject(i).getString("full_name"));
                person.setImageUrl(selectedPeople.getJSONObject(i).getString("mugshot_url_template").replace("{width}","300").replace("{height}","300"));
                persons.add(person);
            } catch (JSONException e) {
                e.printStackTrace();

            }
        }



        try {
            questionText.setText("Who is "+selectedPeople.getJSONObject(answer).getString("full_name")+"?");
            for(int i  = 0 ; i < selectedPeople.length(); i++){
            	setImage(i);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }



    

    public void setImage(int position){
        JSONObject p = null;
        try {
            p =  selectedPeople.getJSONObject(position);
            String imageString = p.getString("mugshot_url_template").replace("{width}","300").replace("{height}","300");
            new ImageGetter().execute(new String[] {imageString,String.valueOf(position)});

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void getPeopleWithImages(int count){
            selectedPeople = new JSONArray();
            for(int i = 0; i < count; i++){
                 selectedPeople.put(helper.getPersonWithImage(people));
            }
    };



    public void getPeopleData(){
        InfoManager iMan = new InfoManager(getBaseContext(),"people");
        String peopleRaw = iMan.Read();
        try {
            people = new JSONArray(peopleRaw);

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public class ImageGetter extends AsyncTask<String, Void, Bitmap> {
        String index;
        @Override
        protected Bitmap doInBackground(String... strings) {
            index = strings[1];
            try {
                URL imgUrl  = new URL(strings[0]);
                InputStream is = (InputStream) imgUrl.getContent();
                Bitmap d = BitmapFactory.decodeStream(is);
                return d;
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
        
            Bitmap circleBitmap = Bitmap.createBitmap(bitmap.getWidth(),bitmap.getHeight(), Bitmap.Config.ARGB_8888);
            BitmapShader shader = new BitmapShader(bitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
            
            Paint paint = new Paint();
            paint.setShader(shader);
            
            paint.setStrokeWidth(10);
          
            Canvas c = new Canvas(circleBitmap);
            c.drawCircle(bitmap.getWidth()/2,bitmap.getHeight()/2,bitmap.getWidth()/2,paint);

            final ImageView im = (ImageView) new ImageView(getBaseContext());
            if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT){
            	im.setPadding(0, 25, 0, 0);
            }else{
            	im.setPadding(25,50, 0, 0);
            }
            
            im.setImageBitmap(circleBitmap);
            im.setTag(index);
            im.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					if(Integer.parseInt(index) == answer){
						im.setImageDrawable(getResources().getDrawable(R.drawable.correct));
					}else{
						im.setImageDrawable(getResources().getDrawable(R.drawable.incorrect));
					}
					im.setMinimumWidth(300);
					im.setMinimumHeight(300);
					
					Handler handler = new Handler();
					
					Runnable runnable =  new Runnable() {
						
						@Override
						public void run() {
							// TODO Auto-generated method stub
							Intent i =  new Intent(getBaseContext(),helper.startNextQuestion(getBaseContext(), "WhoNamesQuestionActivity"));
							startActivity(i);
							finish();
						}
					};					
					
					handler.postDelayed(runnable, 1000);
					
					//TO DO: score implementation here
					
					
				}
			});
                        
            answer_container.addView(im);
            
            
            
        }


    }

    public boolean answerTrue (int imageIndex){
        boolean answerTrue = false;

        if(imageIndex == answer){
            answerTrue = false;
        }

        return answerTrue;
    }


    };

