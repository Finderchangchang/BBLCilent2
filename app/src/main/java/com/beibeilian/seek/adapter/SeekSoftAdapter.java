package com.beibeilian.seek.adapter;

import java.util.List;

import com.beibeilian.android.app.R;
import com.beibeilian.constant.BBLConstant;
import com.beibeilian.seek.model.APP_T_Softad;
import com.beibeilian.util.HelperUtil;
import com.beibeilian.util.HttpConstantUtil;

import android.annotation.SuppressLint;
import android.app.DownloadManager;
import android.app.DownloadManager.Request;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

@SuppressLint("NewApi")
public class SeekSoftAdapter extends BaseAdapter {

	List<APP_T_Softad> mList;
	Context mContext;


	public SeekSoftAdapter(Context mContext, List<APP_T_Softad> mList) {
		this.mContext = mContext;
		this.mList = mList;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return mList.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return mList.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return mList.size();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		ViewHolder viewHolder;
		if (convertView == null) {
			viewHolder = new ViewHolder();
			convertView = LayoutInflater.from(mContext).inflate(R.layout.seek_soft_item, null);
			viewHolder.tv_name = (TextView) convertView.findViewById(R.id.nicknameid);
			viewHolder.tv_content = (TextView) convertView.findViewById(R.id.contentid);
			viewHolder.tv_size = (TextView) convertView.findViewById(R.id.sizeid);
			viewHolder.btn_anzhuang = (Button) convertView.findViewById(R.id.btn_anzhuangid);
			viewHolder.img_logo = (ImageView) convertView.findViewById(R.id.imglogo);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		final APP_T_Softad mApp_T_Softad=mList.get(position);
		viewHolder.tv_name.setText(mApp_T_Softad.getAppname());
		viewHolder.tv_content.setText(mApp_T_Softad.getAppcontent());
		viewHolder.tv_size.setText(mApp_T_Softad.getAppsize()+"M");
		viewHolder.btn_anzhuang.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				try {
					// TODO Auto-generated method stub
					HelperUtil.totastShow("开始下载...", mContext);
					final DownloadManager downloadManager = (DownloadManager) mContext
							.getSystemService(mContext.DOWNLOAD_SERVICE);
					Uri uri = Uri.parse(BBLConstant.PHOTO_BEFORE_URL + mApp_T_Softad.getAppapk());
					Request request = new Request(uri);
					request.setAllowedNetworkTypes(
							DownloadManager.Request.NETWORK_MOBILE | DownloadManager.Request.NETWORK_WIFI);
					request.setDescription("正在下载APP...");
					request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
					request.setMimeType("application/vnd.android.package-archive");
					// 设置为可被媒体扫描器找到
					request.allowScanningByMediaScanner();
					// 设置为可见和可管理
					request.setVisibleInDownloadsUi(true);
					// 获取此次下载的ID
					downloadManager.enqueue(request);
				} catch (Exception e) {
					// TODO: handle exception
				}
			}
		});
		HelperUtil.getPicassoImageByUrlLogo(mContext, mApp_T_Softad.getLogo(), mContext.getResources().getDrawable(R.drawable.icon_touxiang), viewHolder.img_logo);


		return convertView;
	}

	class ViewHolder
	{
		ImageView img_logo;
		TextView tv_name;
		TextView tv_content;
		TextView tv_size;
		Button   btn_anzhuang;
	}
	
}
