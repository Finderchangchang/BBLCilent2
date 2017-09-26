package com.beibeilian.predestined;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;

import com.beibeilian.db.BBLDao;
import com.beibeilian.seek.SeekMeBallActivity;
import com.beibeilian.util.HelperUtil;
import com.beibeilian.util.HttpConstantUtil;
import com.beibeilian.util.PublicConstant;
import com.beibeilian.android.app.R;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;

public class PickBallActivty extends Activity {
	private AnimationDrawable ad;
	private ImageView pick_spray1, pick_spray2, close, voice_msg, pick_spray3;
	private RelativeLayout pick_up_layout;
	int hour, minute, sec;
	BBLDao dao;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.pick_up_bottle);
		dao = new BBLDao(PickBallActivty.this, null, null, 1);
		// ��ʼ��
		pick_spray1 = (ImageView) findViewById(R.id.pick_spray1);
		pick_spray2 = (ImageView) findViewById(R.id.pick_spray2);
		pick_spray3 = (ImageView) findViewById(R.id.pick_spray3);

		voice_msg = (ImageView) findViewById(R.id.bottle_picked_voice_msg);
		close = (ImageView) findViewById(R.id.bottle_picked_close);
		pick_up_layout = (RelativeLayout) findViewById(R.id.pick_up_layout);

		// ��ȡϵͳʱ��
		Calendar c = Calendar.getInstance();
		c.setTimeInMillis(System.currentTimeMillis());
		hour = c.get(Calendar.HOUR_OF_DAY);
		minute = c.get(Calendar.MINUTE);
		sec = c.get(Calendar.SECOND);
		// if(hour>=18 || hour<=6){
		// pick_up_layout.setBackgroundResource(R.drawable.bottle_pick_bg_spotlight_night);
		// }
		// else{
		// pick_up_layout.setBackgroundResource(R.drawable.bottle_pick_bg_spotlight_day);
		// }
		close.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});
		voice_msg.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
               startActivity(new Intent(PickBallActivty.this,SeekMeBallActivity.class));
			}
		});
		new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				try {
					Thread.sleep(2000);
					Map<String, String> map = new HashMap<String, String>();
					map.put("username", dao.queryUserByNewTime().getUsername());
					String result = HelperUtil.postRequest(HttpConstantUtil.Pickball, map);
					if(HelperUtil.flagISNoNull(result))
					{
						JSONObject jsonObject=new JSONObject(result);
						if(jsonObject.optString("state").equals("1000"))
						handler.sendEmptyMessage(1);
					}
				} catch (Exception e) {
					e.printStackTrace();
					handler.sendEmptyMessage(-1);
				}
			}
		}).start();

		new Handler().postDelayed(new Runnable() {

			@Override
			public void run() {
				pick_spray1.setVisibility(View.VISIBLE);
				ad.setOneShot(true);
				ad.start();
			}
		}, 1000);
		new Handler().postDelayed(new Runnable() {

			@Override
			public void run() {
				pick_spray1.setVisibility(View.GONE);
				pick_spray2.setVisibility(View.VISIBLE);
				ad.setOneShot(true);
				ad.start();
			}
		}, 2000);
		new Handler().postDelayed(new Runnable() {

			@Override
			public void run() {
				pick_spray1.setVisibility(View.GONE);
				pick_spray2.setVisibility(View.GONE);
				pick_spray3.setVisibility(View.VISIBLE);
				ad.setOneShot(true);
				ad.start();
			}
		}, 3000);

		new Handler().postDelayed(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				pick_spray1.setVisibility(View.GONE);
				pick_spray2.setVisibility(View.GONE);
				pick_spray3.setVisibility(View.GONE);
				voice_msg.setVisibility(View.VISIBLE);
				doStartAnimation(R.anim.pick_up_scale);
				close.setVisibility(View.VISIBLE);
			}
		}, 4000);

	}
	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {

			switch (msg.what) {

			case 1:
				HelperUtil.totastShow("���ź�,û�м�", getApplicationContext());
				finish();
				break;
			case -1:
				HelperUtil.totastShow(PublicConstant.ToastCatch, getApplicationContext());
				finish();
				break;
			default:
				break;
			}

		}
	};
	private void doStartAnimation(int animId) {
		Animation animation = AnimationUtils.loadAnimation(this, animId);
		voice_msg.startAnimation(animation);
	}

	// ������������
	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		// TODO Auto-generated method stub
		super.onWindowFocusChanged(hasFocus);
		//ad = (AnimationDrawable) getResources().getDrawable(R.anim.pick_up_spray);
		if (pick_spray1 != null && pick_spray2 != null) {
			pick_spray1.setBackgroundDrawable(ad);
			pick_spray2.setBackgroundDrawable(ad);
		}

	}

}
