package com.beibeilian.me;

import org.json.JSONObject;

import com.beibeilian.MainActivity;
import com.beibeilian.android.app.R;
import com.beibeilian.db.BBLDao;
import com.beibeilian.me.model.Version;
import com.beibeilian.service.CoreIMService;
import com.beibeilian.util.ExitApplication;
import com.beibeilian.util.HelperUtil;
import com.beibeilian.util.HttpConstantUtil;
import com.beibeilian.util.UpdateManager;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.view.View.OnClickListener;

public class MeSetActivity extends Activity {
	private Dialog dialog;
	private BBLDao dao;
	private RelativeLayout me_version;
	private RelativeLayout me_exit;
	private RelativeLayout me_about;
	private RelativeLayout me_feedback;
	private RelativeLayout me_remind;
	private RelativeLayout me_uppass;
	private RelativeLayout me_upemail;
	Intent intent;
	private Button btnBack;
	private TextView tv_remind;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.me_set);
		ExitApplication.addActivity(MeSetActivity.this);
		dao = new BBLDao(MeSetActivity.this, null, null, 1);
		me_version = (RelativeLayout) findViewById(R.id.me_version_id);
		me_exit = (RelativeLayout) findViewById(R.id.me_exit_id);
		me_about = (RelativeLayout) findViewById(R.id.me_aboutus_id);
		me_feedback = (RelativeLayout) findViewById(R.id.me_feedback_id);
		me_remind = (RelativeLayout) findViewById(R.id.me_remind_id);
		me_uppass = (RelativeLayout) findViewById(R.id.me_updatepassword_id);
		me_upemail = (RelativeLayout) findViewById(R.id.me_email_id);
		btnBack = (Button) findViewById(R.id.btnBack);
		tv_remind = (TextView) findViewById(R.id.version_remind_id);
		btnBack.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});
		me_exit.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				AlertDialog.Builder builder = new AlertDialog.Builder(MeSetActivity.this);
				builder.setTitle("��ܰ��ʾ");
				builder.setMessage("��,ȷ��Ҫ�˳���?").setCancelable(false)
						.setPositiveButton("ȷ��", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						dao.updateLoginState(dao.queryUserByNewTime().getUsername());
						HelperUtil.cancelAllNotificationManager(MeSetActivity.this);
						MeSetActivity.this.stopService(new Intent(MeSetActivity.this, CoreIMService.class));
						ExitApplication.exit();
						android.os.Process.killProcess(android.os.Process.myUid());
						System.exit(0);
					}
				}).setNegativeButton("ȡ��", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						dialog.cancel();
					}
				}).show();
			}
		});

		me_version.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				verionUpdate();
			}
		});

		me_about.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				intent = new Intent(MeSetActivity.this, MeAboutActivity.class);
				startActivity(intent);
			}
		});
		me_feedback.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				intent = new Intent(MeSetActivity.this, MeSetFeedBackActivity.class);
				startActivity(intent);
			}
		});
		me_remind.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				intent = new Intent(MeSetActivity.this, MeRemindActivity.class);
				startActivity(intent);
			}
		});
		me_upemail.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				intent = new Intent(MeSetActivity.this, MeSetEmailActivity.class);
				startActivity(intent);
			}
		});
		me_uppass.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				intent = new Intent(MeSetActivity.this, MeUpassActivity.class);
				startActivity(intent);
			}
		});

		if (dao.queryUserByNewTime().getUsername().equals(HelperUtil.getIMIE(MeSetActivity.this))) {
			me_uppass.setVisibility(View.GONE);
			me_upemail.setVisibility(View.GONE);
		}
		Version version = dao.queryVersion();

		if (version != null) {
			if (HelperUtil.flagISNoNull(version.getCode())) {
				if (HelperUtil.isUpdateVersion(HelperUtil.getVersionCode(MeSetActivity.this), version.getCode())) {
					tv_remind.setVisibility(View.VISIBLE);
				}
			}
		}
	}

	private void verionUpdate() {
		dialog = new Dialog(MeSetActivity.this, R.style.theme_dialog_alert);
		HelperUtil.customDialogShow(dialog, MeSetActivity.this, "���ڼ���°汾...");
		new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				try {
					JSONObject jsonObject = new JSONObject(HelperUtil.getRequest(HttpConstantUtil.FindVersion));
					handler.sendEmptyMessage(3);
					if (jsonObject.length() > 0) {
						Version version = new Version();
						version.setCode(jsonObject.optString("code"));
						version.setContent(jsonObject.optString("content"));
						version.setSize(jsonObject.optString("size"));
						version.setUrl(jsonObject.optString("url"));
						dao.updateVersion(version);
						if (HelperUtil.isUpdateVersion(HelperUtil.getVersionCode(MeSetActivity.this),
								version.getCode())) {
							handler.sendEmptyMessage(0);
						} else {
							handler.sendEmptyMessage(2);
						}
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
			// case 1:
			// if (dialog != null)
			// dialog.dismiss();
			// dao.updatePhoto(user.getUsername(), HttpConstantUtil.PreUrl
			// + "upload/" + filename, user.getNickname(),user.getSex());
			// HelperUtil.totastShow("�ϴ��ɹ�", MeActivity.this);
			// Bitmap bm = BitmapFactory.decodeFile(filepath);
			// headImg.setImageBitmap(bm);
			// break;
			case -1:
				if (dialog != null)
					dialog.dismiss();
				HelperUtil.totastShow("���������Ƿ���û��Ժ�����", MeSetActivity.this);
				break;
			case 2:
				HelperUtil.totastShow("��ǰ�������°汾", MeSetActivity.this);
				break;
			case 3:
				if (dialog != null)
					dialog.dismiss();
				break;
			// case 4:
			// fileuploadThread();
			// break;
			case 0:
				new UpdateManager(MeSetActivity.this).startUpdateInfo();
				break;
			default:
				break;
			}
		}
	};

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			finish();
		}
		return false;
	}
}
