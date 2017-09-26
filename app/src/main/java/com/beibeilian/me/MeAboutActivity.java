package com.beibeilian.me;

import com.beibeilian.MainActivity;
import com.beibeilian.util.HelperUtil;
import com.beibeilian.android.app.R;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ClickableSpan;
import android.text.style.URLSpan;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class MeAboutActivity extends Activity{

	
	
   private Button btnBack;
   
   private TextView tv_version;
   
   
   
   
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.about_us);
		btnBack=(Button)findViewById(R.id.btnBack);
		tv_version=(TextView)findViewById(R.id.cur_version);
		tv_version.setText("°æ±¾ºÅ:"+HelperUtil.getVersionCode(MeAboutActivity.this));
		btnBack.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});

	      
		
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			finish();
		}
		return false;
	}
}
