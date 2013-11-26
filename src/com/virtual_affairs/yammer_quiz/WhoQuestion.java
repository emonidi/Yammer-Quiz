package com.virtual_affairs.yammer_quiz;

import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
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
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.text.Layout;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
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
    
    // DRAWER
    ListView navList;
    DrawerLayout drawer;
    ActionBarDrawerToggle toggle;
    
    public ArrayList<MItem> setMenuItems(){
    	ArrayList<MItem> menuItems =  new ArrayList<MItem>();
    	
    	MItem people = new MItem();
    	people.setItemText("People");
    	people.setItemIcon(getResources().getDrawable(R.drawable.user));
    	menuItems.add(people);
    	
    	
    	MItem game = new MItem();
    	game.setItemText("Play Game");
    	game.setItemIcon(getResources().getDrawable(R.drawable.play));
    	menuItems.add(game);
    	
    	return menuItems;
    }
    
    @SuppressLint("NewApi")
	public void inflateLayout(){
    	
    	ArrayList<MItem> items = setMenuItems();
    	
    	drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
    	navList = (ListView) findViewById(R.id.drawer);
    	MenuAdapter menuAdapter = new MenuAdapter(getBaseContext(),R.id.drawer,items);
    	navList.setAdapter(menuAdapter);
    	
    	toggle = new ActionBarDrawerToggle(this,drawer,R.drawable.ic_drawer,0,0){

			@Override
			public void onDrawerClosed(View drawerView) {
				// TODO Auto-generated method stub
				getActionBar().setTitle(R.string.app_name);
				invalidateOptionsMenu();
			}

			@Override
			public void onDrawerOpened(View drawerView) {
				// TODO Auto-generated method stub
				getActionBar().setTitle("Menu");
				invalidateOptionsMenu();
			}
    		
    	};
    	
    	
    	drawer.setDrawerListener(toggle);
    	
    	getActionBar().setDisplayHomeAsUpEnabled(false);
    	getActionBar().setHomeButtonEnabled(true);
        
        
    }
    
    
    
    class MenuAdapter extends ArrayAdapter<ArrayList> {
		int resource;
		Context context;
		ArrayList<MItem> items;
		
		public MenuAdapter(Context context, int resource,
				ArrayList<MItem> items) {
			super(context, resource);
			this.items = items;
			this.resource = resource;
			this.context = context;
			// TODO Auto-generated constructor stub
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return items.size();
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			
			
			if(convertView == null){
				View v = null;
				LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				v=inflater.inflate(R.layout.menu_item_layout,parent,false);
				TextView text = (TextView) v.findViewById(R.id.menuTitle);
				text.setText(items.get(position).getItemText());
				ImageView image = (ImageView) v.findViewById(R.id.menuIcon);
				image.setImageDrawable(items.get(position).getItemIcon());
				return v;
			}else{
				return convertView;
			}
			
		}
    }
    
    
    
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // If the nav drawer is open, hide action items related to the content view
        boolean drawerOpen = drawer.isDrawerOpen(navList);
        return super.onPrepareOptionsMenu(menu);
    }
    
    
    @Override
   	public boolean onOptionsItemSelected(MenuItem item) {
   		// TODO Auto-generated method stub
       	if(toggle.onOptionsItemSelected(item)){
       		return true;
       	}else{
       		return false;
       	}
   		//return super.onOptionsItemSelected(item);
   	}
    
    //END DRAWER
    
   

	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_who_question);
        
        inflateLayout();
        
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

