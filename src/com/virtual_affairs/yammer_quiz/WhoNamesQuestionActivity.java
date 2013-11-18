package com.virtual_affairs.yammer_quiz;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.Inflater;

/**
 * Created by emonidi on 13-11-17.
 */
public class WhoNamesQuestionActivity extends Activity {
    ImageView mainImage;
    ListView listView;
    JSONArray selectedPeople;
    Helper helper;
    int answer;
    ArrayList<Person> sPeople;
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        sPeople= new ArrayList<Person>();
        setContentView(R.layout.who_names_question);
        selectedPeople = new JSONArray();
        Helper helper = new Helper();
        answer = helper.makeRandomNum(2);
        String rawPeople = new InfoManager(this,"people").Read();
        try {
            JSONArray people = new JSONArray(rawPeople);
            for(int i=0;i<3;i++){
                selectedPeople.put(helper.getPersonWithImage(people));
            }
            IGetter iGetter = new IGetter();
            iGetter.execute(selectedPeople.getJSONObject(answer).getString("mugshot_url_template").replace("{width}","200").replace("{height}","200"));

            listView = (ListView) findViewById(R.id.mainListView);

            for (int i = 0 ;  i < selectedPeople.length(); i++){
                Person person = new Person();
                person.setFull_name(selectedPeople.getJSONObject(i).getString("full_name"));
                sPeople.add(person);
            }
            lAdapter adapter = new lAdapter(getBaseContext(),R.id.mainListView,sPeople);
            listView.setAdapter(adapter);

            Log.d("OK","OK");
        } catch (JSONException e) {
            e.printStackTrace();
        }


    }

    private class lAdapter extends ArrayAdapter<ArrayList>{
        Context context;
        ArrayList<Person> objects;
        int resource;

        public lAdapter(Context context, int resource, ArrayList<Person> sPeople) {
            super(context, resource, (List) sPeople);
            context = context;
            objects = sPeople;
            resource = resource;
        }


        @Override
        public int getCount() {
            return 3;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            if(convertView==null){
                View v = null;
                LayoutInflater inflater = (LayoutInflater) getBaseContext().getSystemService(LAYOUT_INFLATER_SERVICE);
                v =  inflater.inflate(R.layout.who_name_listview,parent,false);
                TextView answerText = (TextView) v.findViewById(R.id.answerText);
                answerText.setText(objects.get(position).getFull_name());
                final ImageView star = (ImageView) v.findViewById(R.id.imageView);
                v.setClickable(true);
                v.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(position == answer){
                            star.setImageDrawable(getResources().getDrawable(R.drawable.correct));
                        }else{
                            star.setImageDrawable(getResources().getDrawable(R.drawable.incorrect));
                        }
                        Handler handler = new Handler();
                        Runnable runnable = new Runnable() {
                            @Override

                            public void run() {
                                Intent i = new Intent(getBaseContext(),WhoQuestion.class);
                                startActivity(i);
                                finish();
                            }
                        };

                        handler.postDelayed(runnable,1000);
                    }
                });
                return v;
            }else{
                return convertView;
            }

        }
    }



    public class IGetter extends AsyncTask<String,Void, Bitmap>{
        Bitmap bitmap;
        URL url;
        @Override
        protected Bitmap doInBackground(String... strings) {
            try {
                url = new URL(strings[0]);
                bitmap = BitmapFactory.decodeStream(url.openConnection().getInputStream());
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return bitmap;
        };

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
    }
}
