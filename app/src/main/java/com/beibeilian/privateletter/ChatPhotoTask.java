package com.beibeilian.privateletter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import com.beibeilian.android.app.R;
import com.beibeilian.constant.BBLConstant;
import com.beibeilian.db.BBLDao;
import com.beibeilian.util.HelperUtil;
import com.beibeilian.util.HttpConstantUtil;
import com.beibeilian.util.PublicConstant;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.widget.ImageView;
import android.widget.TextView;

public class ChatPhotoTask extends AsyncTask<Integer, Integer, Integer> {

	private TextView tvPhoto;

	private String photourl;

	private Context context;

	public ChatPhotoTask(TextView tvPhoto, String photourl, Context context) {
		this.tvPhoto = tvPhoto;
		this.photourl = photourl;
		this.context = context;
	}

	@Override
	protected Integer doInBackground(Integer... params) {
		// TODO Auto-generated method stub
		try {
			File mInFile = new File(PublicConstant.FilePath + photourl);
			if (mInFile.exists() && mInFile.length() > 10) {
				return 1;
			}
			String savePathdir = PublicConstant.FilePath;
			File filedir = new File(savePathdir);
			if (!filedir.exists()) {
				filedir.mkdirs();
			}
			File file = new File(savePathdir + photourl);
			if (!file.exists()) {
				file.createNewFile();
			}

			String url = photourl;
			if (HelperUtil.flagISNoNull(url)) {
				if (url.contains("http")) {
					url = url.substring(url.lastIndexOf("/") + 1);
				}
				url = BBLConstant.PHOTO_BEFORE_URL + url;
			}
			URLConnection cn = new URL(url).openConnection();
			cn.connect();
			InputStream stream = cn.getInputStream();
			FileOutputStream fos = new FileOutputStream(file);
			byte buf[] = new byte[1024];
			do {
				int numread = stream.read(buf);
				if (numread <= 0) {
					break;
				}
				fos.write(buf, 0, numread);
			} while (true);
			fos.close();
			stream.close();
			return 1;
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("ÏÂÔØ±¨´í" + e);
			File file = new File(PublicConstant.FilePath + photourl);
			if (file.exists()) {
				file.delete();
			}
		}
		return 0;
	}

	@Override
	protected void onPostExecute(Integer result) {

		try {
			if (result > 0) {
				File mInFile = new File(PublicConstant.FilePath + photourl);
				if (mInFile.exists() && mInFile.length() > 10) {
					Bitmap bm = BitmapFactory.decodeFile(PublicConstant.FilePath + photourl);
					Drawable rightDrawable = new BitmapDrawable(bm);
					rightDrawable.setBounds(0, 0, rightDrawable.getMinimumWidth(), rightDrawable.getMinimumHeight());
					tvPhoto.setCompoundDrawables(null, null, rightDrawable, null);
				} else {
					Drawable rightDrawable = context.getResources().getDrawable(R.drawable.recv_image);
					rightDrawable.setBounds(0, 0, rightDrawable.getMinimumWidth(), rightDrawable.getMinimumHeight());
					tvPhoto.setCompoundDrawables(null, null, rightDrawable, null);
				}
			} else {
				Drawable rightDrawable = context.getResources().getDrawable(R.drawable.recv_image);
				rightDrawable.setBounds(0, 0, rightDrawable.getMinimumWidth(), rightDrawable.getMinimumHeight());
				tvPhoto.setCompoundDrawables(null, null, rightDrawable, null);
			}
		} catch (Exception e) {

		}

	}

}
