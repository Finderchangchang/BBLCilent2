package com.beibeilian.circle.widgets;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout.LayoutParams;
import android.widget.LinearLayout;

import com.beibeilian.circle.utils.DensityUtil;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.List;



public class MultiImageView extends LinearLayout {
	public static int MAX_WIDTH = 0;

	// ��Ƭ��Url�б�
	private List<String> imagesList;

	/** ���� ��λΪPixel **/
	private int pxOneWidth = DensityUtil.dip2px(getContext(), 115);// ����ͼʱ��Ŀ�
	private int pxOneHeight = DensityUtil.dip2px(getContext(), 150);// ����ͼʱ��ĸ�
	private int pxMoreWandH = 0;// ����ͼ�Ŀ��
	private int pxImagePadding = DensityUtil.dip2px(getContext(), 3);// ͼƬ��ļ��

	private int MAX_PER_ROW_COUNT = 3;// ÿ����ʾ�����

	private LayoutParams onePicPara;
	private LayoutParams morePara;
	private LayoutParams rowPara;

	private OnItemClickListener mOnItemClickListener;

	public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
		mOnItemClickListener = onItemClickListener;
	}

	public MultiImageView(Context context) {
		super(context);
	}

	public MultiImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public void setList(List<String> lists) throws IllegalArgumentException {
		if (lists == null) {
			throw new IllegalArgumentException("imageList is null...");
		}
		imagesList = lists;

		if (MAX_WIDTH > 0) {
			pxMoreWandH = MAX_WIDTH / 3 - pxImagePadding;
			pxOneWidth = MAX_WIDTH / 2;
			pxOneHeight = MAX_WIDTH * 2 / 3;
			initImageLayoutParams();
		}

		initView();
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		if (MAX_WIDTH == 0) {
			int width = measureWidth(widthMeasureSpec);
			if (width > 0) {
				MAX_WIDTH = width;
				if (imagesList != null && imagesList.size() > 0) {
					setList(imagesList);
				}
			}
		}
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	}

	/**
	 * Determines the width of this view
	 * 
	 * @param measureSpec
	 *            A measureSpec packed into an int
	 * @return The width of the view, honoring constraints from measureSpec
	 */
	private int measureWidth(int measureSpec) {
		int result = 0;
		int specMode = MeasureSpec.getMode(measureSpec);
		int specSize = MeasureSpec.getSize(measureSpec);

		if (specMode == MeasureSpec.EXACTLY) {
			// We were told how big to be
			result = specSize;
		} else {
			// Measure the text
			// result = (int) mTextPaint.measureText(mText) + getPaddingLeft()
			// + getPaddingRight();
			if (specMode == MeasureSpec.AT_MOST) {
				// Respect AT_MOST value if that was what is called for by
				// measureSpec
				result = Math.min(result, specSize);
			}
		}
		return result;
	}

	private void initImageLayoutParams() {

		onePicPara = new LayoutParams(pxOneWidth, pxOneHeight);

		morePara = new LayoutParams(pxMoreWandH, pxMoreWandH);
		morePara.setMargins(0, 0, pxImagePadding, 0);

		int wrap = LayoutParams.WRAP_CONTENT;
		int match = LayoutParams.MATCH_PARENT;
		rowPara = new LayoutParams(match, wrap);
		rowPara.setMargins(0, 0, 0, pxImagePadding);
	}

	// ����imageView��������ʼ����ͬ��View����,��ҪΪÿһ��View�����Ч��
	private void initView() {
		this.setOrientation(VERTICAL);
		this.removeAllViews();
		if (MAX_WIDTH == 0) {
			// Ϊ�˴���onMeasure()������MultiImageView������ȣ�MultiImageView�Ŀ�����Ϊmatch_parent
			addView(new View(getContext()));
			return;
		}

		if (imagesList == null || imagesList.size() == 0) {
			return;
		}

		if (imagesList.size() == 1) {
			for (String url : imagesList) {
				ImageView imageView = new ImageView(getContext());
				imageView.setId(url.hashCode());// ָ��id
				imageView.setLayoutParams(onePicPara);
				imageView.setMinimumWidth(pxMoreWandH);
				imageView.setScaleType(ScaleType.CENTER_CROP);
				ImageLoader.getInstance().displayImage(url, imageView);

				int position = 0;
				imageView.setTag(position);
				imageView.setOnClickListener(mImageViewOnClickListener);
				addView(imageView);
			}

		} else {
			int allCount = imagesList.size();
			if (allCount == 4) {
				MAX_PER_ROW_COUNT = 2;
			} else {
				MAX_PER_ROW_COUNT = 3;
			}
			int rowCount = allCount / MAX_PER_ROW_COUNT + (allCount % MAX_PER_ROW_COUNT > 0 ? 1 : 0);// ����
			for (int rowCursor = 0; rowCursor < rowCount; rowCursor++) {
				LinearLayout rowLayout = new LinearLayout(getContext());
				rowLayout.setOrientation(LinearLayout.HORIZONTAL);

				rowLayout.setLayoutParams(rowPara);
				if (rowCursor == 0) {
					rowLayout.setPadding(0, pxImagePadding, 0, 0);
				}

				int columnCount = allCount % MAX_PER_ROW_COUNT == 0 ? MAX_PER_ROW_COUNT : allCount % MAX_PER_ROW_COUNT;// ÿ�е�����
				if (rowCursor != rowCount - 1) {
					columnCount = MAX_PER_ROW_COUNT;
				}
				addView(rowLayout);

				int rowOffset = rowCursor * MAX_PER_ROW_COUNT;// ��ƫ��
				for (int columnCursor = 0; columnCursor < columnCount; columnCursor++) {
					int position = columnCursor + rowOffset;
					String thumbUrl = imagesList.get(position);

					ImageView imageView = new ImageView(getContext());
					imageView.setId(thumbUrl.hashCode());// ָ��id
					imageView.setLayoutParams(morePara);
					imageView.setScaleType(ScaleType.CENTER_CROP);
					ImageLoader.getInstance().displayImage(thumbUrl, imageView);

					imageView.setTag(position);
					imageView.setOnClickListener(mImageViewOnClickListener);

					rowLayout.addView(imageView);
				}
			}
		}
	}

	// ͼƬ����¼�
	private View.OnClickListener mImageViewOnClickListener = new OnClickListener() {

		@Override
		public void onClick(View view) {
			if (mOnItemClickListener != null) {
				mOnItemClickListener.onItemClick(view, (Integer) view.getTag());
			}
		}
	};

	public interface OnItemClickListener {
		public void onItemClick(View view, int position);
	}
}