package com.beibeilian.circle.spannable;

import android.annotation.SuppressLint;
import android.text.Layout;
import android.text.Selection;
import android.text.Spannable;
import android.text.method.BaseMovementMethod;
import android.text.method.Touch;
import android.text.style.BackgroundColorSpan;
import android.text.style.ClickableSpan;
import android.view.MotionEvent;
import android.widget.TextView;

import com.beibeilian.me.application.BBLApplication;
import com.beibeilian.android.app.R;


@SuppressLint("NewApi")
public class CircleMovementMethod extends BaseMovementMethod {
    public final String TAG = CircleMovementMethod.class.getSimpleName();
    public final static int DEFAULT_COLOR = R.color.transparent;
    private int mTextViewBgColorId ;
    private int mClickableSpanBgClorId;

    private BackgroundColorSpan mBgSpan;
    private ClickableSpan[] mClickLinks;
    private boolean isPassToTv = true;
    /**
     * true：响应textview的点击事件， false：响应设置的clickableSpan事件
     */
    public boolean isPassToTv() {
        return isPassToTv;
    }
    private void setPassToTv(boolean isPassToTv){
        this.isPassToTv = isPassToTv;
    }

    public CircleMovementMethod(){
        mTextViewBgColorId = DEFAULT_COLOR;
        mClickableSpanBgClorId = DEFAULT_COLOR;
    }

    public CircleMovementMethod(int clickableSpanBgClorId){
        mClickableSpanBgClorId = clickableSpanBgClorId;
        mTextViewBgColorId = DEFAULT_COLOR;
    }

    public CircleMovementMethod(int clickableSpanBgClorId, int textViewBgColorId){
        mClickableSpanBgClorId = clickableSpanBgClorId;
        mTextViewBgColorId = textViewBgColorId;
    }

    public boolean onTouchEvent(TextView widget, Spannable buffer,
                                MotionEvent event) {

        int action = event.getAction();
        if(action == MotionEvent.ACTION_DOWN){
            int x = (int) event.getX();
            int y = (int) event.getY();

            x -= widget.getTotalPaddingLeft();
            y -= widget.getTotalPaddingTop();

            x += widget.getScrollX();
            y += widget.getScrollY();

            Layout layout = widget.getLayout();
            int line = layout.getLineForVertical(y);
            int off = layout.getOffsetForHorizontal(line, x);

            mClickLinks = buffer.getSpans(off, off, ClickableSpan.class);
            if(mClickLinks.length > 0){
                // 点击的是Span区域，不要把点击事件传�?
                setPassToTv(false);
                Selection.setSelection(buffer,
                        buffer.getSpanStart(mClickLinks[0]),
                        buffer.getSpanEnd(mClickLinks[0]));
                //设置点击区域的背景色
                mBgSpan = new BackgroundColorSpan(BBLApplication.getContext().getResources().getColor(mClickableSpanBgClorId));
                buffer.setSpan(mBgSpan,
                        buffer.getSpanStart(mClickLinks[0]),
                        buffer.getSpanEnd(mClickLinks[0]),
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }else{
                setPassToTv(true);
                // textview选中效果
                widget.setBackgroundResource(mTextViewBgColorId);
            }

        }else if(action == MotionEvent.ACTION_UP){
            if(mClickLinks.length > 0){
                mClickLinks[0].onClick(widget);
                if(mBgSpan != null){
                    buffer.removeSpan(mBgSpan);
                }

            }else{

            }
            Selection.removeSelection(buffer);
            widget.setBackgroundResource(DEFAULT_COLOR);
        }else if(action == MotionEvent.ACTION_MOVE){

        }else{
            if(mBgSpan != null){
                buffer.removeSpan(mBgSpan);
            }
            widget.setBackgroundResource(DEFAULT_COLOR);
        }
        return Touch.onTouchEvent(widget, buffer, event);
    }
}
