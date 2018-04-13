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
import android.widget.FrameLayout;

import com.hbtl.ekt.R;
import com.hbtl.utils.UiUtils;

public class InfoEmptyCell extends FrameLayout {

    int sHeight = UiUtils.dpToPx(8, getResources());

    public InfoEmptyCell(Context context) {
        this(context, null);
    }

    public InfoEmptyCell(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public InfoEmptyCell(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        initAttrs(context, attrs);
    }

    private void initAttrs(Context context, AttributeSet attrs) {
        TypedArray t = context.obtainStyledAttributes(attrs, R.styleable.CoamView, 0, 0);
        // 读取属性...
        sHeight = t.getDimensionPixelSize(R.styleable.CoamView_sHeight, sHeight);
        // 设置默认值...
        this.setHeight(sHeight);
    }

    public void setHeight(int height) {
        sHeight = height;
        requestLayout();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(sHeight, MeasureSpec.EXACTLY));
    }
}
