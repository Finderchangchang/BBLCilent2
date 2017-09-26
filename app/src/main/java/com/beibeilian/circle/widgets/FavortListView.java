package com.beibeilian.circle.widgets;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

import com.beibeilian.circle.spannable.ISpanClick;
import com.beibeilian.seek.qaq.adapter.FavortListAdapter;

public class FavortListView extends TextView{
    private ISpanClick mSpanClickListener;

    public void setSpanClickListener(ISpanClick listener){
        mSpanClickListener = listener;
    }
    public ISpanClick getSpanClickListener(){
        return  mSpanClickListener;
    }

    public FavortListView(Context context) {
        super(context);
    }

    public FavortListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public FavortListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void setAdapter(FavortListAdapter adapter){
        adapter.bindListView(this);
    }

}
