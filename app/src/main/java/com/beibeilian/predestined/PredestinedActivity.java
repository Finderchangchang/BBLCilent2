package com.beibeilian.predestined;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;

import com.beibeilian.android.app.R;
import com.beibeilian.db.BBLDao;
import com.beibeilian.gridview.PullToRefreshLayout;
import com.beibeilian.gridview.PullToRefreshLayout.OnRefreshListener;
import com.beibeilian.gridview.PullableGridView;
import com.beibeilian.seek.adapter.GridPersonListAdapter;
import com.beibeilian.seek.model.UserInfo;
import com.beibeilian.util.AutoLoadListener;
import com.beibeilian.util.ExitApplication;
import com.beibeilian.util.HelperUtil;
import com.beibeilian.util.HttpConstantUtil;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

@SuppressLint("HandlerLeak")
public class PredestinedActivity extends Activity {

	private static List<UserInfo> personlist = new ArrayList<UserInfo>();

	private int PageNumber = 0;

	private GridPersonListAdapter seekPersonListAdapter;

	private InitLoadThread initLoadThread;

	private RelativeLayout load_progressbar;

	private ProgressBar leftProgressbarid;

	private PullableGridView mgridView;

	PullToRefreshLayout mPullToRefreshLayout;

	private BBLDao mDao;

	private String sex = "ÄÐ";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.predestined);
		ExitApplication.addActivity(PredestinedActivity.this);
		try {
			mDao = new BBLDao(PredestinedActivity.this, null, null, 1);
			sex = mDao.queryUserByNewTime().getSex();
		} catch (Exception e) {
			// TODO: handle exception
		}
		mgridView = (PullableGridView) findViewById(R.id.mgridView);
		((PullToRefreshLayout) findViewById(R.id.refresh_view)).setOnRefreshListener(new OnRefreshListener() {

			@Override
			public void onRefresh(PullToRefreshLayout pullToRefreshLayout) {
				// TODO Auto-generated method stub
				mPullToRefreshLayout = pullToRefreshLayout;
				PageNumber = 0;
				if (load_progressbar != null) {
					load_progressbar.setVisibility(View.GONE);
				}
				if (leftProgressbarid != null) {
					leftProgressbarid.setVisibility(View.VISIBLE);
				}
				if (initLoadThread != null) {
					initLoadThread.interrupt();
				}
				initLoadThread = new InitLoadThread();
				initLoadThread.start();
			}

			@Override
			public void onLoadMore(final PullToRefreshLayout pullToRefreshLayout) {
				// TODO Auto-generated method stub
				mPullToRefreshLayout = pullToRefreshLayout;
				PageNumber++;
				initLoadThread = new InitLoadThread();
				initLoadThread.start();
			}
		});
		leftProgressbarid = (ProgressBar) findViewById(R.id.leftprogressbar_id);
		load_progressbar = (RelativeLayout) findViewById(R.id.list_load_id);
		seekPersonListAdapter = new GridPersonListAdapter(PredestinedActivity.this, personlist);
		mgridView.setAdapter(seekPersonListAdapter);

		initLoadThread = new InitLoadThread();
		initLoadThread.start();

	}

	private class InitLoadThread extends Thread {
		@Override
		public void run() {
			try {
				Map<String, String> map = new HashMap<String, String>();
				map.put("pagenumber", String.valueOf(PageNumber));
				if (!TextUtils.isEmpty(sex) && sex.equals("ÄÐ")) {
					map.put("sex", "Å®");
				} else {
					map.put("sex", "ÄÐ");
				}
				String result = HelperUtil.postRequest(HttpConstantUtil.FindHavealookPageList, map);
				android.os.Message message = handler.obtainMessage();
				message.obj = result;
				message.what = 1;
				message.sendToTarget();
			} catch (Exception e) {
				// TODO: handle exception
			}
		}
	}

	private void parseJson(String result) {
		try {
			JSONArray jsonArray = new JSONArray(result);
			if (jsonArray.length() > 0) {
				if (PageNumber == 0 && personlist != null && personlist.size() > 0) {
					personlist.clear();
				}
				for (int i = 0; i < jsonArray.length(); i++) {
					String username = jsonArray.optJSONObject(i).optString("username");
					String year = jsonArray.optJSONObject(i).optString("birthday");
					String photo = jsonArray.optJSONObject(i).optString("photo");
					String monologue = jsonArray.optJSONObject(i).optString("heartdubai");
					String place = jsonArray.optJSONObject(i).optString("lives");
					String nickname = jsonArray.optJSONObject(i).optString("nickname");
					String sex = jsonArray.optJSONObject(i).optString("sex");
					String maritalstatus = jsonArray.optJSONObject(i).optString("maritalstatus");
					int heartduibaistate = jsonArray.optJSONObject(i).optInt("heartduibaistate");
					String auth = jsonArray.optJSONObject(i).optString("auth");
					UserInfo model = new UserInfo();
					model.setBirthday(year);
					model.setHeartdubai(monologue);
					model.setLives(place);
					model.setNickname(nickname);
					model.setPhoto(photo);
					// model.setState(state);
					model.setUsername(username);
					model.setAuth(auth);
					model.setSex(sex);
					model.setHeartduibaistate(heartduibaistate);
					model.setMaritalstatus(maritalstatus);
					personlist.add(model);
				}

			}
			if (mPullToRefreshLayout != null) {
				if (PageNumber == 0) {
					mPullToRefreshLayout.refreshFinish(PullToRefreshLayout.SUCCEED);
				} else {
					mPullToRefreshLayout.loadmoreFinish(PullToRefreshLayout.SUCCEED);
				}
			}
			if (mgridView.getAdapter() == null) {
				mgridView.setAdapter(seekPersonListAdapter);
				if (seekPersonListAdapter != null)
					seekPersonListAdapter.notifyDataSetChanged();
			} else {
				if (seekPersonListAdapter != null)
					seekPersonListAdapter.notifyDataSetChanged();
			}
			if (load_progressbar != null) {
				load_progressbar.setVisibility(View.GONE);
			}
			if (leftProgressbarid != null)
				leftProgressbarid.setVisibility(View.GONE);
		} catch (Exception e) {
			e.printStackTrace();
			if (mPullToRefreshLayout != null) {
				if (PageNumber == 0) {
					mPullToRefreshLayout.refreshFinish(PullToRefreshLayout.SUCCEED);
				} else {
					mPullToRefreshLayout.loadmoreFinish(PullToRefreshLayout.SUCCEED);

				}
			}
			if (load_progressbar != null) {
				load_progressbar.setVisibility(View.GONE);
			}
			if (leftProgressbarid != null)
				leftProgressbarid.setVisibility(View.GONE);
		}
	}

	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {

			switch (msg.what) {

			case 1:
				parseJson((String) msg.obj);
				break;
			default:
				break;
			}

		}
	};

	@Override
	public void onPause() {
		super.onPause();
		if (initLoadThread != null) {
			initLoadThread.interrupt();
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		// if (refreshListReceiver != null)
		// unregisterReceiver(refreshListReceiver);
	}

	// @Override
	// public void onRefresh() {
	// // TODO Auto-generated method stub
	// PageNumber = 0;
	// if(load_progressbar!=null)
	// {
	// load_progressbar.setVisibility(View.GONE);
	// }
	// if (leftProgressbarid != null)
	// {
	// leftProgressbarid.setVisibility(View.VISIBLE);
	// }
	// if (initLoadThread != null) {
	// initLoadThread.interrupt();
	// }
	// initLoadThread = new InitLoadThread();
	// initLoadThread.start();
	//
	// }
//	AutoLoadListener.AutoLoadCallBack callBack = new AutoLoadListener.AutoLoadCallBack() {
//
//		public void execute() {
//
//			PageNumber++;
//			initLoadThread = new InitLoadThread();
//			initLoadThread.start();
//		}
//
//	};
}
