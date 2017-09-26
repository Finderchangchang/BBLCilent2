package com.beibeilian;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;

import com.beibeilian.android.app.R;
import com.beibeilian.db.BBLDao;
import com.beibeilian.me.model.UserInfoEntiy;
import com.beibeilian.service.CoreIMService;
import com.beibeilian.util.Base64Util;
import com.beibeilian.util.CryptoTools;
import com.beibeilian.util.HelperUtil;
import com.beibeilian.util.HttpConstantUtil;
import com.beibeilian.util.PublicConstant;
import com.beibeilian.util.StringEscapeUtils;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;

public class RegsiterActivity extends Activity {
    EditText et_nianling;
    private Dialog dialog;
    private BBLDao dao;
    private int sex = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.regsiter_yj);
        dialog = new Dialog(RegsiterActivity.this, R.style.theme_dialog_alert);
        dao = new BBLDao(RegsiterActivity.this, null, null, 1);
        et_nianling = (EditText) findViewById(R.id.et_year);
        RelativeLayout rlnan = (RelativeLayout) findViewById(R.id.rl_nan);
        final ImageView imgnan = (ImageView) findViewById(R.id.nan_img_ck);
        final ImageView imgnv = (ImageView) findViewById(R.id.nv_img_ck);
        Button btnback = (Button) findViewById(R.id.btnBack);
        btnback.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                finish();
            }
        });
        rlnan.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                imgnan.setImageDrawable(getResources().getDrawable(R.drawable.img_cked));
                imgnv.setImageDrawable(getResources().getDrawable(R.drawable.img_ckunble));
                sex = 0;
            }
        });
        RelativeLayout rlnv = (RelativeLayout) findViewById(R.id.rl_nv);
        rlnv.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                imgnan.setImageDrawable(getResources().getDrawable(R.drawable.img_ckunble));
                imgnv.setImageDrawable(getResources().getDrawable(R.drawable.img_cked));
                sex = 1;
            }
        });

        Button btnconfirm = (Button) findViewById(R.id.regsiter_confirm);
        btnconfirm.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                quickRegister();
            }
        });
    }

    private void quickRegister() {

        final String user = HelperUtil.getPhoneNumber(getApplicationContext());

//        final String pass = HelperUtil.get6Random();
        final String pass = "123456";

        final String year = et_nianling.getText().toString().trim();
        if (year.length() <= 0) {
            HelperUtil.totastShow("年龄不能为空", getApplicationContext());
            return;
        }
        if ((Integer.parseInt(year) < 16)) {
            HelperUtil.totastShow("年龄不能小于16岁", getApplicationContext());
            return;
        }
        HelperUtil.customDialogShow(dialog, RegsiterActivity.this, "请稍候...");
        final String imei = HelperUtil.getIMIE(getApplicationContext());
        new Thread(new Runnable() {

            @Override
            public void run() {
                String user_name = "";
                if (!TextUtils.isEmpty(user)) {
                    user_name = user.replace("+86", "");
                }
                Map<String, String> map = new HashMap<>();
                map.put("username", user_name);
                map.put("password", pass);
                map.put("year", year);
                if (sex == 0) {
                    map.put("sex", "男");
                } else {
                    map.put("sex", "女");
                }
                map.put("imie", imei);
                map.put("ip", HelperUtil.getLocalIpAddress());
                map.put("channel", HelperUtil.getChannelId(getApplicationContext()));
                map.put("appid", HelperUtil.getAPPlId(getApplicationContext()));
                JSONObject jsonobject = null;
                try {
                    String json = HelperUtil.postRequest(HttpConstantUtil.Regsiter, map);
                    jsonobject = new JSONObject(json);
                    if (jsonobject != null && jsonobject.length() > 0) {
                        int res = jsonobject.getInt("result");
                        if (res == 1) {
                            if (sex == 0) {
                                dao.initUser(user_name, "帅哥", pass, "1", "男");
                                dao.updatePhoto(user_name, "", "帅哥", "男");
                            } else {
                                dao.initUser(user_name, "美女", pass, "1", "女");
                                dao.updatePhoto(user_name, "", "美女", "女");
                            }
                            handler.sendEmptyMessage(1);
                        } else if (res == 2) {
                            handler.sendEmptyMessage(2);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.e("test", "注册==" + e.toString());
                    handler.sendEmptyMessage(-1);
                }
            }
        }).start();
    }

    private Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case 1:
                    if (dialog != null) {
                        dialog.dismiss();
                    }
                    HelperUtil.totastShow("注册成功\n您的账号是您的手机号\n您的密码是：123456", getApplicationContext());
                    startService(new Intent(RegsiterActivity.this, CoreIMService.class));
                    Intent intent = new Intent(RegsiterActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                    break;
                case -1:
                    if (dialog != null) {
                        dialog.dismiss();
                    }
                    HelperUtil.totastShow(PublicConstant.ToastCatch, getApplicationContext());
                    break;
                case 2:
                    if (dialog != null) {
                        dialog.dismiss();
                    }
                    HelperUtil.totastShow("账号已被注册", getApplicationContext());
                    break;
                default:
                    break;
            }
        }
    };


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // TODO Auto-generated method stub
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }


}
