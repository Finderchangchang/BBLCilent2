package com.beibeilian.circle.spannable;

import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.view.View;

import com.beibeilian.me.application.BBLApplication;
import com.beibeilian.android.app.R;


public class NameClickable extends ClickableSpan implements View.OnClickListener {
    private final ISpanClick mListener;
    private int mPosition;

    public NameClickable(ISpanClick l, int position) {
        mListener = l;
        mPosition = position;
    }

    @Override
    public void onClick(View widget) {
        mListener.onClick(mPosition);
    }

    @Override
    public void updateDrawState(TextPaint ds) {
        super.updateDrawState(ds);
        try
        {
        int colorValue = BBLApplication.getContext().getResources().getColor(
                R.color.color_8290AF);
        ds.setColor(colorValue);
        ds.setUnderlineText(false);
        ds.clearShadowLayer();
        }
        catch(Exception e)
        {
        	e.printStackTrace();
        }
    }
}
