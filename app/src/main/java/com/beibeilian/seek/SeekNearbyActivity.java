package com.beibeilian.seek;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;

import com.beibeilian.MainActivity;
import com.beibeilian.android.app.R;
import com.beibeilian.seek.adapter.SeekPersonListAdapter;
import com.beibeilian.seek.model.UserInfo;
import com.beibeilian.util.HelperUtil;
import com.beibeilian.util.HttpConstantUtil;
import com.beibeilian.util.PublicConstant;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;

public class SeekNearbyActivity extends Activity {
	private static List<UserInfo> personlist = new ArrayList<UserInfo>();

	private int PageNumber = 0;

	private ListView Listview;

	private SeekPersonListAdapter seekPersonListAdapter;

	private InitLoadThread initLoadThread;

	private Dialog dialog;

	private Button btnBack;

	private String locationProvider;

	private LocationManager locationManager;

	private void gpsRefresh() {

		// 获取地理位置管理器
		locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		List<String> providers = locationManager.getProviders(true);
		if (providers.contains(LocationManager.PASSIVE_PROVIDER)){
			locationProvider = LocationManager.PASSIVE_PROVIDER;
		}else if (providers.contains(LocationManager.GPS_PROVIDER)) {
			// 如果是GPS
			locationProvider = LocationManager.GPS_PROVIDER;
		} else if (providers.contains(LocationManager.NETWORK_PROVIDER)) {
			// 如果是Network
			locationProvider = LocationManager.NETWORK_PROVIDER;
		} else {
//			HelperUtil.totastShow("请打开手机定位功能！", getApplicationContext());
			HelperUtil.openGPS(SeekNearbyActivity.this);
		}
		// 获取Location
		try {
				Location location = locationManager.getLastKnownLocation(locationProvider);
			if (location != null) {
				// 不为空,显示地理位置经纬度
				lat = String.valueOf(location.getLatitude());
				lon = String.valueOf(location.getLongitude());
				if (HelperUtil.flagISNoNull(lat) && lat.equals("0.0") && HelperUtil.flagISNoNull(lon) && lon.equals("0.0")) {
					lat = "30.36343082758504";
					lon = "120.08120170819764";
					handler.sendEmptyMessage(10);
				} else {
					handler.sendEmptyMessage(10);
				}
			} else {
				lat = "30.36343082758504";
				lon = "120.08120170819764";
				handler.sendEmptyMessage(10);
			}
			// 监视地理位置变化
			locationManager.requestLocationUpdates(locationProvider, 10 * 60000, 1, locationListener);
		} catch (Exception e) {
			lat = "30.36343082758504";
			lon = "120.08120170819764";
			handler.sendEmptyMessage(10);
		}

	}

	private LocationListener locationListener = new LocationListener() {

		@Override
		public void onStatusChanged(String provider, int status, Bundle arg2) {

		}

		@Override
		public void onProviderEnabled(String provider) {

		}

		@Override
		public void onProviderDisabled(String provider) {

		}

		@Override
		public void onLocationChanged(Location location) {

			lat = String.valueOf(location.getLatitude());
			lon = String.valueOf(location.getLongitude());

			if (HelperUtil.flagISNoNull(lat) && lat.equals("0.0") && HelperUtil.flagISNoNull(lon)
					&& lon.equals("0.0")) {
				lat = "43.114638";
				lon = "128.913603";
				handler.sendEmptyMessage(10);
			} else {
				handler.sendEmptyMessage(10);
			}

		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.seek_nearby);
		Listview = (ListView) findViewById(R.id.listview);
		btnBack = (Button) findViewById(R.id.btnBack);
		dialog = new Dialog(SeekNearbyActivity.this, R.style.theme_dialog_alert);
		seekPersonListAdapter = new SeekPersonListAdapter(SeekNearbyActivity.this, personlist);
		HelperUtil.customDialogShow(dialog, SeekNearbyActivity.this, "正在加载中...");

		btnBack.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				startActivity(new Intent(SeekNearbyActivity.this, MainActivity.class));
				finish();
			}
		});
		
		gpsRefresh();

	}

	private String lat;
	private String lon;

	private class InitLoadThread extends Thread {
		@Override
		public void run() {
			try {
				Map<String, String> map = new HashMap<String, String>();
				map.put("pagenumber", String.valueOf(PageNumber));
				map.put("lat", lat);
				map.put("lon", lon);
				android.os.Message message = handler.obtainMessage();
				message.obj = HelperUtil.postRequest(HttpConstantUtil.FindNearbyList, map);
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
					// String state = jsonArray.optJSONObject(i)
					// .optString("state");
					String monologue = jsonArray.optJSONObject(i).optString("heartdubai");
					String place = jsonArray.optJSONObject(i).optString("lives");
					String nickname = jsonArray.optJSONObject(i).optString("nickname");
					// String logintime = jsonArray.optJSONObject(i).optString(
					// "time");
					String distance = jsonArray.optJSONObject(i).optString("distance");
					String sex = jsonArray.optJSONObject(i).optString("sex");

					int heartduibaistate = jsonArray.optJSONObject(i).optInt("heartduibaistate");
					UserInfo model = new UserInfo();
					model.setBirthday(year);
					model.setHeartdubai(monologue);
					model.setLives(place);
					model.setNickname(nickname);
					model.setPhoto(photo);
					// model.setState(state);
					model.setUsername(username);
					// model.setTime(logintime);
					model.setDistance(distance);
					model.setSex(sex);
					model.setHeartduibaistate(heartduibaistate);
					personlist.add(model);
				}
				if (Listview.getAdapter() == null) {
					Listview.setAdapter(seekPersonListAdapter);
				} else {
					if (seekPersonListAdapter != null) {
						seekPersonListAdapter.notifyDataSetChanged();
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

	private int RECORDPOST = 0;
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
			case 10:
				if (RECORDPOST != 0)
					return;
				else {
					if (initLoadThread != null) {
						initLoadThread.interrupt();
					}
					initLoadThread = new InitLoadThread();
					initLoadThread.start();
					RECORDPOST = 1;
				}
				break;
			case 11:
				new Handler().postDelayed(new Runnable() {

					@Override
					public void run() {
						// TODO Auto-generated method stub
						if (dialog != null) {
							dialog.dismiss();
						}
						// HelperUtil.totastShow("请检查获取位置权限是否被第三方软件限制或您的附近没有人哦",
						// SeekNearbyActivity.this);
					}
				}, 10000);

				break;
			default:
				break;
			}

		}
	};

	@Override
	public void onDestroy() {
		super.onDestroy();
		RECORDPOST = 0;
		if (dialog != null) {
			dialog.dismiss();
		}

		if (initLoadThread != null) {
			initLoadThread.interrupt();
		}
		if(locationManager!=null){  
            //移除监听器  
            locationManager.removeUpdates(locationListener);  
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


}
