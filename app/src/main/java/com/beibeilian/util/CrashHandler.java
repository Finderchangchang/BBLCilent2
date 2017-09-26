package com.beibeilian.util;

import java.lang.Thread.UncaughtExceptionHandler;
import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.reflect.Field;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.beibeilian.MainActivity;
import com.beibeilian.WelcomeActivity;
import com.beibeilian.util.fileupload.CrashFileUploadMultipartPost;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Build;
import android.os.Environment;
import android.os.Looper;
import android.util.Log;

/**
 * ��ȡȫ���쳣��
 * @author ��ƽԭ
 *
 */
public class CrashHandler implements UncaughtExceptionHandler {

	public static final String TAG = "CrashHandler";

	/**
	 * ϵͳĬ�ϵ�UncaughtException������
	 */
	private Thread.UncaughtExceptionHandler mDefaultHandler;
	/**
	 * CrashHandlerʵ��
	 */
	private static CrashHandler INSTANCE = new CrashHandler();
	/**
	 * �����Context����
	 */
	private Context mContext;
	/**
	 * �����洢�豸��Ϣ���쳣��Ϣ
	 */
	private Map<String, String> infos = new HashMap<String, String>();

	/**
	 * ���ڸ�ʽ������,��Ϊ��־�ļ�����һ����
	 */
	private DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");

	private StringBuffer sb;
	
	
	
	/** ��ֻ֤��һ��CrashHandlerʵ�� */
	private CrashHandler() {
	}

	/** ��ȡCrashHandlerʵ�� ,����ģʽ */
	public static CrashHandler getInstance() {
		return INSTANCE;
	}

	/**
	 * ��ʼ��
	 * 
	 * @param context
	 */
	public void init(Context context) {

		mContext = context;
		mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
		// ���ø�CrashHandlerΪ�����Ĭ�ϴ�����
		Thread.setDefaultUncaughtExceptionHandler(this);
	}


	/**
	 * ��UncaughtException����ʱ��ת��ú���������
	 */
	@Override
	public void uncaughtException(Thread thread, Throwable ex) {
		if (!handleException(ex) && mDefaultHandler != null) {
			// ����û�û�д�������ϵͳĬ�ϵ��쳣������������
			mDefaultHandler.uncaughtException(thread, ex);
		} else {
//			try {
//				Thread.sleep(3000);
//			} catch (InterruptedException e) {
//				Log.e(TAG, "error : ", e);
//			}
			   
			    Intent intent = new Intent(mContext,MainActivity.class);  
	            PendingIntent restartIntent = PendingIntent.getActivity(    
	                    mContext, 0, intent,    
	                    Intent.FLAG_ACTIVITY_NEW_TASK);                                                                      
	            AlarmManager mgr = (AlarmManager)mContext.getSystemService(Context.ALARM_SERVICE);    
	            mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 500,    
	                    restartIntent); // 1���Ӻ�����Ӧ��   
	            ExitApplication.exit();
	            System.exit(0);
				android.os.Process.killProcess(android.os.Process.myUid());
		}
	}
	/**
	 * �Զ��������,�ռ�������Ϣ ���ʹ��󱨸�Ȳ������ڴ����.
	 * 
	 * @param ex
	 * @return true:��������˸��쳣��Ϣ;���򷵻�false.
	 */
	private boolean handleException(Throwable ex) {
		if (ex == null) {
			return false;
		}
		// ʹ��Toast����ʾ�쳣��Ϣ
		new Thread() {
			@Override
			public void run() {
				Looper.prepare();
				Looper.loop();
			}
		}.start();
		// �ռ��豸������Ϣ
		collectDeviceInfo(mContext);
		// ������־�ļ�
		saveCrashInfo2File(ex);
		System.out.println("�ϴ�ʧ��11==========");
		return true;
	}

	/**
	 * �ռ��豸������Ϣ
	 * 
	 * @param ctx
	 */
	public void collectDeviceInfo(Context ctx) {
		try {
			PackageManager pm = ctx.getPackageManager();
			PackageInfo pi = pm.getPackageInfo(ctx.getPackageName(),
					PackageManager.GET_ACTIVITIES);
			if (pi != null) {
				String versionName = pi.versionName == null ? "null"
						: pi.versionName;
				String versionCode = pi.versionCode + "";
				infos.put("versionName", versionName);
				infos.put("versionCode", versionCode);
			}
		} catch (NameNotFoundException e) {
			Log.e(TAG, "an error occured when collect package info", e);
		}
		Field[] fields = Build.class.getDeclaredFields();
		for (Field field : fields) {
			try {
				field.setAccessible(true);
				infos.put(field.getName(), field.get(null).toString());
				Log.d(TAG, field.getName() + " : " + field.get(null));
			} catch (Exception e) {
				Log.e(TAG, "an error occured when collect crash info", e);
			}
		}
	}

	/**
	 * ���������Ϣ���ļ���
	 * 
	 * @param ex
	 * @return �����ļ�����,���ڽ��ļ����͵�������
	 */
	private String saveCrashInfo2File(Throwable ex) {

		sb = getThrowableString(ex);
		try {
			long timestamp = System.currentTimeMillis();
			String time = formatter.format(new Date());
			final String fileName = "crash-" + time + "-" + timestamp + ".log";
			if (Environment.getExternalStorageState().equals(
					Environment.MEDIA_MOUNTED)) {
				final String path = "/sdcard/beibeilian/crash/";
				File dir = new File(path);
				if (!dir.exists()) {
					dir.mkdirs();
				}
				FileOutputStream fos = new FileOutputStream(path + fileName);
				fos.write(sb.toString().getBytes());
				fos.close();
				
				
				new CrashFileUploadMultipartPost(mContext).execute(path+fileName);
				
//				new Thread(new Runnable() {
//					
//					@Override
//					public void run() {
//						// TODO Auto-generated method stub
//						FileUploadPost fileUploadPost=new FileUploadPost(mContext);
//						fileUploadPost.execute(path+fileName);
//					}
//				}).start();
				
//				Integer call = new UploadFileUtil(path+fileName,HttpConstantUtil.FILEUpload).call();
//				if(call == 1){
//					new File(path+fileName).delete();
//				}
				
			}
			return fileName;
		} catch (Exception e) {
//			System.out.println("�ϴ�ʧ��=========="+e);
			Log.e(TAG, "an error occured while writing file...", e);
		}
		return null;
	}

	private StringBuffer getThrowableString(Throwable ex) {

		StringBuffer sb = new StringBuffer();
		try
		{
		for (Map.Entry<String, String> entry : infos.entrySet()) {
			String key = entry.getKey();
			String value = entry.getValue();
			sb.append(key + "=" + value + "\n");
		}

		Writer writer = new StringWriter();
		PrintWriter printWriter = new PrintWriter(writer);
		ex.printStackTrace(printWriter);
		Throwable cause = ex.getCause();
		while (cause != null) {
			cause.printStackTrace(printWriter);
			cause = cause.getCause();
		}
		printWriter.close();
		String result = writer.toString();
		sb.append(result);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return sb;
	}

}
