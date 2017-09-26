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
public class MeMonologueActivity extends Activity {
	private Button savebtn;
	private EditText duibai;

	private BBLDao dao;
	
	private Dialog dialog;
	
	private Button btnBack;
	
	
	private String username;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.me_monologue);
		dialog = new Dialog(MeMonologueActivity.this,
				R.style.theme_dialog_alert);
		dao=new BBLDao(MeMonologueActivity.this, null, null, 1);
		username=dao.queryUserByNewTime().getUsername();
		savebtn = (Button) findViewById(R.id.duibai_save_btnid);
		duibai = (EditText) findViewById(R.id.me_duibai_content_id);
		btnBack = (Button) findViewById(R.id.btnBack);
		btnBack.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				startActivity(new Intent(MeMonologueActivity.this,
						MainActivity.class));
				finish();

			}
		});
		savebtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stubt
				String content=duibai.getText().toString().trim();
				if(content.length()==0)
				{
					HelperUtil.totastShow("���ݲ���Ϊ��",
							MeMonologueActivity.this);
					return;
				}
				save(content);
			}
		});
		init();
	}

	private void init() {
		HelperUtil.customDialogShow(dialog, MeMonologueActivity.this, "���ڼ�����...");
		new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				try {
					Map<String, String> map = new HashMap<String, String>();
					map.put("username",username);
					JSONArray jsonArray = new JSONArray(HelperUtil.postRequest(
							HttpConstantUtil.FindDubai, map));
					MeMonologueActivity.this
					.runOnUiThread(new Runnable() {

						@Override
						public void run() {
							// TODO Auto-generated method stub
							if(dialog!=null)
							{
								dialog.dismiss();
							}
						}
					});
					if (jsonArray.length() > 0) {
						final String cont=jsonArray.optJSONObject(0)
								.optString("heartdubai");
						MeMonologueActivity.this.runOnUiThread(new Runnable() {

							@Override
							public void run() {
								duibai.setText(cont);
							}
						});
					}
				} catch (Exception e) {
					// TODO: handle exception

					MeMonologueActivity.this.runOnUiThread(new Runnable() {

						@Override
						public void run() {
							// TODO Auto-generated method stub
							if(dialog!=null)
							{
								dialog.dismiss();
							}
							HelperUtil.totastShow("����������Ժ�����",
									MeMonologueActivity.this);
						}
					});
				}

			}
		}).start();

	}

	private void save(final String content) {
		HelperUtil.customDialogShow(dialog, MeMonologueActivity.this, "���ڱ�����...");
		new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				try {
					Map<String, String> map = new HashMap<String, String>();
					map.put("username",username);
					map.put("duibai", content);
					JSONObject jsonObject = new JSONObject(
							HelperUtil.postRequest(HttpConstantUtil.UpDubai,
									map));
					MeMonologueActivity.this
					.runOnUiThread(new Runnable() {

						@Override
						public void run() {
							// TODO Auto-generated method stub
							if(dialog!=null)
							{
								dialog.dismiss();
							}
						}
					});
					if (jsonObject.length() > 0) {
						if (jsonObject.getInt("result") > 0) {

							MeMonologueActivity.this.runOnUiThread(new Runnable() {

								@Override
								public void run() {
							     HelperUtil.totastShow("����ɹ�,�������˹����...(���ܰ�����ϵ��ʽ����桢���Ϸ��ַ����������ַ��ȵ�)",MeMonologueActivity.this);
								}
							});
						}
					}
				} catch (Exception e) {
					// TODO: handle exception

					MeMonologueActivity.this.runOnUiThread(new Runnable() {

						@Override
						public void run() {
							// TODO Auto-generated method stub
							if(dialog!=null)
							{
								dialog.dismiss();
							}
							HelperUtil.totastShow("����������Ժ�����",
									MeMonologueActivity.this);
						}
					});
				}

			}
		}).start();

	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			startActivity(new Intent(this, MainActivity.class));
			finish();
		}
		return false;
	}

}
