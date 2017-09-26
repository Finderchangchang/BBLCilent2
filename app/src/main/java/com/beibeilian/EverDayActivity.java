package com.beibeilian;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;

import com.beibeilian.android.app.R;
import com.beibeilian.db.BBLDao;
import com.beibeilian.im.video.VideoCallActivity;
import com.beibeilian.receiver.ReceiverConstant;
import com.beibeilian.seek.adapter.GridPersonListAdapter;
import com.beibeilian.seek.model.UserInfo;
import com.beibeilian.util.HelperUtil;
import com.beibeilian.util.HttpConstantUtil;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.GridView;

public class EverDayActivity extends Activity{
	private static List<UserInfo> personlist = new ArrayList<UserInfo>();

	private int PageNumber = 0;


	private GridPersonListAdapter seekPersonListAdapter;

	private InitLoadThread initLoadThread;
	
	private GridView mGridView;
	
	private Dialog mDialog;
	private BBLDao dao=null;
	private String nickname="帅哥";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.predestined_tj);
		dao=new BBLDao(EverDayActivity.this, null, null, 1);
		nickname=dao.queryUserByNewTime().getNickname();
		mDialog=new Dialog(EverDayActivity.this, R.style.theme_dialog_alert);
		mGridView=(GridView) findViewById(R.id.mgridView);
		seekPersonListAdapter = new GridPersonListAdapter(
				EverDayActivity.this, personlist);
		mGridView.setAdapter(seekPersonListAdapter);
		HelperUtil.customDialogShow(mDialog, EverDayActivity.this, "请稍候...");
		initLoadThread = new InitLoadThread();
		initLoadThread.start();
		
		Button btnConfirm=(Button) findViewById(R.id.btn_yjql);
		btnConfirm.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				try {
					// TODO Auto-generated method stub
					for (int i = 0; i < personlist.size(); i++) {
						Intent mIntent = new Intent(ReceiverConstant.MESSAGE_SEND_ACTION);
						mIntent.putExtra("toID", personlist.get(i).getUsername());
						mIntent.putExtra("nickname", nickname);
						mIntent.putExtra("message", "你好,我喜欢你这个类型的，我们可以相互了解一下吗");
						sendBroadcast(mIntent);
					} 
				} catch (Exception e) {
					// TODO: handle exception
				}
				handler.sendEmptyMessage(2);
				finish();  
			}
		});
	}
	private class InitLoadThread extends Thread {
		@Override
		public void run() {
			try {
				Map<String, String> map = new HashMap<String, String>();
				map.put("pagenumber", String.valueOf(PageNumber));
				String result = HelperUtil.postRequest(
						HttpConstantUtil.selectUserInfoIndexPageJQR, map);
				android.os.Message message = handler.obtainMessage();
				message.obj = result;
				message.what = 1;
				message.sendToTarget();
			} catch (Exception e) {
				// TODO: handle exception
				if(mDialog!=null)
				{
					mDialog.dismiss();
				}
			}
		}
	}

	private void parseJson(String result) {
		try {
			JSONArray jsonArray = new JSONArray(result);
			if (jsonArray.length() > 0) {
				if (PageNumber == 0 && personlist != null
						&& personlist.size() > 0) {
					personlist.clear();
				}
				for (int i = 0; i < jsonArray.length(); i++) {
					String username = jsonArray.optJSONObject(i).optString(
							"username");
					String year = jsonArray.optJSONObject(i).optString(
							"birthday");
					String photo = jsonArray.optJSONObject(i)
							.optString("photo");
					String monologue = jsonArray.optJSONObject(i).optString(
							"heartdubai");
					String place = jsonArray.optJSONObject(i)
							.optString("lives");
					String nickname = jsonArray.optJSONObject(i).optString(
							"nickname");
					String sex = jsonArray.optJSONObject(i).optString("sex");
					String maritalstatus = jsonArray.optJSONObject(i).optString("maritalstatus");
					int heartduibaistate = jsonArray.optJSONObject(i).optInt("heartduibaistate");
					String auth = jsonArray.optJSONObject(i).optString(
							"auth");
					UserInfo model = new UserInfo();
					model.setBirthday(year);
					model.setHeartdubai(monologue);
					model.setLives(place);
					model.setNickname(nickname);
					model.setPhoto(photo);
					//model.setState(state);
					model.setUsername(username);
					model.setAuth(auth);
					model.setSex(sex);
					model.setHeartduibaistate(heartduibaistate);
					model.setMaritalstatus(maritalstatus);
					personlist.add(model);
				}

			}
			if (mGridView.getAdapter() == null) {
				mGridView.setAdapter(seekPersonListAdapter);
				if (seekPersonListAdapter != null)
					seekPersonListAdapter.notifyDataSetChanged();
			} else {
				if (seekPersonListAdapter != null)
					seekPersonListAdapter.notifyDataSetChanged();
			}
			if(mDialog!=null)
			{
				mDialog.dismiss();
			}
		} catch (Exception e) {
			e.printStackTrace();
			if(mDialog!=null)
			{
				mDialog.dismiss();
			}
		}
	}

	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {

			switch (msg.what) {

			case 1:
				parseJson((String) msg.obj);
				break;
			case 2:
                new Handler().postDelayed(new Runnable() {
					
					@Override
					public void run() {
						// TODO Auto-generated method stub
						handler.sendEmptyMessage(3);
					}
				}, 6000);
				break;
			case 3:
				//startActivity(new Intent(EverDayActivity.this, VideoCallActivity.class));
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
			handler.sendEmptyMessage(2);
			finish();
		}
		return super.onKeyDown(keyCode, event);
	}

	
	
	
}
