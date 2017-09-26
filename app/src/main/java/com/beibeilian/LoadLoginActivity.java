package com.beibeilian;

import com.beibeilian.android.app.R;
import com.beibeilian.util.ExitApplication;
import com.beibeilian.util.HelperUtil;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class LoadLoginActivity extends Activity{

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login_load);
		ExitApplication.addActivity(LoadLoginActivity.this);
		Button btnYJZC=(Button) findViewById(R.id.btn_yjregister);
		Button btnZHLOGIN=(Button) findViewById(R.id.btn_zhlogin);
        btnYJZC.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				startActivity(new Intent(LoadLoginActivity.this,RegsiterActivity.class));

			}
		});
        btnZHLOGIN.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				startActivity(new Intent(LoadLoginActivity.this,LoginActivity.class));

			}
		});
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			finish();
		}
		return super.onKeyDown(keyCode, event);
	}

	
	
	
	
}
