package com.beibeilian.me;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import com.beibeilian.MainActivity;
import com.beibeilian.db.BBLDao;
import com.beibeilian.util.HelperUtil;
import com.beibeilian.util.HttpConstantUtil;
import com.beibeilian.android.app.R;
public class MeSetEmailActivity extends Activity {

	private Button btnsave;

	private BBLDao dao;

	private Dialog dialog;

	private EditText etQuestion;
	
	private EditText etAnswer;
	
	private Button btnBack;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.me_set_email);
		etQuestion = (EditText) findViewById(R.id.et_quersion);
		etAnswer = (EditText) findViewById(R.id.et_answer);
		dialog = new Dialog(MeSetEmailActivity.this,
				R.style.theme_dialog_alert);
		dao = new BBLDao(MeSetEmailActivity.this, null, null, 1);
		btnsave = (Button) findViewById(R.id.save_btnid);
		btnBack = (Button) findViewById(R.id.btnBack);
		btnBack.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();

			}
		});
		btnsave.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				save();
			}
		});
		init();

	}

	private void init()
	{
		HelperUtil.customDialogShow(dialog, MeSetEmailActivity.this,"���ڼ�����...");
		new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub

				try {
					Map<String, String> map = new HashMap<String, String>();
					map.put("username", dao.queryUserByNewTime().getUsername());
					final JSONObject jsonObject = new JSONObject(HelperUtil
							.postRequest(HttpConstantUtil.FindQuesstion, map));
					MeSetEmailActivity.this.runOnUiThread(new Runnable() {

						@Override
						public void run() {
							// TODO Auto-generated method stub
							if (dialog != null) {
								dialog.dismiss();
							}
						}
					});
					if (jsonObject.length() > 0) {
						MeSetEmailActivity.this.runOnUiThread(new Runnable() {

							@Override
							public void run() {
								// TODO Auto-generated method stub
								  etQuestion.setText(jsonObject.optString("quesstion"));
							}
						});
					 
					}
				} catch (Exception e) {
					// TODO: handle exception
					MeSetEmailActivity.this.runOnUiThread(new Runnable() {

						@Override
						public void run() {
							// TODO Auto-generated method stub
							if (dialog != null) {
								dialog.dismiss();
							}
							HelperUtil.totastShow("����������Ժ�����",
									MeSetEmailActivity.this);
						}
					});
				}

			}
		}).start();
		
		
		
	}
	
	
	private void save() {
		final String etquestion = etQuestion.getText().toString().trim();
		final String etanswer = etAnswer.getText().toString().trim();
		if (etquestion.length() == 0||etanswer.length()>20) {
			HelperUtil.totastShow("�ܱ����ⲻ��Ϊ���Ҳ��ܴ���20����",
					MeSetEmailActivity.this);
			return;
		}
		if (etanswer.length() == 0||etanswer.length()>20) {
			HelperUtil.totastShow("�ܱ��𰸲���Ϊ���Ҳ��ܴ���20����",
					MeSetEmailActivity.this);
			return;
		}
		HelperUtil.customDialogShow(dialog, MeSetEmailActivity.this,"���� ������...");
		new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub

				try {
				
					
					Map<String, String> map = new HashMap<String, String>();
					map.put("username", dao.queryUserByNewTime().getUsername());
					map.put("quesstion", etquestion);
					map.put("pass", etanswer);
					JSONObject jsonObject = new JSONObject(HelperUtil
							.postRequest(HttpConstantUtil.AddQuesstion, map));
					MeSetEmailActivity.this.runOnUiThread(new Runnable() {

						@Override
						public void run() {
							// TODO Auto-generated method stub
							if (dialog != null) {
								dialog.dismiss();
							}
						}
					});
					if (jsonObject.length() > 0) {
						if (jsonObject.getInt("result") > 0) {

							MeSetEmailActivity.this
									.runOnUiThread(new Runnable() {

										@Override
										public void run() {
											// TODO Auto-generated method stub
											HelperUtil.totastShow("���óɹ�",
													MeSetEmailActivity.this);
										}
									});
						}
					}
				} catch (Exception e) {
					// TODO: handle exception
					MeSetEmailActivity.this.runOnUiThread(new Runnable() {

						@Override
						public void run() {
							// TODO Auto-generated method stub
							if (dialog != null) {
								dialog.dismiss();
							}
							HelperUtil.totastShow("����������Ժ�����",
									MeSetEmailActivity.this);
						}
					});
				}

			}
		}).start();

	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			finish();
		}
		return false;
	}
}
