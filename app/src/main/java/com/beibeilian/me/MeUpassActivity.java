package com.beibeilian.me;

import java.util.HashMap;

import org.json.JSONObject;

import com.beibeilian.LoginActivity;
import com.beibeilian.db.BBLDao;
import com.beibeilian.service.CoreIMService;
import com.beibeilian.util.ExitApplication;
import com.beibeilian.util.HelperUtil;
import com.beibeilian.util.HttpConstantUtil;
import com.beibeilian.android.app.R;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

/**
 * �޸�����
 * 
 */
public class MeUpassActivity extends Activity implements OnClickListener {

	private Button btnBack, btnModify;// ���ذ�ť���޸����밴ť

	private EditText et_curPwd, et_newPwd, et_rePwd;// ��ǰ���룬�����룬�ظ����������ı���

	String curName = "";// ��ǰ����

	private Dialog dialog;// ����dialog

	private BBLDao dao;// ����dao

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.me_pwd_modify);
		ExitApplication.addActivity(MeUpassActivity.this);
		dialog = new Dialog(MeUpassActivity.this, R.style.theme_dialog_alert);
		dao = new BBLDao(MeUpassActivity.this, null, null, 1);
		init();
		btnModify.setOnClickListener(this);
		btnBack.setOnClickListener(this);
		curName = dao.queryUserByNewTime().getUsername();
	}

	/**
	 * ��ʼ���ؼ�
	 */
	private void init() {
		et_curPwd = (EditText) findViewById(R.id.et_curPwd);
		et_newPwd = (EditText) findViewById(R.id.et_newPwd);
		et_rePwd = (EditText) findViewById(R.id.et_rePwd);
		btnModify = (Button) findViewById(R.id.btnOk);
		btnBack = (Button) findViewById(R.id.btnBack);
		et_curPwd.setText(dao.queryUserByNewTime().getPassword());
	}

	/**
	 * �������¼�
	 */
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btnOk:
			changePwd();
			break;

		case R.id.btnBack:
			this.finish();
			break;
		}
	}

	/**
	 * �޸�����
	 */
	private void changePwd() {
		final String curPwdStr = et_curPwd.getText().toString().trim();// ��ǰ����
		final String newPwdStr = et_newPwd.getText().toString().trim();// ������
		final String rePwdStr = et_rePwd.getText().toString().trim();// �ٴ���������
		if (curPwdStr.length() == 0) {
			HelperUtil.totastShow("��ǰ���벻��Ϊ��", MeUpassActivity.this);
			return;
		}
		if ((newPwdStr.length() == 0) || (newPwdStr.length() < 6)) {
			HelperUtil.totastShow("�����벻��Ϊ���Ҳ��ܵ���6λ", MeUpassActivity.this);
			return;
		}

		if ((rePwdStr.length() == 0) || (rePwdStr.length() < 6)) {
			HelperUtil.totastShow("�ٴ���������벻��Ϊ���Ҳ��ܵ���6λ", MeUpassActivity.this);
			return;
		}
		if (HelperUtil.CheckChinese(newPwdStr) || HelperUtil.CheckChinese(rePwdStr)) {
			HelperUtil.totastShow("���벻�ܰ�������", MeUpassActivity.this);
			return;
		}

		if (newPwdStr.equals(rePwdStr)) {// ���������ٴ�����������ͬ �ɷ�������
			final HashMap<String, String> rawParams = new HashMap<String, String>();
			rawParams.put("username", curName);
			rawParams.put("oldpass", HelperUtil.MD5(curPwdStr));
			rawParams.put("newpass", newPwdStr);
			HelperUtil.customDialogShow(dialog, MeUpassActivity.this, "���������������...");
			new Thread(new Runnable() {

				@Override
				public void run() {

					try {
						String result = HelperUtil.postRequest(HttpConstantUtil.UpPass, rawParams);
						JSONObject jsonObject = new JSONObject(result);
						int number = jsonObject.getInt("result");

						// Log.e("aaa", number + "�޸�����");
						if (number == 0) {
							handler.sendEmptyMessage(0);
						} else if (number == 1) {
							handler.sendEmptyMessage(1);// 1�޸ĳɹ� 0ʧ��2ԭʼ�������
						} else {
							handler.sendEmptyMessage(2);
						}
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						handler.sendEmptyMessage(3);
					}
				}
			}).start();
		} else {
			HelperUtil.totastShow("��������������벻��ͬ", MeUpassActivity.this);
		}

	}

	/**
	 * ����hander��Ϣ
	 */
	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case 0:
				if (dialog != null) {
					dialog.dismiss();
				}
				HelperUtil.totastShow("�޸�ʧ��,������������������", MeUpassActivity.this);
				break;
			case 1:
				if (dialog != null) {
					dialog.dismiss();
				}
				HelperUtil.totastShow("�޸ĳɹ�,�����µ�¼", MeUpassActivity.this);
				dao.updateLoginState(curName);
				stopService(new Intent(MeUpassActivity.this, CoreIMService.class));

				Intent intent = new Intent(MeUpassActivity.this, LoginActivity.class);
				startActivity(intent);
				finish();
				ExitApplication.exit();
				break;
			case 2:
				if (dialog != null) {
					dialog.dismiss();
				}
				HelperUtil.totastShow("ԭʼ�������", MeUpassActivity.this);
				break;
			case 3:
				if (dialog != null) {
					dialog.dismiss();
				}
				HelperUtil.totastShow("�޸�ʧ��,����������쳣", MeUpassActivity.this);
				break;
			default:
				break;
			}
		};
	};

	/**
	 * �������¼�
	 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {

			this.finish();
		}
		return false;
	}
}
