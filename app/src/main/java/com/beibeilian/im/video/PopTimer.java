package com.beibeilian.im.video;

import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

/**
 * ��ʱ��������
 *
 */
public class PopTimer {
	private int interval = 10; // ���
	private int times = 1; // ���� Ĭ��1
	private Timer timer = null; // ��ʱ��
	private TimerTask task; // ����
	private Context context;

	public PopTimer(final Context context) {
		timer = new Timer();
		this.context = context;
		times = 1;
		task = new TimerTask() {
			@Override
			public void run() {
				if (times <= 0) {
					stopPop();
					return;
				}
				Intent intent3 = new Intent();
				intent3.setAction(MyReceiver.STARTPOP);
				context.sendBroadcast(intent3);
				times--;
			}
		};
	}

	public void startPop() {
		timer.schedule(task, interval * 1000, interval * 1000);
	}

	public void stopPop() {
		timer.cancel();
	}

}
