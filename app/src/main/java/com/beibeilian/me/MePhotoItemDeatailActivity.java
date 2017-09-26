package com.beibeilian.me;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

import com.beibeilian.util.ExitApplication;
import com.beibeilian.util.HelperUtil;
import com.beibeilian.util.PublicConstant;
import com.beibeilian.android.app.R;
import com.beibeilian.constant.BBLConstant;

import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;

public class MePhotoItemDeatailActivity extends Activity{

	private Button btnBack;
	private ImageView imgView;
	private String photourl,savePath;
	private int FileLength;	//�ļ���С
	private ProgressDialog progressDialog;	//������
	private int DownedFileLength = 0;//��ʼ�ļ���С
	private LoadImageThread loadImageThread;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.me_photo_detail);
		ExitApplication.addActivity(MePhotoItemDeatailActivity.this);
		btnBack=(Button)findViewById(R.id.btnBack);
		imgView=(ImageView)findViewById(R.id.picid);
		photourl=getIntent().getStringExtra("photourl");
        btnBack.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});	
		if(HelperUtil.flagISNoNull(photourl)&&photourl.contains(".jpg"))
		{
		    try
		    {
		    	
		    	
			savePath=PublicConstant.FilePath+photourl.substring(photourl.lastIndexOf("/")+1);
			File file=new File(savePath);
			if(file.exists())
			{
				FileInputStream fs = null;
				BitmapFactory.Options bfOptions = new BitmapFactory.Options();
				bfOptions.inDither = false;
				bfOptions.inPurgeable = true;
				bfOptions.inInputShareable = true;
				bfOptions.inSampleSize = 2;
				bfOptions.inTempStorage = new byte[64 * 1024];
				fs = new FileInputStream(file);
				Bitmap bm = BitmapFactory.decodeFileDescriptor(fs.getFD(),
						null, bfOptions);
				Drawable drawable = new BitmapDrawable(bm);
				imgView.setBackgroundDrawable(drawable);
				return;
			}		
			progressDialog = new ProgressDialog(
					MePhotoItemDeatailActivity.this);
			progressDialog
					.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
			progressDialog.setMessage("�͹ۣ����Ͼ���...");
			progressDialog.setCancelable(true);
			progressDialog.setMax(100);
			progressDialog.show();
			if(loadImageThread!=null)
			{
				loadImageThread.interrupt();
			}	
			loadImageThread=new LoadImageThread();
			loadImageThread.start();
		    }
		    catch (Exception e) {
				// TODO: handle exception
			}
		}
		
	}
	
	
	/**
	 * �����ƶ�ͼƬ�߳� 
	 *
	 */
	private class LoadImageThread extends Thread {
		@Override
		public void run() {
			try {
				String savePathdir = PublicConstant.FilePath;
				File filedir = new File(savePathdir);
				if (!filedir.exists()) {
					filedir.mkdirs();
				}
				File file = new File(savePath);
				if (!file.exists()) {
					file.createNewFile();
				}
				
				String url = photourl;
				if(HelperUtil.flagISNoNull(url))
				{
				if (url.contains("http")) {
					url = url.substring(url.lastIndexOf("/") + 1);
				}
				url = BBLConstant.PHOTO_BEFORE_URL + url;
				}
				URLConnection cn = new URL(url).openConnection();
				cn.connect();
				InputStream stream = cn.getInputStream();
				int length=cn.getContentLength();//��ȡ�������ݵĳ��ȡ�����ָapk�ļ��Ĵ�С
				
				FileOutputStream fos = new FileOutputStream(file);// ��apk�ļ������sd������ʱ��û�б��浽sd��
				int count = 0;// ���ڻ�ȡ���ص��ٶ�
				byte buf[] = new byte[1024];// ����byte�ֽ�
				// ѭ�����൱���û������ʼ��ť�ˣ����ڿ�ʼ��apk������д�뵽�ļ���
				do {
					int numread = stream.read(buf);// ÿ�ζ�ȡ1024��byte�ֽ�
					count += numread;
					DownedFileLength= (int) (((float) count / length) * 100);// ��������%����ʾ
				    handler.sendEmptyMessage(1);
					if (numread <= 0) {
						// �������֪ͨ��װ
						 handler.sendEmptyMessage(2);
						break;
					}
					fos.write(buf, 0, numread);// apk������д�뵽�ļ���
				} while (true);// ���ȡ����ֹͣ����

				fos.close();
				stream.close();
					   
			} catch (Exception e) {
				e.printStackTrace();
			   System.out.println("���ر���"+e);
				File file = new File(savePath);
				if (file.exists()) {
					file.delete();
				}
				  handler.sendEmptyMessage(3);
			}
		}
	}

	/**
	 * ����hander��Ϣ
	 */
	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			if (!Thread.currentThread().isInterrupted()) {
				switch (msg.what) {
				case 1:
					progressDialog.setProgress(DownedFileLength);					
					break;
				case 2:
					if(progressDialog!=null)
					{
					progressDialog.dismiss();
					}
					try
					{
					File file = new File(savePath);
					FileInputStream fs = null;
					BitmapFactory.Options bfOptions = new BitmapFactory.Options();
					bfOptions.inDither = false;
					bfOptions.inPurgeable = true;
					bfOptions.inInputShareable = true;
					bfOptions.inSampleSize = 2;
					bfOptions.inTempStorage = new byte[64 * 1024];
					fs = new FileInputStream(file);
					Bitmap bm = BitmapFactory.decodeFileDescriptor(fs.getFD(),
							null, bfOptions);
					Drawable drawable = new BitmapDrawable(bm);
					imgView.setBackgroundDrawable(drawable);
					}
					catch (Exception e) {
						// TODO: handle exception
					}
					break;
				case 3:
					if(progressDialog!=null)
					{
					progressDialog.dismiss();
					}
					
					break;
				default:
					break;
				}
			}
		}

	};
    @Override
	public void onDestroy()
	{
		super.onDestroy();
		if(loadImageThread!=null)
		{
			loadImageThread.interrupt();
		}		
	}
    @Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			finish();
		}
		return false;
	}
}
