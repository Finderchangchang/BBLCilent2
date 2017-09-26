package com.beibeilian;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;
import com.beibeilian.util.ExitApplication;
import com.beibeilian.util.HelperUtil;
import com.beibeilian.util.HttpConstantUtil;
import com.beibeilian.android.app.R;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class FindPassInputAnswerActivity extends Activity {

	private Button btnsave;

	private Dialog dialog;

	private EditText etQuestion;
	
	private EditText etAnswer;
	
	private EditText etPassword;
	
	private Button btnBack;
	
	private String quesstion;
	
	private String username;
	
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.findpass_inputanswer);
		ExitApplication.addActivity(FindPassInputAnswerActivity.this);
		etQuestion = (EditText) findViewById(R.id.et_quersion);
		etAnswer = (EditText) findViewById(R.id.et_answer);
		etPassword = (EditText) findViewById(R.id.et_password);
		dialog = new Dialog(FindPassInputAnswerActivity.this,
				R.style.theme_dialog_alert);
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
				findISExits();
			}
		});
		quesstion=getIntent().getStringExtra("quesstion");
		username=getIntent().getStringExtra("username");
		init();

	}

	private void init()
	{
		  etQuestion.setText(quesstion);	
		  
	}
	
	
	private void findISExits() {
		final String etanswer = etAnswer.getText().toString().trim();
		final String etpass = etPassword.getText().toString().trim();
		if (etanswer.length() == 0) {
			HelperUtil.totastShow("�ܱ��𰸲���Ϊ��",FindPassInputAnswerActivity.this);
			return;
		}
		if (etpass.length() == 0) {
			HelperUtil.totastShow("�µĵ�¼���벻��Ϊ��",FindPassInputAnswerActivity.this);
			return;
		}
		if (etpass.length()<6||etpass.length() > 16) {
			HelperUtil.totastShow("���벻�ܵ���6λ����16λ", this);
			return;
		}
		if (HelperUtil.CheckChinese(etpass)) {
			HelperUtil.totastShow("���벻�ܰ�������", this);
			return;
		}
		HelperUtil.customDialogShow(dialog, FindPassInputAnswerActivity.this,"���� ������...");
		new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				try {					
					Map<String, String> map = new HashMap<String, String>();
					map.put("username", username);
					map.put("quesstion", quesstion);
					map.put("quesstionpass", etanswer);
					map.put("password", etpass);
					JSONObject jsonObject = new JSONObject(HelperUtil
							.postRequest(HttpConstantUtil.FindQuesstionAndUpass, map));
					FindPassInputAnswerActivity.this.runOnUiThread(new Runnable() {

						@Override
						public void run() {
							// TODO Auto-generated method stub
							if (dialog != null) {
								dialog.dismiss();
							}
						}
					});
					if (jsonObject.length() > 0) {
						
						int result=jsonObject.optInt("result");
						if(result==1)
						{
							FindPassInputAnswerActivity.this.runOnUiThread(new Runnable() {

								@Override
								public void run() {
									// TODO Auto-generated method stub
									
									HelperUtil.totastShow("�޸ĳɹ�",
											FindPassInputAnswerActivity.this);
									startActivity(new Intent(FindPassInputAnswerActivity.this,LoginActivity.class));
									finish();
								}
							});
						}
						if(result==-1)
						{
							FindPassInputAnswerActivity.this.runOnUiThread(new Runnable() {

								@Override
								public void run() {
									// TODO Auto-generated method stub
									
									HelperUtil.totastShow("�ܱ��𰸲���ȷ",
											FindPassInputAnswerActivity.this);
									
								}
							});
						}
						if(result==0)
						{
							FindPassInputAnswerActivity.this.runOnUiThread(new Runnable() {

								@Override
								public void run() {
									// TODO Auto-generated method stub
									
									HelperUtil.totastShow("�޸�ʧ��",
											FindPassInputAnswerActivity.this);
							
								}
							});
						}
					}
					else
					{
						FindPassInputAnswerActivity.this.runOnUiThread(new Runnable() {

							@Override
							public void run() {
								// TODO Auto-generated method stub
								
								HelperUtil.totastShow("�޸ĳ����쳣,���Ժ�����",
										FindPassInputAnswerActivity.this);
						
							}
						});
					}
				} catch (Exception e) {
					// TODO: handle exception
					FindPassInputAnswerActivity.this.runOnUiThread(new Runnable() {

						@Override
						public void run() {
							// TODO Auto-generated method stub
							if (dialog != null) {
								dialog.dismiss();
							}
							HelperUtil.totastShow("����������Ժ�����",
									FindPassInputAnswerActivity.this);
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