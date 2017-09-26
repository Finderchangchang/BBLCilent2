package com.beibeilian;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import com.beibeilian.db.BBLDao;
import com.beibeilian.me.MeDataActivity;
import com.beibeilian.service.CoreIMService;
import com.beibeilian.util.ExitApplication;
import com.beibeilian.util.HelperUtil;
import com.beibeilian.util.HttpConstantUtil;
import com.beibeilian.util.PublicConstant;
import com.beibeilian.android.app.R;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.telephony.TelephonyManager;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

public class RegsiterTwoActivity extends Activity implements OnClickListener {
	private Button finishbtn;
	private Spinner yearselectsp;
	private Spinner sexselectsp;
	private List<String> yearlist = new ArrayList<String>();
	private List<String> sexlist = new ArrayList<String>();
	private ArrayAdapter<String> yearadapter;
	private ArrayAdapter<String> sexadapter;
	
	private Button btnBack;

	private Dialog dialog;
	
	private BBLDao dao;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.regsiter_two);
		ExitApplication.addActivity(RegsiterTwoActivity.this);
		dialog = new Dialog(RegsiterTwoActivity.this,
				R.style.theme_dialog_alert);
		dao=new BBLDao(RegsiterTwoActivity.this,null,null,1);
		finishbtn = (Button) findViewById(R.id.regsiter_finishid);
		yearselectsp = (Spinner) findViewById(R.id.yearspinnerid);
		sexselectsp = (Spinner) findViewById(R.id.sexspinnerid);
		btnBack=(Button)findViewById(R.id.btnBack);
		btnBack.setOnClickListener(this);
		finishbtn.setOnClickListener(this);
		for (int i = 16; i <= 80; i++) {
			yearlist.add(String.valueOf(i) + "��");
		}
		sexlist.add("��");
		sexlist.add("Ů");
		yearadapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, yearlist);
		yearadapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		yearselectsp.setAdapter(yearadapter);
		sexadapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, sexlist);
		sexadapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		sexselectsp.setAdapter(sexadapter);
		yearselectsp.setSelection(5);
		sexselectsp.setSelection(1);

	}
	String imie="";
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.regsiter_finishid:
            
			final String year = yearselectsp.getSelectedItem().toString();
			final String sex = sexselectsp.getSelectedItem().toString();
			final String user = getIntent().getStringExtra("username");
			final String pass = getIntent().getStringExtra("password");
			try
			{
			TelephonyManager tm = (TelephonyManager) this
					.getSystemService(TELEPHONY_SERVICE);
			imie = tm.getDeviceId();
			}
			catch (Exception e) {
				// TODO: handle exception
			}
			HelperUtil.customDialogShow(dialog, RegsiterTwoActivity.this,"����ע����...");
			new Thread(new Runnable() {

				@Override
				public void run() {
					// TODO Auto-generated method stub
					if (user != null && pass != null) {
						Map<String, String> map = new HashMap<String, String>();
						map.put("username", user);
						map.put("password", pass);
						map.put("year", year);
						map.put("sex", sex);
						map.put("imie", imie);
						map.put("ip", HelperUtil.getLocalIpAddress());
						map.put("channel",HelperUtil.getChannelId(RegsiterTwoActivity.this) );
						map.put("appid",HelperUtil.getAPPlId(RegsiterTwoActivity.this));
						JSONObject jsonobject = null;
						try {
							jsonobject =new JSONObject(HelperUtil.postRequest(HttpConstantUtil.Regsiter, map));
						if (jsonobject.length() > 0) {
							int res = jsonobject.getInt("result");						
							if (res == 1) {
								if(sex.equals("��"))
								{
									dao.initUser(user,"˧��" , pass, "1",sex);
									dao.updatePhoto(user,"","˧��","��");
								}
								else
								{
									dao.initUser(user,"��Ů" , pass, "1",
											sex);
									dao.updatePhoto(user,"","��Ů","Ů");
								}
								handler.sendEmptyMessage(1);
							} else if (res == 2) {
								handler.sendEmptyMessage(2);
							}	
						}
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
							handler.sendEmptyMessage(-1);
						}
					}
				}
			}).start();

			break;
		case R.id.btnBack:
//			startActivity(new Intent(RegsiterTwoActivity.this,RegsiterOneActivity.class));
			finish();
			break;
		default:
			break;
		}
	}

	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case 1:
				if(dialog!=null)
				{
					dialog.dismiss();
				}
				HelperUtil.totastShow("ע��ɹ�,���׾������ƻ�������",RegsiterTwoActivity.this);
				startService(new Intent(RegsiterTwoActivity.this, CoreIMService.class));
				Intent intent = new Intent(RegsiterTwoActivity.this,MainActivity.class);
				startActivity(intent);
				finish();
			break;
			case -1:
				if(dialog!=null)
				{
					dialog.dismiss();
				}
				HelperUtil.totastShow(PublicConstant.ToastCatch,RegsiterTwoActivity.this);
			break;
			case 2:
				if(dialog!=null)
				{
					dialog.dismiss();
				}
				HelperUtil.totastShow("�˺��ѱ�ע��",RegsiterTwoActivity.this);
				break;
			default:
				break;
			}
		}
	};
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
//			startActivity(new Intent(RegsiterTwoActivity.this,RegsiterOneActivity.class));
			finish();
		}
		return false;
	}
}
