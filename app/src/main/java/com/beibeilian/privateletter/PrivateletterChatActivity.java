package com.beibeilian.privateletter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import com.beibeilian.android.app.R;
import com.beibeilian.chat.widget.DropdownListView;
import com.beibeilian.chat.widget.DropdownListView.OnRefreshListenerHeader;
import com.beibeilian.chat.widget.MyEditText;
import com.beibeilian.db.BBLDao;
import com.beibeilian.me.MeMemberActivity;
import com.beibeilian.me.MePhotoImageViewUploadActivity;
import com.beibeilian.me.application.BBLApplication;
import com.beibeilian.me.model.UserInfoEntiy;
import com.beibeilian.predestined.PersionDetailActivity;
import com.beibeilian.privateletter.adapter.FaceAdapter;
import com.beibeilian.privateletter.adapter.PrivateletterChatAdapter;
import com.beibeilian.privateletter.model.HomeMessage;
import com.beibeilian.receiver.ReceiverConstant;
import com.beibeilian.seek.model.UserInfo;
import com.beibeilian.service.CoreIMService;
import com.beibeilian.util.ComparatorValues;
import com.beibeilian.util.HelperUtil;
import com.beibeilian.util.PreferencesUtils;
import com.beibeilian.util.PublicConstant;
import com.beibeilian.util.ServiceIsRunningUtil;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ImageSpan;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ProgressBar;
import android.widget.TextView;

public class PrivateletterChatActivity extends Activity implements OnClickListener, OnRefreshListenerHeader {

	private int mCurrentPage = 0;// ��ǰ����ҳ

	private InputMethodManager mInputMethodManager;

	private List<String> mFaceMapKeys;

	private String toUser;

	private String toName;

	private String username;

	private BBLDao dao;

	private List<HomeMessage> listMessage = new ArrayList<HomeMessage>();

	private List<HomeMessage> listMessageUi = new ArrayList<HomeMessage>();

	private int PAGENUMBER = 0;

	private TextView tv_title;

	private PrivateletterChatAdapter privateletterChatAdapter;

	private InitLoadThread initLoadThread;

	private IntentFilter intentFilter;

	private MessageReceiver messageReceiver;

	private Button btnBack;

	private ProgressBar connProgressbar;

	private Button btnShow;

	private String nickname;

	// private ViewPager mViewPager;

	private MyEditText input;

	private Button send;

	private DropdownListView mListView;

	private LinearLayout chat_face_container;

	private ImageView image_face;// ����ͼ��

	// private LinearLayout chat_bottom_replay,chat_bottom;
	private boolean isFromSend = false;
	private Button btnReplay;
	private String vip = "0";

	private class MessageReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			if (intent.getAction().equals(ReceiverConstant.MESSAGEREFEASHLIST_ACTION)) {
				String fromUser = intent.getStringExtra("fromUser");
				if (fromUser.equals(toUser)) {
					dao.updateImSeeState(toUser);
					PAGENUMBER = 0;
					if (initLoadThread != null) {
						initLoadThread.interrupt();
					}
					initLoadThread = new InitLoadThread();
					initLoadThread.start();
				}
			}
			if (intent.getAction().equals(ReceiverConstant.MESSAGE_SEND_REFREASH_ACTION)) {
				PAGENUMBER = 0;
				if (initLoadThread != null) {
					initLoadThread.interrupt();
				}
				initLoadThread = new InitLoadThread();
				initLoadThread.start();

			}

			if (intent.getAction().equals(ReceiverConstant.RECONN_RECEIVER_START_ACTION)) {
				connProgressbar.setVisibility(View.VISIBLE);
			}
			if (intent.getAction().equals(ReceiverConstant.RECONN_RECEIVER_SUCCESS_ACTION)) {
				connProgressbar.setVisibility(View.GONE);
			}
			if (intent.getAction().equals(ReceiverConstant.ME_MEMBER_REFESH_ACTION)) {
				vip = PreferencesUtils.getString(PrivateletterChatActivity.this, "vip");
				// if(vip!=null&&vip.equals("1"))
				// {
				// chat_bottom.setVisibility(View.VISIBLE);
				// chat_bottom_replay.setVisibility(View.GONE);
				// }
			}
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.chat_main);
		vip = PreferencesUtils.getString(PrivateletterChatActivity.this, "vip");
		connProgressbar = (ProgressBar) findViewById(R.id.connectid);
		intentFilter = new IntentFilter();
		intentFilter.addAction(ReceiverConstant.MESSAGEREFEASHLIST_ACTION);
		intentFilter.addAction(ReceiverConstant.MESSAGE_SEND_REFREASH_ACTION);
		intentFilter.addAction(ReceiverConstant.RECONN_RECEIVER_START_ACTION);
		intentFilter.addAction(ReceiverConstant.RECONN_RECEIVER_SUCCESS_ACTION);
		intentFilter.addAction(ReceiverConstant.ME_MEMBER_REFESH_ACTION);
		messageReceiver = new MessageReceiver();
		registerReceiver(messageReceiver, intentFilter);
		initView();
		if (initLoadThread != null) {
			initLoadThread.interrupt();
		}
		initLoadThread = new InitLoadThread();
		initLoadThread.start();

	}

	private void initView() {
		mInputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);

		mListView = (DropdownListView) findViewById(R.id.message_chat_listview);
		// ����ͼ��
		image_face = (ImageView) findViewById(R.id.image_face);
		// ���鲼��
		chat_face_container = (LinearLayout) findViewById(R.id.chat_face_container);
		// mViewPager = (ViewPager) findViewById(R.id.face_viewpager);
		// chat_bottom_replay=(LinearLayout) findViewById(R.id.bottom_replay);
		// chat_bottom=(LinearLayout) findViewById(R.id.bottom);
		btnReplay = (Button) findViewById(R.id.btn_replay_pay);

		input = (MyEditText) findViewById(R.id.input_sms);
		input.setOnClickListener(this);
		send = (Button) findViewById(R.id.send_sms);
		// ���鰴ť
		image_face.setOnClickListener(this);
		// ����
		send.setOnClickListener(this);
		btnReplay.setOnClickListener(this);

		mListView.setOnRefreshListenerHead(this);
		mListView.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View arg0, MotionEvent arg1) {
				if (arg1.getAction() == MotionEvent.ACTION_DOWN) {
					if (chat_face_container.getVisibility() == View.VISIBLE) {
						chat_face_container.setVisibility(View.GONE);
					}
					mInputMethodManager.hideSoftInputFromWindow(input.getWindowToken(), 0);
				}
				return false;
			}
		});
		input.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				if (event.getAction() == MotionEvent.ACTION_DOWN) {
					if (chat_face_container.getVisibility() == View.VISIBLE) {
						chat_face_container.setVisibility(View.GONE);
					}
				}
				return false;
			}
		});
		dao = new BBLDao(PrivateletterChatActivity.this, null, null, 1);
		UserInfoEntiy mineuser = dao.queryUserByNewTime();
		username = mineuser.getUsername();
		nickname = mineuser.getNickname();
		toUser = getIntent().getStringExtra("toUser");
		toName = getIntent().getStringExtra("toName");
		String minephoto = null;
		String youphoto = null;
		String yousex = null;
		try {
			UserInfo photomine = dao.queryPhoto(username);
			if (photomine != null)
				minephoto = photomine.getPhoto();
			UserInfo photoyou = dao.queryPhoto(toUser);
			if (photoyou != null) {
				youphoto = photoyou.getPhoto();
				yousex = photoyou.getSex();
			}
		} catch (Exception e) {
		}
		privateletterChatAdapter = new PrivateletterChatAdapter(PrivateletterChatActivity.this, listMessageUi,
				minephoto, youphoto, toUser, toName, dao.queryUserByNewTime().getSex(), yousex, dao, nickname);
		btnShow = (Button) findViewById(R.id.showdetail_id);
		tv_title = (TextView) findViewById(R.id.ivTitleName);
		btnBack = (Button) findViewById(R.id.btnBack);
		btnBack.setOnClickListener(this);
		if (HelperUtil.flagISNoNull(toName)) {
			tv_title.setText(toName);
		} else {
			UserInfo model = dao.queryPhoto(toUser);
			if (model != null && HelperUtil.flagISNoNull(model.getNickname()))
				tv_title.setText(model.getNickname());
			else
				tv_title.setText(toUser);

		}
		btnShow.setOnClickListener(this);

		initFacePage();

		Button btnVideo = (Button) findViewById(R.id.send_video);
		btnVideo.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
				Intent intent = new Intent(
						Intent.ACTION_PICK,
						android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
				startActivityForResult(intent, 1);
			}
		});

		// if(vip==null||!vip.equals("1"))
		// {
		// isFromSend=dao.queryChatISSend(username, toUser);
		// if(isFromSend)
		// {
		// chat_bottom.setVisibility(View.GONE);
		// chat_bottom_replay.setVisibility(View.VISIBLE);
		// }
		// else
		// {
		// chat_bottom.setVisibility(View.VISIBLE);
		// chat_bottom_replay.setVisibility(View.GONE);
		// }
		// }
		// else
		// {
		// chat_bottom.setVisibility(View.VISIBLE);
		// chat_bottom_replay.setVisibility(View.GONE);
		// }
	}
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == 1 && resultCode == Activity.RESULT_OK
				&& data != null) {
			Uri selectedImage = data.getData();
			String[] filePathColumns = { MediaStore.Images.Media.DATA };
			Cursor c = this.getContentResolver().query(selectedImage,
					filePathColumns, null, null, null);
			c.moveToFirst();
			int columnIndex = c.getColumnIndex(filePathColumns[0]);
			String filepath = c.getString(columnIndex);
			c.close();
			Intent intent = new Intent(this,
					ChatImageViewUploadActivity.class);
			intent.putExtra("filePath", filepath);
			intent.putExtra("toUser", toUser);
			startActivity(intent);
		}
	}
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {

		case R.id.input_sms:// �����
			if (chat_face_container.getVisibility() == View.VISIBLE) {
				chat_face_container.setVisibility(View.GONE);
			}
			break;
		case R.id.image_face:// ����
			mInputMethodManager.hideSoftInputFromWindow(input.getWindowToken(), 0);
			if (chat_face_container.getVisibility() == View.GONE) {
				chat_face_container.setVisibility(View.VISIBLE);
			} else {
				chat_face_container.setVisibility(View.GONE);
			}

			break;
		case R.id.send_sms:// ����

			vip = PreferencesUtils.getString(PrivateletterChatActivity.this, "vip");
			if (TextUtils.isEmpty(vip) || !vip.equals("1")) {
				boolean result = dao.queryChatISSend3(username);
				if (result) {
					startActivity(new Intent(PrivateletterChatActivity.this, MeMemberActivity.class));
					return;
				}
			}
			String message = input.getText().toString().trim();
			if (message.length() == 0) {
				HelperUtil.totastShow("���ݲ���Ϊ��", PrivateletterChatActivity.this);
				return;
			}
			if (message.length() > 200) {
				HelperUtil.totastShow("����̫��", PrivateletterChatActivity.this);
				return;
			}
			if (!ServiceIsRunningUtil.isServiceRunning(PrivateletterChatActivity.this,
					PublicConstant.CodeServicePackName)) {
				// System.out.println("�������з���");
				startService(new Intent(PrivateletterChatActivity.this, CoreIMService.class));
				try {
					Thread.sleep(2000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			Intent intent = new Intent(ReceiverConstant.MESSAGE_SEND_ACTION);
			intent.putExtra("toID", toUser);
			intent.putExtra("nickname", nickname);
			intent.putExtra("message", message);
			sendBroadcast(intent);
			input.setText("");
			break;
		case R.id.btnBack:
			finish();
			break;
		case R.id.showdetail_id:
			intent = new Intent(PrivateletterChatActivity.this, PersionDetailActivity.class);
			intent.putExtra("toUser", toUser);
			intent.putExtra("toName", toName);
			startActivity(intent);
			break;
		case R.id.btn_replay_pay:
			startActivity(new Intent(PrivateletterChatActivity.this, MeMemberActivity.class));
			break;
		default:
			break;
		}
	}

	private void initFacePage() {
		// TODO Auto-generated method stub
		Set<String> keySet = BBLApplication.getFaceMap().keySet();
		mFaceMapKeys = new ArrayList<String>();
		mFaceMapKeys.addAll(keySet);
		//
		// List<View> lv = new ArrayList<View>();
		// for (int i = 0; i < BBLApplication.NUM_PAGE; ++i)
		// lv.add(getGridView(i));
		// FacePageAdeapter adapter = new FacePageAdeapter(lv);
		// mViewPager.setAdapter(adapter);
		// mViewPager.setCurrentItem(mCurrentPage);
		GridView indicator = (GridView) findViewById(R.id.face_dots_container);
		indicator.setNumColumns(7);
		indicator.setSelector(new ColorDrawable(Color.TRANSPARENT));// ����GridViewĬ�ϵ��Ч��
		indicator.setBackgroundColor(Color.TRANSPARENT);
		indicator.setCacheColorHint(Color.TRANSPARENT);
		indicator.setHorizontalSpacing(1);
		indicator.setVerticalSpacing(1);
		indicator.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		indicator.setGravity(Gravity.CENTER);
		indicator.setAdapter(new FaceAdapter(this, 0));
		indicator.setOnTouchListener(forbidenScroll());
		// adapter.notifyDataSetChanged();
		chat_face_container.setVisibility(View.GONE);

		indicator.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				// TODO Auto-generated method stub
				if (arg2 == BBLApplication.NUM) {// ɾ������λ��
					int selection = input.getSelectionStart();
					String text = input.getText().toString();
					if (selection > 0) {
						String text2 = text.substring(selection - 1);
						if ("]".equals(text2)) {
							int start = text.lastIndexOf("[");
							int end = selection;
							input.getText().delete(start, end);
							return;
						}
						input.getText().delete(selection - 1, selection);
					}
				} else {
					int count = mCurrentPage * BBLApplication.NUM + arg2;

					// �����ⲿ�֣���EditText����ʾ����
					Bitmap bitmap = BitmapFactory.decodeResource(getResources(),
							(Integer) BBLApplication.getFaceMap().values().toArray()[count]);
					if (bitmap != null) {
						int rawHeigh = bitmap.getHeight();
						int rawWidth = bitmap.getHeight();
						int newHeight = 40;
						int newWidth = 40;
						// ������������
						float heightScale = ((float) newHeight) / rawHeigh;
						float widthScale = ((float) newWidth) / rawWidth;
						// �½�������
						Matrix matrix = new Matrix();
						matrix.postScale(heightScale, widthScale);
						// ����ͼƬ����ת�Ƕ�
						// matrix.postRotate(-30);
						// ����ͼƬ����б
						// matrix.postSkew(0.1f, 0.1f);
						// ��ͼƬ��Сѹ��
						// ѹ����ͼƬ�Ŀ�͸��Լ�kB��С����仯
						Bitmap newBitmap = Bitmap.createBitmap(bitmap, 0, 0, rawWidth, rawHeigh, matrix, true);
						ImageSpan imageSpan = new ImageSpan(PrivateletterChatActivity.this, newBitmap);
						String emojiStr = mFaceMapKeys.get(count);
						SpannableString spannableString = new SpannableString(emojiStr);
						spannableString.setSpan(imageSpan, emojiStr.indexOf('['), emojiStr.indexOf(']') + 1,
								Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
						input.append(spannableString);
					} else {
						String ori = input.getText().toString();
						int index = input.getSelectionStart();
						StringBuilder stringBuilder = new StringBuilder(ori);
						stringBuilder.insert(index, mFaceMapKeys.get(count));
						input.setText(stringBuilder.toString());
						input.setSelection(index + mFaceMapKeys.get(count).length());
					}
				}
			}
		});

		// indicator.setOnPageChangeListener(new OnPageChangeListener() {
		//
		// @Override
		// public void onPageSelected(int arg0) {
		// mCurrentPage = arg0;
		// }
		//
		// @Override
		// public void onPageScrolled(int arg0, float arg1, int arg2) {
		// // do nothing
		// }
		//
		// @Override
		// public void onPageScrollStateChanged(int arg0) {
		// // do nothing
		// }
		// });

	}

	// private GridView getGridView(int i) {
	// // TODO Auto-generated method stub
	// GridView gv = new GridView(this);
	// gv.setNumColumns(7);
	// gv.setSelector(new ColorDrawable(Color.TRANSPARENT));// ����GridViewĬ�ϵ��Ч��
	// gv.setBackgroundColor(Color.TRANSPARENT);
	// gv.setCacheColorHint(Color.TRANSPARENT);
	// gv.setHorizontalSpacing(1);
	// gv.setVerticalSpacing(1);
	// gv.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
	// LayoutParams.MATCH_PARENT));
	// gv.setGravity(Gravity.CENTER);
	// gv.setAdapter(new FaceAdapter(this, i));
	// gv.setOnTouchListener(forbidenScroll());
	// gv.setOnItemClickListener(new OnItemClickListener() {
	//
	// @Override
	// public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
	// long arg3) {
	// // TODO Auto-generated method stub
	// if (arg2 == BBLApplication.NUM) {// ɾ������λ��
	// int selection = input.getSelectionStart();
	// String text = input.getText().toString();
	// if (selection > 0) {
	// String text2 = text.substring(selection - 1);
	// if ("]".equals(text2)) {
	// int start = text.lastIndexOf("[");
	// int end = selection;
	// input.getText().delete(start, end);
	// return;
	// }
	// input.getText().delete(selection - 1, selection);
	// }
	// } else {
	// int count = mCurrentPage * BBLApplication.NUM + arg2;
	//
	// // �����ⲿ�֣���EditText����ʾ����
	// Bitmap bitmap = BitmapFactory.decodeResource(
	// getResources(), (Integer) BBLApplication
	// .getFaceMap().values().toArray()[count]);
	// if (bitmap != null) {
	// int rawHeigh = bitmap.getHeight();
	// int rawWidth = bitmap.getHeight();
	// int newHeight = 40;
	// int newWidth = 40;
	// // ������������
	// float heightScale = ((float) newHeight) / rawHeigh;
	// float widthScale = ((float) newWidth) / rawWidth;
	// // �½�������
	// Matrix matrix = new Matrix();
	// matrix.postScale(heightScale, widthScale);
	// // ����ͼƬ����ת�Ƕ�
	// // matrix.postRotate(-30);
	// // ����ͼƬ����б
	// // matrix.postSkew(0.1f, 0.1f);
	// // ��ͼƬ��Сѹ��
	// // ѹ����ͼƬ�Ŀ�͸��Լ�kB��С����仯
	// Bitmap newBitmap = Bitmap.createBitmap(bitmap, 0, 0,
	// rawWidth, rawHeigh, matrix, true);
	// ImageSpan imageSpan = new ImageSpan(
	// PrivateletterChatActivity.this, newBitmap);
	// String emojiStr = mFaceMapKeys.get(count);
	// SpannableString spannableString = new SpannableString(
	// emojiStr);
	// spannableString.setSpan(imageSpan,
	// emojiStr.indexOf('['),
	// emojiStr.indexOf(']') + 1,
	// Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
	// input.append(spannableString);
	// } else {
	// String ori = input.getText().toString();
	// int index = input.getSelectionStart();
	// StringBuilder stringBuilder = new StringBuilder(ori);
	// stringBuilder.insert(index, mFaceMapKeys.get(count));
	// input.setText(stringBuilder.toString());
	// input.setSelection(index
	// + mFaceMapKeys.get(count).length());
	// }
	// }
	// }
	// });
	// return gv;
	// }

	// ��ֹ��pageview�ҹ���
	private OnTouchListener forbidenScroll() {
		return new OnTouchListener() {
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_MOVE) {
					return true;
				}
				return false;
			}
		};
	}

	// @Override
	// public boolean onTouch(View v, MotionEvent event) {
	// switch (v.getId()) {
	// case R.id.message_chat_listview:
	// mInputMethodManager.hideSoftInputFromWindow(
	// input.getWindowToken(), 0);
	// // mFaceSwitchBtn
	// // .setImageResource(R.drawable.qzone_edit_face_drawable);
	// // mFaceRoot.setVisibility(View.GONE);
	// // mIsFaceShow = false;
	// break;
	//// case R.id.input_sms:
	//// mInputMethodManager.showSoftInput(mChatEditText, 0);
	//// // mFaceSwitchBtn
	//// // .setImageResource(R.drawable.qzone_edit_face_drawable);
	//// // mFaceRoot.setVisibility(View.GONE);
	//// // mIsFaceShow = false;
	//// break;
	//
	// default:
	// break;
	// }
	// return false;
	// }

	public void hideSoftInputView() {
		InputMethodManager manager = ((InputMethodManager) this.getSystemService(Activity.INPUT_METHOD_SERVICE));
		if (getWindow().getAttributes().softInputMode != WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN) {
			if (getCurrentFocus() != null)
				manager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
		}
	}

	private class InitLoadThread extends Thread {
		@Override
		public synchronized void run() {

			List<HomeMessage> listTemp = dao.queryChatMessageList(username, toUser, String.valueOf(PAGENUMBER));
			if (listTemp != null && listTemp.size() > 0) {

				if (listMessage.size() > 0) {
					listMessage.clear();
				}
				for (int i = 0; i < listTemp.size(); i++) {
					String from = listTemp.get(i).getFrom();
					if (from.equals("IN")) {
						isFromSend = true;
					}
					String content = listTemp.get(i).getContent();
					String time = listTemp.get(i).getTime();
					String sendstate = listTemp.get(i).getSendstate();
					String imid = listTemp.get(i).getImid();
					String[] newMsg = new String[] { from, content, time, sendstate, imid };
					android.os.Message message = handler.obtainMessage();
					message.what = PublicConstant.UpdateList;
					message.obj = newMsg;
					message.sendToTarget();
				}
				handler.sendEmptyMessage(PublicConstant.JsonYesUI);
			} else {
				handler.sendEmptyMessage(PublicConstant.JsonNoUI);
			}
		}
	}

	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {

			switch (msg.what) {
			case PublicConstant.JsonYesUI:
				updateSoureToUi();
				updateYesUi();
				mListView.onRefreshCompleteHeader();
				break;
			case PublicConstant.JsonNoUI:
				updateNoUi();
				mListView.onRefreshCompleteHeader();
				break;
			case PublicConstant.UpdateList:
				String[] newMsg = (String[]) msg.obj;
				updateList(newMsg[0], newMsg[1], newMsg[2], newMsg[3], newMsg[4]);
				break;
			default:
				break;
			}
		}

	};

	public void updateYesUi() {
		try {
			if (PAGENUMBER == 0) {
				if (privateletterChatAdapter != null) {
					mListView.requestLayout();
					mListView.setAdapter(privateletterChatAdapter);
					mListView.setSelection(mListView.getBottom());
				}
			} else {
				if (privateletterChatAdapter != null) {
					mListView.requestLayout();
					privateletterChatAdapter.notifyDataSetChanged();
					mListView.onRefreshCompleteHeader();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();

		}

	}

	public void updateNoUi() {

		if (PAGENUMBER > 0) {
			if (privateletterChatAdapter != null) {
				mListView.requestLayout();
				privateletterChatAdapter.notifyDataSetChanged();
			}
		} else {

			if (listMessageUi.size() > 0) {
				listMessageUi.clear();
			}
			mListView.setAdapter(privateletterChatAdapter);
		}
	}

	public void updateList(String from, String content, String time, String sendstate, String imid) {
		HomeMessage model = new HomeMessage();
		model.setFrom(from);
		model.setImid(imid);
		model.setTime(time);
		model.setSendstate(sendstate);
		model.setContent(content);
		listMessage.add(model);
	}

	public void updateSoureToUi() {
		if (PAGENUMBER == 0 && listMessageUi != null && listMessageUi.size() > 0) {
			listMessageUi.clear();
		}
		for (int i = listMessage.size() - 1; i >= 0; i--) {
			String content = listMessage.get(i).getContent();
			String time = listMessage.get(i).getTime();
			String sendstate = listMessage.get(i).getSendstate();
			String from = listMessage.get(i).getFrom();
			String imid = listMessage.get(i).getImid();
			HomeMessage model = new HomeMessage();
			model.setFrom(from);
			model.setImid(imid);
			model.setTime(time);
			model.setSendstate(sendstate);
			model.setContent(content);
			listMessageUi.add(model);
		}
		Collections.sort(listMessageUi, new ComparatorValues());

	}

	@Override
	public void onRefresh() {
		// TODO Auto-generated method stub
		PAGENUMBER++;
		if (initLoadThread != null) {
			initLoadThread.interrupt();
		}
		initLoadThread = new InitLoadThread();
		initLoadThread.start();

	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		if (messageReceiver != null) {
			unregisterReceiver(messageReceiver);
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			finish();
		}
		return false;
	}

}