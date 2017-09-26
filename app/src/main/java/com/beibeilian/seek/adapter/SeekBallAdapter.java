package com.beibeilian.seek.adapter;

import java.util.List;

import com.beibeilian.android.app.R;
import com.beibeilian.db.BBLDao;
import com.beibeilian.me.MeMemberActivity;
import com.beibeilian.model.PayRule;
import com.beibeilian.predestined.PersionDetailActivity;
import com.beibeilian.seek.model.Ball;
import com.beibeilian.util.SensitiveWordsUtil;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
public class SeekBallAdapter extends BaseAdapter{
    List<Ball> list;
    Context mContext;
	private int vipState = 0;
	private String relation = "1";
	public SeekBallAdapter(Context mContext,List<Ball> list)
	{
		this.mContext=mContext;
		this.list=list;
	     BBLDao	dao = new BBLDao(mContext, null, null, 1);
		String username = dao.queryUserByNewTime().getUsername();
		this.vipState = dao.findVipMember(username);
		PayRule mPayRule = dao.findPayRule();
		if (mPayRule != null) {
			relation = mPayRule.getRelation();
		} else {
			relation = "1";
		}
	}
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return list.get(position);
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
		if(convertView==null)
		{
			viewHolder=new ViewHolder();
			convertView = LayoutInflater.from(mContext).inflate(
					R.layout.seek_throw_ball_item, null);
			viewHolder.tv_content=(TextView) convertView.findViewById(R.id.tvcontent);
			viewHolder.tv_name=(TextView) convertView.findViewById(R.id.tvname);
			viewHolder.tv_time=(TextView)convertView.findViewById(R.id.tvtime);			
			convertView.setTag(viewHolder);
		}
		else
		{
			viewHolder = (ViewHolder) convertView.getTag();	
		}
		
		final Ball model=list.get(position);
		if(model!=null)
		{
			viewHolder.tv_content.setText(model.getContent());
			if (SensitiveWordsUtil.sensitiveWords(model.getContent()) && vipState <= 0 && relation.equals("1")) {
				viewHolder.tv_content.setText("����ܰ����:������Ϣ�п��ܰ�����ϵ��ʽ,VIP��Ա���ܲ鿴.�����ͨVIP��Ա��");
			}
			viewHolder.tv_name.setText(model.getName());
			viewHolder.tv_time.setText(model.getModtime());
		}
		convertView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (SensitiveWordsUtil.sensitiveWords(model.getContent()) && vipState <= 0 && relation.equals("1")) {
					mContext.startActivity(new Intent(mContext, MeMemberActivity.class));
				}
				else
				{
				Intent intent = new Intent(mContext,
						PersionDetailActivity.class);
				intent.putExtra("toUser", model.getUsername());
				intent.putExtra("toName", model.getName());
				intent.putExtra("toPhoto","");
				mContext.startActivity(intent);
				}
			}
		});
		return convertView;
	}
	
	class ViewHolder
	{
		TextView tv_name;
		TextView tv_content;
		TextView tv_time;
	}

}
