package com.outmao.xcprojector.util;


import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.text.TextUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;


//保存数据utils
public class SharepreferencesUtils {

	private Context mContext;
	private SharedPreferences sharedpreferences;
	private Editor editor;

	private static SharepreferencesUtils _shareInstance;
	
	public synchronized static SharepreferencesUtils getShareInstance(){
		return _shareInstance;
	}

	public synchronized static void initSharepreferencesUtils(Context applicationContext){
		if (_shareInstance == null) {
			_shareInstance = new SharepreferencesUtils(applicationContext);
		}
	}

	private SharepreferencesUtils(Context context) {
		this.mContext = context;
		sharedpreferences = mContext.getSharedPreferences("utils",
				Context.MODE_PRIVATE);
		editor = sharedpreferences.edit();
	}

	public void remove(String key){
		editor.remove(key);
		editor.commit();
	}

	public <T> T getObject(String key){
		String value = getString(key);
		if (value == null) {
			return null;
		}
		Gson gson = new Gson();
		try {
			return gson.fromJson(value,new TypeToken<T>(){}.getType());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public void putObject(String key, Object obj){
		if (obj == null) {
			putString(key, "");
			return;
		}
		Gson gson = new Gson();
		String json = gson.toJson(obj);
		putString(key, json);
	}
	
	public void putString(String key, String value){
		editor.putString(key, value);
		editor.commit();
	}
	
	public String getString(String key){
		return sharedpreferences.getString(key, null);
	}
	
	public void putInt(String key, int value){
		editor.putInt(key, value);
		editor.commit();
	}
	
	public int getInt(String key){
		return sharedpreferences.getInt(key, 0);
	}
	
	public int getInt(String key, int defaultValue){
		return sharedpreferences.getInt(key, defaultValue);
	}
	
	public void putBoolean(String key, boolean value){
		editor.putBoolean(key, value);
		editor.commit();
	}
	
	public boolean getBoolean(String key){
		return sharedpreferences.getBoolean(key, false);
	}

	public boolean getBoolean(String key, boolean defaulValue){
		return sharedpreferences.getBoolean(key, defaulValue);
	}
	
	public void putLong(String key, long value){
		editor.putLong(key, value);
		editor.commit();
	}
	
	public long getLong(String key, long defaultValue){
		return sharedpreferences.getLong(key, defaultValue);
	}

	public void setGifImg(String gifImg){
		if(TextUtils.isEmpty(gifImg)){
			return;
		}
		putString("GIF_IMG",gifImg);
	}

	public String getGifImg(){
		return getString("GIF_IMG");
	}

	//保存Chat Token
	public void setChatToken(String chattoken){
		if(TextUtils.isEmpty(chattoken)){
			return;
		}
		putString("CHAT_TOKEN",chattoken);
	}
	public String getChatToken(){
		return getString("CHAT_TOKEN");
	}





}
