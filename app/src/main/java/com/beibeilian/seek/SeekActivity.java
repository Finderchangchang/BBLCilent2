package com.beibeilian.seek;

import com.beibeilian.android.app.R;
import com.beibeilian.db.BBLDao;
import com.beibeilian.seek.qaq.CircleMainActivity;
import com.beibeilian.util.ExitApplication;
import com.beibeilian.util.HelperUtil;
import com.beibeilian.util.PublicConstant;
import com.beibeilian.util.SweetAlertDialog;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RelativeLayout;

public class SeekActivity extends Activity implements OnClickListener {
	private RelativeLayout seek_search;
	private RelativeLayout seek_qaq;
	private RelativeLayout seek_nearby;
	private RelativeLayout seek_ball;
	private RelativeLayout seek_soft;

	private Intent intent;
	
	BBLDao dao;
	SweetAlertDialog dialog;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.seek);
		ExitApplication.addActivity(SeekActivity.this);
		dao=new BBLDao(SeekActivity.this,null, null, 1);
		dialog=new SweetAlertDialog(SeekActivity.this);
		seek_search = (RelativeLayout) findViewById(R.id.seek_search_id);
		seek_qaq = (RelativeLayout) findViewById(R.id.seek_qaq_id);
		seek_nearby = (RelativeLayout) findViewById(R.id.seek_nearby_id);
		seek_ball = (RelativeLayout) findViewById(R.id.seek_ball_id);
		seek_soft = (RelativeLayout) findViewById(R.id.seek_soft_id);

		seek_search.setOnClickListener(this);
		seek_qaq.setOnClickListener(this);
		seek_nearby.setOnClickListener(this);
		seek_ball.setOnClickListener(this);
		seek_soft.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub

		switch (v.getId()) {
		case R.id.seek_search_id:
			intent = new Intent(SeekActivity.this, SeekSearchActivity.class);
			startActivity(intent);
			break;
		case R.id.seek_qaq_id:
			startActivity(new Intent(SeekActivity.this, CircleMainActivity.class));
			break;
		case R.id.seek_nearby_id:
			startActivity(new Intent(SeekActivity.this, SeekNearbyActivity.class));
			break;
		case R.id.seek_ball_id:
			startActivity(new Intent(SeekActivity.this, SeekThrowBallActivity.class));
			break;
		case R.id.seek_soft_id:
			startActivity(new Intent(SeekActivity.this, SeekSoftActivity.class));
			break;
		default:

			break;
		}
	}
	


}
