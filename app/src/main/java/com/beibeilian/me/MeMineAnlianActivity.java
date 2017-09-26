package com.beibeilian.me;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;

import com.beibeilian.MainActivity;
import com.beibeilian.db.BBLDao;
import com.beibeilian.listview.PullableListView;
import com.beibeilian.listview.PullableListView.OnLoadListener;
import com.beibeilian.seek.adapter.SeekPersonListTimeAdapter;
import com.beibeilian.seek.model.UserInfo;
import com.beibeilian.util.HelperUtil;
import com.beibeilian.util.HttpConstantUtil;
import com.beibeilian.util.PublicConstant;
import com.beibeilian.android.app.R;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class MeMineAnlianActivity extends Activity implements OnLoadListener {

	private static List<UserInfo> personlist = new ArrayList<UserInfo>();

	private int PageNumber = 0;

	private PullableListView Listview;

	private PullableListView mPullableListView;

	private SeekPersonListTimeAdapter seekPersonListAdapter;

	private InitLoadThread initLoadThread;

	private Dialog dialog;

	private Button btnBack;

	private BBLDao dao;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.me_mineanlian);
		dao = new BBLDao(MeMineAnlianActivity.this, null, null, 1);
		Listview = (PullableListView) findViewById(R.id.listview);

		btnBack = (Button) findViewById(R.id.btnBack);
		dialog = new Dialog(MeMineAnlianActivity.this, R.style.theme_dialog_alert);
		seekPersonListAdapter = new SeekPersonListTimeAdapter(MeMineAnlianActivity.this, personlist);
		btnBack.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				startActivity(new Intent(MeMineAnlianActivity.this, MainActivity.class));
				finish();
			}
		});

		HelperUtil.customDialogShow(dialog, MeMineAnlianActivity.this, "正在加载中...");
		if (initLoadThread != null) {
			initLoadThread.interrupt();
		}
		initLoadThread = new InitLoadThread();
		initLoadThread.start();
		Listview.setOnLoadListener(this);
		Listview.setHasMoreData(false);
	}

	private class InitLoadThread extends Thread {
		@Override
		public void run() {
			try {
				Map<String, String> map = new HashMap<String, String>();
				map.put("username", dao.queryUserByNewTime().getUsername());
				map.put("pagenumber", String.valueOf(PageNumber));
				android.os.Message message = handler.obtainMessage();
				message.obj = HelperUtil.postRequest(HttpConstantUtil.FindMeWhoAnlianList, map);
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
				if (PageNumber == 0) {
					if (personlist.size() > 0) {
						personlist.clear();
					}
				}
				for (int i = 0; i < jsonArray.length(); i++) {
					String username = jsonArray.optJSONObject(i).optString("username");
					String year = jsonArray.optJSONObject(i).optString("birthday");
					String photo = jsonArray.optJSONObject(i).optString("photo");
					// String state = jsonArray.optJSONObject(i).optString(
					// "state");
					String monologue = jsonArray.optJSONObject(i).optString("heartdubai");
					String place = jsonArray.optJSONObject(i).optString("lives");
					String nickname = jsonArray.optJSONObject(i).optString("nickname");
					String time = jsonArray.optJSONObject(i).optString("time");
					String sex = jsonArray.optJSONObject(i).optString("sex");
					UserInfo model = new UserInfo();
					model.setBirthday(year);
					model.setHeartdubai(monologue);
					model.setLives(place);
					model.setNickname(nickname);
					model.setPhoto(photo);
					// model.setState(state);
					model.setUsername(username);
					model.setTime(time);
					model.setSex(sex);
					personlist.add(model);
				}
				if (Listview.getAdapter() == null) {
					Listview.setAdapter(seekPersonListAdapter);
				}
				if (seekPersonListAdapter != null) {
					seekPersonListAdapter.notifyDataSetChanged();
				}

			}
			if (jsonArray.length() >= 0 && jsonArray.length() < 15) {
				Listview.setHasMoreData(false);
			} else {
				Listview.setHasMoreData(true);
			}
			if (PageNumber > 0) {
				if (mPullableListView != null) {
					mPullableListView.finishLoading();
				}
			}
			if (dialog != null) {
				dialog.dismiss();
			}
		} catch (Exception e) {

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
	public void onDestroy() {
		super.onDestroy();

		if (dialog != null) {
			dialog.dismiss();
		}

		if (initLoadThread != null) {
			initLoadThread.interrupt();
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			startActivity(new Intent(this, MainActivity.class));
			finish();
		}
		return false;
	}

	@Override
	public void onLoad(PullableListView pullableListView) {
		// TODO Auto-generated method stub

		mPullableListView = pullableListView;

		PageNumber++;

		if (initLoadThread != null) {
			initLoadThread.interrupt();
		}
		initLoadThread = new InitLoadThread();
		initLoadThread.start();
	}

}
