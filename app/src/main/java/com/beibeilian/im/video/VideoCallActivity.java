package com.beibeilian.im.video;

import com.beibeilian.android.app.R;
import com.beibeilian.receiver.ReceiverConstant;
import com.beibeilian.util.HelperUtil;
import com.beibeilian.util.PreferencesUtils;
import com.beibeilian.util.SweetAlertDialog;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

public class VideoCallActivity extends Activity implements OnClickListener {

	private SurfaceView surfaceView;
	private SeekBar skbProgress;
	private Player player;
	private String videoUrl = "http://112.74.63.86/BBLFiLeServer/mp4/v1.mp4";
	private String videoName="晴儿";
	private String[] videoNames={"晴儿","萍萍","小梅","花花","饥渴少妇"};

	private String videoPre="http://112.74.63.86/BBLFiLeServer/mp4/";
	private String[] videourls={videoPre+"v1.mp4",videoPre+"v2.mp4",videoPre+"v3.mp4",videoPre+"v4.mp4",videoPre+"v5.mp4"};
	private Button btnAnswercall;
	private Button btnRefusecall;
	private int start = 0;
	private int count = 0;
	private PopTimer popTimer;
	private PlayerTimer playerTimer;
	private String vip;
	private SweetAlertDialog dialog;
	private TextView callStateTextView;
	private TextView nickTextView;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_video_call);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		try {
			int index = (int) (Math.random() * videourls.length);
			videoUrl = videourls[index];
			int indextemp = (int) (Math.random() * videoNames.length);
			videoName = videoNames[indextemp];
			
		} catch (Exception e) {
			// TODO: handle exception
		}
		setViews();

	}

	private void setViews() {
		try {
			playerTimer = new PlayerTimer(getApplicationContext());
			callStateTextView = (TextView) findViewById(R.id.tv_call_state);
			nickTextView = (TextView) findViewById(R.id.tv_nick);
			surfaceView = (SurfaceView) findViewById(R.id.surfaceView1);
			skbProgress = (SeekBar) findViewById(R.id.skbProgress);
			skbProgress.setOnSeekBarChangeListener(new SeekBarChangeEvent());
			btnAnswercall = (Button) findViewById(R.id.btn_answer_call);
			btnRefusecall = (Button) findViewById(R.id.btn_refuse_call);
			callStateTextView.setText("邀请您视频通话");
			nickTextView.setText(videoName);
			player = new Player(surfaceView, skbProgress, null, null);
			IntentFilter filter = new IntentFilter();
			filter.addAction(MyReceiver.STARTPOP);
			registerReceiver(mReceiver, filter);
			vip = PreferencesUtils.getString(VideoCallActivity.this, "vip");

			btnAnswercall.setOnClickListener(this);
			btnRefusecall.setOnClickListener(this);
		} catch (Exception e) {
			// TODO: handle exception
		}

	}

	class SeekBarChangeEvent implements SeekBar.OnSeekBarChangeListener {
		int progress;

		@Override
		public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
			try {
				// 原本是(progress/seekBar.getMax())*player.mediaPlayer.getDuration()
				if (dialog != null) {
					dialog.dismiss();
				} 
			} catch (Exception e) {
				// TODO: handle exception
			}
			try {
				this.progress = progress * player.mediaPlayer.getDuration() / seekBar.getMax();
				if (progress >= 5) {
					if (TextUtils.isEmpty(vip) || !vip.equals("1")) {
						
						try {
							runOnUiThread(new Runnable() {
								
								@Override
								public void run() {
									// TODO Auto-generated method stub
									HelperUtil.totastShow("请开通VIP会员！",getApplicationContext());
								}
							});
							if (playerTimer != null) {
								playerTimer.stopPop();
							}
							if (popTimer != null) {
								popTimer.stopPop();
							}
							player.pause();
						} catch (Exception e) {
							// TODO: handle exception
						}
						sendBroadcast(new Intent(ReceiverConstant.StartPayDialogACTION));
						finish();
					}
				} 
				
				if(progress>=95)
				{
					try {
						runOnUiThread(new Runnable() {
							
							@Override
							public void run() {
								// TODO Auto-generated method stub
								HelperUtil.totastShow("通话结束！",getApplicationContext());
							}
						});
						player.pause();
						if (popTimer != null) {
							popTimer.stopPop();
						}
						player.stop();
						finish();
					} catch (Exception e) {
						// TODO: handle exception
					}
				}
				
			} catch (Exception e) {
				// TODO: handle exception
			}

		}

		@Override
		public void onStartTrackingTouch(SeekBar seekBar) {

		}

		@Override
		public void onStopTrackingTouch(SeekBar seekBar) {
			try {
				// seekTo()的参数是相对与影片时间的数字，而不是与seekBar.getMax()相对的数字
				player.mediaPlayer.seekTo(progress);
				player.setTv();
			} catch (Exception e) {
				// TODO: handle exception
			}
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if ((keyCode == KeyEvent.KEYCODE_BACK)) {
			if (popTimer != null) {
				popTimer.stopPop();
			}
			player.stop();
			finish();
			return true;
		}
		return true;

	}

//	public boolean onTouchEvent(MotionEvent event) {
//		// TODO Auto-generated method stub
//		int touchEvent = event.getAction();
//		switch (touchEvent) {
//		case MotionEvent.ACTION_DOWN:
//			if (playerTimer != null) {
//				playerTimer.stopPop();
//			}
//			if (popTimer != null) {
//				popTimer.stopPop();
//			}
//			popTimer = new PopTimer(getApplicationContext());
//			popTimer.startPop();
//			break;
//		case MotionEvent.ACTION_UP:
//			break;
//		case MotionEvent.ACTION_MOVE:
//			break;
//		default:
//			break;
//		}
//		return super.onTouchEvent(event);
//	}

	@Override
	protected void onDestroy() {
		
		super.onDestroy();
		try {
			unregisterReceiver(mReceiver);
			player.stop();
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	BroadcastReceiver mReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent2) {
			if (MyReceiver.STARTPOP.equals(intent2.getAction())) {
				WindowManager.LayoutParams lp = getWindow().getAttributes();
				lp.flags |= WindowManager.LayoutParams.FLAG_FULLSCREEN;
				getWindow().setAttributes(lp);
				getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
				if (count == 0) {
					player.playUrl(videoUrl);
					playerTimer.stopPop();
					count++;
					start++;
				}
			}
		}
	};

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.btn_answer_call:
			try {
				try {
					callStateTextView.setText("");
					dialog = new SweetAlertDialog(VideoCallActivity.this);
					dialog.setTitle("请稍候...");
					dialog.show();
				} catch (Exception e) {
					// TODO: handle exception
				}
				new Thread(new Runnable() {

					@Override
					public void run() {
						// TODO Auto-generated method stub

						try {
							playerTimer.startPop();
							if (playerTimer != null) {
								playerTimer.stopPop();
							}
							if (start == 0) {
								player.playUrl(videoUrl);
								start++;
							} else {
								player.start();
							} 
						} catch (Exception e) {
							// TODO: handle exception
						}
					}
				}).start();
			} catch (Exception e) {
				// TODO: handle exception
			}
			break;
		case R.id.btn_refuse_call:
			try {
				player.pause();
				if (popTimer != null) {
					popTimer.stopPop();
				}
				player.stop();
				finish();
			} catch (Exception e) {
				// TODO: handle exception
			}
			break;

		default:
			break;
		}
	}

}
