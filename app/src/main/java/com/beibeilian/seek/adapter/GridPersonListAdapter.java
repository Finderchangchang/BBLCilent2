package com.beibeilian.seek.adapter;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Random;

import com.beibeilian.circle.widgets.CircularImage;
import com.beibeilian.constant.BBLConstant;
import com.beibeilian.db.BBLDao;
import com.beibeilian.predestined.PersionDetailActivity;
import com.beibeilian.predestined.PredestinedActivity;
import com.beibeilian.seek.model.UserInfo;
import com.beibeilian.util.DateFormatUtil;
import com.beibeilian.util.HelperUtil;
import com.beibeilian.util.ImageHelper;
import com.beibeilian.util.PublicConstant;
import com.beibeilian.android.app.R;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.view.View.OnClickListener;

public class GridPersonListAdapter extends BaseAdapter {

    private Context context;
    private List<UserInfo> userInfo;

    public GridPersonListAdapter(Context context, List<UserInfo> userInfo) {
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
                    R.layout.grid_personlist_item, null);
            viewHolder.tv_nickname = (TextView) convertView
                    .findViewById(R.id.nicknameid);
            viewHolder.tv_gl = (TextView) convertView.findViewById(R.id.gl_tv);
            viewHolder.tv_year = (TextView) convertView
                    .findViewById(R.id.yearid);
            viewHolder.tv_photo = (ImageView) convertView
                    .findViewById(R.id.touxiangid);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        final UserInfo model = (UserInfo) getItem(position);
        try {
            if (model != null) {
                String url = model.getPhoto();
                if (HelperUtil.flagISNoNull(url)) {
                    if (url.contains("http")) {
                        url = url.substring(url.lastIndexOf("/") + 1);
                    }
                    url = BBLConstant.PHOTO_BEFORE_URL + url;
                    model.setPhoto(url);
                } else {
                    model.setPhoto("");
                }
                if (!TextUtils.isEmpty(model.getPhoto())) {
                    try {
                        HelperUtil.getPicassoImageByUrl(context, url, context.getResources().getDrawable(R.drawable.icon_touxiang), viewHolder.tv_photo);
                    } catch (Exception e) {
                        // TODO: handle exception
                    }
                } else {
                    viewHolder.tv_photo.setImageResource(R.drawable.icon_touxiang);
                }

                viewHolder.tv_nickname.setText(model.getNickname());
                int val = (int) (Math.random() * 100 + 1);
                viewHolder.tv_gl.setText(val + "m");
                viewHolder.tv_year.setText(model.getBirthday() + "Ëê");
                convertView.setOnClickListener(new OnClickListener() {

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


        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return convertView;
    }

    static class ViewHolder {
        TextView tv_nickname;
        TextView tv_year;
        TextView tv_gl;
        ImageView tv_photo;

    }
}
