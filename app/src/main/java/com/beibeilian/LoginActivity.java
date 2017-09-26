package com.beibeilian;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import com.beibeilian.constant.BBLConstant;
import com.beibeilian.db.BBLDao;
import com.beibeilian.me.MeActivity;
import com.beibeilian.me.MeSetActivity;
import com.beibeilian.me.model.UserInfoEntiy;
import com.beibeilian.me.model.Version;
import com.beibeilian.model.PayRule;
import com.beibeilian.service.CoreIMService;
import com.beibeilian.util.Base64Util;
import com.beibeilian.util.CryptoTools;
import com.beibeilian.util.ExitApplication;
import com.beibeilian.util.HelperUtil;
import com.beibeilian.util.HttpConstantUtil;
import com.beibeilian.util.PublicConstant;
import com.beibeilian.util.StringEscapeUtils;
import com.beibeilian.android.app.R;

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
import android.provider.SyncStateContract.Helpers;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class LoginActivity extends Activity implements OnClickListener {
	private TextView regsiter_view;
	private EditText user;
	private EditText pass;
	private Button btn_login, btn_quick_login;
	private Intent intent;
	private BBLDao dao;
	private String username;
	private String password;
	private Dialog dialog;
	private LoginThread loginThread;
	private TextView tv_findpass;
	private TextView tv_clearuser;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login);
		dialog = new Dialog(LoginActivity.this, R.style.theme_dialog_alert);
		regsiter_view = (TextView) findViewById(R.id.login_regsiterid);
		user = (EditText) findViewById(R.id.emailid);
		pass = (EditText) findViewById(R.id.loginpassid);
		btn_login = (Button) findViewById(R.id.btn_login_id);
		btn_quick_login = (Button) findViewById(R.id.btn_quick_login_id);
		tv_findpass = (TextView) findViewById(R.id.find_pass_id);
		tv_clearuser = (TextView) findViewById(R.id.clearusername_id);
		regsiter_view.setOnClickListener(this);
		btn_login.setOnClickListener(this);
		btn_quick_login.setOnClickListener(this);
		tv_findpass.setOnClickListener(this);
		tv_clearuser.setOnClickListener(this);
		dao = new BBLDao(LoginActivity.this, null, null, 1);
		UserInfoEntiy model = dao.queryUserByNewTime();
		if (model != null) {
			user.setText(model.getUsername());
			pass.setText(model.getPassword());
			if (model.getUsername() != null) {
				tv_clearuser.setVisibility(View.VISIBLE);
				if (model.getUsername().equals(HelperUtil.getIMIE(LoginActivity.this))) {
					tv_findpass.setVisibility(View.GONE);
				}
			}
		}
		HelperUtil.uploadChannel(LoginActivity.this);
		 Button btnback=(Button) findViewById(R.id.btnBack);
	       btnback.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});
	}
	
	

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.login_regsiterid:
			intent = new Intent(LoginActivity.this, RegsiterOneActivity.class);
			startActivity(intent);
			// finish();
			break;
		case R.id.btn_login_id:
			username = user.getText().toString().trim();
			password = pass.getText().toString().trim();
			if (username.length() == 0) {
				HelperUtil.totastShow("�ʺŲ���Ϊ��", LoginActivity.this);
				return;
			}
			if (password.length() == 0) {
				HelperUtil.totastShow("���벻��Ϊ��", LoginActivity.this);
				return;
			}
			HelperUtil.customDialogShow(dialog, LoginActivity.this, "���ڵ�¼...");
			if (loginThread != null) {
				loginThread.interrupt();
			}
			loginThread = new LoginThread();
			loginThread.start();
			break;
		case R.id.btn_quick_login_id:
			String imie = HelperUtil.getIMIE(LoginActivity.this);
			if (HelperUtil.flagISNoNull(imie)) {
				quickLogin(imie);
			} else {
				HelperUtil.totastShow("�޷���⵽�豸ID������һ��Ȩ�����ã���ʹ���˺ŵ�¼", getApplicationContext());
			}
			break;
		case R.id.find_pass_id:
			intent = new Intent(LoginActivity.this, FindPassActivity.class);
			startActivity(intent);
			// finish();
			break;
		case R.id.clearusername_id:
			AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
			builder.setTitle("��ܰ��ʾ");
			builder.setMessage("��ȷ��Ҫ��յ�¼���ʺ���Ϣ��?").setCancelable(false)
					.setPositiveButton("ȷ��", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							dao.delUserinfo();
							HelperUtil.totastShow("��ճɹ�", LoginActivity.this);
							tv_clearuser.setVisibility(View.GONE);
							user.setText("");
							pass.setText("");
						}
					}).setNegativeButton("ȡ��", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							dialog.cancel();
						}
					}).show();
			break;
		default:
			break;
		}
	}

	private class LoginThread extends Thread {

		@Override
		public void run() {
			try {
				Map<String, String> map = new HashMap<String, String>();
				map.put("username", username);
				String imie = HelperUtil.getIMIE(LoginActivity.this);
				if (username.equals(imie)) {
					String newpass = null;
					byte[] temp = CryptoTools.des3EncodeECB(password.getBytes("UTF-8"));
					newpass = new String(Base64Util.encode(temp).getBytes(), "UTF-8");
					map.put("password", newpass);
				} else {
					map.put("password", HelperUtil.MD5(password));
				}
				JSONObject jsonObject = new JSONObject(HelperUtil.postRequest(HttpConstantUtil.Login, map));
				if (jsonObject.length() > 0) {
					String result = jsonObject.optString("result");
					if (HelperUtil.flagISNoNull(result)) {
						if (result.equals("1")) {
							String sex = jsonObject.optString("sex");
							String nickname = jsonObject.optString("nickname");
							String level = jsonObject.optString("level");
							String photo = jsonObject.optString("photo");
							dao.initUser(username, nickname, password, level, sex);
							if (HelperUtil.flagISNoNull(photo))
								dao.updatePhoto(username, photo, nickname, sex);
							handler.sendEmptyMessage(1);
						} else if (result.equals("0")) {
							handler.sendEmptyMessage(0);
						} else {
							handler.sendEmptyMessage(2);
						}

					}
				} else {
					handler.sendEmptyMessage(2);
				}
			} catch (Exception e) {
				e.printStackTrace();
				handler.sendEmptyMessage(-1);
			}
		}
	}

	private String getDesspass = "";
	private String getRandromspass = "";

	private void quickLogin(final String imie) {
		HelperUtil.customDialogShow(dialog, LoginActivity.this, "���ڵ�¼...");
		new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				try {
					Map<String, String> map = new HashMap<String, String>();
					map.put("imie", imie);
					map.put("cversion", HelperUtil.getVersionCode(LoginActivity.this));
					map.put("ip", HelperUtil.getLocalIpAddress());
					map.put("channel",HelperUtil.getChannelId(LoginActivity.this) );
					map.put("appid",HelperUtil.getAPPlId(LoginActivity.this));
					JSONObject jsonObject = new JSONObject(HelperUtil.postRequest(HttpConstantUtil.QuickLogin, map));
					if (jsonObject.optInt("result") == 1) {
						getDesspass = jsonObject.optString("despass");// ��⵽��ע�᷵������
						String sex = jsonObject.optString("sex");
						String nickname = jsonObject.optString("nickname");
						String level = jsonObject.optString("level");
						String photo = jsonObject.optString("photo");
						byte[] tmpt;
						tmpt = Base64Util.decode(getDesspass);
						tmpt = CryptoTools.ees3DecodeECB(tmpt);
						String strpass = new String(tmpt, "UTF-8");
						dao.initUser(imie, nickname, StringEscapeUtils.escapeSql(strpass), level, sex);
						if (HelperUtil.flagISNoNull(photo))
							dao.updatePhoto(username,photo, nickname, sex);
						handler.sendEmptyMessage(3);
					} else {
						getRandromspass = jsonObject.optString("randompass");// ��ע�᷵������
						byte[] tmpt;
						tmpt = Base64Util.decode(getRandromspass);
						tmpt = CryptoTools.ees3DecodeECB(tmpt);
						String strpass = new String(tmpt, "UTF-8");
						String newpass = StringEscapeUtils.escapeSql(strpass);
						dao.initUser(imie, "˧��", newpass, "1", "��");
						dao.updatePhoto(imie, "", "˧��", "��");
						handler.sendEmptyMessage(4);
					}

				} catch (Exception e) {
					e.printStackTrace();
					handler.sendEmptyMessage(-1);
				}
			}
		}).start();

	}
	
	
	

	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {

			switch (msg.what) {

			case -1:
				if (dialog != null) {
					dialog.dismiss();
				}
				HelperUtil.totastShow(PublicConstant.ToastCatch, LoginActivity.this);
				break;
			case 0:
				if (dialog != null) {
					dialog.dismiss();
				}
				HelperUtil.totastShow("�ʺŻ��������", LoginActivity.this);
				break;
			case 1:
				if (dialog != null) {
					dialog.dismiss();
				}
				startService(new Intent(LoginActivity.this, CoreIMService.class));
				intent = new Intent(LoginActivity.this, MainActivity.class);
				startActivity(intent);
				finish();
				break;
			case 2:
				if (dialog != null) {
					dialog.dismiss();
				}
				HelperUtil.totastShow("���ʺŲ�����", LoginActivity.this);
				break;
			case 3:
				if (dialog != null) {
					dialog.dismiss();
				}
				startService(new Intent(LoginActivity.this, CoreIMService.class));
				intent = new Intent(LoginActivity.this, MainActivity.class);
				startActivity(intent);
				finish();
				break;
			case 4:
				if (dialog != null) {
					dialog.dismiss();
				}
				HelperUtil.totastShow("��¼�ɹ�,�뾡�����ƻ�������", LoginActivity.this);
				startService(new Intent(LoginActivity.this, CoreIMService.class));
				intent = new Intent(LoginActivity.this, MainActivity.class);
				startActivity(intent);
				finish();
				break;
			default:
				break;
			}

		}

	};

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
//			if (loginThread != null) {
//				loginThread.interrupt();
//			}
//			ExitApplication.exit();
			finish();
		}
		return false;
	}
}
