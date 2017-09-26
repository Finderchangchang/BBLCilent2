package com.beibeilian.privateletter.adapter;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ImageSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.beibeilian.circle.widgets.CircularImage;
import com.beibeilian.constant.BBLConstant;
import com.beibeilian.db.BBLDao;
import com.beibeilian.privateletter.LoadNickNameAndPhotoTask;
import com.beibeilian.privateletter.model.MessageList;
import com.beibeilian.seek.model.UserInfo;
import com.beibeilian.util.DateFormatUtil;
import com.beibeilian.util.HelperUtil;
import com.beibeilian.android.app.R;

public class PrivateletterAdapter extends BaseAdapter {

	private List<MessageList> listMessage;

	private Context context;

	private BBLDao dao;

	private String username;

	private String sex;

	public PrivateletterAdapter(Context context, List<MessageList> listMessage, String username) {
		this.username = username;
		this.context = context;
		this.listMessage = listMessage;
		dao = new BBLDao(context, null, null, 1);
		sex = dao.queryUserByNewTime().getSex();
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return listMessage.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return listMessage.get(position);
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
			convertView = LayoutInflater.from(context).inflate(R.layout.privateletter_listview_item, null);
			viewHolder.tv_time = (TextView) convertView.findViewById(R.id.letter_item_time);
			viewHolder.img_head = (CircularImage) convertView.findViewById(R.id.letter_item_icon);
			viewHolder.tv_content = (TextView) convertView.findViewById(R.id.letter_item_msg);
			viewHolder.tv_see_state = (TextView) convertView.findViewById(R.id.letter_item_unreadmsg);
			viewHolder.tv_name = (TextView) convertView.findViewById(R.id.letter_item_name);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}

		try {
			MessageList messageList = (MessageList) getItem(position);
			if (messageList != null) {
				if (HelperUtil.flagISNoNull(messageList.getFromtime()))
					viewHolder.tv_time.setText(DateFormatUtil.getDDTime(messageList.getFromtime()));
				if (messageList.getFromuser().equals(username)) {
					viewHolder.tv_content.setText("给您发来了新的消息");
					int count = dao.queryCountByUser(messageList.getOutuser());
					if (count != 0) {
						viewHolder.tv_see_state.setText(String.valueOf(count));
						viewHolder.tv_see_state.setVisibility(View.VISIBLE);
					} else {
						viewHolder.tv_see_state.setVisibility(View.GONE);
					}
					new LoadNickNameAndPhotoTask(viewHolder.tv_name, viewHolder.img_head, messageList.getOutuser(),
							context, dao).execute(0);
//					UserInfo model = new UserInfo();
//					model = dao.queryPhoto(messageList.getOutuser());
//					
//					if (model != null) {
//						
//						String url = model.getPhoto();
//						if(HelperUtil.flagISNoNull(url))
//						{
//						if (url.contains("http")) {
//							url = url.substring(url.lastIndexOf("/") + 1);
//						}
//						url = BBLConstant.PHOTO_BEFORE_URL + url;
//						model.setPhoto(url);
//						}
//						else
//						{
//							model.setPhoto("");	
//						}
//					} else {
//						new LoadNickNameAndPhotoTask(viewHolder.tv_name, viewHolder.img_head, messageList.getOutuser(),
//								context, dao).execute(0);
//	
//					}

//					if (model != null && HelperUtil.flagISNoNull(model.getPhoto())) {
//						HelperUtil.getPicassoImageByUrl(context, model.getPhoto(),
//								context.getResources().getDrawable(R.drawable.icon_touxiang), viewHolder.img_head);
//
//					} else {
//						if (model != null && HelperUtil.flagISNoNull(model.getSex())) {
//							viewHolder.img_head.setImageResource(R.drawable.icon_touxiang);
//							viewHolder.tv_name.setText(model.getNickname());
//						} else {
//							viewHolder.img_head.setImageResource(R.drawable.icon_touxiang);
//							viewHolder.tv_name.setText(messageList.getOutuser());
//						}
//					}
				} else {
                    try {
						if (messageList.getFromcontent().contains(".jpg")) {
							viewHolder.tv_content.setText("图片");
						} else {
							viewHolder.tv_content.setText(HelperUtil.convertNormalStringToSpannableString(context,
									messageList.getFromcontent(), true));
						} 
					} catch (Exception e) {
						// TODO: handle exception
					}
					int count = dao.queryCountByUser(messageList.getFromuser());
					if (count != 0) {
						viewHolder.tv_see_state.setText(String.valueOf(count));
						viewHolder.tv_see_state.setVisibility(View.VISIBLE);
					} else {
						viewHolder.tv_see_state.setVisibility(View.GONE);
					}
					new LoadNickNameAndPhotoTask(viewHolder.tv_name, viewHolder.img_head, messageList.getFromuser(),
							context, dao).execute(0);
//					UserInfo model = new UserInfo();
//					model = dao.queryPhoto(messageList.getFromuser());
//					if (model != null) {
//
//					} else {
//						new LoadNickNameAndPhotoTask(viewHolder.tv_name, viewHolder.img_head, messageList.getFromuser(),
//								context, dao).execute(0);
//
//					}
//
//					if (model != null && HelperUtil.flagISNoNull(model.getPhoto())) {
//						HelperUtil.getPicassoImageByUrl(context, model.getPhoto(),
//								context.getResources().getDrawable(R.drawable.icon_touxiang), viewHolder.img_head);
//						if (model != null && HelperUtil.flagISNoNull(model.getSex())) {
//
//							viewHolder.tv_name.setText(model.getNickname());
//
//						} else {
//				
//							viewHolder.tv_name.setText(messageList.getFromuser());
//
//						}
//					} else {
//						viewHolder.img_head.setImageResource(R.drawable.icon_touxiang);
//
//						if (model != null && HelperUtil.flagISNoNull(model.getSex())) {
//							viewHolder.tv_name.setText(model.getNickname());
//
//						} else {
//							viewHolder.tv_name.setText(messageList.getFromuser());
//
//						}
//					}
				}

			}

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return convertView;
	}

	private SpannableStringBuilder handler(final TextView gifTextView, String content) {

		SpannableStringBuilder sb = new SpannableStringBuilder(content);
		String regex = "(\\#\\[face/png/f_static_)\\d{3}(.png\\]\\#)";
		Pattern p = Pattern.compile(regex);
		Matcher m = p.matcher(content);
		while (m.find()) {
			try {
				String tempText = m.group();
				String png = tempText.substring("#[".length(), tempText.length() - "]#".length());
				sb.setSpan(new ImageSpan(context, BitmapFactory.decodeStream(context.getAssets().open(png))), m.start(),
						m.end(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
			} catch (Exception e) {
				System.out.println("报错" + e.toString());
			}
		}
		return sb;
	}

	static class ViewHolder {

		TextView tv_time;

		CircularImage img_head;

		TextView tv_content;

		TextView tv_see_state;

		TextView tv_name;
	}

}
