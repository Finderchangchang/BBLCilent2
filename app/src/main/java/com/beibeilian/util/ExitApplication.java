package com.beibeilian.util;

import android.app.Activity;
import android.app.Application;
import java.util.ArrayList;

/**
 * 
 * ��ȫ�˳�activity
 * 
 * @author DingCuilin
 * 
 */
public class ExitApplication {

	public static ArrayList<Activity> activities = new ArrayList<Activity>();;

	public static void addActivity(Activity activity) {
		activities.add(activity);
	}

	/**
	 * �Ӽ������Ƴ�һ��
	 * 
	 * @param activity
	 */
	public static void removeActivity(Activity activity) {
		activities.remove(activity);
	}

	public static void exit() {
		for (Activity activity : activities) {
			if (activity != null) {
				activity.finish();
			}
		}
	}

}
