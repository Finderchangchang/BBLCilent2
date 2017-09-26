package com.beibeilian.circle.widgets;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.beibeilian.circle.bean.ActionItem;
import com.beibeilian.circle.utils.DensityUtil;
import com.beibeilian.android.app.R;


public class SnsPopupWindow extends PopupWindow implements OnClickListener{

	private TextView digBtn;
	private TextView commentBtn;

	// ʵ����һ������
	private Rect mRect = new Rect();
	// �����λ�ã�x��y��
	private final int[] mLocation = new int[2];
	// ����������ѡ��ʱ�ļ���
	private OnItemClickListener mItemClickListener;
	// ���嵯���������б�
	private ArrayList<ActionItem> mActionItems = new ArrayList<ActionItem>();

	public void setmItemClickListener(OnItemClickListener mItemClickListener) {
		this.mItemClickListener = mItemClickListener;
	}
	public ArrayList<ActionItem> getmActionItems() {
		return mActionItems;
	}
	public void setmActionItems(ArrayList<ActionItem> mActionItems) {
		this.mActionItems = mActionItems;
	}


	public SnsPopupWindow(Context context) {
		View view = LayoutInflater.from(context).inflate(R.layout.social_sns_popupwindow, null);
		digBtn = (TextView) view.findViewById(R.id.digBtn);
		commentBtn = (TextView) view.findViewById(R.id.commentBtn);
		digBtn.setOnClickListener(this);
		commentBtn.setOnClickListener(this);

		this.setContentView(view);
		this.setWidth(DensityUtil.dip2px(context, 100));
		this.setHeight(DensityUtil.dip2px(context, 30));
		this.setFocusable(true);
		this.setOutsideTouchable(true);
		this.update();
		// ʵ����һ��ColorDrawable��ɫΪ��͸��
		ColorDrawable dw = new ColorDrawable(0000000000);
		// ��back���������ط�ʹ����ʧ,������������ܴ���OnDismisslistener �����������ؼ��仯�Ȳ���
		this.setBackgroundDrawable(dw);
		this.setAnimationStyle(R.style.social_pop_anim);
		
		initItemData();
	}
	private void initItemData() {
		addAction(new ActionItem("��"));
		addAction(new ActionItem("����"));
	}

	public void showPopupWindow(View parent){
		parent.getLocationOnScreen(mLocation);
		// ���þ��εĴ�С
		mRect.set(mLocation[0], mLocation[1], mLocation[0] + parent.getWidth(),mLocation[1] + parent.getHeight());
		digBtn.setText(mActionItems.get(0).mTitle);
		if(!this.isShowing()){
			showAtLocation(parent, Gravity.NO_GRAVITY, mLocation[0] - this.getWidth()
					, mLocation[1] - ((this.getHeight() - parent.getHeight()) / 2));
		}else{
			dismiss();
		}
	}

	@Override
	public void onClick(View view) {
		dismiss();
		switch (view.getId()) {
		case R.id.digBtn:
			mItemClickListener.onItemClick(mActionItems.get(0), 0);
			break;
		case R.id.commentBtn:
			mItemClickListener.onItemClick(mActionItems.get(1), 1);
			break;
		default:
			break;
		}
	}
	
	/**
	 * ���������
	 */
	public void addAction(ActionItem action) {
		if (action != null) {
			mActionItems.add(action);
		}
	}
	
	/**
	 * �������������������ť�����¼�
	 */
	public static interface OnItemClickListener {
		public void onItemClick(ActionItem item, int position);
	}
}
