package com.beibeilian.seek;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.beibeilian.MainActivity;
import com.beibeilian.db.BBLDao;
import com.beibeilian.me.dialog.MeDataDialogActivity;
import com.beibeilian.receiver.ReceiverConstant;
import com.beibeilian.util.ExitApplication;
import com.beibeilian.util.HelperUtil;
import com.beibeilian.util.PublicConstant;
import com.beibeilian.android.app.R;
import android.view.View.OnClickListener;
public class SeekSearchActivity extends Activity{


	private IntentFilter intentFilter;
	
	private DialogReceiver dialogReceiver;
	
	private Button btnBack;
	
	private TextView tv_province;
	
	private TextView tv_year;
	
	
	private Button btnSearch;
	
	private BBLDao dao;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.seek_search);
		ExitApplication.addActivity(this);
		dao=new BBLDao(SeekSearchActivity.this,null,null,1);
		btnBack=(Button)findViewById(R.id.btnBack);
		tv_province=(TextView)findViewById(R.id.tv_province_id);
		tv_year=(TextView)findViewById(R.id.tv_year_id);
		btnSearch=(Button)findViewById(R.id.btn_search_id);
        dialogReceiver=new DialogReceiver();
		intentFilter = new IntentFilter();
		intentFilter.addAction(ReceiverConstant.MeDataDialogPassValue_ACTION);
		registerReceiver(dialogReceiver, intentFilter);
		tv_province.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(SeekSearchActivity.this,
						MeDataDialogActivity.class);
				intent.putExtra("Tag", PublicConstant.MeDataDialogProvinceTag);
				startActivity(intent);
			}
		});
		
		tv_year.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(SeekSearchActivity.this,
						MeDataDialogActivity.class);
				intent.putExtra("Tag", PublicConstant.MeDataDialogConditionYearTag);
				startActivity(intent);
			}
		});
//	     tv_sex.setOnClickListener(new OnClickListener() {
//			
//			@Override
//			public void onClick(View v) {
//				// TODO Auto-generated method stub
//				Intent intent = new Intent(SeekSearchActivity.this,
//						MeDataDialogActivity.class);
//				intent.putExtra("Tag", PublicConstant.MeDataDialogSEXTag);
//				startActivity(intent);
//			}
//		});
		btnSearch.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				String pro=tv_province.getText().toString().trim();
				String year= tv_year.getText().toString().trim();
//				String sex=tv_sex.getText().toString().trim();
				if(pro.length()==0)
				{
					HelperUtil.totastShow("ʡ/�в���Ϊ��", SeekSearchActivity.this);
					return;
				}
				if(year.length()==0)
				{
					HelperUtil.totastShow("���䲻��Ϊ��", SeekSearchActivity.this);
					return;
				}
//				if(sex.length()==0)
//				{
//					HelperUtil.totastShow("�Ա���Ϊ��", SeekSearchActivity.this);
//					return;
//				}
				if(year.equals("40����")) year="40-100";
				Intent intent = new Intent(SeekSearchActivity.this,
						SeekSearchResultActivity.class);
				intent.putExtra("province", pro);
				intent.putExtra("year",year);
				intent.putExtra("sex",dao.queryUserByNewTime().getSex());
				startActivity(intent);
				
			}
		});
		btnBack.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				startActivity(new Intent(SeekSearchActivity.this,MainActivity.class));
				finish();
			}
		});
        
	}


	private class DialogReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context arg0, Intent intent) {
			// TODO Auto-generated method stub

			if (intent.getAction().equals(
					ReceiverConstant.MeDataDialogPassValue_ACTION)) {

				String tagValue = intent.getStringExtra("TagValue");
				String tag = intent.getStringExtra("Tag");
				if (tag.equals(PublicConstant.MeDataDialogProvinceTag)) {
					tv_province.setText(tagValue);
				}
				if (tag.equals(PublicConstant.MeDataDialogConditionYearTag)) {
					tv_year.setText(tagValue);
				}
//				if (tag.equals(PublicConstant.MeDataDialogSEXTag)) {
//					tv_sex.setText(tagValue);
//				}
			}

		}
	}

	public void onDestroy() {
		super.onDestroy();
		if (dialogReceiver != null) {
			unregisterReceiver(dialogReceiver);
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
