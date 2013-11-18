package com.virtual_affairs.yammer_quiz;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import android.content.Context;
import android.graphics.Region.Op;
import android.util.Log;

public class InfoManager {
	String token = null;
	String FILENAME = "token";
	FileOutputStream fos;
	FileInputStream fis;
	Context ctx;
	
	
	public InfoManager(Context baseContext,String filename) {
		this.ctx = baseContext;
		this.FILENAME = filename;
	}




    public String Read(){
		try {
			token = "";
			fis = ctx.openFileInput(FILENAME);
			InputStreamReader isr = new InputStreamReader(fis);
			BufferedReader buf = new BufferedReader(isr);
			token = buf.readLine().toString();
			fis.close();
			return token;
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	public void Save(String token){
		try {
			fos = ctx.openFileOutput(FILENAME, Context.MODE_PRIVATE);
			fos.write(token.getBytes());
			fos.close();
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}
