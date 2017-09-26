package com.beibeilian.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import com.beibeilian.db.BBLDao;
import com.beibeilian.me.model.Version;
import com.beibeilian.android.app.R;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;

public class UpdateManager {
	private Context mContext;
	private Dialog downloadDialog;
	// ���ذ���װ·��
	private static final String savePath = PublicConstant.APKPATH;

	private static final String saveFileName = savePath + "beibeilian.apk";

	// ��������֪ͨuiˢ�µ�hander��msg����
	private ProgressBar mProgress;

	private static final int DOWN_UPDATE = 1;

	private static final int DOWN_OVER = 2;

	private static final int TimeOUT = 3;

	private static final int NoSDK = 4;

	private static final int UpdateIng = 5;

	private int progress;

	private Thread downLoadThread;

	private boolean interceptFlag = false;

	private Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case DOWN_UPDATE:
				mProgress.setProgress(progress);
				break;

			case DOWN_OVER:// ���ؽ���ִ�а�װ
				installApk();
				break;
			case TimeOUT:
				if (downloadDialog != null) {
					downloadDialog.dismiss();
				}
				if (mProgress != null) {
					mProgress.setVisibility(View.GONE);
				}
				HelperUtil.totastShow("���������Ƿ����û����������", mContext);
				break;
			case NoSDK:
				HelperUtil.totastShow("�����SD��", mContext);
				break;
			case UpdateIng:
				downProgressloadDialog();
				break;
			default:
				break;
			}
		};
	};

	private BBLDao dao;
	private Version model;

	public UpdateManager(Context context) {
		this.mContext = context;
		dao = new BBLDao(mContext, null, null, 1);
		model = dao.queryVersion();
		System.out.println(model.getCode() + model.getContent() + model.getSize() + model.getUrl());
	}

	// �ⲿ�ӿ�����activity����
	public void startUpdateInfo() {

		showDownloadDialog(model.getCode(), model.getSize(), model.getContent());
	}

	private void showDownloadDialog(String code, String size, String content) {

		AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
		builder.setTitle("�汾����");
		builder.setMessage("��ǰ���µİ汾��Ҫ����,�汾��" + code + ",�ļ���С" + size + "M," + content + ",Ϊ�˽�ʡ������������ѡ����WIFI�����¸��¡�")
				.setCancelable(false).setPositiveButton("ȷ��", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						mHandler.sendEmptyMessage(UpdateIng);
						dialog.dismiss();
					}
				}).setNegativeButton("ȡ��", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						dialog.dismiss();
						if (downLoadThread != null) {
							downLoadThread.interrupt();
						}
					}
				}).show();
		// downloadDialog = builder.create();
		// downloadDialog.show();

	}

	private void downProgressloadDialog() {
		final LayoutInflater inflater = LayoutInflater.from(mContext);
		View v = inflater.inflate(R.layout.update_progress, null);
		mProgress = (ProgressBar) v.findViewById(R.id.progress);
		AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
		builder.setCancelable(false);
		builder.setView(v);
		builder.setTitle("���ڰ汾����...").setNegativeButton("ȡ��", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				interceptFlag = true;
				if (downLoadThread != null) {
					downLoadThread.interrupt();
				}
				dialog.dismiss();
			}
		});
		downloadDialog = builder.create();
		downloadDialog.show();
		if (downLoadThread != null) {
			downLoadThread.interrupt();
		}
		downLoadThread = new Thread(mdownApkRunnable);
		downLoadThread.start();

	}

	private Runnable mdownApkRunnable = new Runnable() {

		@Override
		public void run() {

			try {

				// �ж��Ƿ���SD��
				if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
					mHandler.sendEmptyMessage(NoSDK);
					return;
				}
				URL url = new URL(model.getUrl());// ����url

				HttpURLConnection conn = (HttpURLConnection) url.openConnection();// ����������ʵĶ���
				conn.connect();
				conn.setConnectTimeout(10000); // �������ӳ�ʱΪ10s
				conn.setReadTimeout(10000);
				int length = conn.getContentLength();// ��ȡ�������ݵĳ��ȡ�����ָapk�ļ��Ĵ�С
				InputStream is = conn.getInputStream();// ��apk�ļ�
														// ��������ʽд�뵽��Ӧ�ó�����ڴ���
				File file = new File(savePath);// apk����ĸ�Ŀ¼------------------//�����ļ���
				if (!file.exists()) {// ���file������ �򴴽�
					file.mkdirs();
				}
				String apkFile = saveFileName;// ���浽sd���ϵ�apk�ļ���
				File ApkFile = new File(apkFile);// �����ļ�����ʱ���ļ��������ǿյ�
				if (ApkFile.exists()) {
					ApkFile.delete();
				}
				FileOutputStream fos = new FileOutputStream(ApkFile);// ��apk�ļ������sd������ʱ��û�б��浽sd��
				int count = 0;// ���ڻ�ȡ���ص��ٶ�
				byte buf[] = new byte[1024];// ����byte�ֽ�
				// ѭ�����൱���û������ʼ��ť�ˣ����ڿ�ʼ��apk������д�뵽�ļ���
				do {
					int numread = is.read(buf);// ÿ�ζ�ȡ1024��byte�ֽ�
					count += numread;
					progress = (int) (((float) count / length) * 100);// ��������%����ʾ
					// ���½���
					mHandler.sendEmptyMessage(DOWN_UPDATE);
					if (numread <= 0) {
						// �������֪ͨ��װ
						mHandler.sendEmptyMessage(DOWN_OVER);
						break;
					}
					fos.write(buf, 0, numread);// apk������д�뵽�ļ���
				} while (!interceptFlag);// ���ȡ����ֹͣ����

				fos.close();
				is.close();

			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				mHandler.sendEmptyMessage(TimeOUT);
			}
		}
	};

	// /**
	// * ����apk
	// */
	// private void downloadApk() {
	// downLoadThread = new Thread(mdownApkRunnable);
	// downLoadThread.start();
	// }

	/**
	 * ��װapk
	 */
	private void installApk() {
		File apkfile = new File(saveFileName);
		if (!apkfile.exists()) {
			return;
		}

		Intent i = new Intent(Intent.ACTION_VIEW);
		i.setDataAndType(Uri.parse("file://" + apkfile.toString()), "application/vnd.android.package-archive");
		mContext.startActivity(i);
	}
}
