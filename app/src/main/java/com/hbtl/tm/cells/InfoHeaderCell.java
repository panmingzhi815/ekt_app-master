/*
 * This is the source code of Telegram for Android v. 3.x.x.
 * It is licensed under GNU GPL v. 2 or later.
 * You should have received a copy of the license in this archive (see LICENSE).
 *
 * Copyright Nikolai Kudashov, 2013-2016.
 */

package com.hbtl.tm.cells;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.hbtl.ekt.R;
import com.hbtl.tm.components.LayoutHelper;
import com.hbtl.tm.AndroidUtilities;
import com.hbtl.tm.LocaleController;

public class InfoHeaderCell extends FrameLayout {

    private TextView mTitleView;

    private String cTitle = "";

    public InfoHeaderCell(Context context) {
        this(context, null);
    }

    public InfoHeaderCell(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public InfoHeaderCell(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        initAttrs(context, attrs);
    }

    private void initAttrs(Context context, AttributeSet attrs) {
        mTitleView = new TextView(getContext());
        mTitleView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 15);
        mTitleView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        mTitleView.setTextColor(0xff3e90cf);
        mTitleView.setGravity((LocaleController.isRTL ? Gravity.RIGHT : Gravity.LEFT) | Gravity.CENTER_VERTICAL);
        addView(mTitleView, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT, (LocaleController.isRTL ? Gravity.RIGHT : Gravity.LEFT) | Gravity.TOP, 17, 15, 17, 0));

        TypedArray t = context.obtainStyledAttributes(attrs, R.styleable.CoamView, 0, 0);
        // 读取属性...
        cTitle = t.getString(R.styleable.CoamView_cTitle);
        // 设置默认值...
        this.setText(cTitle);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(38), MeasureSpec.EXACTLY));
    }

    public void setText(String text) {
        mTitleView.setText(text);
    }
}
