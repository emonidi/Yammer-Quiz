package com.virtual_affairs.yammer_quiz;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Shader;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
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
    TextView questionTextView;
    LinearLayout loaderLayout;
    LinearLayout contentLayout;
    ListView answerList;
    Display display ;
    int screenWidth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.who_names_question);
    
        helper = new Helper();
        display = getWindowManager().getDefaultDisplay();
        screenWidth = helper.getScreenSize(display).x;
        setInitialLayout();
        
        selectedPeople = getSelectedPeople();
        questionTextView = (TextView) findViewById(R.id.questionText);
        questionTextView.setText("What is the position of "+selectedPeople.get(answer).full_name+"?");
        GetImage imageGetter = new GetImage();
        imageGetter.execute(selectedPeople.get(answer).getImageUrl());
        makeAnwserList();

    }
    
    private void makeAnwserList(){
    	answerList = (ListView) findViewById(R.id.mainListView);
    	LAdapter adapter = new LAdapter(getBaseContext(), R.id.mainListView, selectedPeople);
    	answerList.setAdapter(adapter);
    
    }
    
    private class LAdapter extends ArrayAdapter<ArrayList>{
    	Context context;
    	int resource;
    	ArrayList<Person> people;
		public LAdapter(Context context, int resource, ArrayList<Person> people) {
			super(context, resource, (List) people);
			this.context = context;
			this.resource = resource;
			this.people = people ;
			// TODO Auto-generated constructor stub
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return 3;
		}

		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			
			if(convertView == null){
				View v = null;
				LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
				v = inflater.inflate(R.layout.who_name_listview, parent,false);
				TextView textView = (TextView) v.findViewById(R.id.answerText);
				textView.setText(people.get(position).jobTitle);
				final ImageView star = (ImageView) v.findViewById(R.id.imageView);
				v.setOnClickListener(new View.OnClickListener() {
					
					@Override
					public void onClick(View v) {
						if(position == answer){
							star.setImageDrawable(getResources().getDrawable(R.drawable.correct));
						}else{
							star.setImageDrawable(getResources().getDrawable(R.drawable.incorrect));
						}						
						
						Handler handler = new Handler();
						Runnable runnable = new Runnable() {
							
							@Override
							public void run() {
								// TODO Auto-generated method stub
								Intent i = new Intent(getBaseContext(),helper.startNextQuestion(getBaseContext(), "WhoQuestion"));
								startActivity(i);
								finish();
							}
						};
						
						handler.postDelayed(runnable, 1000);
					}
				});
				return v;
			}else{
				return convertView;
			}
			
			
		}
		
	
    	
    } 
    
    private void setInitialLayout(){
    	loaderLayout = (LinearLayout) findViewById(R.id.loaderLayout);
    	contentLayout = (LinearLayout) findViewById(R.id.contentLayout);
    	contentLayout.setX(screenWidth*2);
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
            
            ObjectAnimator pAnim = ObjectAnimator.ofFloat(loaderLayout, "x", screenWidth*-1);
            pAnim.setDuration(500);
            pAnim.start();
            
            ObjectAnimator cAnim = ObjectAnimator.ofFloat(contentLayout, "x", 0);
            cAnim.setDuration(500);
            cAnim.start();
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
        ArrayList<Person> selectedPeople = new ArrayList<Person>();
        for(int i = 0 ;  i < 3; i++){
            JSONObject person=  null;
            String jobTitle = "";
            String image = "no_photo";
            while (image.contains("no_photo") && jobTitle.isEmpty() ){
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
                p.imageUrl = person.getString("mugshot_url_template").replace("{width}", "300").replace("{height}", "300");
                p.full_name = person.getString("full_name");
                p.jobTitle = person.getString("job_title");
                selectedPeople.add(p);
            } catch (JSONException e) {
                e.printStackTrace();
            }


        }
        return selectedPeople;
    }
}
