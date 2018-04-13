/*
 * This is the source code of Telegram for Android v. 3.x.x.
 * It is licensed under GNU GPL v. 2 or later.
 * You should have received a copy of the license in this archive (see LICENSE).
 *
 * Copyright Nikolai Kudashov, 2013-2016.
 */

package com.hbtl.tm.cells;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import com.hbtl.ekt.R;
import com.hbtl.tm.AndroidUtilities;

public class InfoShadowCell extends View {

    private int size = 12;

    public InfoShadowCell(Context context) {
        this(context, null);
    }

    public InfoShadowCell(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public InfoShadowCell(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        setBackgroundResource(R.drawable.greydivider);
    }

    public void setSize(int value) {
        size = value;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(widthMeasureSpec), MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(size), MeasureSpec.EXACTLY));
    }
}
