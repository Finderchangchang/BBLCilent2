package com.beibeilian.privateletter;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;

import com.beibeilian.db.BBLDao;
import com.beibeilian.me.MeMemberActivity;
import com.beibeilian.receiver.ReceiverConstant;
import com.beibeilian.util.ExitApplication;
import com.beibeilian.util.HelperUtil;
import com.beibeilian.util.HttpConstantUtil;
import com.beibeilian.util.PreferencesUtils;
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
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;

public class ChatImageViewUploadActivity extends Activity {

	private ImageView imageview;
	private Button btnUpload;
	private Button btnCancel;
	private String filePath = null,toUser=null;
	private Dialog dialog;
	private BBLDao dao;

	private IntentFilter intentFilter;

	private String newfilepath;
	private FileUploadReceiver fileUploadReceiver;
	private String username;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.img_preview);
		ExitApplication.addActivity(this);
		fileUploadReceiver = new FileUploadReceiver();
		intentFilter = new IntentFilter();
		intentFilter.addAction(ReceiverConstant.FileUploadFaile_ACTION);
		intentFilter.addAction(ReceiverConstant.FileUploadSuccess_ACTION);
		registerReceiver(fileUploadReceiver, intentFilter);
		dialog = new Dialog(ChatImageViewUploadActivity.this, R.style.theme_dialog_alert);
		dao = new BBLDao(ChatImageViewUploadActivity.this, null, null, 1);
        username=dao.queryUserByNewTime().getUsername();
		imageview = (ImageView) findViewById(R.id.img_show);
		btnCancel = (Button) findViewById(R.id.btn_cancel);
		btnUpload = (Button) findViewById(R.id.btn_upload);
		btnUpload.setText("发送");
		btnUpload.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
			  String vip = PreferencesUtils.getString(ChatImageViewUploadActivity.this, "vip");
				if (TextUtils.isEmpty(vip) || !vip.equals("1")) {
					boolean result = dao.queryChatISSend3(username);
					if (result) {
						startActivity(new Intent(ChatImageViewUploadActivity.this, MeMemberActivity.class));
						return;
					}
				}
				HelperUtil.customDialogShow(dialog, ChatImageViewUploadActivity.this, "正在发送...");
				upload();
			}
		});
		btnCancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});
		filePath = getIntent().getStringExtra("filePath");
		toUser = getIntent().getStringExtra("toUser");

		if (filePath != null) {
			try {
				File file = new File(filePath);
				FileInputStream fs = null;
				BitmapFactory.Options bfOptions = new BitmapFactory.Options();
				bfOptions.inDither = false;
				bfOptions.inPurgeable = true;
				bfOptions.inInputShareable = true;
				bfOptions.inSampleSize = 2;
				bfOptions.inTempStorage = new byte[64 * 1024];
				fs = new FileInputStream(file);
				Bitmap bm = BitmapFactory.decodeFileDescriptor(fs.getFD(), null, bfOptions);
				Drawable drawable = new BitmapDrawable(bm);
				imageview.setBackgroundDrawable(drawable);
			} catch (Exception e) {

			}
		}
	}

	private class FileUploadReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			if (intent.getAction().equals(ReceiverConstant.FileUploadSuccess_ACTION)) {
				handler.sendEmptyMessage(1);
			}
			if (intent.getAction().equals(ReceiverConstant.FileUploadFaile_ACTION)) {
				handler.sendEmptyMessage(-1);
			}
		}

	}

	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case 1:
				HelperUtil.totastShow("发送成功", ChatImageViewUploadActivity.this);
				Intent intent = new Intent(ReceiverConstant.MESSAGE_SEND_ACTION);
				intent.putExtra("toID", toUser);
				intent.putExtra("nickname",  dao.queryUserByNewTime().getNickname());
				intent.putExtra("message", newfilepath.substring(newfilepath.lastIndexOf("/") + 1));
				sendBroadcast(intent);
				finish();
				break;
			case -1:
				if (dialog != null) {
					dialog.dismiss();
				}
				HelperUtil.totastShow("请检查网络是否可用或稍候再试", ChatImageViewUploadActivity.this);
				break;
			default:
				break;
			}
		}
	};

	
	private void upload() {
		new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				try {
					File file = new File(filePath);
					FileInputStream fs = null;
					// TODO
					BitmapFactory.Options bfOptions = new BitmapFactory.Options();
					bfOptions.inDither = false;
					bfOptions.inPurgeable = true;
					bfOptions.inInputShareable = true;
					bfOptions.inSampleSize = 0;
					bfOptions.inTempStorage = new byte[64 * 1024];
					try {
						fs = new FileInputStream(file);
						Bitmap bm = BitmapFactory.decodeFileDescriptor(fs.getFD(), null, bfOptions);
						// Bitmap newbm = HelperUtil.compressImage(bm);

						newfilepath = PublicConstant.FilePath + dao.queryUserByNewTime().getUsername()
								+ HelperUtil.DateTime() + ".jpg";
						File filedir = new File(PublicConstant.FilePath);
						if (!filedir.exists()) {
							filedir.mkdirs();
						}
						File newfile = new File(newfilepath);
						if (!newfile.exists()) {
							newfile.createNewFile();
						}
						FileOutputStream out = new FileOutputStream(newfile);
						if (bm.compress(Bitmap.CompressFormat.JPEG, 100, out)) {
							out.flush();
							out.close();
						}
						new FileUploadMultipartPost(ChatImageViewUploadActivity.this).execute(newfilepath);

					} catch (Exception e) {
						// TODO: handle exception
						ChatImageViewUploadActivity.this.runOnUiThread(new Runnable() {
							@Override
							public void run() {
								// TODO Auto-generated method stub
								HelperUtil.totastShow("请检查网络是否可用或稍候再试", ChatImageViewUploadActivity.this);
								if (dialog != null) {
									dialog.dismiss();
								}
							}
						});
					}
				} catch (Exception e1) {
					// TODO: handle exception
					ChatImageViewUploadActivity.this.runOnUiThread(new Runnable() {
						@Override
						public void run() {
							// TODO Auto-generated method stub
							HelperUtil.totastShow("请检查网络是否可用或稍候再试", ChatImageViewUploadActivity.this);
							if (dialog != null) {
								dialog.dismiss();
							}
						}
					});
				}
			}

		}).start();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		if (fileUploadReceiver != null) {
			unregisterReceiver(fileUploadReceiver);
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
