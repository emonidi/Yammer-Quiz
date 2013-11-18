package com.virtual_affairs.yammer_quiz;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Application.ActivityLifecycleCallbacks;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.opengl.Visibility;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

public class SignInActivity extends Activity {
	
	private final String clientId = "xjuP1YM8JmrMVApI1eXzw";
	private final String clientSecret = "YLc4pGbEtuYUyDjcIu9M9K1ixKnnGlbQQtNtmRMA4";
	private WebView webView;
	private WebViewClient webViewClient;
	private String token = null;
	private Context context;
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        ActionBar actionBar = (ActionBar) getActionBar();
        actionBar.hide();
		setContentView(R.layout.web_view);
		context = getBaseContext();
		webView = (WebView) findViewById(R.id.webView1);
        webView.setVisibility(View.GONE);
		webView.getSettings().setJavaScriptEnabled(true);
		
		
		WebViewClient wvc = new webViewClient();
		
		
		webView.setWebViewClient(wvc);
		webView.loadUrl("https://www.yammer.com/dialog/oauth?client_id="+clientId+"&redirect_uri=step-one-activity://mywebsite.com&response_type=token");
		
		
	}
	
	
	private class webViewClient extends WebViewClient{
		
		@Override
		public void onPageStarted(WebView view, String url, Bitmap favicon) {
			// TODO Auto-generated method stub
			super.onPageStarted(view, url, favicon);
			if(url.contains("#access_token") && token==null){
				webView.setVisibility(View.GONE);
				String [] splitString = url.split("access_token=");
				token = splitString[1];
				InfoManager tManager = new InfoManager(getBaseContext(),"token");
				tManager.Save(token);
				Intent i = new Intent(getBaseContext(),Splash.class);
				startActivity(i);
				finish();
			}
		}

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            if(url.contains("xjuP1YM8JmrMVApI1eXzw")){
                webView.setVisibility(View.VISIBLE);
            }
        }
    }
	
}
