package com.beibeilian.seek;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;

import com.beibeilian.android.app.R;
import com.beibeilian.seek.adapter.SeekSoftAdapter;
import com.beibeilian.seek.model.APP_T_Softad;
import com.beibeilian.seek.model.UserInfo;
import com.beibeilian.util.HelperUtil;
import com.beibeilian.util.HttpConstantUtil;
import com.beibeilian.util.PublicConstant;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;

public class SeekSoftActivity extends Activity {

	ListView mListView;
	SeekSoftAdapter mSeekSoftAdapter;
	private InitLoadThread initLoadThread;
	private static List<APP_T_Softad> personlist = new ArrayList<APP_T_Softad>();

	private Dialog dialog;

	private Button btnBack;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.seek_soft);
		btnBack = (Button) findViewById(R.id.btnBack);
		dialog = new Dialog(SeekSoftActivity.this, R.style.theme_dialog_alert);
		mListView = (ListView) findViewById(R.id.listview);
		mSeekSoftAdapter = new SeekSoftAdapter(SeekSoftActivity.this, personlist);
		btnBack.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});
		if (initLoadThread != null) {
			initLoadThread.interrupt();
		}
		HelperUtil
		.customDialogShow(dialog, SeekSoftActivity.this, "正在加载中...");
		initLoadThread = new InitLoadThread();
		initLoadThread.start();
	}

	private class InitLoadThread extends Thread {
		@Override
		public void run() {
			try {
				android.os.Message message = handler.obtainMessage();
				message.obj = HelperUtil.getRequest(HttpConstantUtil.FindSoft);
				message.what = 1;
				message.sendToTarget();
			} catch (Exception e) {
				// TODO: handle exception
				handler.sendEmptyMessage(PublicConstant.JsonCatch);
			}
		}
	}

	private void parseJson(String result) {
		try {
			JSONArray jsonArray = new JSONArray(result);
			if (jsonArray.length() > 0) {
				if (personlist.size() > 0) {
					personlist.clear();
				}
				for (int i = 0; i < jsonArray.length(); i++) {
					String appname = jsonArray.optJSONObject(i).optString("appname");
					String appcontent = jsonArray.optJSONObject(i).optString("appcontent");
					String appsize = jsonArray.optJSONObject(i).optString("appsize");
					String logo = jsonArray.optJSONObject(i).optString("logo");
					String appapk = jsonArray.optJSONObject(i).optString("appapk");
					APP_T_Softad model = new APP_T_Softad();
					model.setAppcontent(appcontent);
					model.setAppname(appname);
					model.setAppsize(appsize);
					model.setLogo(logo);
					model.setAppapk(appapk);
					personlist.add(model);
				}
				if (mListView.getAdapter() == null)

				{
					mListView.setAdapter(mSeekSoftAdapter);
				} else {
					if (mSeekSoftAdapter != null) {
						mSeekSoftAdapter.notifyDataSetChanged();
					}
				}
			}
			if (dialog != null) {
				dialog.dismiss();
			}

		} catch (Exception e) {
			if (dialog != null) {
				dialog.dismiss();
			}
		}

	}

	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {

			switch (msg.what) {

			case 1:
				parseJson((String) msg.obj);
				break;
			case PublicConstant.JsonCatch:
				if (dialog != null) {
					dialog.dismiss();
				}
				break;
			default:
				break;
			}

		}
	};

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			finish();
		}
		return super.onKeyDown(keyCode, event);
	}

}
