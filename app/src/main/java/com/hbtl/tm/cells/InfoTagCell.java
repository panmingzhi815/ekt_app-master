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
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.hbtl.ekt.R;
import com.hbtl.tm.components.LayoutHelper;
import com.hbtl.tm.AndroidUtilities;
import com.hbtl.tm.LocaleController;

public class InfoTagCell extends FrameLayout {

    private TextView mLabelView;
    private TextView mTagView;

    private static Paint paint;
    private boolean needDivider;

    private String cLabel = "";
    private String cTag = "";
    private boolean vDivider = true;

    public InfoTagCell(Context context) {
        this(context, null);
    }

    public InfoTagCell(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public InfoTagCell(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        initAttrs(context, attrs);
    }

    private void initAttrs(Context context, AttributeSet attrs) {
        if (paint == null) {
            paint = new Paint();
            paint.setColor(0xffd9d9d9);
            paint.setStrokeWidth(1);
        }

        mLabelView = new TextView(context);
        mLabelView.setTextColor(0xff212121);
        mLabelView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
        mLabelView.setLines(1);
        mLabelView.setMaxLines(1);
        mLabelView.setSingleLine(true);
        mLabelView.setEllipsize(TextUtils.TruncateAt.END);
        mLabelView.setGravity((LocaleController.isRTL ? Gravity.RIGHT : Gravity.LEFT) | Gravity.CENTER_VERTICAL);
        addView(mLabelView, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT, (LocaleController.isRTL ? Gravity.RIGHT : Gravity.LEFT) | Gravity.TOP, 17, 0, 17, 0));

        mTagView = new TextView(context);
        mTagView.setTextColor(0xff2f8cc9);
        mTagView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
        mTagView.setLines(1);
        mTagView.setMaxLines(1);
        mTagView.setSingleLine(true);
        mTagView.setEllipsize(TextUtils.TruncateAt.END);
        mTagView.setGravity((LocaleController.isRTL ? Gravity.LEFT : Gravity.RIGHT) | Gravity.CENTER_VERTICAL);
        addView(mTagView, LayoutHelper.createFrame(LayoutHelper.WRAP_CONTENT, LayoutHelper.MATCH_PARENT, (LocaleController.isRTL ? Gravity.LEFT : Gravity.RIGHT) | Gravity.TOP, 17, 0, 17, 0));

        TypedArray t = context.obtainStyledAttributes(attrs, R.styleable.CoamView, 0, 0);
        // 读取属性...
        cLabel = t.getString(R.styleable.CoamView_cLabel);
        cTag = t.getString(R.styleable.CoamView_cTag);
        vDivider = t.getBoolean(R.styleable.CoamView_vDivider, true);
        // 设置默认值...
        this.setLabelTag(cLabel, cTag, vDivider);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(MeasureSpec.getSize(widthMeasureSpec), AndroidUtilities.dp(48) + (needDivider ? 1 : 0));

        int availableWidth = getMeasuredWidth() - getPaddingLeft() - getPaddingRight() - AndroidUtilities.dp(34);
        int width = availableWidth / 2;

        if (mTagView.getVisibility() == VISIBLE) {
            mTagView.measure(MeasureSpec.makeMeasureSpec(width, MeasureSpec.AT_MOST), MeasureSpec.makeMeasureSpec(getMeasuredHeight(), MeasureSpec.EXACTLY));
            width = availableWidth - mTagView.getMeasuredWidth() - AndroidUtilities.dp(8);
        } else {
            width = availableWidth;
        }
        mLabelView.measure(MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(getMeasuredHeight(), MeasureSpec.EXACTLY));
    }

    public void setLabelColor(int color) {
        mLabelView.setTextColor(color);
    }

    public void setTagColor(int color) {
        mTagView.setTextColor(color);
    }

    public void setLabel(String label) {
        mLabelView.setText(label);
    }

    public void setTag(String tag) {
        mTagView.setText(tag);

        if (tag != null)
            mTagView.setVisibility(VISIBLE);
        else
            mTagView.setVisibility(INVISIBLE);
    }

    public void setLabelTag(String label, String tag, boolean divider) {
        this.setLabel(label);
        this.setTag(tag);

        needDivider = divider;
        setWillNotDraw(!divider);
        requestLayout();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (needDivider) {
            canvas.drawLine(getPaddingLeft(), getHeight() - 1, getWidth() - getPaddingRight(), getHeight() - 1, paint);
        }
    }
}
