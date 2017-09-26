package com.beibeilian.me;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;

import com.beibeilian.android.app.R;
import com.beibeilian.constant.BBLConstant;
import com.beibeilian.db.BBLDao;
import com.beibeilian.model.PayRule;
import com.beibeilian.orderdialog.OrderDailog2;
import com.beibeilian.receiver.ReceiverConstant;
import com.beibeilian.util.ExitApplication;
import com.beibeilian.util.HelperUtil;
import com.beibeilian.util.HttpConstantUtil;

import android.app.Activity;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class MeMemberActivity extends Activity {

	private BBLDao dao;
	private Button btnBack, btnMember;
	private TextView tv_ts;
	private Dialog mDialog;
	private IntentFilter mIntentFilter;
	private RefreshReceiver mRefreshReceiver;
	private String username;
	private RelativeLayout rl_order_one_ck;

	private RelativeLayout rl_order_two_ck;

	private RelativeLayout rl_order_three_ck;

	private RelativeLayout rl_order_four_ck;

	private RelativeLayout rl_order_five_ck;

	
	private ImageView img_one, img_two, img_three, img_four,img_five;
	
	private int select_value = 1;// 10元
	
	private String price_15 = "5", price_30 = "10", price_180 = "50", price_360 = "100",price_720="200";


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.me_member);
		ExitApplication.addActivity(MeMemberActivity.this);
		mDialog = new Dialog(MeMemberActivity.this, R.style.theme_dialog_alert);
		dao = new BBLDao(MeMemberActivity.this, null, null, 1);
		PayRule mPayRule = dao.findPayRule();
		if (mPayRule != null && HelperUtil.flagISNoNull(mPayRule.getPrice_15())) {
			price_15 = mPayRule.getPrice_15();
			price_30 = mPayRule.getPrice_30();
			price_180 = mPayRule.getPrice_180();
			price_360 = mPayRule.getPrice_360();
			price_720=  mPayRule.getMarriage();
		}
		username = dao.queryUserByNewTime().getUsername();
		btnBack = (Button) findViewById(R.id.btnBack);
		btnMember = (Button) findViewById(R.id.btn_member_kaitong_id);

		tv_ts = (TextView) findViewById(R.id.tv_ts_id);

		rl_order_one_ck = (RelativeLayout) findViewById(R.id.rl_order_one);
		rl_order_two_ck = (RelativeLayout) findViewById(R.id.rl_order_two);
		rl_order_three_ck = (RelativeLayout) findViewById(R.id.rl_order_three);
		rl_order_four_ck = (RelativeLayout) findViewById(R.id.rl_order_four);
		rl_order_five_ck = (RelativeLayout) findViewById(R.id.rl_order_five);
		img_one = (ImageView) findViewById(R.id.img_one);
		img_two = (ImageView) findViewById(R.id.img_two);
		img_three = (ImageView) findViewById(R.id.img_three);
		img_four = (ImageView) findViewById(R.id.img_four);
		img_five = (ImageView) findViewById(R.id.img_five);
		TextView tv15=(TextView) findViewById(R.id.tv_price_15);
		TextView tv30=(TextView) findViewById(R.id.tv_price_30);
		TextView tv180=(TextView) findViewById(R.id.tv_price_180);
		TextView tv360=(TextView) findViewById(R.id.tv_price_360);
		TextView tv720=(TextView) findViewById(R.id.tv_price_720);

		tv15.setText(price_15+"元/15天");
		tv30.setText(price_30+"元/永久会员+送50元话费");
		tv180.setText(price_180+"元/半年");
		tv360.setText(price_360+"元/1年");
		tv720.setText(price_720+"元/2年");

		mIntentFilter = new IntentFilter();
		mRefreshReceiver = new RefreshReceiver();
		mIntentFilter.addAction(ReceiverConstant.ME_MEMBER_REFESH_ACTION);
		registerReceiver(mRefreshReceiver, mIntentFilter);
		rl_order_one_ck.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				img_one.setImageDrawable(getResources().getDrawable(R.drawable.select));
				img_two.setImageDrawable(getResources().getDrawable(R.drawable.unenable));
				img_three.setImageDrawable(getResources().getDrawable(R.drawable.unenable));
				img_four.setImageDrawable(getResources().getDrawable(R.drawable.unenable));
				img_five.setImageDrawable(getResources().getDrawable(R.drawable.unenable));

				select_value = 0;

			}
		});
		rl_order_two_ck.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				img_one.setImageDrawable(getResources().getDrawable(R.drawable.unenable));
				img_two.setImageDrawable(getResources().getDrawable(R.drawable.select));
				img_three.setImageDrawable(getResources().getDrawable(R.drawable.unenable));
				img_four.setImageDrawable(getResources().getDrawable(R.drawable.unenable));
				img_five.setImageDrawable(getResources().getDrawable(R.drawable.unenable));

				select_value = 1;

			}
		});
		rl_order_three_ck.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				img_one.setImageDrawable(getResources().getDrawable(R.drawable.unenable));
				img_two.setImageDrawable(getResources().getDrawable(R.drawable.unenable));
				img_three.setImageDrawable(getResources().getDrawable(R.drawable.select));
				img_four.setImageDrawable(getResources().getDrawable(R.drawable.unenable));
				img_five.setImageDrawable(getResources().getDrawable(R.drawable.unenable));

				select_value = 2;

			}
		});
		rl_order_four_ck.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				img_one.setImageDrawable(getResources().getDrawable(R.drawable.unenable));
				img_two.setImageDrawable(getResources().getDrawable(R.drawable.unenable));
				img_three.setImageDrawable(getResources().getDrawable(R.drawable.unenable));
				img_four.setImageDrawable(getResources().getDrawable(R.drawable.select));
				img_five.setImageDrawable(getResources().getDrawable(R.drawable.unenable));

				select_value = 3;

			}
		});
		
		rl_order_five_ck.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				img_one.setImageDrawable(getResources().getDrawable(R.drawable.unenable));
				img_two.setImageDrawable(getResources().getDrawable(R.drawable.unenable));
				img_three.setImageDrawable(getResources().getDrawable(R.drawable.unenable));
				img_four.setImageDrawable(getResources().getDrawable(R.drawable.unenable));
				img_five.setImageDrawable(getResources().getDrawable(R.drawable.select));

				
				select_value = 4;

			}
		});
		

		btnBack.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});
		btnMember.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(MeMemberActivity.this,
						OrderDailog2.class);
				intent.putExtra("member_value", select_value+"");
				startActivity(intent);
			}
		});
		checkMember();
	}

	private class RefreshReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			if (intent.getAction().equals(
					ReceiverConstant.ME_MEMBER_REFESH_ACTION)) {
				checkMember();
			}
		}

	}

	private int remaintime = 0;

	private void checkMember() {
		HelperUtil.customDialogShow(mDialog, MeMemberActivity.this, "请稍候...");

		new Thread(new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				try {
					Map<String, String> map = new HashMap<String, String>();
					map.put("username", username);
					JSONObject jsonObject = new JSONObject(HelperUtil
							.postRequest(HttpConstantUtil.CheckIsOrNOMember, map));
					if (jsonObject.optInt("result") == BBLConstant.MEMBER_STATE_YES) {
						remaintime = jsonObject.optInt("remaintime");
						handler.sendEmptyMessage(1);
					} else if (jsonObject.optInt("result") == BBLConstant.MEMBER_STATE_OUT) {
						handler.sendEmptyMessage(2);
					} else {
						handler.sendEmptyMessage(0);
					}
				} catch (Exception e) {
					handler.sendEmptyMessage(-1);
				}
			}
		}).start();
	}

	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {

			switch (msg.what) {
			case 0:
				if (mDialog != null)
					mDialog.dismiss();
				tv_ts.setText("您还没开通VIP会员");
				break;
			case 1:
				if (mDialog != null)
					mDialog.dismiss();
				tv_ts.setText("您的VIP会员剩余"+remaintime+"天");
				btnMember.setText("VIP会员续费");
				break;
			case 2:
				if (mDialog != null)
					mDialog.dismiss();
				tv_ts.setText("您的VIP会员已过期");
				break;
			case -1:
				if (mDialog != null)
					mDialog.dismiss();
				HelperUtil.totastShow("网请检查网络是否可用或稍候重试",
						getApplicationContext());
				break;

			default:
				break;
			}

		}
	};

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (mRefreshReceiver != null) {
			unregisterReceiver(mRefreshReceiver);
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			finish();
		}
		return false;
	}

}
