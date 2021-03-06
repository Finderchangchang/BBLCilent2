package com.beibeilian.seek.qaq;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.beibeilian.circle.bean.CircleItem;
import com.beibeilian.circle.bean.CommentItem;
import com.beibeilian.circle.bean.FavortItem;
import com.beibeilian.circle.bean.User;
import com.beibeilian.circle.contral.CirclePublicCommentContral;
import com.beibeilian.circle.utils.CommonUtils;
import com.beibeilian.constant.BBLConstant;
import com.beibeilian.db.BBLDao;
import com.beibeilian.listview.PullToRefreshLayout;
import com.beibeilian.listview.PullToRefreshLayout.OnRefreshListener;
import com.beibeilian.listview.PullableListView;
import com.beibeilian.listview.PullableListView.OnLoadListener;
import com.beibeilian.photo.activity.QAAddActivity;
import com.beibeilian.receiver.ReceiverConstant;
import com.beibeilian.seek.qaq.adapter.CircleAdapter;
import com.beibeilian.util.HelperUtil;
import com.beibeilian.util.HttpConstantUtil;
import com.beibeilian.util.SweetAlertDialog;
import com.beibeilian.android.app.R;

import android.view.View.OnTouchListener;

public class MeCircleMainActivity extends Activity implements OnLoadListener {
	private PullableListView mCircleLv;
	private CircleAdapter mAdapter;
	private LinearLayout mEditTextBody;
	private EditText mEditText;
	private TextView sendTv;
	private static List<CircleItem> list_source = new ArrayList<CircleItem>();
	private int Pagenumber = 0;
	private int mScreenHeight;
	private int mEditTextBodyHeight;
	private CirclePublicCommentContral mCirclePublicCommentContral;
	private PullableListView mPullableListView;
	private ComplaintThread initLoadThread;
	private PullToRefreshLayout mpullToRefreshLayout;
	private Button btnback,btnSend;
	private BBLDao dao;
	private SweetAlertDialog dialog;
	
	RefeshReceiver mRefeshReceiver;
	IntentFilter mIntentFilter;
	
	class RefeshReceiver extends BroadcastReceiver
	{

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			if(intent.getAction().equals(ReceiverConstant.FileUploadSuccess_ACTION))
			{
				Pagenumber = 0;
				if (initLoadThread != null) {
					initLoadThread.interrupt();
				}
				initLoadThread = new ComplaintThread();
				initLoadThread.start();
			}
		}
	}

	@Override 
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.me_circle_main);
		dao = new BBLDao(MeCircleMainActivity.this, null, null, 1);
		dialog = new SweetAlertDialog(MeCircleMainActivity.this);
		mIntentFilter=new IntentFilter();
		mRefeshReceiver=new RefeshReceiver();
		mIntentFilter.addAction(ReceiverConstant.FileUploadSuccess_ACTION);
		registerReceiver(mRefeshReceiver, mIntentFilter);
		initView();
	}

	private void initView() {
		mCircleLv = (PullableListView) findViewById(R.id.circleLv);
		mCircleLv.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (mEditTextBody.getVisibility() == View.VISIBLE) {
					mEditTextBody.setVisibility(View.GONE);
					CommonUtils.hideSoftInput(MeCircleMainActivity.this, mEditText);
					return true;
				}
				return false;
			}
		});
		mAdapter = new CircleAdapter(this);

		mEditTextBody = (LinearLayout) findViewById(R.id.editTextBodyLl);
		mEditText = (EditText) findViewById(R.id.circleEt);
		sendTv = (TextView) findViewById(R.id.sendTv);
		btnback = (Button) findViewById(R.id.btnBack);
		btnSend = (Button) findViewById(R.id.btn_send_id);

		mCirclePublicCommentContral = new CirclePublicCommentContral(this, mEditTextBody, mEditText, sendTv, dao);
		mCirclePublicCommentContral.setmListView(mCircleLv);
		mAdapter.setmCirclePublicCommentContral(mCirclePublicCommentContral);
		mAdapter.setDatas(list_source);
		dialog.setTitleText("���Ժ�...");
		dialog.show();
		initLoadThread = new ComplaintThread();
		initLoadThread.start();

		mCircleLv.setOnLoadListener(this);
		mCircleLv.setHasMoreData(false);
		((PullToRefreshLayout) findViewById(R.id.predestined_layout)).setOnRefreshListener(new OnRefreshListener() {

			@Override
			public void onRefresh(PullToRefreshLayout pullToRefreshLayout) {
				// TODO Auto-generated method stub
				mpullToRefreshLayout = pullToRefreshLayout;
				Pagenumber = 0;
				if (initLoadThread != null) {
					initLoadThread.interrupt();
				}
				initLoadThread = new ComplaintThread();
				initLoadThread.start();

			}
		});
		btnback.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});
		btnSend.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				startActivity(new Intent(MeCircleMainActivity.this, QAAddActivity.class));
			}
		});

	}

	private class ComplaintThread extends Thread {
		@Override
		public synchronized void run() {
			try {
				Map<String, String> map = new HashMap<String, String>();
				map.put("pagenumber", String.valueOf(Pagenumber));
				map.put("username", dao.queryUserByNewTime().getUsername());

				String result = HelperUtil.postRequest(HttpConstantUtil.Findcomplanitpagelistbyuser, map);
				android.os.Message message = handler.obtainMessage();
				message.obj = result;
				message.what = 1;
				message.sendToTarget();

			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
				handler.sendEmptyMessage(-1);
			}
		}
	}

	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case 1:
				 if (dialog != null)
				 dialog.dismiss();
				parseJson((String) msg.obj);
				break;
			case -1:
				 if (dialog != null)
				 dialog.dismiss();
				break;
			default:
				break;
			}
		}
	};

	private void parseJson(String result) {
		try {
			if (Pagenumber == 0) {
				if (list_source != null && list_source.size() > 0) {
					list_source.clear();
				}
			}
			JSONArray jsonArray = new JSONArray(result);
			if (jsonArray.length() > 0) {
				for (int i = 0; i < jsonArray.length(); i++) {
					String photo = jsonArray.optJSONObject(i).optString("photo");
					String content = jsonArray.optJSONObject(i).optString("content");
					String time = jsonArray.optJSONObject(i).optString("time");
					String headimg = jsonArray.optJSONObject(i).optString("headimg");
					String zan = jsonArray.optJSONObject(i).optString("zan");
					String commit = jsonArray.optJSONObject(i).optString("commit");
					String sex = jsonArray.optJSONObject(i).optString("sex");
					String id = jsonArray.optJSONObject(i).optString("id");
					String type = jsonArray.optJSONObject(i).optString("type");
					String username = jsonArray.optJSONObject(i).optString("username");
					String nickname = jsonArray.optJSONObject(i).optString("nickname");
					CircleItem item = new CircleItem();
					User user = new User(username, nickname, headimg);
					item.setId(id);
					item.setUser(user);
					item.setContent(content);
					item.setCreateTime(time);
					List<FavortItem> listFavortItem = new ArrayList<FavortItem>();
					if (HelperUtil.flagISNoNull(zan)) {
						JSONArray jsonArray2 = new JSONArray(zan);
						for (int j = 0; j < jsonArray2.length(); j++) {
							username = jsonArray2.optJSONObject(j).optString("username");
							nickname = jsonArray2.optJSONObject(j).optString("nickname");
							id = jsonArray2.optJSONObject(j).optString("id");
							FavortItem model = new FavortItem();
							model.setId(id);
							model.setUser(new User(username, nickname, ""));
							listFavortItem.add(model);
						}
					}
					item.setFavorters(listFavortItem);
					List<CommentItem> listCommentItem = new ArrayList<CommentItem>();

					if (HelperUtil.flagISNoNull(commit)) {
						JSONArray jsonArray2 = new JSONArray(commit);
						for (int j = 0; j < jsonArray2.length(); j++) {
							username = jsonArray2.optJSONObject(j).optString("username");
							nickname = jsonArray2.optJSONObject(j).optString("nickname");
							content = jsonArray2.optJSONObject(j).optString("content");
							id = jsonArray2.optJSONObject(j).optString("id");
							CommentItem model = new CommentItem();
							model.setId(id);
							model.setUser(new User(username, nickname, ""));
							model.setToReplyUser(new User(username, nickname, ""));
							model.setContent(content);
							listCommentItem.add(model);
						}
					}
					item.setComments(listCommentItem);

					item.setType("2");
					if (HelperUtil.flagISNoNull(photo)) {
						JSONArray jsonArray3 = new JSONArray(photo);
						List<String> listphoto = new ArrayList<String>();
						for (int k = 0; k < jsonArray3.length(); k++) {
							listphoto.add(BBLConstant.PHOTO_BEFORE_URL+jsonArray3.getString(k));
						}
						item.setPhotos(listphoto);
					}
					list_source.add(item);

				}

			}

			if (mCircleLv.getAdapter() == null) {
				mCircleLv.setAdapter(mAdapter);
			} else {
				if (mAdapter != null) {
					mAdapter.notifyDataSetChanged();
				}
			}

			if (Pagenumber == 0) {
				if (mpullToRefreshLayout != null) {
					mpullToRefreshLayout.refreshFinish(PullToRefreshLayout.SUCCEED);

				}
			} else {
				if (mPullableListView != null) {
					mPullableListView.finishLoading();
				}
			}
			if (jsonArray.length() >= 0 && jsonArray.length() < 15) {
				mCircleLv.setHasMoreData(false);
			} else {
				mCircleLv.setHasMoreData(true);
			}

		} catch (Exception e) {
			if (Pagenumber == 0) {
				if (mpullToRefreshLayout != null) {
					mpullToRefreshLayout.refreshFinish(PullToRefreshLayout.FAIL);
				}
			}
			e.printStackTrace();

		}

	}

	public int getScreenHeight() {
		return mScreenHeight;
	}

	public int getEditTextBodyHeight() {
		return mEditTextBodyHeight;
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			if (mEditTextBody != null && mEditTextBody.getVisibility() == View.VISIBLE) {
				mEditTextBody.setVisibility(View.GONE);
				return true;
			}
			finish();
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public void onLoad(PullableListView pullableListView) {
		// TODO Auto-generated method stub
		mPullableListView = pullableListView;
		Pagenumber++;
		Log.e("test", "666");
		initLoadThread = new ComplaintThread();
		initLoadThread.start();
	}
	
	@Override
	protected void onDestroy()
	{
		super.onDestroy();
		if(mRefeshReceiver!=null) unregisterReceiver(mRefeshReceiver);
	}

}
