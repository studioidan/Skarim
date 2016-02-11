package com.studioidan.skarim.data;

import java.lang.reflect.Type;
import com.google.gson.Gson;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;


public class CPM {

	public static Editor editor;

	public static final String iphoneDownloadLinkK = "iphoneDownloadLink";

	public static boolean putLong(String Key,long value,Context con)
	{
		editor = PreferenceManager.getDefaultSharedPreferences(con).edit();
		editor.putLong(Key, value);
		return editor.commit();
	}
	public static long getLong(String key,long def,Context con) {
		return PreferenceManager.getDefaultSharedPreferences(con).getLong(key, def);
	}
	
	public static boolean putInt(String Key,int value,Context con)
	{
		editor = PreferenceManager.getDefaultSharedPreferences(con).edit();
		editor.putInt(Key, value);
		return editor.commit();
	}
	public static Integer getInt(String key,int def,Context con) {
		return PreferenceManager.getDefaultSharedPreferences(con).getInt(key, def);
	}

	public static boolean putFloat(String Key,float value,Context con)
	{
		editor = PreferenceManager.getDefaultSharedPreferences(con).edit();
		editor.putFloat(Key, value);
		return editor.commit();
	}
	public static Float getFloat(String key,float def,Context con) {
		return PreferenceManager.getDefaultSharedPreferences(con).getFloat(key, def);
	}

	public static boolean putBoolean(String Key,boolean value,Context con)
	{
		editor = PreferenceManager.getDefaultSharedPreferences(con).edit();
		editor.putBoolean(Key, value);
		return editor.commit();
	}
	public static Boolean getBoolean(String key,Boolean def,Context con) {
		return PreferenceManager.getDefaultSharedPreferences(con).getBoolean(key, def);
	}

	public static boolean putString(String Key,String value,Context con)
	{
		editor = PreferenceManager.getDefaultSharedPreferences(con).edit();
		editor.putString(Key, value);
		return editor.commit();
	}
	public static String getString(String key,String def,Context con) {
		return PreferenceManager.getDefaultSharedPreferences(con).getString(key, def);
	}

	public static Object getArrayObject(String key,Type type,Context con)
	{
		// Type type = new TypeToken<List<Student>>(){}.getType();
		SharedPreferences appSharedPrefs = PreferenceManager.getDefaultSharedPreferences(con);
		String json = appSharedPrefs.getString(key, "");
		if(json.equals(""))
			return null;
		Gson gson = new Gson();
		Object answer = gson.fromJson(json,type);
		return answer;
	}
	public static <T> T getObject(String key, Class<T> clazz,Context con)
	{
		//call new Student().getClass() in the second parameter
		String json = PreferenceManager.getDefaultSharedPreferences(con).getString(key, "");
		if(json.equals(""))
			return null;
		Gson gson = new Gson();
		return (T) gson.fromJson(json, clazz);
	}
	public static boolean putObject(String Key,Object obj,Context con)
	{
		Gson gson = new Gson();
		String json = gson.toJson(obj);
		Editor editor = PreferenceManager.getDefaultSharedPreferences(con).edit();
		editor.putString(Key, json);
		Boolean answer = editor.commit();
		return answer;
	}
}
