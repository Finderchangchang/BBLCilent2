package com.beibeilian.seek.adapter;

import java.util.List;

import com.beibeilian.android.app.R;
import com.beibeilian.circle.widgets.CircularImage;
import com.beibeilian.constant.BBLConstant;
import com.beibeilian.me.MeMemberActivity;
import com.beibeilian.predestined.PersionDetailActivity;
import com.beibeilian.receiver.ReceiverConstant;
import com.beibeilian.seek.model.UserInfo;
import com.beibeilian.util.HelperUtil;
import com.beibeilian.util.SPSUtils;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class SeekPersonListAdapter extends BaseAdapter {

	private Context context;
	private List<UserInfo> userInfo;

	public SeekPersonListAdapter(Context context, List<UserInfo> userInfo) {
		this.context = context;
		this.userInfo = userInfo;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return userInfo.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return userInfo.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		ViewHolder viewHolder;
		if (convertView == null) {
			viewHolder = new ViewHolder();
			convertView = LayoutInflater.from(context).inflate(
					R.layout.seek_personlist_item, null);
			viewHolder.tv_nickname = (TextView) convertView
					.findViewById(R.id.nicknameid);
			viewHolder.tv_year = (TextView) convertView
					.findViewById(R.id.yearid);
			viewHolder.tv_distance = (TextView) convertView
					.findViewById(R.id.distanceid);
			// viewHolder.tv_state = (TextView) convertView
			// .findViewById(R.id.stateid);
			viewHolder.tv_monologue = (TextView) convertView
					.findViewById(R.id.monologueid);
			viewHolder.tv_place = (TextView) convertView
					.findViewById(R.id.placeid);
			viewHolder.tv_photo = (CircularImage) convertView
					.findViewById(R.id.touxiangid);
			viewHolder.tv_maritalstatus = (TextView) convertView
					.findViewById(R.id.tvmaritalstatusid);
			 viewHolder.tv_auth = (TextView) convertView
			 .findViewById(R.id.authid);
			 viewHolder.tv_call=(TextView)convertView.findViewById(R.id.callid);
			convertView.setTag(viewHolder);
		} else {

			viewHolder = (ViewHolder) convertView.getTag();
		}
		final UserInfo model = (UserInfo) getItem(position);
		try {
			if (model != null) {
				String url = model.getPhoto();
				if(HelperUtil.flagISNoNull(url))
				{
				if (url.contains("http")) {
					url = url.substring(url.lastIndexOf("/") + 1);
				}
				url = BBLConstant.PHOTO_BEFORE_URL + url;
				model.setPhoto(url);
				}
				else
				{
					model.setPhoto("");	
				}
				
				if(HelperUtil.flagISNoNull(model.getAuth())&&model.getAuth().equals("1"))
				{
					viewHolder.tv_auth.setVisibility(View.VISIBLE);
				}
				else
				{
					viewHolder.tv_auth.setVisibility(View.INVISIBLE);
				}
				viewHolder.tv_nickname.setText(model.getNickname());
				
				viewHolder.tv_place.setText(model.getLives());
				viewHolder.tv_year.setText(model.getBirthday());
				if(HelperUtil.flagISNoNull(model.getMaritalstatus()))
				{
					viewHolder.tv_maritalstatus.setText(model.getMaritalstatus());
				}
				else
				{
					viewHolder.tv_maritalstatus.setText("");
				}
//				if(model.getHeartduibaistate()==0)
//				{
//					viewHolder.tv_monologue.setText(model.getHeartdubai());
//				}
//				else
//				{
//					viewHolder.tv_monologue.setText("");
//				}
//				String duibai=model.getHeartdubai();
//				if(HelperUtil.flagISNoNull(duibai))
//				{
//					if(!duibai.equals("δͨ�����"))
//					{
//						viewHolder.tv_monologue.setText(duibai);
//					}
//					else
//					{
//						viewHolder.tv_monologue.setText("");
//					}
//				}
//				else
//				{
//					viewHolder.tv_monologue.setText("");
//
//				}
				viewHolder.tv_photo.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub

						Intent intent = new Intent(context,
								PersionDetailActivity.class);
						intent.putExtra("toUser", model.getUsername());
						intent.putExtra("toName", model.getNickname());
						intent.putExtra("toPhoto",
								model.getPhoto() != null ? model.getPhoto()
										: "");
						context.startActivity(intent);
					}
				});
				
			}
			
			viewHolder.tv_call.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					String vip = SPSUtils.getVip(context);
					if (TextUtils.isEmpty(vip) || !vip.equals("1")) {
						String al = SPSUtils.getDZH(context);
						if (!TextUtils.isEmpty(al)) {
							int value = Integer.parseInt(al);
							if (value < 3) {
								HelperUtil.totastShow("���к��ɹ�",context);
								Intent mIntent=new Intent(ReceiverConstant.MESSAGE_SEND_ACTION);
								mIntent.putExtra("toID", model.getUsername());
								mIntent.putExtra("nickname","˧��");
								mIntent.putExtra("message", "���,��ϲ����������͵ģ����ǿ����໥�˽�һ����");
								context.sendBroadcast(mIntent);
								SPSUtils.updateDZH(context, value + 1 + "");
							} else {
								context.startActivity(new Intent(context, MeMemberActivity.class));
							}
						} else {
							HelperUtil.totastShow("���к��ɹ�",context);
							Intent mIntent=new Intent(ReceiverConstant.MESSAGE_SEND_ACTION);
							mIntent.putExtra("toID", model.getUsername());
							mIntent.putExtra("nickname","˧��");
							mIntent.putExtra("message", "���,��ϲ����������͵ģ����ǿ����໥�˽�һ����");
							context.sendBroadcast(mIntent);
							SPSUtils.updateDZH(context, "1");
						}
					} else {
						HelperUtil.totastShow("���к��ɹ�",context);
						Intent mIntent=new Intent(ReceiverConstant.MESSAGE_SEND_ACTION);
						mIntent.putExtra("toID", model.getUsername());
						mIntent.putExtra("nickname","˧��");
						mIntent.putExtra("message", "���,��ϲ����������͵ģ����ǿ����໥�˽�һ����");
						context.sendBroadcast(mIntent);
					}

					
					
					
				}
			});
			if (HelperUtil.flagISNoNull(model.getDistance())) {
				Double dis = Double.parseDouble(model.getDistance());
				if (dis > 1000) {
					viewHolder.tv_distance.setText(String.format("%.2f",
							((dis / 1000))) + "����");
				} else {
					try
					{
					if(model.getDistance().length()>4)
					{
					viewHolder.tv_distance.setText(model.getDistance().substring(0,4) + "��");
					}
					else
					{
					viewHolder.tv_distance.setText(model.getDistance()+ "��");	
					}
					}
					catch(Exception e)
					{
						viewHolder.tv_distance.setText(model.getDistance()+ "��");		
					}
				}
			}
			
			
			if (HelperUtil.flagISNoNull(model.getSex())
					&& model.getSex().equals("��")) {
				viewHolder.tv_nickname.setTextColor(context.getResources()
						.getColor(R.color.gray_bg));
			} else {
				viewHolder.tv_nickname.setTextColor(context.getResources()
						.getColor(R.color.gray_bg));
			}
			if (model != null && HelperUtil.flagISNoNull(model.getPhoto())) {
				HelperUtil.getPicassoImageByUrl(context, model.getPhoto(), context.getResources().getDrawable(R.drawable.icon_touxiang), viewHolder.tv_photo);

//				if (model.getSex().equals("Ů")) {
////					new AsynImageLoaderAccount().showImageAsyn(viewHolder.tv_photo,
////							model.getPhoto(), R.drawable.nv,model.getUsername());
//					HelperUtil.getPicassoImageByUrl(context, model.getPhoto(), context.getResources().getDrawable(R.drawable.nv), viewHolder.tv_photo);
//				} else {
////					new AsynImageLoaderAccount().showImageAsyn(viewHolder.tv_photo,
////							model.getPhoto(), R.drawable.nan,model.getUsername());
//					HelperUtil.getPicassoImageByUrl(context, model.getPhoto(), context.getResources().getDrawable(R.drawable.nan), viewHolder.tv_photo);
//
//				}
			} else {
				viewHolder.tv_photo.setImageResource(R.drawable.icon_touxiang);

//				if (model.getSex().equals("Ů")) {
//					viewHolder.tv_photo.setImageResource(R.drawable.nv);
//				} else {
//					viewHolder.tv_photo.setImageResource(R.drawable.nan);
//				}
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return convertView;
	}

	static class ViewHolder {
		TextView tv_nickname;
		TextView tv_year;
		TextView tv_distance;
		// TextView tv_state;
		TextView tv_monologue;
		TextView tv_place;
		CircularImage tv_photo;
		TextView tv_maritalstatus;
		 TextView tv_auth;
		 TextView tv_call;

	}
}
