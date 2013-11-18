package com.virtual_affairs.yammer_quiz;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.LauncherActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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
    int answer;

    LinearLayout mainLayout;
    ProgressBar progressBar;
    ListView listView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_who_question);

        answer = helper.makeRandomNum(3);

        getPeopleData();
        getPeopleWithImages(3);
        questionText = (TextView) findViewById(R.id.questionText);
        listView = (ListView) findViewById(R.id.answerList);
        ArrayList<Person> persons = new ArrayList<Person>();
        for(int i = 0; i < selectedPeople.length(); i++){
            Person person = new Person();
            try {
                person.setFull_name(selectedPeople.getJSONObject(i).getString("full_name"));
                person.setImageUrl(selectedPeople.getJSONObject(i).getString("mugshot_url_template").replace("{width}","200").replace("{height}","200"));
                persons.add(person);
            } catch (JSONException e) {
                e.printStackTrace();

            }
        }

        PeopleAdapter adapter = new PeopleAdapter(this,R.layout.people_list_item,persons);
        listView.setAdapter(adapter);


        try {
            questionText.setText("Who is "+selectedPeople.getJSONObject(answer).getString("full_name")+"?");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }



    public class PeopleAdapter extends ArrayAdapter<Person> {
       Context context;
       ArrayList<Person> objects;
       int resource;
       public PeopleAdapter(Context context, int resource, ArrayList<Person> objects) {
           super(context, resource, objects);

           resource= resource;
           context = context;
           objects = objects;

       }


       @Override
       public View getView(int position, View convertView, ViewGroup parent) {

           if(convertView == null){
               LayoutInflater inflater  = (LayoutInflater) getBaseContext().getSystemService(LAYOUT_INFLATER_SERVICE);
               final View v = inflater.inflate(R.layout.people_list_item,parent,false);
               ImageView imageView = (ImageView) parent.findViewById(R.id.imageView);
               final int pos = position;
               setImage(pos);

               v.setOnClickListener(new View.OnClickListener() {
                   @Override
                   public void onClick(View view) {
                       if(pos == answer){
                           appendIcon(view,"correct");
                       }else{
                           appendIcon(view,"incorrect");
                       }
                   }
               });
               return v;
           }else{
               return convertView;
           }

       };

       private void appendIcon(View v, String iconType){
            ImageView icon  = (ImageView) v.findViewById(R.id.imageView);
            Drawable drawable;
            if(iconType.equals("correct")){
                  drawable = getResources().getDrawable(R.drawable.correct);
            }else{
                  drawable = getResources().getDrawable(R.drawable.incorrect);
            }
            icon.setImageDrawable(drawable);
            final Intent i  = new Intent(getContext(),WhoNamesQuestionActivity.class);
            Handler handler  = new Handler();
            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    startActivity(i);
                    finish();
                }
            };
            handler.postDelayed(runnable,1000);
       }

       @Override
       public int getCount() {
           return 3;
       }
   }

    public void setImage(int position){
        JSONObject p = null;
        try {
            p =  selectedPeople.getJSONObject(position);
            String imageString = p.getString("mugshot_url_template").replace("{width}","100").replace("{height}","100");
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

    public class ImageGetter extends AsyncTask<String, Void, Drawable> {
        String index;
        @Override
        protected Drawable doInBackground(String... strings) {
            index = strings[1];
            try {
                URL imgUrl  = new URL(strings[0]);
                InputStream is = (InputStream) imgUrl.getContent();
                Drawable d = Drawable.createFromStream(is,"personImage_"+strings[1]);
                return d;
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Drawable drawable) {
            super.onPostExecute(drawable);
            BitmapDrawable bDrawable = (BitmapDrawable) drawable;
            Bitmap circleBitmap = Bitmap.createBitmap(bDrawable.getBitmap().getWidth(),bDrawable.getBitmap().getHeight(), Bitmap.Config.ARGB_8888);
            BitmapShader shader = new BitmapShader(bDrawable.getBitmap(), Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
            Paint paint = new Paint();
            paint.setShader(shader);
            Canvas c = new Canvas(circleBitmap);
            c.drawCircle(bDrawable.getBitmap().getWidth()/2,bDrawable.getBitmap().getHeight()/2,bDrawable.getBitmap().getWidth()/2,paint);

            View view = (View) listView.getChildAt(Integer.parseInt(index));
            ImageView imageView = (ImageView) view.findViewById(R.id.imageView);
            imageView.setImageBitmap(circleBitmap);
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

