package com.beibeilian;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import com.beibeilian.android.app.R;
import com.beibeilian.constant.BBLConstant;
import com.beibeilian.db.BBLDao;
import com.beibeilian.im.video.VideoCallActivity;
import com.beibeilian.me.MeActivity;
import com.beibeilian.me.MeMemberActivity;
import com.beibeilian.me.model.UserInfoEntiy;
import com.beibeilian.me.model.Version;
import com.beibeilian.model.PayRule;
import com.beibeilian.predestined.PredestinedActivity;
import com.beibeilian.privateletter.PrivateletterActivity;
import com.beibeilian.receiver.ReceiverConstant;
import com.beibeilian.seek.SeekActivity;
import com.beibeilian.service.CoreIMService;
import com.beibeilian.util.ExitApplication;
import com.beibeilian.util.HelperUtil;
import com.beibeilian.util.HttpConstantUtil;
import com.beibeilian.util.PreferencesUtils;
import com.beibeilian.util.PublicConstant;
import com.beibeilian.util.ServiceIsRunningUtil;
import com.beibeilian.util.UpdateManager;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.TabActivity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TabHost;
import android.widget.TextView;

@SuppressLint({"HandlerLeak", "ResourceAsColor"})
public class MainActivity extends TabActivity {
    private TabHost tabhost;
    private ImageView tab_one_btn;
    private ImageView tab_two_btn;
    private ImageView tab_three_btn;
    private ImageView tab_four_btn;
    private RelativeLayout tab_one_back;
    private RelativeLayout tab_two_back;
    private RelativeLayout tab_three_back;
    private RelativeLayout tab_four_back;

    private IntentFilter intentFilter;

    private GuardReceiver guardReceiver;

    private BBLDao dao;

    private TextView tab_two_unread;

    private TextView tab_four_unread;

    private TextView tab_three_unread;

    private UserInfoEntiy user;

    private String username, password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        ExitApplication.addActivity(MainActivity.this);
        dao = new BBLDao(MainActivity.this, null, null, 1);
        user = dao.queryUserByNewTime();
        intentFilter = new IntentFilter();
        guardReceiver = new GuardReceiver();
        intentFilter.setPriority(2147483647);
        intentFilter.addAction(ReceiverConstant.GuardReceiver);
        intentFilter.addAction(ReceiverConstant.PredestinedLiaoLiao_ACTION);
        intentFilter.addAction(ReceiverConstant.TAB_TWO_REMAIND_ACTION);
        intentFilter.addAction(ReceiverConstant.TAB_THREE_REMAIND_ACTION);
        intentFilter.addAction(ReceiverConstant.StartPayDialogACTION);
        registerReceiver(guardReceiver, intentFilter);
        initView();
        loginUpateLevel();

        if (!ServiceIsRunningUtil.isServiceRunning(MainActivity.this, PublicConstant.CodeServicePackName)) {
            startService(new Intent(MainActivity.this, CoreIMService.class));
        }
        findMyGroupnum();
        refeshPayRule();
        checkMemberState();
        username = user.getUsername();
        password = user.getPassword();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                // TODO Auto-generated method stub
                String everday = PreferencesUtils.getString(MainActivity.this, "everday");
                if (TextUtils.isEmpty(everday) || (Integer.parseInt(HelperUtil.DateTimeDay()) > Integer.parseInt(everday))) {
                    handler.sendEmptyMessage(3);
                    PreferencesUtils.putString(MainActivity.this, "everday", HelperUtil.DateTimeDay());
                } else {
                    handler.sendEmptyMessage(5);
                }
            }
        }, 5000);
    }

    private class GuardReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            // TODO Auto-generated method stub

            if (intent.getAction().equals(ReceiverConstant.GuardReceiver)) {
                if (!ServiceIsRunningUtil.isServiceRunning(context, PublicConstant.CodeServicePackName)) {
                    context.startService(new Intent(context, CoreIMService.class));
                }
            }
            if (intent.getAction().equals(ReceiverConstant.PredestinedLiaoLiao_ACTION)) {
                tabhost.setCurrentTabByTag("缘分");
                tab_one_btn.setImageResource(R.drawable.predestined_press);
                tab_two_btn.setImageResource(R.drawable.private_msg_nom);
                tab_three_btn.setImageResource(R.drawable.seek_nom);
                tab_four_btn.setImageResource(R.drawable.me_nom);
                tab_one_back.setBackgroundDrawable(getResources().getDrawable(R.drawable.tab_everyone_press_back));
                tab_two_back.setBackgroundDrawable(getResources().getDrawable(android.R.color.transparent));
                tab_three_back.setBackgroundDrawable(getResources().getDrawable(android.R.color.transparent));
                tab_four_back.setBackgroundDrawable(getResources().getDrawable(android.R.color.transparent));
                sendBroadcast(new Intent(ReceiverConstant.Predestined_ACTION));
            }
            if (intent.getAction().equals(ReceiverConstant.TAB_TWO_REMAIND_ACTION)) {
                int result = dao.queryCountByUserNewState(user.getUsername());
                if (result > 0) {
                    tab_two_unread.setVisibility(View.VISIBLE);
                } else {
                    tab_two_unread.setVisibility(View.GONE);
                }
            }

            if (intent.getAction().equals(ReceiverConstant.TAB_THREE_REMAIND_ACTION)) {
                tab_three_unread.setVisibility(View.VISIBLE);
                String type = intent.getStringExtra("type");
                if (type.equals(PublicConstant.GROUPTYPE)) {
                    updateGroupstate();
                }
            }

            if (intent.getAction().equals(ReceiverConstant.TAB_FOUR_REMAIND_ACTION)) {
                tab_four_unread.setVisibility(View.VISIBLE);
                String type = intent.getStringExtra("type");
                if (type.equals(PublicConstant.VISITTYPE)) {
                    updateVisitState();
                }
                if (type.equals(PublicConstant.ANLIANTYPE)) {
                    updateAnliantState();
                }
                if (type.equals(PublicConstant.MYQAQTYPE)) {
                    updateMyqaqState();
                }
            }
            if (intent.getAction().equals(ReceiverConstant.StartPayDialogACTION)) {
                startActivity(new Intent(context, MeMemberActivity.class));
            }
        }

    }

    private void checkMemberState() {

        new Thread(new Runnable() {

            @Override
            public void run() {
                // TODO Auto-generated method stu
                try {
                    Thread.sleep(2000);
                    String username = dao.queryUserByNewTime().getUsername();
                    Map<String, String> map = new HashMap<String, String>();
                    map.put("username", username);
                    JSONObject jsonObject = new JSONObject(HelperUtil.postRequest(HttpConstantUtil.CheckONMember, map));
                    handler.sendEmptyMessage(0);
                    if (jsonObject.optInt("result") == BBLConstant.MEMBER_STATE_NUMBER_OUT) {
                        dao.deleteVipMember();
//						PreferencesUtils.putString(MainActivity.this, "vip","0");
                    } else if (jsonObject.optInt("result") == BBLConstant.MEMBER_STATE_OUT) {
                        dao.deleteVipMember();
//						PreferencesUtils.putString(MainActivity.this, "vip","0");
                    } else {
                        PreferencesUtils.putString(MainActivity.this, "vip", "1");
                        dao.insertVipMember(username);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void refeshPayRule() {

        new Thread(new Runnable() {

            @Override
            public void run() {
                // TODO Auto-generated method stub
                try {
                    Thread.sleep(2000);
                    JSONObject jsonObject = new JSONObject(HelperUtil.getRequest(HttpConstantUtil.FindPriceAndPower));
                    if (jsonObject.optString("state").equals("1")) {
                        PayRule model = new PayRule();
                        model.setVisit(jsonObject.optString("visit"));
                        model.setAnlian(jsonObject.optString("anlian"));
                        model.setBall(jsonObject.optString("ball"));
                        model.setChat(jsonObject.optString("chat"));
                        model.setGift(jsonObject.optString("gift"));
                        model.setMarriage(jsonObject.optString("marriage"));
                        model.setRelation(jsonObject.optString("relation"));
                        model.setPrice_15(jsonObject.optString("price15"));
                        model.setPrice_30(jsonObject.optString("price30"));
                        model.setPrice_180(jsonObject.optString("price180"));
                        model.setPrice_360(jsonObject.optString("price360"));
                        dao.deletePayRule();
                        dao.insertPayRule(model);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();

    }

    private void findMyGroupnum() {
        new Thread(new Runnable() {

            @Override
            public void run() {
                // TODO Auto-generated method stub
                try {
                    Thread.sleep(2000);
                    Map<String, String> map = new HashMap<String, String>();
                    map.put("username", user.getUsername());
                    JSONArray jsonObject = new JSONArray(HelperUtil.postRequest(HttpConstantUtil.FindGroupnum, map));
                    if (jsonObject != null && jsonObject.length() > 0) {
                        for (int i = 0; i < jsonObject.length(); i++) {
                            String group = jsonObject.optJSONObject(i).optString("groupId");
                            dao.insertMygroup(user.getUsername(), group);
                        }
                    }
                } catch (Exception e) {

                }
            }
        }).start();
    }

    public void updateVisitState() {
        PreferencesUtils.putInt(MainActivity.this, "visitstate", 0);
    }

    public void updateAnliantState() {
        PreferencesUtils.putInt(MainActivity.this, "anlianstate", 0);
    }

    public void updateMyqaqState() {
        PreferencesUtils.putInt(MainActivity.this, "myqaqstate", 0);
    }

    public void updateGroupstate() {
        PreferencesUtils.putInt(MainActivity.this, "groupstate", 0);
    }

    @Override
    public void onResume() {
        super.onResume();

        HelperUtil.cancelAllNotificationManager(MainActivity.this);

        if (tabhost.getCurrentTab() == 0) {
            tab_one_btn.setImageResource(R.drawable.predestined_press);
            tab_two_btn.setImageResource(R.drawable.private_msg_nom);
            tab_three_btn.setImageResource(R.drawable.seek_nom);
            tab_four_btn.setImageResource(R.drawable.me_nom);
            tab_one_back.setBackgroundDrawable(getResources().getDrawable(R.drawable.tab_everyone_press_back));
            tab_two_back.setBackgroundDrawable(getResources().getDrawable(android.R.color.transparent));
            tab_three_back.setBackgroundDrawable(getResources().getDrawable(android.R.color.transparent));
            tab_four_back.setBackgroundDrawable(getResources().getDrawable(android.R.color.transparent));
        } else if (tabhost.getCurrentTab() == 1) {
            tab_one_btn.setImageResource(R.drawable.predestined_nom);
            tab_two_btn.setImageResource(R.drawable.private_msg_press);
            tab_three_btn.setImageResource(R.drawable.seek_nom);
            tab_four_btn.setImageResource(R.drawable.me_nom);
            tab_one_back.setBackgroundDrawable(getResources().getDrawable(android.R.color.transparent));
            tab_two_back.setBackgroundDrawable(getResources().getDrawable(R.drawable.tab_everyone_press_back));
            tab_three_back.setBackgroundDrawable(getResources().getDrawable(android.R.color.transparent));
            tab_four_back.setBackgroundDrawable(getResources().getDrawable(android.R.color.transparent));
        } else if (tabhost.getCurrentTab() == 2) {
            tab_one_btn.setImageResource(R.drawable.predestined_nom);
            tab_two_btn.setImageResource(R.drawable.private_msg_nom);
            tab_three_btn.setImageResource(R.drawable.seek_press);
            tab_four_btn.setImageResource(R.drawable.me_nom);
            tab_one_back.setBackgroundDrawable(getResources().getDrawable(android.R.color.transparent));
            tab_two_back.setBackgroundDrawable(getResources().getDrawable(android.R.color.transparent));
            tab_three_back.setBackgroundDrawable(getResources().getDrawable(R.drawable.tab_everyone_press_back));
            tab_four_back.setBackgroundDrawable(getResources().getDrawable(android.R.color.transparent));
        } else if (tabhost.getCurrentTab() == 3) {
            tab_one_btn.setImageResource(R.drawable.predestined_nom);
            tab_two_btn.setImageResource(R.drawable.private_msg_nom);
            tab_three_btn.setImageResource(R.drawable.seek_nom);
            tab_four_btn.setImageResource(R.drawable.me_press);
            tab_one_back.setBackgroundDrawable(getResources().getDrawable(android.R.color.transparent));
            tab_two_back.setBackgroundDrawable(getResources().getDrawable(android.R.color.transparent));
            tab_three_back.setBackgroundDrawable(getResources().getDrawable(android.R.color.transparent));
            tab_four_back.setBackgroundDrawable(getResources().getDrawable(R.drawable.tab_everyone_press_back));
        }

        int result = dao.queryCountByUserNewState(user.getUsername());
        if (result > 0) {
            tab_two_unread.setVisibility(View.VISIBLE);
        } else {
            tab_two_unread.setVisibility(View.GONE);
        }

        int visitstate = PreferencesUtils.getInt(MainActivity.this, "visitstate");
        int remindanlian = PreferencesUtils.getInt(MainActivity.this, "anlianstate");
        int myqaq = PreferencesUtils.getInt(MainActivity.this, "myqaqstate");
        int group = PreferencesUtils.getInt(MainActivity.this, "groupstate");

        if (visitstate == 0) {
            tab_four_unread.setVisibility(View.VISIBLE);
        } else if (remindanlian == 0) {
            tab_four_unread.setVisibility(View.VISIBLE);
        } else if (myqaq == 0) {
            tab_four_unread.setVisibility(View.VISIBLE);
        } else {
            tab_four_unread.setVisibility(View.GONE);
        }
        if (group == 0) {
            tab_three_unread.setVisibility(View.VISIBLE);
        } else {
            tab_three_unread.setVisibility(View.GONE);
        }
        Version version = dao.queryVersion();

        if (version != null) {
            if (HelperUtil.flagISNoNull(version.getCode())) {
                if (HelperUtil.isUpdateVersion(HelperUtil.getVersionCode(MainActivity.this), version.getCode())) {
                    tab_four_unread.setVisibility(View.VISIBLE);
                    return;
                }
            }
        }
        tab_four_unread.setVisibility(View.GONE);

    }

    private void initView() {

        try {
            tabhost = this.getTabHost();
            TabHost.TabSpec spec;
            Intent intent;
            intent = new Intent(MainActivity.this, PredestinedActivity.class);
            spec = tabhost.newTabSpec("缘分").setIndicator("缘分").setContent(intent);
            tabhost.addTab(spec);
            intent = new Intent(MainActivity.this, PrivateletterActivity.class);
            spec = tabhost.newTabSpec("私信").setIndicator("私信").setContent(intent);
            tabhost.addTab(spec);
            intent = new Intent(MainActivity.this, SeekActivity.class);
            spec = tabhost.newTabSpec("发现").setIndicator("发现").setContent(intent);
            tabhost.addTab(spec);
            intent = new Intent(MainActivity.this, MeActivity.class);
            spec = tabhost.newTabSpec("我的").setIndicator("我的").setContent(intent);
            tabhost.addTab(spec);

            // tabhost.setCurrentTab(0);
            tab_one_btn = (ImageView) findViewById(R.id.tab_one_id);
            tab_two_btn = (ImageView) findViewById(R.id.tab_two_id);
            tab_three_btn = (ImageView) findViewById(R.id.tab_three_id);
            tab_four_btn = (ImageView) findViewById(R.id.tab_four_id);
            // tab_one_text=(TextView)findViewById(R.id.tab_one_text_id);
            // tab_two_text=(TextView)findViewById(R.id.tab_two_text_id);
            // tab_three_text=(TextView)findViewById(R.id.tab_three_text_id);
            // tab_four_text=(TextView)findViewById(R.id.tab_four_text_id);
            tab_one_back = (RelativeLayout) findViewById(R.id.tab_one_back_id);
            tab_two_back = (RelativeLayout) findViewById(R.id.tab_two_back_id);
            tab_three_back = (RelativeLayout) findViewById(R.id.tab_three_back_id);
            tab_four_back = (RelativeLayout) findViewById(R.id.tab_four_back_id);

            tab_two_unread = (TextView) findViewById(R.id.tab_two_unread);

            tab_four_unread = (TextView) findViewById(R.id.tab_four_unread);

            tab_three_unread = (TextView) findViewById(R.id.tab_three_unread);

            tab_one_back.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    // TODO Auto-generated method stub
                    // tabhost.setCurrentTab(0);
                    tabhost.setCurrentTabByTag("缘分");
                    tab_one_btn.setImageResource(R.drawable.predestined_press);
                    tab_two_btn.setImageResource(R.drawable.private_msg_nom);
                    tab_three_btn.setImageResource(R.drawable.seek_nom);
                    tab_four_btn.setImageResource(R.drawable.me_nom);
                    tab_one_back.setBackgroundDrawable(getResources().getDrawable(R.drawable.tab_everyone_press_back));
                    tab_two_back.setBackgroundDrawable(getResources().getDrawable(android.R.color.transparent));
                    tab_three_back.setBackgroundDrawable(getResources().getDrawable(android.R.color.transparent));
                    tab_four_back.setBackgroundDrawable(getResources().getDrawable(android.R.color.transparent));
                    // sendBroadcast(new Intent(
                    // ReceiverConstant.Predestined_ACTION));
                }
            });
            tab_two_back.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    // TODO Auto-generated method stub
                    // tabhost.setCurrentTab(1);
                    tabhost.setCurrentTabByTag("私信");
                    tab_one_btn.setImageResource(R.drawable.predestined_nom);
                    tab_two_btn.setImageResource(R.drawable.private_msg_press);
                    tab_three_btn.setImageResource(R.drawable.seek_nom);
                    tab_four_btn.setImageResource(R.drawable.me_nom);
                    tab_one_back.setBackgroundDrawable(getResources().getDrawable(android.R.color.transparent));
                    tab_two_back.setBackgroundDrawable(getResources().getDrawable(R.drawable.tab_everyone_press_back));
                    tab_three_back.setBackgroundDrawable(getResources().getDrawable(android.R.color.transparent));
                    tab_four_back.setBackgroundDrawable(getResources().getDrawable(android.R.color.transparent));
                }
            });
            tab_three_back.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    // TODO Auto-generated method stub
                    tabhost.setCurrentTabByTag("发现");
                    tab_one_btn.setImageResource(R.drawable.predestined_nom);
                    tab_two_btn.setImageResource(R.drawable.private_msg_nom);
                    tab_three_btn.setImageResource(R.drawable.seek_press);
                    tab_four_btn.setImageResource(R.drawable.me_nom);
                    tab_one_back.setBackgroundDrawable(getResources().getDrawable(android.R.color.transparent));
                    tab_two_back.setBackgroundDrawable(getResources().getDrawable(android.R.color.transparent));
                    tab_three_back
                            .setBackgroundDrawable(getResources().getDrawable(R.drawable.tab_everyone_press_back));
                    tab_four_back.setBackgroundDrawable(getResources().getDrawable(android.R.color.transparent));

                }
            });
            tab_four_back.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    // TODO Auto-generated method stub
                    tabhost.setCurrentTabByTag("我的");
                    tab_one_btn.setImageResource(R.drawable.predestined_nom);
                    tab_two_btn.setImageResource(R.drawable.private_msg_nom);
                    tab_three_btn.setImageResource(R.drawable.seek_nom);
                    tab_four_btn.setImageResource(R.drawable.me_press);
                    tab_one_back.setBackgroundDrawable(getResources().getDrawable(android.R.color.transparent));
                    tab_two_back.setBackgroundDrawable(getResources().getDrawable(android.R.color.transparent));
                    tab_three_back.setBackgroundDrawable(getResources().getDrawable(android.R.color.transparent));
                    tab_four_back.setBackgroundDrawable(getResources().getDrawable(R.drawable.tab_everyone_press_back));
                }
            });
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }
    }

    private void loginUpateLevel() {
        new Thread(new Runnable() {

            @Override
            public void run() {
                // TODO Auto-generated method stub

                try {
                    JSONObject jsonObject = new JSONObject(HelperUtil.getRequest(HttpConstantUtil.FindVersion));
                    if (jsonObject.length() > 0) {
                        Version version = new Version();
                        version.setCode(jsonObject.optString("code"));
                        version.setContent(jsonObject.optString("content"));
                        version.setSize(jsonObject.optString("size"));
                        version.setUrl(jsonObject.optString("url"));
                        if (HelperUtil.isUpdateVersion(HelperUtil.getVersionCode(MainActivity.this),
                                version.getCode())) {
                            dao.updateVersion(version);
                            handler.sendEmptyMessage(1);
                        }
                    }
                    Map<String, String> map = new HashMap<String, String>();
                    UserInfoEntiy user = dao.queryUserByNewTime();
                    if (user != null && HelperUtil.flagISNoNull(user.getState()) && user.getState().equals("1")) {

                        map.put("username", user.getUsername());
                        map.put("password", HelperUtil.MD5(user.getPassword()));
                        jsonObject = new JSONObject(HelperUtil.postRequest(HttpConstantUtil.Login, map));
                        if (jsonObject.length() > 0) {
                            String result = jsonObject.optString("result");
                            if (HelperUtil.flagISNoNull(result)) {
                                if (result.equals("1")) {
                                    String sex = jsonObject.optString("sex");
                                    String nickname = jsonObject.optString("nickname");
                                    String level = jsonObject.optString("level");
                                    dao.initUser(user.getUsername(), nickname, user.getPassword(), level, sex);
                                }
                            }
                        }
                    }

//					int count = dao.queryMessageCount(user.getUsername());
//					if (count == 0) {
//						dao.delImNoMine(user.getUsername());
//						map.put("pagenumber", "0");
//						JSONArray jsonArray = new JSONArray(HelperUtil.postRequest(HttpConstantUtil.FindImRecord, map));
//						if (jsonArray.length() > 0) {
//
//							for (int i = 0; i < jsonArray.length(); i++) {
//								String outuser = jsonArray.optJSONObject(i).optString("outuser");
//								String inuser = jsonArray.optJSONObject(i).optString("inuser");
//								String content = jsonArray.optJSONObject(i).optString("content");
//								String time = jsonArray.optJSONObject(i).optString("time");
//								String imid = jsonArray.optJSONObject(i).optString("imid");
//								dao.updateRefreashIm(outuser, inuser, content, time, "1", imid, "1");
//							}
//						} else {
//							dao.delImNoMine(user.getUsername());
//						}
//					}
//					jsonObject = new JSONObject(HelperUtil.getRequest(HttpConstantUtil.FindNotice));
//					if (jsonObject.length() > 0) {
//						android.os.Message message = handler.obtainMessage();
//						message.obj = new String[] { jsonObject.optString("title"), jsonObject.optString("content") };
//						message.what = 2;
//						message.sendToTarget();
//					}
                } catch (Exception e) {
                    // TODO: handle exception
                    e.printStackTrace();
                }

            }
        }).start();
    }

    private void showNotice(final String title, final String content) {

        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle(title);
        builder.setMessage(content).setCancelable(false).setNegativeButton("关闭", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        }).show();

    }

    private Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case 1:
                    tab_four_unread.setVisibility(View.VISIBLE);
                    new UpdateManager(MainActivity.this).startUpdateInfo();
                    break;
                case 2:
                    String[] message = (String[]) msg.obj;
                    showNotice(message[0], message[1]);
                    break;
                case 3:
                    startActivity(new Intent(MainActivity.this, EverDayActivity.class));
                    break;
                case 5:
                    new Handler().postDelayed(new Runnable() {

                        @Override
                        public void run() {
                            // TODO Auto-generated method stub
                            handler.sendEmptyMessage(6);
                        }
                    }, 6000);
                    break;
                case 6:
                    //startActivity(new Intent(MainActivity.this, VideoCallActivity.class));
                    break;
                default:
                    break;
            }

        }
    };

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK && event.getAction() != KeyEvent.ACTION_UP) {// 不响应按键抬起时的动作
            // TODO 代码
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            return true;// 注意这儿返回值为true时该事件将不会继续往下传递，false时反之。根据程序的需要调整
        }
        return super.dispatchKeyEvent(event);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (guardReceiver != null)
            unregisterReceiver(guardReceiver);
    }


//	@Override
//	public void onLocationChanged(AMapLocation amapLocation) {
//		// TODO Auto-generated method stub
//		if (amapLocation != null && amapLocation.getErrorCode() == 0) {
//			final double lat = amapLocation.getLatitude();
//			final double lon = amapLocation.getLongitude();
//			new Thread(new Runnable() {
//
//				@Override
//				public void run() {
//					// TODO Auto-generated method stub
//					Map<String, String> map = new HashMap<String, String>();
//					map.put("username", dao.queryUserByNewTime().getUsername());
//					map.put("lat", String.valueOf(lat));
//					map.put("lon", String.valueOf(lon));
//					try {
//						HelperUtil.postRequest(HttpConstantUtil.UpLocation, map);
//					} catch (Exception e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//					}
//
//				}
//			}).start();
//
//		}
//
//	}

}
