package com.beibeilian.util;


import android.content.Context;

public class SPSUtils {

	public static void updateDZH(Context mContext,String value)
	{
		PreferencesUtils.putString(mContext,"DZH",value);
	}
	
	public static String getDZH(Context mContext)
	{
		return PreferencesUtils.getString(mContext, "DZH");
	}
	
	public static void updateAL(Context mContext,String value)
	{
		PreferencesUtils.putString(mContext,"AL",value);
	}
	
	public static String getAL(Context mContext)
	{
		return PreferencesUtils.getString(mContext, "AL");
	}
	
	public static void updatePHOTO(Context mContext,String value)
	{
		PreferencesUtils.putString(mContext,"PHOTO",value);
	}
	
	public static String getPHOTO(Context mContext)
	{
		return PreferencesUtils.getString(mContext, "PHOTO");
	}
	
	public static void updateSMS(Context mContext,String value)
	{
		PreferencesUtils.putString(mContext,"SMS",value);
	}
	
	public static String getSMS(Context mContext)
	{
		return PreferencesUtils.getString(mContext, "SMS");
	}
	public static void updateXQ(Context mContext,String value)
	{
		PreferencesUtils.putString(mContext,"XQ",value);
	}
	
	public static String getXQ(Context mContext)
	{
		return PreferencesUtils.getString(mContext, "XQ");
	}
	
	public static String getVip(Context mContext)
	{
		return PreferencesUtils.getString(mContext, "vip");
	}
	
}
