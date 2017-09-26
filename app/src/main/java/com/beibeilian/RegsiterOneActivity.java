package com.beibeilian;

import com.beibeilian.util.ExitApplication;
import com.beibeilian.util.HelperUtil;
import com.beibeilian.android.app.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class RegsiterOneActivity extends Activity implements OnClickListener {
	private Button nextstepbtn;
	private EditText username;
	private EditText password;

	private Button btnBack;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.regsiter_one);
		ExitApplication.addActivity(RegsiterOneActivity.this);
		nextstepbtn = (Button) findViewById(R.id.regsiter_xyb_id);
		username = (EditText) findViewById(R.id.emailid);
		password = (EditText) findViewById(R.id.passid);
		btnBack = (Button) findViewById(R.id.btnBack);
		btnBack.setOnClickListener(this);
		nextstepbtn.setOnClickListener(this);

	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		Intent intent;
		switch (v.getId()) {
		case R.id.regsiter_xyb_id:
			String user = username.getText().toString().trim();
			String pass = password.getText().toString().trim();
			if (user.length() > 0 && pass.length() > 0) {
				if(user.equals("admin")||user.equals("ADMIN")||user.equals("beibeilian"))
				{
					HelperUtil.totastShow("���ʺŽ�ֹע��", this);
					return;
				}
				if(user.length()>15)
				{
					HelperUtil.totastShow("�˺Ų��ܸ���15λ", this);
					return;	
				}
				if (HelperUtil.inputMactches(user)) {
					if (pass.length()>=6&&pass.length() <= 16) {
						if (!HelperUtil.CheckChinese(pass)) {
							intent = new Intent(RegsiterOneActivity.this,
									RegsiterTwoActivity.class);
							intent.putExtra("username", user);
							intent.putExtra("password", pass);
							startActivity(intent);
//							finish();
						} else {
							HelperUtil.totastShow("���벻�ܰ�������", this);
						}
					} else {
						HelperUtil.totastShow("���벻�ܵ���6λ����16λ", this);
					}
				} else {
					HelperUtil.totastShow("�ʺ������֡���ĸ���", this);
				}
			} else {
				HelperUtil.totastShow("�ʺź����벻��Ϊ��", this);
			}
			break;
		case R.id.btnBack:
//			startActivity(new Intent(RegsiterOneActivity.this,
//					LoginActivity.class));
			finish();
			break;
		default:
			break;
		}
	}
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
//			startActivity(new Intent(RegsiterOneActivity.this,
//					LoginActivity.class));
			finish();
		}
		return false;
	}
}
