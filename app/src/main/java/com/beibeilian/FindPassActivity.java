package com.beibeilian;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;

import com.beibeilian.util.ExitApplication;
import com.beibeilian.util.HelperUtil;
import com.beibeilian.util.HttpConstantUtil;
import com.beibeilian.android.app.R;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.view.View.OnClickListener;

public class FindPassActivity extends Activity {

    private Button btnBack;
    private EditText et_email;
    private Button btnSend;

    private FindThread findThread;

    private Dialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.find_pass);
        ExitApplication.addActivity(FindPassActivity.this);
        dialog = new Dialog(FindPassActivity.this, R.style.theme_dialog_alert);
        btnBack = (Button) findViewById(R.id.btnBack);
        btnSend = (Button) findViewById(R.id.sendid);
        et_email = (EditText) findViewById(R.id.emailid);
        btnSend.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                int len = et_email.getText().toString().trim().length();
                if (len == 0) {
                    HelperUtil.totastShow("�ʺŲ���Ϊ��", FindPassActivity.this);
                    return;
                }
                HelperUtil.customDialogShow(dialog, FindPassActivity.this, "���ڼ���ʺ�... ");
                if (findThread != null) {
                    findThread.interrupt();
                }
                findThread = new FindThread();
                findThread.start();

            }
        });
        btnBack.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
//			   startActivity(new Intent(FindPassActivity.this,LoginActivity.class))	;
                finish();

            }
        });
    }

    private class FindThread extends Thread {

        @Override
        public void run() {
            try {

                Map<String, String> map = new HashMap<String, String>();
                map.put("username", et_email.getText().toString().trim());
                JSONObject jsonObject = new JSONObject(HelperUtil.postRequest(
                        HttpConstantUtil.FindQuesstion, map));
                handler.sendEmptyMessage(1);
                if (jsonObject.length() > 0) {
                    String question = jsonObject.optString("quesstion");

                    if (HelperUtil.flagISNoNull(question)) {
                        handler.sendEmptyMessage(1);
                        Intent intent = new Intent(FindPassActivity.this, FindPassInputAnswerActivity.class);
                        intent.putExtra("username", et_email.getText().toString().trim());
                        intent.putExtra("quesstion", question);
                        startActivity(intent);
                    } else {
                        handler.sendEmptyMessage(2);
                    }
                } else {
                    handler.sendEmptyMessage(2);
                }
            } catch (Exception e) {
                e.printStackTrace();
                handler.sendEmptyMessage(0);
            }

        }
    }
    /**
     * ���ܽ����㷨 ִ��һ�μ��ܣ����ν���
     */
    public static String convertMD5(String inStr) {


        char[] a = inStr.toCharArray();
        for (int i = 0; i < a.length; i++) {
            a[i] = (char) (a[i] ^ 't');
        }
        String s = new String(a);
        return s;


    }
    private Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case 1:
                    if (dialog != null) dialog.dismiss();
                    break;
                case 2:
                    if (dialog != null) dialog.dismiss();
                    HelperUtil.totastShow("���ʺ�δ�����ܱ��򲻴��ڴ��ʺ�", FindPassActivity.this);
                    break;
                case 0:
                    if (dialog != null) dialog.dismiss();
                    HelperUtil.totastShow("���������Ƿ���û��Ժ�����", FindPassActivity.this);
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            startActivity(new Intent(FindPassActivity.this, LoginActivity.class));
//			   finish();
        }
        return false;
    }

    @Override
    public void onPause() {
        super.onPause();
        if (dialog != null) dialog.dismiss();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (findThread != null) {
            findThread.interrupt();
        }
    }

}
