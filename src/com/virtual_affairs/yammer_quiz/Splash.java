package com.virtual_affairs.yammer_quiz;

import android.os.Bundle;
import android.os.Handler;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.graphics.Point;
import android.view.Display;
import android.view.Gravity;
import android.view.Menu;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class Splash extends Activity {
	TextView splashStatusText;
	Float originalSignInButtonY;
	ObjectAnimator objectAnimator;
	LinearLayout signInLayout;
	ImageView signInButton;
	String token;
	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash);
		
		signInLayout = (LinearLayout) findViewById(R.id.signInButtonLayout);
		
		signInButton = (ImageView) findViewById(R.id.signInButton);
		signInButton.setClickable(true);
		signInLayout.setAlpha(0);
		
		signInButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent i = new Intent(getBaseContext(),SignInActivity.class);
				startActivity(i);
				finish();
			}
		});
		
		ActionBar actionBar =  getActionBar();
		actionBar.hide();
		
		InfoManager tManager = new InfoManager(getBaseContext(),"token");
		token = tManager.Read();
		
		if(token == null){
			showSignInButton();
		}else{
			getPeople();
		}
				
		
	}
	
	private void getPeople(){
		splashStatusText = (TextView) findViewById(R.id.splashStatusText);
		splashStatusText.setText("Getting people...");
		LinearLayout splashStatusLayout= (LinearLayout) findViewById(R.id.splashStatusLayout);
		splashStatusLayout.setVisibility(View.VISIBLE);
		PeopleGetter peopleGetter = new PeopleGetter(getBaseContext());
		peopleGetter.execute(new String[] {token});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.splash, menu);
		return true;
	}
	

	@SuppressLint("NewApi")
	private void showSignInButton(){
		
		ObjectAnimator buttonAnim = ObjectAnimator.ofFloat(signInLayout, "alpha",1);
		buttonAnim.setDuration(400);
		buttonAnim.start();
	}
	
	
	
	
	
	
	
	

}
