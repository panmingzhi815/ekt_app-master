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
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.hbtl.ekt.R;
import com.hbtl.tm.components.LayoutHelper;
import com.hbtl.tm.AndroidUtilities;

public class InfoLabelCell extends FrameLayout {

    private TextView mInfoView;

    private static Paint paint;
    private boolean needDivider;

    private String cInfo = "";
    private boolean vDivider = true;

    public InfoLabelCell(Context context) {
        this(context, null);
    }

    public InfoLabelCell(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public InfoLabelCell(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        initAttrs(context, attrs);
    }

    private void initAttrs(Context context, AttributeSet attrs) {
        if (paint == null) {
            paint = new Paint();
            paint.setColor(0xffd9d9d9);
            paint.setStrokeWidth(1);
        }

        mInfoView = new TextView(context);
        mInfoView.setTextColor(0xffa3a3a3);
        mInfoView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 13);
        mInfoView.setGravity(Gravity.CENTER);
        mInfoView.setPadding(0, AndroidUtilities.dp(19), 0, AndroidUtilities.dp(19));
        addView(mInfoView, LayoutHelper.createFrame(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT, Gravity.CENTER, 17, 0, 17, 0));

        TypedArray t = context.obtainStyledAttributes(attrs, R.styleable.CoamView, 0, 0);
        // 读取属性...
        cInfo = t.getString(R.styleable.CoamView_cInfo);
        vDivider = t.getBoolean(R.styleable.CoamView_vDivider, true);
        // 设置默认值...
        this.setInfo(cInfo, vDivider);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
    }

    public void setInfo(String text, boolean divider) {
        mInfoView.setText(text);
        needDivider = divider;
        setWillNotDraw(!divider);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (needDivider) {
            canvas.drawLine(getPaddingLeft(), getHeight() - 1, getWidth() - getPaddingRight(), getHeight() - 1, paint);
        }
    }
}
