package com.beibeilian.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import org.jivesoftware.smack.ConnectionListener;
import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.filter.MessageTypeFilter;
import org.jivesoftware.smack.filter.PacketFilter;
import org.jivesoftware.smack.filter.PacketTypeFilter;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smackx.muc.MultiUserChat;

import com.beibeilian.MainActivity;
import com.beibeilian.android.app.R;
import com.beibeilian.db.BBLDao;
import com.beibeilian.me.model.Remind;
import com.beibeilian.me.model.UserInfoEntiy;
import com.beibeilian.predestined.PersionDetailActivity;
import com.beibeilian.receiver.ReceiverConstant;
import com.beibeilian.util.HelperUtil;
import com.beibeilian.util.HttpConstantUtil;
import com.beibeilian.util.MessageConstantUtil;
import com.beibeilian.util.PublicConstant;
import com.beibeilian.util.XmppConnManager;


import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

@SuppressLint("HandlerLeak")
public class CoreIMService extends Service {

	private static XMPPConnection xmppConnection = null;
	private BBLDao dao;
	private String username;
	private String password;
	private SendMessageReceiver sendMessageReceiver;
	private IntentFilter intentFilter;
	private NotificationManager notificationManager;
	private int remind_voice;
	private int remind_zhendong;
	private MultiUserChat multiUserChat;
	static {
		try {
			Class.forName("org.jivesoftware.smack.ReconnectionManager");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}

	private Remind remind;

	@Override
	public void onCreate() {
		super.onCreate();

	}

	private void LoginAction() {

		new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				try {

					XmppConnManager.getInstance().closeConnection();
					if (connectionListener != null && xmppConnection != null) {
						xmppConnection.removeConnectionListener(connectionListener);
					}
					if (receiverPacketListener != null && xmppConnection != null) {
						xmppConnection.removePacketListener(receiverPacketListener);
					}
					if (sendPacketListener != null && xmppConnection != null) {
						xmppConnection.removePacketSendingListener(sendPacketListener);
					}
					if (mGroupReceiverPacketListener != null && xmppConnection != null) {
						xmppConnection.removePacketListener(mGroupReceiverPacketListener);
					}
					xmppConnection = XmppConnManager.getInstance().getConnection();
					xmppConnection.login(username, password);
					Presence presence = new Presence(Presence.Type.available);
					xmppConnection.sendPacket(presence);
					xmppConnection.addConnectionListener(connectionListener);
					PacketTypeFilter receiverfilter = new PacketTypeFilter(Message.class);
					xmppConnection.addPacketListener(receiverPacketListener, receiverfilter);
					List<String> listgroup = dao.findmygroup(username);
					if (listgroup != null && listgroup.size() > 0) {
						for (int i = 0; i < listgroup.size(); i++) {
							XmppConnManager.getInstance().joinChatRoom(listgroup.get(i), username);
						}
					}
					Log.e("room", "�������������Ϣ");
					PacketFilter groupPacketFilter = new MessageTypeFilter(Message.Type.groupchat);
					xmppConnection.addPacketListener(mGroupReceiverPacketListener, groupPacketFilter);
					PacketTypeFilter sendfilter = new PacketTypeFilter(Message.class);
					xmppConnection.addPacketSendingListener(sendPacketListener, sendfilter);
					sendBroadcast(new Intent(ReceiverConstant.RECONN_RECEIVER_SUCCESS_ACTION));
				} catch (Exception e) {
					Log.e("test", "��¼"+e.toString());

					sendBroadcast(new Intent(ReceiverConstant.RECONN_RECEIVER_START_ACTION));
				}

			}
		}).start();

	}



	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub
		dao = new BBLDao(CoreIMService.this, null, null, 1);
		UserInfoEntiy user = dao.queryUserByNewTime();
		username = user.getUsername();
		password = user.getPassword();
		notificationManager = (NotificationManager) this.getSystemService(android.content.Context.NOTIFICATION_SERVICE);
		remind = dao.queryRemind(username);
		try {
			remind_voice = Integer.parseInt(remind.getVoice());
		} catch (NumberFormatException e) {
			remind_voice = 1;
		}
		try {
			remind_zhendong = Integer.parseInt(remind.getZhendong());
		} catch (NumberFormatException e) {
			remind_zhendong = 1;
		}
		intentFilter = new IntentFilter();
		sendMessageReceiver = new SendMessageReceiver();
		intentFilter.addAction(ReceiverConstant.MESSAGE_SEND_ACTION);
		intentFilter.addAction(ReceiverConstant.MESSAGE_VISIT_ACTION);
		intentFilter.addAction(ReceiverConstant.MESSAGE_ANLIAN_ACTION);
		intentFilter.addAction(ReceiverConstant.MESSAGE_ZAN_ACTION);
		intentFilter.addAction(ReceiverConstant.MESSAGE_COMMIT_ACTION);
		intentFilter.addAction(ReceiverConstant.MESSAGE_ZHAOHU_ACTION);

		intentFilter.addAction(ReceiverConstant.RemindReceiver_ACTION);
		intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
		intentFilter.addAction(ReceiverConstant.MESSAGE_GROUP_SEND);
		registerReceiver(sendMessageReceiver, intentFilter);
		LoginAction();
		if (connMonitorTimer != null) {
			connMonitorTimer.schedule(connMonitorTask, 30 * 1000, 30 * 1000);
		}
		flags = START_STICKY;
//		Notification notification = new Notification(R.drawable.logosmall, getString(R.string.app_name),
//				System.currentTimeMillis());
//		PendingIntent pendingintent = PendingIntent.getActivity(this, PublicConstant.STARTCOMMANDSERVICCEPEND,
//				new Intent(this, MainActivity.class), PublicConstant.STARTCOMMANDSERVICCEPEND);
//		notification.setLatestEventInfo(this, "", "������ʱ��Ϊ�������µ���Ϣ", pendingintent);
//		startForeground(0x111, notification);

		return super.onStartCommand(intent, flags, startId);
	}

	private Timer connMonitorTimer = new Timer();

	private TimerTask connMonitorTask = new TimerTask() {

		@Override
		public void run() {
			// TODO Auto-generated method stub
			handler.sendEmptyMessage(1);
		}
	};

	private ConnectionListener connectionListener = new ConnectionListener() {

		@Override
		public void connectionClosed() {

		}

		@Override
		public void connectionClosedOnError(Exception arg0) {
			// ����������粻�������߱��������߼������¼�
			if (arg0.getMessage().contains("conflict")) { // ��������
				XmppConnManager.getInstance().closeConnection();
				// �����������ͨ������һ���㲥����ʾ�û��������ߣ������ܼ򵥣��������µ�¼
			} else if (arg0.getMessage().contains("Connection timed out")) {// ���ӳ�ʱ
				// �����κβ�������ʵ���Զ�����
			}
		}

		@Override
		public void reconnectingIn(int arg0) {
			// �������ӵĶ������ڽ��еĶ���������Ĳ���arg0��һ������ʱ�����֣��������ʧ�ܵĴ������࣬���ֻ�Խ��Խ�󣬿�ʼ��ʱ����9
			// System.out.println("��ʼ����");
			sendBroadcast(new Intent(ReceiverConstant.RECONN_RECEIVER_START_ACTION));
		}

		@Override
		public void reconnectionFailed(Exception arg0) {
			// ��������ʧ��

		}

		@Override
		public void reconnectionSuccessful() {
			// ����������ˣ����������Ϸ������������¼�
			// System.out.println("�����ɹ�");
			Presence presence = new Presence(Presence.Type.available);
			if (xmppConnection != null) {
				xmppConnection.sendPacket(presence);
			}
			sendBroadcast(new Intent(ReceiverConstant.RECONN_RECEIVER_SUCCESS_ACTION));
		}

	};

	/**
	 * ��Ϣ����
	 */

	private PacketListener receiverPacketListener = new PacketListener() {
		@Override
		public void processPacket(Packet packet) {
			try {
				Log.e("test","���յ���Ϣ");
				if (packet instanceof Message) {
					Message msg = (Message) packet;
					String msgtype = (String) msg.getProperty("msgtype");
					String fromJID = HelperUtil.getJabberID(msg.getFrom());
					if (HelperUtil.flagISNoNull(msgtype) && msgtype.equals("0")) {
						String chatMessage = msg.getBody();
						String fromUser = fromJID.substring(0, fromJID.lastIndexOf("@"));
						if (!fromUser.equals(username)) {
							String msgtime = (String) msg.getProperty("msgtime");
							dao.insertIm(fromUser, username, chatMessage, msgtime, "1",
									String.valueOf(msg.getPacketID()), MessageConstantUtil.SEE_STATE_NO);
							String nickname = (String) msg.getProperty("msgnickname");
//							messageRemindNotification(nickname + "�����������µ���Ϣ", fromUser, nickname);
							remindNotification("��Ϣ����", nickname+"�����������µ���Ϣ", fromUser, nickname, PublicConstant.MessagePend);
							Intent intent = new Intent(ReceiverConstant.MESSAGEREFEASHLIST_ACTION);
							intent.putExtra("fromUser", fromUser);
							sendBroadcast(intent);
							sendBroadcast(new Intent(ReceiverConstant.TAB_TWO_REMAIND_ACTION));
						}
					}
					if (HelperUtil.flagISNoNull(msgtype) && msgtype.equals("1")) {
						String fromUser = fromJID.substring(0, fromJID.lastIndexOf("@"));
						String nickname = (String) msg.getProperty("msgnickname");
						if (!fromUser.equals(username)) {
//							visitRemindNotification(nickname + "�ոշ�������...", fromUser, nickname);
							remindNotification("�ÿ�����", nickname+"�ոշ�������...", fromUser, nickname, PublicConstant.VisitPend);
							Intent intent = new Intent(ReceiverConstant.TAB_FOUR_REMAIND_ACTION);
							intent.putExtra("type", PublicConstant.VISITTYPE);
							sendBroadcast(intent);

						}
					}

					if (HelperUtil.flagISNoNull(msgtype) && msgtype.equals("2")) {
						String fromUser = fromJID.substring(0, fromJID.lastIndexOf("@"));
						String nickname = (String) msg.getProperty("msgnickname");
						if (!fromUser.equals(username)) {
//							visitRemindNotification(nickname + "�ոշ�������...", fromUser, nickname);

							remindNotification("������", nickname+"�ո�����������Ķ�̬...", fromUser, nickname, PublicConstant.ZANPend);
							Intent intent = new Intent(ReceiverConstant.TAB_FOUR_REMAIND_ACTION);
							intent.putExtra("type", PublicConstant.MYQAQTYPE);
							sendBroadcast(intent);

						}
					}
					if (HelperUtil.flagISNoNull(msgtype) && msgtype.equals("3")) {
						String fromUser = fromJID.substring(0, fromJID.lastIndexOf("@"));
						String nickname = (String) msg.getProperty("msgnickname");
						if (!fromUser.equals(username)) {
//							visitRemindNotification(nickname + "�ոշ�������...", fromUser, nickname);
							remindNotification("��������", nickname+"�ո�������������Ķ�̬...", fromUser, nickname, PublicConstant.COMMITPend);
							Intent intent = new Intent(ReceiverConstant.TAB_FOUR_REMAIND_ACTION);
							intent.putExtra("type", PublicConstant.MYQAQTYPE);
							sendBroadcast(intent);

						}
					}



					if (HelperUtil.flagISNoNull(msgtype) && msgtype.equals("8")) {
						String fromUser = fromJID.substring(0, fromJID.lastIndexOf("@"));
						String nickname = (String) msg.getProperty("msgnickname");
						if (!fromUser.equals(username)) {
//							anlianRemindNotification(nickname + "�ոհ�������...", fromUser, nickname);
							remindNotification("��������", nickname+"�ոհ�������...", fromUser, nickname, PublicConstant.ANLIANPend);
							Intent intent = new Intent(ReceiverConstant.TAB_THREE_REMAIND_ACTION);
							intent.putExtra("type", PublicConstant.ANLIANTYPE);
							sendBroadcast(intent);
						}
					}
				}
			} catch (Exception e) {
				// SMACK silently discards exceptions dropped from
				// processPacket :(
				// L.e("failed to process packet:");
				e.printStackTrace();
			}
		}
	};
	private PacketListener sendPacketListener = new PacketListener() {

		@Override
		public void processPacket(Packet packet) {
			// TODO Auto-generated method stub
			try {
				if (packet instanceof Message) {
					Message msg = (Message) packet;
					// String chatMessage = msg.getBody();
					Log.e("test", "send====");

					String msgtype = (String) msg.getProperty("msgtype");
					if (HelperUtil.flagISNoNull(msgtype) && msgtype.equals("0")) {
						dao.updateIm(msg.getPacketID(), MessageConstantUtil.OUT_SEND_SUCCESS);
					}

				}
			} catch (Exception e) {
				// TODO: handle exception
			}
		}
	};

	// /** ������Ϣ������ */
	// private static class ChatPacketListener implements PacketListener {
	// @Override
	// public void processPacket(Packet packet) {
	// // TODO Auto-generated method stub
	// Message message = (Message) packet;
	// if (message.getBody() != null) {
	// Log.e("room", "����������Ϣ:" + message.getFrom() + "***" + message.getBody() +
	// "***" + message.getTo()
	// + "***" + message.getType() + "***" + message.toXML());
	// }
	//
	// }
	// }

	private PacketListener mGroupReceiverPacketListener = new PacketListener() {
		@Override
		public void processPacket(Packet packet) {
			try {
				if (packet instanceof Message) {
					Message msg = (Message) packet;
					Log.e("test", "group mGroupPacketListener");
					String msggroup = (String) msg.getProperty("msggroup");
					String fromuser = (String) msg.getProperty("msgsenduser");
					if (!fromuser.equals(username)) {
						String msgtime = (String) msg.getProperty("msgtime");
						dao.insertImGroup(fromuser, msggroup, msg.getBody(), msgtime, "1",
								String.valueOf(msg.getPacketID()), MessageConstantUtil.SEE_STATE_NO);
						Intent intent = new Intent(ReceiverConstant.MESSAGE_GROUP_RECEIVER);
						intent.putExtra("grouptype", msggroup);
						sendBroadcast(intent);
						intent = new Intent(ReceiverConstant.TAB_THREE_REMAIND_ACTION);
						intent.putExtra("type", PublicConstant.GROUPTYPE);
						sendBroadcast(intent);
						remindNotification("Ⱥ����", "�����µ�Ⱥ��Ϣ", "", "", PublicConstant.GroupMessagePend);
						// sendBroadcast(new
						// Intent(ReceiverConstant.TAB_TWO_REMAIND_ACTION));
					}

				}
			} catch (Exception e) {
				// TODO: handle exception
			}
		}
	};

	private class SendMessageReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			if (intent.getAction().equals(ReceiverConstant.MESSAGE_SEND_ACTION)) {
				String toID = intent.getStringExtra("toID");
				String nickname = intent.getStringExtra("nickname");
				String message = intent.getStringExtra("message");
				// System.out.println("toid====" + toID);
				// System.out.println("message====" + message);
				sendMessage(toID, message, nickname);
			}
			if (intent.getAction().equals(ReceiverConstant.MESSAGE_ZHAOHU_ACTION)) {
				String toID = intent.getStringExtra("toID");
				String nickname = intent.getStringExtra("nickname");
				// System.out.println("toid====" + toID);
				// System.out.println("message====" + message);
				sendMessage(toID, "�����к���:����,�п����ǿ�����һ����?", nickname);
			}
			if (intent.getAction().equals(ReceiverConstant.MESSAGE_GROUP_SEND)) {
				String toID = intent.getStringExtra("toID");
				String message = intent.getStringExtra("message");
				Log.e("test", "group MESSAGE_GROUP_SEND");

				// System.out.println("toid====" + toID);
				// System.out.println("message====" + message);
				sendMessageGroup(toID, message);
			}
			if (intent.getAction().equals(ReceiverConstant.RemindReceiver_ACTION)) {
				remind = dao.queryRemind(username);
				try {
					remind_voice = Integer.parseInt(remind.getVoice());
				} catch (NumberFormatException e) {
					remind_voice = 1;
				}
				try {
					remind_zhendong = Integer.parseInt(remind.getZhendong());
				} catch (NumberFormatException e) {
					remind_zhendong = 1;
				}
			}

			if (intent.getAction().equals(ReceiverConstant.MESSAGE_VISIT_ACTION)) {
				String toID = intent.getStringExtra("toID");
				String nickname = intent.getStringExtra("nickname");
				sendUserAction(toID, nickname, "1", "�ÿ�");
			}

			if (intent.getAction().equals(ReceiverConstant.MESSAGE_ZAN_ACTION)) {

				String toID = intent.getStringExtra("toID");
				String nickname = intent.getStringExtra("nickname");
				sendUserAction(toID, nickname, "2", "��");
			}
			if (intent.getAction().equals(ReceiverConstant.MESSAGE_COMMIT_ACTION)) {
				String toID = intent.getStringExtra("toID");
				String nickname = intent.getStringExtra("nickname");
				sendUserAction(toID, nickname, "3", "����");
			}
			if (intent.getAction().equals(ReceiverConstant.MESSAGE_ANLIAN_ACTION)) {
				String toID = intent.getStringExtra("toID");
				String nickname = intent.getStringExtra("nickname");
				sendUserAction(toID, nickname, "8", "�ÿ�");
			}
			ConnectivityManager manager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo gprs = manager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
			NetworkInfo wifi = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
			if (!gprs.isConnected() && !wifi.isConnected()) {
				// netRemindNotification("��ǰ����Ͽ�,������������");
				remindNotification("��������", "��ǰ����Ͽ�,������������", "", "", PublicConstant.NetPend);
			} else {
				HelperUtil.cancelNetNotificationManager(CoreIMService.this);
			}

		}
	}

	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
				case 1:
					try {
						if (!isAuthenticated()) {
							sendBroadcast(new Intent(ReceiverConstant.RECONN_RECEIVER_START_ACTION));
							System.out.println("�����߿�ʼ��¼");
							LoginAction();

						} else {
							System.out.println("����=========");

						}
					} catch (Exception e) {
						// TODO: handle exception
						sendBroadcast(new Intent(ReceiverConstant.RECONN_RECEIVER_START_ACTION));
						LoginAction();
					}
					break;
				default:
					break;
			}

		}

	};

	public boolean isAuthenticated() {
		if (xmppConnection != null) {
			return (xmppConnection.isConnected() && xmppConnection.isAuthenticated());
		}
		return false;
	}

	private SendSaveThread saveThread;

	private void sendMessage(String toID, String message, String nickname) {
		try {
			Message newMessage = new Message(toID + PublicConstant.XMPPDomain, Message.Type.chat);
			String msgtime = HelperUtil.messageTime();
			try {
				newMessage.setProperty("msgtime", msgtime);
				newMessage.setProperty("msgtype", "0");
				if (!HelperUtil.flagISNoNull(nickname)) {
					nickname = toID;
				}
				newMessage.setProperty("msgnickname", nickname);
				newMessage.setBody(message);
				xmppConnection.sendPacket(newMessage);
				if (isAuthenticated()) {
					dao.insertIm(username, toID, message, msgtime, MessageConstantUtil.OUT_SEND_ING,
							newMessage.getPacketID(), MessageConstantUtil.SEE_STATE_YES);
				} else {
					dao.insertIm(username, toID, message, msgtime, MessageConstantUtil.OUT_SEND_FAILE,
							newMessage.getPacketID(), MessageConstantUtil.SEE_STATE_YES);
					sendBroadcast(new Intent(ReceiverConstant.RECONN_RECEIVER_START_ACTION));
				}
				sendBroadcast(new Intent(ReceiverConstant.MESSAGE_SEND_REFREASH_ACTION));
				saveThread = new SendSaveThread(username, toID, message, newMessage.getPacketID());
				saveThread.start();
				sendBroadcast(new Intent(ReceiverConstant.RECONN_RECEIVER_SUCCESS_ACTION));

			} catch (Exception e) {
				// TODO: handle exception
				sendBroadcast(new Intent(ReceiverConstant.RECONN_RECEIVER_START_ACTION));
				dao.insertIm(username, toID, message, msgtime, MessageConstantUtil.OUT_SEND_FAILE,
						newMessage.getPacketID(), MessageConstantUtil.SEE_STATE_YES);
				sendBroadcast(new Intent(ReceiverConstant.MESSAGE_SEND_REFREASH_ACTION));
				saveThread = new SendSaveThread(username, toID, message, newMessage.getPacketID());
				saveThread.start();
			}
		} catch (Exception e) {
			// TODO: handle exception
			sendBroadcast(new Intent(ReceiverConstant.RECONN_RECEIVER_START_ACTION));
		}
	}

	private void sendMessageGroup(String toID, String message) {
		try {
			Message newMessage = new Message(toID + PublicConstant.GroupXMPPDomain, Message.Type.groupchat);
			String msgtime = HelperUtil.messageTime();
			try {
				newMessage.setProperty("msgtime", msgtime);
				newMessage.setProperty("msgtype", "10");
				newMessage.setProperty("msgsenduser", username);
				newMessage.setProperty("msggroup", toID);
				newMessage.setBody(message);
				multiUserChat = XmppConnManager.getInstance().joinChatRoom(toID, username);
				if (isAuthenticated() && multiUserChat.isJoined()) {
					multiUserChat.sendMessage(newMessage);
					dao.insertImGroup(username, toID, message, msgtime, MessageConstantUtil.OUT_SEND_ING,
							newMessage.getPacketID(), MessageConstantUtil.SEE_STATE_YES);

				} else {
					dao.insertImGroup(username, toID, message, msgtime, MessageConstantUtil.OUT_SEND_FAILE,
							newMessage.getPacketID(), MessageConstantUtil.SEE_STATE_YES);
					sendBroadcast(new Intent(ReceiverConstant.RECONN_RECEIVER_START_ACTION));
				}
				sendBroadcast(new Intent(ReceiverConstant.MESSAGE_GROUP_LIST_REFRESH));
				sendBroadcast(new Intent(ReceiverConstant.RECONN_RECEIVER_SUCCESS_ACTION));

			} catch (Exception e) {
				// TODO: handle exception
				sendBroadcast(new Intent(ReceiverConstant.RECONN_RECEIVER_START_ACTION));
				dao.insertImGroup(username, toID, message, msgtime, MessageConstantUtil.OUT_SEND_FAILE,
						newMessage.getPacketID(), MessageConstantUtil.SEE_STATE_YES);
				sendBroadcast(new Intent(ReceiverConstant.MESSAGE_GROUP_LIST_REFRESH));
			}
		} catch (Exception e) {
			// TODO: handle exception
			sendBroadcast(new Intent(ReceiverConstant.RECONN_RECEIVER_START_ACTION));
		}
	}

	public void sendUserAction(String toID, String nickname, String type, String body) {
		Message newMessage = new Message(toID + PublicConstant.XMPPDomain, Message.Type.chat);
		newMessage.setProperty("msgtype", type);
		if (!HelperUtil.flagISNoNull(nickname)) {
			nickname = toID;
		}
		newMessage.setProperty("msgnickname", nickname);
		newMessage.setBody(body);
		if (isAuthenticated()) {
			xmppConnection.sendPacket(newMessage);
		}
	}

	private class SendSaveThread extends Thread {
		String touser;
		String inuser;
		String content;
		String imid;

		public SendSaveThread(String touser, String inuser, String content, String imid) {
			this.touser = touser;
			this.inuser = inuser;
			this.content = content;
			this.imid = imid;
		}

		@Override
		public void run() {
			try {
				Map<String, String> map = new HashMap<String, String>();
				map.put("touser", touser);
				map.put("inuser", inuser);
				map.put("content", content);
				map.put("imid", imid);
				HelperUtil.postRequest(HttpConstantUtil.UpdateImSave, map);
			} catch (Exception e) {
			}
		}
	}


	private void remindNotification(String title, String msg, String touser, String nickname, int action) {
		try {
			if (remind_voice == 1) {
				MediaPlayer.create(CoreIMService.this, R.raw.office).start();
			}
			Notification notification = new Notification(R.drawable.logosmall, msg, System.currentTimeMillis());
			notification.flags |= Notification.FLAG_ONGOING_EVENT;
			notification.flags |= Notification.FLAG_AUTO_CANCEL;
			notification.flags |= Notification.FLAG_SHOW_LIGHTS;
			notification.defaults = Notification.DEFAULT_LIGHTS;
			notification.ledARGB = Color.BLUE;
			notification.ledOnMS = 5000;// LED��
			if (remind_zhendong == 1) {
				notification.defaults |= Notification.DEFAULT_VIBRATE;
				long[] vibrate = { 0, 100, 200, 300 }; // 0�����ʼ�񶯣���100�����ֹͣ���ٹ�200������ٴ���300����
				notification.vibrate = vibrate;
			}
			Intent notificationIntent = null;
			if (action == PublicConstant.MessagePend || action == PublicConstant.ANLIANPend
					|| action == PublicConstant.COMMITPend || action == PublicConstant.ZANPend) {
				notificationIntent = new Intent(CoreIMService.this, MainActivity.class);
			}
			if (action == PublicConstant.VisitPend) {
				notificationIntent = new Intent(CoreIMService.this, PersionDetailActivity.class);
			}
			if (action == PublicConstant.NetPend) {
				if (android.os.Build.VERSION.SDK_INT > 10) {
					notificationIntent = new Intent(android.provider.Settings.ACTION_SETTINGS);
				} else {
					notificationIntent = new Intent(android.provider.Settings.ACTION_WIRELESS_SETTINGS);
				}
			}
			notificationIntent.putExtra("toUser", touser);
			notificationIntent.putExtra("toName", nickname);
			PendingIntent contentItent = PendingIntent.getActivity(CoreIMService.this, action, notificationIntent,
					action);
			//notification.setLatestEventInfo(CoreIMService.this, title, msg, contentItent);
			notificationManager.notify(action, notification);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	@Override
	public void onDestroy() {
		super.onDestroy();

		XmppConnManager.getInstance().closeConnection();
		if (connMonitorTimer != null) {
			connMonitorTimer.cancel();
		}
		if (connMonitorTask != null) {
			connMonitorTask.cancel();
		}
		if (connectionListener != null) {
			xmppConnection.removeConnectionListener(connectionListener);
		}
		if (receiverPacketListener != null) {
			xmppConnection.removePacketListener(receiverPacketListener);
		}
		if (sendPacketListener != null) {
			xmppConnection.removePacketSendingListener(sendPacketListener);
		}
		if (sendMessageReceiver != null) {
			this.unregisterReceiver(sendMessageReceiver);
		}
		if (dao.queryUserByNewTime().getState().equals("0")) {
		} else {
			Intent localIntent = new Intent();
			localIntent.setClass(this, CoreIMService.class); // ����ʱ��������Service
			this.startService(localIntent);
		}
		stopForeground(true);

	}

}
