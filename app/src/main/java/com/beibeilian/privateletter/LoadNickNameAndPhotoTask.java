package com.beibeilian.privateletter;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import com.beibeilian.android.app.R;
import com.beibeilian.constant.BBLConstant;
import com.beibeilian.db.BBLDao;
import com.beibeilian.util.HelperUtil;
import com.beibeilian.util.HttpConstantUtil;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

public class LoadNickNameAndPhotoTask extends
		AsyncTask<Integer, Integer, Integer> {

	private TextView tvnickname;

	private ImageView imghead;

	private String username;
	
	private String nickname;
	
	private String photo;
	
	private String sex;
	
	private Context context;
	
	private BBLDao dao;
	
	public LoadNickNameAndPhotoTask(TextView tvnickname, ImageView imghead,String username,Context context,BBLDao dao) {
		this.tvnickname = tvnickname;
		this.imghead = imghead;
		this.username=username;
		this.context=context;
		this.dao=dao;
	}

	@Override
	protected Integer doInBackground(Integer... params) {
		// TODO Auto-generated method stub
		Map<String,String> map=new HashMap<String, String>();
		map.put("username",username);
		try {
			JSONObject jsonObject=new JSONObject(HelperUtil.postRequest(HttpConstantUtil.FindNicknameAndPhoto, map));
			if(jsonObject.length()>0)
			{
				nickname=jsonObject.optString("nickname");
				Log.e("test","êÇ³Æ=="+nickname);
				photo=jsonObject.optString("photo");
				sex=jsonObject.optString("sex");
				return 1;
			}
		}catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return 0;
	}

	@Override
	protected void onPostExecute(Integer result) {
	
		try
		{
		if(result>0)
		{
			dao.updatePhoto(username, photo, nickname, sex);
			tvnickname.setText(nickname);
			
			if (photo.contains("http")) {
				photo = photo.substring(photo.lastIndexOf("/") + 1);
			}
			photo = BBLConstant.PHOTO_BEFORE_URL + photo;
//			if(sex.equals("ÄÐ"))
//			{
//			tvnickname.setTextColor(context.getResources().getColor(R.color.nan));
			if(HelperUtil.flagISNoNull(photo)&&photo.contains("jpg"))
			{
//				new AsynImageLoader().showImageAsyn(imghead,photo,
//						R.drawable.nan);
				HelperUtil.getPicassoImageByUrl(context, photo, context.getResources().getDrawable(R.drawable.icon_touxiang), imghead);

			}
			else
			{
				imghead.setImageResource(R.drawable.icon_touxiang);
			}
//			}
//			else
//			{
//				tvnickname.setTextColor(context.getResources().getColor(R.color.nv));
//				if(HelperUtil.flagISNoNull(photo)&&photo.contains("jpg"))
//				{
////					new AsynImageLoader().showImageAsyn(imghead,photo,
////							R.drawable.nv);
//					HelperUtil.getPicassoImageByUrl(context, photo, context.getResources().getDrawable(R.drawable.nv), imghead);
//
//				}
//				else
//				{
//					imghead.setImageResource(R.drawable.nv);
//				}
//			}
		}
		}
		catch(Exception e)
		{
			
		}
		
	}

}
