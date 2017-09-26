package com.beibeilian.privateletter.adapter;

import java.io.File;
import java.util.List;

import com.beibeilian.android.app.R;
import com.beibeilian.circle.widgets.CircularImage;
import com.beibeilian.constant.BBLConstant;
import com.beibeilian.db.BBLDao;
import com.beibeilian.me.MePhotoItemDeatailActivity;
import com.beibeilian.model.PayRule;
import com.beibeilian.predestined.PersionDetailActivity;
import com.beibeilian.privateletter.ChatPhotoTask;
import com.beibeilian.privateletter.model.HomeMessage;
import com.beibeilian.receiver.ReceiverConstant;
import com.beibeilian.util.DateFormatUtil;
import com.beibeilian.util.HelperUtil;
import com.beibeilian.util.PublicConstant;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.text.ClipboardManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.TextView;

public class PrivateletterChatAdapter extends BaseAdapter {

	private List<HomeMessage> listMessage;

	private Context context;

	private String minephoto;

	private String youphoto;

	private String youuser;

	private String youname;

	private String mysex;

	private String yousex;

	/** �����ĸ���ѡ��� */
	private PopupWindow popupWindow;

	/** ���ƣ�ɾ�� */
	private TextView copy, delete;

	private LayoutInflater inflater;
	/**
	 * ִ�ж�����ʱ��
	 */
	protected long mAnimationTime = 150;

	private BBLDao dao;

	private String nickname;

	private int vipState = 0;

	private String relation = "1";

	public PrivateletterChatAdapter(Context context, List<HomeMessage> listMessage, String minephoto, String youphoto,
			String youuser, String youname, String mysex, String yousex, BBLDao dao, String nickname) {
		this.context = context;
		this.listMessage = listMessage;

		this.youuser = youuser;
		this.youname = youname;
		this.mysex = mysex;
		this.yousex = yousex;
		this.dao = dao;
		inflater = LayoutInflater.from(context);
		initPopWindow();
		this.nickname = nickname;
		String username = dao.queryUserByNewTime().getUsername();
		this.vipState = dao.findVipMember(username);
		PayRule mPayRule = dao.findPayRule();
		if (mPayRule != null) {
			relation = mPayRule.getRelation();
		} else {
			relation = "1";
		}

		if (HelperUtil.flagISNoNull(youphoto)) {
			if (youphoto.contains("http")) {
				youphoto = youphoto.substring(youphoto.lastIndexOf("/") + 1);
			}
			youphoto = BBLConstant.PHOTO_BEFORE_URL + youphoto;
		}
		if (HelperUtil.flagISNoNull(minephoto)) {
			if (minephoto.contains("http")) {
				minephoto = minephoto.substring(minephoto.lastIndexOf("/") + 1);
			}
			minephoto = BBLConstant.PHOTO_BEFORE_URL + minephoto;
		}
		this.minephoto = minephoto;
		this.youphoto = youphoto;
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

	public int getItemViewType(int position) {
		// TODO Auto-generated method stub
		HomeMessage homeMessage = (HomeMessage) getItem(position);

		if (homeMessage.getFrom().equals("OUT")) {
			return 0;
		} else {
			return 1;
		}

	}

	public int getViewTypeCount() {
		// TODO Auto-generated method stub
		return 2;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		final HomeMessage homeMessage = (HomeMessage) getItem(position);
		ViewHolder viewHolder;
		if (convertView == null) {
			viewHolder = new ViewHolder();
			if (homeMessage.getFrom().equals("OUT")) {
				convertView = LayoutInflater.from(context).inflate(R.layout.row_sent_message, null);
			} else {
				convertView = LayoutInflater.from(context).inflate(R.layout.row_received_message, null);
			}
			viewHolder.tv_time = (TextView) convertView.findViewById(R.id.timestamp);
			viewHolder.img_head = (CircularImage) convertView.findViewById(R.id.iv_userhead);
			viewHolder.tv_content = (TextView) convertView.findViewById(R.id.tv_chatcontent);
			viewHolder.img_state = (ImageView) convertView.findViewById(R.id.msg_status);

			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		if (HelperUtil.flagISNoNull(homeMessage.getTime()))
			viewHolder.tv_time.setText(DateFormatUtil.getDDTime(homeMessage.getTime()));

		// new LoadChatMessageTask(context, viewHolder.tv_content,
		// homeMessage.getContent()).execute(null);
		if (homeMessage.getFrom().equals("IN")) {
			if (homeMessage.getContent().contains(".jpg")) {
				viewHolder.tv_content.setText("");
				viewHolder.tv_content.setBackgroundResource(R.drawable.chatfrom_bg);
				File mInFile=new File(PublicConstant.FilePath+homeMessage.getContent());
				if(mInFile.exists()&&mInFile.length()>10)
				{
					Bitmap bm = BitmapFactory.decodeFile(PublicConstant.FilePath+homeMessage.getContent());
					Drawable rightDrawable = new BitmapDrawable(bm); 
		            rightDrawable.setBounds(0, 0, rightDrawable.getMinimumWidth(), rightDrawable.getMinimumHeight());  
		            viewHolder.tv_content.setCompoundDrawables(null, null, rightDrawable, null); 
				}
				else
				{
					Drawable rightDrawable = context.getResources().getDrawable(R.drawable.recv_image);  
		            rightDrawable.setBounds(0, 0, rightDrawable.getMinimumWidth(), rightDrawable.getMinimumHeight());  
		            viewHolder.tv_content.setCompoundDrawables(null, null, rightDrawable, null); 
					new ChatPhotoTask( viewHolder.tv_content, homeMessage.getContent(), context);

				}
				viewHolder.tv_content.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						Intent mIntent = new Intent(context, MePhotoItemDeatailActivity.class);
						mIntent.putExtra("photourl", homeMessage.getContent());
						context.startActivity(mIntent);
					}
				});
			} else {
	            viewHolder.tv_content.setCompoundDrawables(null, null, null, null); 
				viewHolder.tv_content
				.setText(HelperUtil.convertNormalStringToSpannableString(context, homeMessage.getContent(), true));
				viewHolder.tv_content.setBackgroundResource(R.drawable.chatfrom_bg);
				viewHolder.tv_content.setOnLongClickListener(new popAction(convertView, position, homeMessage.getFrom()));
				
			}
		}
		else
		{
			if (homeMessage.getContent().contains(".jpg")) {
				viewHolder.tv_content.setText("");
				viewHolder.tv_content.setBackgroundResource(R.drawable.chatto_bg);
				File mInFile=new File(PublicConstant.FilePath+homeMessage.getContent());
				if(mInFile.exists()&&mInFile.length()>10)
				{
					Bitmap bm = BitmapFactory.decodeFile(PublicConstant.FilePath+homeMessage.getContent());
					Drawable rightDrawable = new BitmapDrawable(bm); 
		            rightDrawable.setBounds(0, 0, rightDrawable.getMinimumWidth(), rightDrawable.getMinimumHeight());  
		            viewHolder.tv_content.setCompoundDrawables(null, null, rightDrawable, null); 
				}
				else
				{
					Drawable rightDrawable = context.getResources().getDrawable(R.drawable.recv_image);  
		            rightDrawable.setBounds(0, 0, rightDrawable.getMinimumWidth(), rightDrawable.getMinimumHeight());  
		            viewHolder.tv_content.setCompoundDrawables(null, null, rightDrawable, null); 
					new ChatPhotoTask( viewHolder.tv_content, homeMessage.getContent(), context);

				}
				viewHolder.tv_content.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						Intent mIntent = new Intent(context, MePhotoItemDeatailActivity.class);
						mIntent.putExtra("photourl", homeMessage.getContent());
						context.startActivity(mIntent);
					}
				});
			} else {
	            viewHolder.tv_content.setCompoundDrawables(null, null, null, null); 
				viewHolder.tv_content
				.setText(HelperUtil.convertNormalStringToSpannableString(context, homeMessage.getContent(), true));
				viewHolder.tv_content.setBackgroundResource(R.drawable.chatto_bg);
				viewHolder.tv_content.setOnLongClickListener(new popAction(convertView, position, homeMessage.getFrom()));
				
			}
		}
		

		if (homeMessage.getFrom().equals("OUT")) {
			if (homeMessage.sendstate.equals("0")) // 0�������ڷ�����,-1������ʧ��,1����ɹ�
			{
				if (viewHolder.img_state != null)
					viewHolder.img_state.setVisibility(View.GONE);
			}
			if (homeMessage.sendstate.equals("1")) // 0�������ڷ�����,-1������ʧ��,1����ɹ�
			{
				if (viewHolder.img_state != null)
					viewHolder.img_state.setVisibility(View.GONE);
			}
			if (homeMessage.sendstate.equals("-1")) // 0�������ڷ�����,-1������ʧ��,1����ɹ�
			{
				if (viewHolder.img_state != null)
					viewHolder.img_state.setVisibility(View.VISIBLE);
			}
			try {

				HelperUtil.getPicassoImageByUrl(context, minephoto,
						context.getResources().getDrawable(R.drawable.icon_touxiang), viewHolder.img_head);
				// if (HelperUtil.flagISNoNull(mysex) && mysex.equals("��")) {
				// // new AsynImageLoader().showImageAsyn(
				// // viewHolder.img_head, minephoto, R.drawable.nan);
				// HelperUtil.getPicassoImageByUrl(context, minephoto,
				// context.getResources().getDrawable(R.drawable.nan),
				// viewHolder.img_head);
				//
				// } else {
				// // new AsynImageLoader().showImageAsyn(
				// // viewHolder.img_head, minephoto, R.drawable.nv);
				// HelperUtil.getPicassoImageByUrl(context, minephoto,
				// context.getResources().getDrawable(R.drawable.nv),
				// viewHolder.img_head);
				//
				// }
			} catch (Exception e) {

			}

			viewHolder.img_state.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub

					AlertDialog.Builder builder = new AlertDialog.Builder(context);
					builder.setTitle("��ܰ��ʾ");
					builder.setMessage("��ȷ���ط���?").setCancelable(false)
							.setPositiveButton("ȷ��", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							Intent intent = new Intent(ReceiverConstant.MESSAGE_SEND_ACTION);
							intent.putExtra("toID", youuser);
							intent.putExtra("nickname", nickname);
							intent.putExtra("message", homeMessage.getContent());
							context.sendBroadcast(intent);
						}
					}).setNegativeButton("ȡ��", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							dialog.cancel();
						}
					}).show();

				}
			});

		} else {
			// if
			// (SensitiveWordsUtil.sensitiveWords(homeMessage.getContent())&&vipState<=0&&relation.equals("1"))
			// {
			// viewHolder.tv_content.setText("����ܰ����:������Ϣ�п��ܰ�����ϵ��ʽ,VIP��Ա���ܲ鿴.�����ͨVIP��Ա��");
			// }
			// viewHolder.tv_content.setOnClickListener(new OnClickListener() {
			//
			// @Override
			// public void onClick(View v) {
			// // TODO Auto-generated method stub
			// if
			// (SensitiveWordsUtil.sensitiveWords(homeMessage.getContent())&&vipState<=0&&relation.equals("1"))
			// {
			// context.startActivity(new
			// Intent(context,MeMemberActivity.class));
			// }
			// }
			// });

			viewHolder.img_head.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					Intent intent = new Intent(context, PersionDetailActivity.class);
					intent.putExtra("toUser", youuser);
					intent.putExtra("toName", youname);
					context.startActivity(intent);
				}
			});

			if (HelperUtil.flagISNoNull(youphoto)) {
				try {
					HelperUtil.getPicassoImageByUrl(context, youphoto,
							context.getResources().getDrawable(R.drawable.icon_touxiang), viewHolder.img_head);
							// if (HelperUtil.flagISNoNull(yousex)) {
							// if (yousex.equals("��")) {
							// // new AsynImageLoader().showImageAsyn(
							// // viewHolder.img_head, youphoto,
							// // R.drawable.nan);
							// HelperUtil.getPicassoImageByUrl(context,
							// youphoto,
							// context.getResources().getDrawable(R.drawable.nan),
							// viewHolder.img_head);
							//
							// } else {
							// // new AsynImageLoader().showImageAsyn(
							// // viewHolder.img_head, youphoto,
							// // R.drawable.nv);
							// HelperUtil.getPicassoImageByUrl(context,
							// youphoto,
							// context.getResources().getDrawable(R.drawable.nv),
							// viewHolder.img_head);
							//
							// }

					// } else {
					// if (HelperUtil.flagISNoNull(mysex) && mysex.equals("��"))
					// {
					// // new AsynImageLoader().showImageAsyn(
					// // viewHolder.img_head, youphoto,
					// // R.drawable.nv);
					// HelperUtil.getPicassoImageByUrl(context, youphoto,
					// context.getResources().getDrawable(R.drawable.nv),
					// viewHolder.img_head);
					//
					// } else {
					// // new AsynImageLoader().showImageAsyn(
					// // viewHolder.img_head, youphoto,
					// // R.drawable.nan);
					// HelperUtil.getPicassoImageByUrl(context, youphoto,
					// context.getResources().getDrawable(R.drawable.nan),
					// viewHolder.img_head);
					//
					// }
					// }
				} catch (Exception e) {
				}
			} else {
				try {
					HelperUtil.getPicassoImageByUrl(context, youphoto,
							context.getResources().getDrawable(R.drawable.icon_touxiang), viewHolder.img_head);
					// if (HelperUtil.flagISNoNull(yousex)) {
					// if (yousex.equals("��")) {
					// // new
					// // AsynImageLoader().showImageAsyn(viewHolder.img_head,
					// // youphoto, R.drawable.nan);
					// HelperUtil.getPicassoImageByUrl(context, youphoto,
					// context.getResources().getDrawable(R.drawable.nan),
					// viewHolder.img_head);
					//
					// } else {
					// // new
					// // AsynImageLoader().showImageAsyn(viewHolder.img_head,
					// // youphoto, R.drawable.nv);
					// HelperUtil.getPicassoImageByUrl(context, youphoto,
					// context.getResources().getDrawable(R.drawable.nv),
					// viewHolder.img_head);
					//
					// }
					// } else {
					// if (HelperUtil.flagISNoNull(mysex) && mysex.equals("��"))
					// {
					// // new AsynImageLoader().showImageAsyn(
					// // viewHolder.img_head, youphoto, R.drawable.nv);
					// HelperUtil.getPicassoImageByUrl(context, youphoto,
					// context.getResources().getDrawable(R.drawable.nv),
					// viewHolder.img_head);
					//
					// } else {
					// // new AsynImageLoader().showImageAsyn(
					// // viewHolder.img_head, youphoto, R.drawable.nan);
					// HelperUtil.getPicassoImageByUrl(context, youphoto,
					// context.getResources().getDrawable(R.drawable.nan),
					// viewHolder.img_head);
					//
					// }
					// }
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		return convertView;
	}

	static class ViewHolder {

		TextView tv_time;

		CircularImage img_head;

		TextView tv_content;

		ImageView img_state;

		ProgressBar pb_sending;

	}

	/**
	 * ����listitem�������¼�
	 */
	@Override
	public boolean areAllItemsEnabled() {
		return false;
	}

	@Override
	public boolean isEnabled(int position) {
		return false;
	}

	/**
	 * ��ʼ��������pop
	 */
	private void initPopWindow() {
		View popView = inflater.inflate(R.layout.chat_item_copy_delete_menu, null);
		copy = (TextView) popView.findViewById(R.id.chat_copy_menu);
		delete = (TextView) popView.findViewById(R.id.chat_delete_menu);
		popupWindow = new PopupWindow(popView, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		popupWindow.setBackgroundDrawable(new ColorDrawable(0));
		// ����popwindow���ֺ���ʧ����
		// popupWindow.setAnimationStyle(R.style.PopMenuAnimation);
	}

	/**
	 * ��ʾpopWindow
	 */
	public void showPop(View parent, int x, int y, final View view, final int position, final String fromOrTo) {
		// ����popwindow��ʾλ��
		popupWindow.showAtLocation(parent, 0, x, y);
		// ��ȡpopwindow����
		popupWindow.setFocusable(true);
		// ����popwindow�������������򣬱�رա�
		popupWindow.setOutsideTouchable(true);
		// Ϊ��ť���¼�
		// ����
		copy.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (popupWindow.isShowing()) {
					popupWindow.dismiss();
				}
				// ��ȡ������������
				ClipboardManager cm = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
				// ���ı����ݸ��Ƶ�������
				cm.setText(listMessage.get(position).getContent());
			}
		});
		// ɾ��
		delete.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (popupWindow.isShowing()) {
					popupWindow.dismiss();
				}
				// if (fromOrTo.equals("IN")) {
				// // from
				// leftRemoveAnimation(view, position);
				// } else if (fromOrTo.equals("OUT")) {
				// // to
				// rightRemoveAnimation(view, position);
				// }
				dao.delIMOne(listMessage.get(position).getImid());
				listMessage.remove(position);
				notifyDataSetChanged();

			}
		});
		popupWindow.update();
		if (popupWindow.isShowing()) {

		}
	}

	/**
	 * ÿ��ITEM��more��ť��Ӧ�ĵ������
	 */
	public class popAction implements OnLongClickListener {
		int position;
		View view;
		String fromOrTo;

		public popAction(View view, int position, String fromOrTo) {
			this.position = position;
			this.view = view;
			this.fromOrTo = fromOrTo;
		}

		@Override
		public boolean onLongClick(View v) {
			// TODO Auto-generated method stub
			int[] arrayOfInt = new int[2];
			// ��ȡ�����ť������
			v.getLocationOnScreen(arrayOfInt);
			int x = arrayOfInt[0];
			int y = arrayOfInt[1];
			// System.out.println("x: " + x + " y:" + y + " w: " +
			// v.getMeasuredWidth() + " h: " + v.getMeasuredHeight() );
			showPop(v, x, y, view, position, fromOrTo);
			return true;
		}
	}

	// /**
	// * itemɾ������
	// * */
	// private void rightRemoveAnimation(final View view, final int position) {
	// final Animation animation = (Animation) AnimationUtils.loadAnimation(
	// context, R.anim.chatto_remove_anim);
	// animation.setAnimationListener(new AnimationListener() {
	// public void onAnimationStart(Animation animation) {
	// }
	//
	// public void onAnimationRepeat(Animation animation) {
	// }
	//
	// public void onAnimationEnd(Animation animation) {
	// // view.setAlpha(0);
	// performDismiss(view, position);
	// animation.cancel();
	// }
	// });
	//
	// view.startAnimation(animation);
	// }

	// /**
	// * itemɾ������
	// * */
	// private void leftRemoveAnimation(final View view, final int position) {
	// final Animation animation = (Animation)
	// AnimationUtils.loadAnimation(context, R.anim.chatfrom_remove_anim);
	// animation.setAnimationListener(new AnimationListener() {
	// public void onAnimationStart(Animation animation) {
	// }
	//
	// public void onAnimationRepeat(Animation animation) {
	// }
	//
	// public void onAnimationEnd(Animation animation) {
	// // view.setAlpha(0);
	// performDismiss(view, position);
	// animation.cancel();
	// }
	// });
	//
	// view.startAnimation(animation);
	// }

	// /**
	// * �ڴ˷�����ִ��itemɾ��֮��������item���ϻ������¹����Ķ��������ҽ�position�ص�������onDismiss()��
	// *
	// * @param dismissView
	// * @param dismissPosition
	// */
	// private void performDismiss(final View dismissView,
	// final int dismissPosition) {
	// final ViewGroup.LayoutParams lp = dismissView.getLayoutParams();//
	// ��ȡitem�Ĳ��ֲ���
	// final int originalHeight = dismissView.getHeight();// item�ĸ߶�
	//
	// ValueAnimator animator = ValueAnimator.ofInt(originalHeight, 0)
	// .setDuration(mAnimationTime);
	// animator.start();
	//
	// animator.addListener(new AnimatorListenerAdapter() {
	// @Override
	// public void onAnimationEnd(Animator animation) {
	// listMessage.remove(dismissPosition);
	// notifyDataSetChanged();
	// // ��δ������Ҫ����Ϊ���ǲ�û�н�item��ListView���Ƴ������ǽ�item�ĸ߶�����Ϊ0
	// // ���������ڶ���ִ�����֮��item���û���
	// ViewHelper.setAlpha(dismissView, 1f);
	// ViewHelper.setTranslationX(dismissView, 0);
	// ViewGroup.LayoutParams lp = dismissView.getLayoutParams();
	// lp.height = originalHeight;
	// dismissView.setLayoutParams(lp);
	// }
	// });
	//
	// animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
	// @Override
	// public void onAnimationUpdate(ValueAnimator valueAnimator) {
	// // ��δ����Ч����ListViewɾ��ĳitem֮��������item���ϻ�����Ч��
	// lp.height = (Integer) valueAnimator.getAnimatedValue();
	// dismissView.setLayoutParams(lp);
	// }
	// });
	//
	// }
	//

}
