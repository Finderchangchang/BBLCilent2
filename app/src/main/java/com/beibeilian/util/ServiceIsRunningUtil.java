package com.beibeilian.util;

import java.util.Iterator;
import java.util.List;

import android.app.ActivityManager;
import android.content.Context;
/**
 * �ж�service�Ƿ�������
 * @author Administrator
 *
 */
public class ServiceIsRunningUtil {
	public static boolean isServiceRunning(Context mContext, String className) {
		boolean serviceRunning = false;
		ActivityManager am = (ActivityManager)mContext.getSystemService(mContext.ACTIVITY_SERVICE);
		List<ActivityManager.RunningServiceInfo> l = am.getRunningServices(Integer.MAX_VALUE);
		Iterator<ActivityManager.RunningServiceInfo> i = l.iterator();
		while (i.hasNext()) {
		ActivityManager.RunningServiceInfo runningServiceInfo = (ActivityManager.RunningServiceInfo) i.next();
		if(runningServiceInfo.service.getClassName().equals(className)){
		serviceRunning = true;
		}
		}
		return serviceRunning;
	}
}
