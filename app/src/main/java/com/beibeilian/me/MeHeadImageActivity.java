package com.beibeilian.me;



import java.io.File;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;

import com.beibeilian.MainActivity;
import com.beibeilian.constant.BBLConstant;
import com.beibeilian.db.BBLDao;
import com.beibeilian.me.model.UserInfoEntiy;
import com.beibeilian.me.widget.ClipImageLayout;
import com.beibeilian.receiver.ReceiverConstant;
import com.beibeilian.util.HelperUtil;
import com.beibeilian.util.HttpConstantUtil;
import com.beibeilian.util.PublicConstant;
import com.beibeilian.util.fileupload.FileUploadMultipartPost;
import com.beibeilian.android.app.R;

import android.app.Activity;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class MeHeadImageActivity extends Activity {

	private Dialog dialog;

	private Button btnBack,btnUpload;

    private ClipImageLayout headimage;
    
    private BBLDao dao;
    
    private IntentFilter mIntentFilter;
    
    private FileUploadReceiver mFileUploadReceiver;
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.me_headimage);		
		btnBack = (Button) findViewById(R.id.btnBack);
		btnUpload=(Button)findViewById(R.id.btnUpload);
        headimage=(ClipImageLayout)findViewById(R.id.headimage_layout);
		dao=new BBLDao(MeHeadImageActivity.this,null,null,1);
		btnBack.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
				finish();
			}
		});
		btnUpload.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				uploadImageToServer();
			}
		});
		mFileUploadReceiver=new FileUploadReceiver();
		mIntentFilter=new IntentFilter();
		mIntentFilter.addAction(ReceiverConstant.FileUploadFaile_ACTION);
		mIntentFilter.addAction(ReceiverConstant.FileUploadSuccess_ACTION);
		registerReceiver(mFileUploadReceiver, mIntentFilter);
	    byte[] b=getIntent().getByteArrayExtra("bitmap");
		Bitmap bitmap = BitmapFactory.decodeByteArray(b, 0, b.length);
		Drawable drawable =new BitmapDrawable(bitmap);
		headimage.setImageDraw(MeHeadImageActivity.this,drawable);
	}
	
	private class FileUploadReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			if (intent.getAction().equals(
					ReceiverConstant.FileUploadSuccess_ACTION)) {
				handler.sendEmptyMessage(2);
			}
			if (intent.getAction().equals(
					ReceiverConstant.FileUploadFaile_ACTION)) {
				handler.sendEmptyMessage(-1);
			}
			
		}

	}

	/**
	 * ����ü�֮���ͼƬ���� �����ϴ�
	 * 
	 * @param picdata
	 */
	private static String filepath;
	private static String filename;

	private void uploadImageToServer() {
		dialog = new Dialog(MeHeadImageActivity.this, R.style.theme_dialog_alert);
		HelperUtil.customDialogShow(dialog, MeHeadImageActivity.this, "�����ϴ�...");
		new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				try {
					
					filename = dao.queryUserByNewTime().getUsername()
							+ HelperUtil.DateTime() + ".jpg";
					filepath = PublicConstant.FilePath + filename;
					Bitmap photo=headimage.clip();
					if (photo != null) {
						File filedir = new File(PublicConstant.FilePath);
						if (!filedir.exists()) filedir.mkdirs();
						File file = new File(filepath);
						file.createNewFile();
						FileOutputStream out = new FileOutputStream(file);
						if (photo
								.compress(Bitmap.CompressFormat.JPEG, 100, out)) {
							out.flush();
							out.close();
						}
						FileUploadMultipartPost fileUploadMultipartPost = new FileUploadMultipartPost(
								MeHeadImageActivity.this);
						fileUploadMultipartPost.execute(filepath);
					}
				} catch (Exception e) {
					// TODO: handle exception
					e.printStackTrace();
					handler.sendEmptyMessage(-1);
				}
			}
		}).start();

	}

	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case 1:
				if (dialog != null)
					dialog.dismiss();
				UserInfoEntiy user=dao.queryUserByNewTime();
				dao.updatePhoto(user.getUsername(),filename, user.getNickname(),user.getSex());
				HelperUtil.totastShow("�ϴ��ɹ�", MeHeadImageActivity.this);
				Intent intent=new Intent(ReceiverConstant.ME_HEADIMAGE_FILEPATH_ACTION);
				intent.putExtra("getfilepath",filepath);
				sendBroadcast(intent);
				finish();
//				Bitmap bm = BitmapFactory.decodeFile(filepath);
//				headImg.setImageBitmap(bm);
				break;
			case -1:
				if (dialog != null)
					dialog.dismiss();
				HelperUtil.totastShow("���������Ƿ���û��Ժ�����", MeHeadImageActivity.this);
				break;
			case 2:
				fileuploadThread();
				break;
			
			}}};
	
			private void fileuploadThread() {
				new Thread(new Runnable() {

					@Override
					public void run() {
						// TODO Auto-generated method stub
						try {
							Map<String, String> map = new HashMap<String, String>();
							map.put("username",dao.queryUserByNewTime().getUsername());
							map.put("photourl", filename);
							JSONObject jsonObject = new JSONObject(
									HelperUtil.postRequest(
											HttpConstantUtil.UpHeadImage, map));
							int result = jsonObject.getInt("result");
							if (result > 0) {
								handler.sendEmptyMessage(1);
							}
						} catch (Exception e) {
							handler.sendEmptyMessage(-1);
						}
					}
				}).start();
			}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if(mFileUploadReceiver!=null) unregisterReceiver(mFileUploadReceiver);
	};

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			finish();
		}
		return false;
	}

	
}
