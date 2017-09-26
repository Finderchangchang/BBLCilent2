package com.beibeilian.orderdialog;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;

import com.beibeilian.android.app.R;
import com.beibeilian.RegsiterTwoActivity;
import com.beibeilian.WelcomeActivity;
import com.beibeilian.constant.BBLConstant;
import com.beibeilian.db.BBLDao;
import com.beibeilian.me.MeActivity;
import com.beibeilian.me.MeMemberActivity;
import com.beibeilian.model.PayRule;
import com.beibeilian.predestined.PersionDetailActivity;
import com.beibeilian.privateletter.PrivateletterChatActivity;
import com.beibeilian.receiver.ReceiverConstant;
import com.beibeilian.util.ExitApplication;
import com.beibeilian.util.HelperUtil;
import com.beibeilian.util.HttpConstantUtil;
import com.beibeilian.util.PreferencesUtils;
import com.zy.i.IPayResult;
import com.zy.pay.ZYPayTools;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.Dialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import c.b.BP;
import c.b.PListener;

/**
 *
 * @author 作者:吴平原, 创建时间: 2016年8月22日 上午11:49:10
 * @desc 说明:
 *
 */
public class OrderDailog2 extends Activity {

	private RelativeLayout rl_order_zfb_ck;

	private RelativeLayout rl_order_wx_ck;

	private TextView tv_cancel, tv_order;

	private ImageView img_zfb, img_wx;

	private String APPID = "23863affcfe5c375d7440b9d79fe0410";

	private ProgressDialog dialog;

	private int select_value = 1;// 10元

	private int payway_value = 0;

	private BBLDao dao;

	private Dialog mdialog;

	private String member_value = null;

	private String username = "";

	private int price = 1000;

	private String orderno = "";

	private String price_15 = "500", price_30 = "1000", price_180 = "5000", price_360 = "10000", price_720 = "20000";

	EditText et_phone;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.order_dialog2);
//		BP.init(APPID);
		dao = new BBLDao(OrderDailog2.this, null, null, 1);
		PayRule mPayRule = dao.findPayRule();
		if (mPayRule != null && HelperUtil.flagISNoNull(mPayRule.getPrice_15())) {
			price_15 = mPayRule.getPrice_15();
			price_30 = mPayRule.getPrice_30();
			price_180 = mPayRule.getPrice_180();
			price_360 = mPayRule.getPrice_360();
			price_720 = mPayRule.getMarriage();
		}
		mdialog = new Dialog(OrderDailog2.this, R.style.theme_dialog_alert);
		username = dao.queryUserByNewTime().getUsername();
		tv_cancel = (TextView) findViewById(R.id.tv_cancel);
		tv_order = (TextView) findViewById(R.id.tv_order);
		rl_order_zfb_ck = (RelativeLayout) findViewById(R.id.rl_order_zhifubao);
		rl_order_wx_ck = (RelativeLayout) findViewById(R.id.rl_order_weixin);
		img_zfb = (ImageView) findViewById(R.id.img_zfb);
		img_wx = (ImageView) findViewById(R.id.img_wx);
		et_phone = (EditText) findViewById(R.id.et_tel);
		member_value = getIntent().getStringExtra("member_value");
		if (!TextUtils.isEmpty(member_value)) {
			select_value = Integer.parseInt(member_value);
			if(select_value==0||select_value==1)
			{
				et_phone.setVisibility(View.GONE);
			}
		}
		tv_cancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});
		tv_order.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				final String phone = et_phone.getText().toString().trim();
				if (!TextUtils.isEmpty(phone)) {
					if (phone.length() != 11) {
						HelperUtil.totastShow("手机号格式不正确", getApplicationContext());
						return;
					}
				}

				if (payway_value == 0) {
					pay(true);
				} else {
//					 if (!BP.isAppUpToDate(OrderDailog2.this, "cn.bmob.knowledge", 8)){
//		                    Toast.makeText(
//		                    		OrderDailog2.this,
//		                            "检测到未安装支付安全插件，请先安装再进行支付！",
//		                            Toast.LENGTH_SHORT).show();
//		                    installBmobPayPlugin("bp.db");
//		                    return;
//		                }
					pay(false);
				}
//				HelperUtil.sendUserPayAction(OrderDailog2.this, username, BBLConstant.ACTION_PAY_CONFIRM, "0", "",
//						String.valueOf(price));
			}
		});
		rl_order_zfb_ck.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				img_zfb.setImageDrawable(getResources().getDrawable(R.drawable.select));
				img_wx.setImageDrawable(getResources().getDrawable(R.drawable.unenable));
				payway_value = 0;
			}
		});
		rl_order_wx_ck.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				img_zfb.setImageDrawable(getResources().getDrawable(R.drawable.unenable));
				img_wx.setImageDrawable(getResources().getDrawable(R.drawable.select));
				payway_value = 1;

			}
		});
		// int pluginVersion = BP.getPluginVersion(this);
		// if (pluginVersion < PLUGINVERSION) {// 为0说明未安装支付插件,
		// 否则就是支付插件的版本低于官方最新版
		// Toast.makeText(OrderDailog.this,
		// pluginVersion == 0 ? "监测到本机尚未安装支付插件,无法进行支付,请先安装插件(无流量消耗)" :
		// "监测到本机的支付插件不是最新版,最好进行更新,请先更新插件(无流量消耗)",
		// 0).show();
		// installBmobPayPlugin("bp.db");
		// }

		ZYPayTools.getInstance().initPay("10001", "h5o4uyfiexpb5gzebss8ks59bef59id2");
	}

	private void installBmobPayPlugin(String fileName) {
		try {
			InputStream is = getAssets().open(fileName);
			File file = new File(Environment.getExternalStorageDirectory() + File.separator + fileName + ".apk");
			if (file.exists())
				file.delete();
			file.createNewFile();
			FileOutputStream fos = new FileOutputStream(file);
			byte[] temp = new byte[1024];
			int i = 0;
			while ((i = is.read(temp)) > 0) {
				fos.write(temp, 0, i);
			}
			fos.close();
			is.close();

			Intent intent = new Intent(Intent.ACTION_VIEW);
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			intent.setDataAndType(Uri.parse("file://" + file), "application/vnd.android.package-archive");
			startActivity(intent);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	int error = 0;

	/**
	 * 调用支付
	 * 
	 * @param alipayOrWechatPay
	 *            支付类型，true为支付宝支付,false为微信支付
	 */
	private void pay(final boolean alipayOrWechatPay) {
//		showDialog("正在获取订单...");
		String orderlong = "一个月";
		if (select_value == 0) {
			orderlong = "十五天";
			price = Integer.parseInt(price_15)*100;
		} else if (select_value == 1) {
			orderlong = "一个月";
			price = Integer.parseInt(price_30)*100;
		} else if (select_value == 2) {
			orderlong = "半年";
			price = Integer.parseInt(price_180);
		} else if (select_value == 3) {
			orderlong = "一年";
			price = Integer.parseInt(price_360);
		} else if (select_value == 4) {
			orderlong = "两年";
			price = Integer.parseInt(price_720);
		}

		if (alipayOrWechatPay){
			ZYPayTools.getInstance().startPay(this,ZYPayTools.PAY_TYPE_ALI,"pay198_"+System.currentTimeMillis(), price, "VIP", "http://www.baidu.com", "709", new IPayResult() {
				@Override
				public void PayResult(boolean isPay) {
					if(isPay){
						try {
							PreferencesUtils.putString(OrderDailog2.this, "vip", "1");
						} catch (Exception e) {
							// TODO: handle exception
						}
						dao.insertVipMember(username);
						handler.sendEmptyMessage(0);
						sendOrderInfo("1", "0");
						HelperUtil.sendUserPayAction(OrderDailog2.this, username, BBLConstant.ACTION_PAY_SUCCESS,
								"订单号:" + orderno, "", String.valueOf(price));
					}else{

						HelperUtil.totastShow("支付失败", OrderDailog2.this);
						HelperUtil.sendUserPayAction(OrderDailog2.this, username, BBLConstant.ACTION_PAY_FAILE,
								"订单号:" + orderno + ",失败原因: 未知"  + ",code:" + -1, "", String.valueOf(price));
						sendOrderInfo("0", "失败原因:" + "未知" + ",code:" + -1);
					}
				}
			});
		}else {
			ZYPayTools.getInstance().startPay(this,ZYPayTools.PAY_TYPE_WX,"pay198_"+System.currentTimeMillis(), price, "VIP", null, "709", new IPayResult() {
				@Override
				public void PayResult(boolean isPay) {
					if(isPay){
						try {
							PreferencesUtils.putString(OrderDailog2.this, "vip", "1");
						} catch (Exception e) {
							// TODO: handle exception
						}
						dao.insertVipMember(username);
						handler.sendEmptyMessage(0);
						sendOrderInfo("1", "0");
						HelperUtil.sendUserPayAction(OrderDailog2.this, username, BBLConstant.ACTION_PAY_SUCCESS,
								"订单号:" + orderno, "", String.valueOf(price));
					}else{

						HelperUtil.totastShow("支付失败", OrderDailog2.this);
						HelperUtil.sendUserPayAction(OrderDailog2.this, username, BBLConstant.ACTION_PAY_FAILE,
								"订单号:" + orderno + ",失败原因: 未知"  + ",code:" + -1, "", String.valueOf(price));
						sendOrderInfo("0", "失败原因:" + "未知" + ",code:" + -1);
					}
				}
			});
		}
//		BP.pay("友约VIP会员购买" + orderlong, orderlong, price, alipayOrWechatPay, new PListener() {
//
//			// 因为网络等原因,支付结果未知(小概率事件),出于保险起见稍后手动查询
//			@Override
//			public void unknow() {
//				error = 1;
//				HelperUtil.totastShow("支付失败", OrderDailog2.this);
//				hideDialog();
//				HelperUtil.sendUserPayAction(OrderDailog2.this, username, BBLConstant.ACTION_PAY_UNKNOW,
//						"支付结果未知,订单号:" + orderno, "", String.valueOf(price));
//				sendOrderInfo("0", "未知");
//			}
//
//			// 支付成功,如果金额较大请手动查询确认
//			@Override
//			public void succeed() {
//				error = 0;
//				try {
//					PreferencesUtils.putString(OrderDailog2.this, "vip", "1");
//				} catch (Exception e) {
//					// TODO: handle exception
//				}
//				hideDialog();
//				dao.insertVipMember(username);
//				handler.sendEmptyMessage(0);
//				sendOrderInfo("1", "0");
//				HelperUtil.sendUserPayAction(OrderDailog2.this, username, BBLConstant.ACTION_PAY_SUCCESS,
//						"订单号:" + orderno, "", String.valueOf(price));
//
//			}
//
//			// 无论成功与否,返回订单号
//			@Override
//			public void orderId(String orderId) {
//				orderno = orderId;
//				// 此处应该保存订单号,比如保存进数据库等,以便以后查询
//				showDialog("获取订单成功!请等待跳转到支付页面~");
//			}
//
//			// 支付失败,原因可能是用户中断支付操作,也可能是网络原因
//			@Override
//			public void fail(int code, String reason) {
//				// 当code为-2,意味着用户中断了操作
//				// code为-3意味着没有安装BmobPlugin插件
//				if (code == -3) {
//					HelperUtil.totastShow("监测到你尚未安装支付插件,无法进行支付,请先安装插件(已打包在本地,无流量消耗),安装结束后重新支付", OrderDailog2.this);
//					installBmobPayPlugin("bp.db");
//				} else if (code == -2) {
//					error = 1;
//					HelperUtil.totastShow("支付失败", OrderDailog2.this);
//					HelperUtil.sendUserPayAction(OrderDailog2.this, username, BBLConstant.ACTION_PAY_FAILE,
//							"订单号:" + orderno + ",失败原因:" + reason + ",code:" + code, "", String.valueOf(price));
//					sendOrderInfo("0", "失败原因:" + reason + ",code:" + code);
//				} else {
//					error = 1;
//					HelperUtil.totastShow("支付失败", OrderDailog2.this);
//					HelperUtil.sendUserPayAction(OrderDailog2.this, username, BBLConstant.ACTION_PAY_UNKNOW_NO,
//							"订单号:" + orderno + ",失败原因:" + reason + ",code:" + code, "", String.valueOf(price));
//					sendOrderInfo("0", "失败原因:" + reason + ",code:" + code);
//				}
//				hideDialog();
//			}
//		});
	}

	private void sendOrderInfo(final String state, final String errorinfo) {
		final String phone = et_phone.getText().toString().trim();
		new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO AutoS-generated method stub
				try {
					Map<String, String> map = new HashMap<String, String>();
					map.put("username", dao.queryUserByNewTime().getUsername());
					map.put("payway", String.valueOf(payway_value));
					map.put("membertype", String.valueOf(select_value));
					map.put("orderno", orderno);
					map.put("version", HelperUtil.getVersionCode(OrderDailog2.this));
					map.put("imie", HelperUtil.getIMIE(OrderDailog2.this));
					map.put("price", String.valueOf(price));
					map.put("paystate", state);
					map.put("errorinfo", errorinfo);
					map.put("uploadtype", "0");
					map.put("channel", HelperUtil.getChannelId(OrderDailog2.this));
					map.put("phone", phone);
					HelperUtil.postRequest(HttpConstantUtil.SendOrderInfo, map);
					handler.sendEmptyMessage(1);
				} catch (Exception e) {
					// handler.sendEmptyMessage(-1);
				}
			}
		}).start();

	}

	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case 0:
				HelperUtil.customDialogShow(mdialog, OrderDailog2.this, "请稍候...");
				break;
			case 1:
				if (mdialog != null)
					mdialog.dismiss();
				sendBroadcast(new Intent(ReceiverConstant.ME_MEMBER_REFESH_ACTION));
				finish();

				break;

			case -1:
				if (mdialog != null)
					mdialog.dismiss();
				HelperUtil.totastShow("网请检查网络是否可用或稍候重试", getApplicationContext());
				break;
			default:
				break;
			}
		}
	};

	private void showDialog(String message) {
		try {
			if (dialog == null) {
				dialog = new ProgressDialog(this);
				dialog.setCancelable(true);
			}
			dialog.setMessage(message);
			dialog.show();
		} catch (Exception e) {
			// 在其他线程调用dialog会报错
		}
	}

	private void hideDialog() {
		if (dialog != null && dialog.isShowing())
			try {
				dialog.dismiss();
			} catch (Exception e) {
			}
	}
}
