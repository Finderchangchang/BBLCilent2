package com.beibeilian;


import com.beibeilian.android.app.R;
import com.beibeilian.db.BBLDao;
import com.beibeilian.me.model.UserInfoEntiy;
import com.beibeilian.util.ExitApplication;
import com.beibeilian.util.HelperUtil;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class WelcomeActivity extends Activity {
	private LoadThread mLoadThread;
	private BBLDao dao;
	private UserInfoEntiy user;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.welcome);
		
		ExitApplication.addActivity(WelcomeActivity.this);
		dao = new BBLDao(WelcomeActivity.this, null, null, 1);
		user = dao.queryUserByNewTime();
		Onload();
		HelperUtil.uploadChannel(WelcomeActivity.this);
	}
	private void Onload() {

		mLoadThread = new LoadThread();
		mLoadThread.start();

	}

	private class LoadThread extends Thread {
		@Override
		public void run() {

			try {
				Thread.sleep(3000);
				if (user == null || user.getState() == null) {
					startActivity(new Intent(WelcomeActivity.this,
							LoadLoginActivity.class));
					finish();
				} else if (user != null
						&& HelperUtil.flagISNoNull(user.getState())) {
					String state = user.getState();
					if (HelperUtil.flagISNoNull(state) && state.equals("1")) {
						startActivity(new Intent(WelcomeActivity.this,
								MainActivity.class));
						finish();
					} else {
						startActivity(new Intent(WelcomeActivity.this,
								LoadLoginActivity.class));
						finish();
					}
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				startActivity(new Intent(WelcomeActivity.this,
						LoadLoginActivity.class));
				finish();
			}
		}
	}


}
