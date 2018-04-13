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
import android.os.Build;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.hbtl.ekt.R;
import com.hbtl.tm.components.FrameLayoutFixed;
import com.hbtl.tm.components.LayoutHelper;
import com.hbtl.tm.widgets.Switch;
import com.hbtl.tm.AndroidUtilities;
import com.hbtl.tm.LocaleController;

public class InfoSwitchCell extends FrameLayoutFixed {

    private TextView mLabelView;
    private TextView mTextView;
    private Switch mSwitchView;

    private static Paint paint;
    private boolean needDivider;
    private boolean isMultiline;

    private String cLabel = "";
    private String cText = "";
    private boolean vOpen = true;
    private boolean vDivider = true;

    public InfoSwitchCell(Context context) {
        this(context, null);
    }

    public InfoSwitchCell(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public InfoSwitchCell(Context context, AttributeSet attrs, int defStyleAttr) {
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
        mLabelView.setGravity((LocaleController.isRTL ? Gravity.RIGHT : Gravity.LEFT) | Gravity.CENTER_VERTICAL);
        mLabelView.setEllipsize(TextUtils.TruncateAt.END);
        addView(mLabelView, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT, (LocaleController.isRTL ? Gravity.RIGHT : Gravity.LEFT) | Gravity.TOP, LocaleController.isRTL ? 64 : 17, 0, LocaleController.isRTL ? 17 : 64, 0));

        mTextView = new TextView(context);
        mTextView.setTextColor(0xff8a8a8a);
        mTextView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 13);
        mTextView.setGravity(LocaleController.isRTL ? Gravity.RIGHT : Gravity.LEFT);
        mTextView.setLines(1);
        mTextView.setMaxLines(1);
        mTextView.setSingleLine(true);
        mTextView.setPadding(0, 0, 0, 0);
        mTextView.setEllipsize(TextUtils.TruncateAt.END);
        addView(mTextView, LayoutHelper.createFrame(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT, (LocaleController.isRTL ? Gravity.RIGHT : Gravity.LEFT) | Gravity.TOP, LocaleController.isRTL ? 64 : 17, 35, LocaleController.isRTL ? 17 : 64, 0));

        mSwitchView = new Switch(context);
        mSwitchView.setDuplicateParentStateEnabled(false);
        mSwitchView.setFocusable(false);
        mSwitchView.setFocusableInTouchMode(false);
        mSwitchView.setClickable(false);
        addView(mSwitchView, LayoutHelper.createFrame(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT, (LocaleController.isRTL ? Gravity.LEFT : Gravity.RIGHT) | Gravity.CENTER_VERTICAL, 14, 0, 14, 0));

        TypedArray t = context.obtainStyledAttributes(attrs, R.styleable.CoamView, 0, 0);
        // 读取属性...
        cLabel = t.getString(R.styleable.CoamView_cLabel);
        cText = t.getString(R.styleable.CoamView_cText);
        vOpen = t.getBoolean(R.styleable.CoamView_vOpen, true);
        vDivider = t.getBoolean(R.styleable.CoamView_vDivider, true);
        // 设置默认值...
        if (cText == null) {
            this.setTextAndCheck(cLabel, vOpen, vDivider);
        } else {
            this.setTextAndValueAndCheck(cLabel, cText, vOpen, false, vDivider);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (isMultiline) {
            super.onMeasure(widthMeasureSpec, View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        } else {
            super.onMeasure(widthMeasureSpec, View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(mTextView.getVisibility() == VISIBLE ? 64 : 48) + (needDivider ? 1 : 0), View.MeasureSpec.EXACTLY));
        }
    }

    public void setTextAndCheck(String text, boolean checked, boolean divider) {
        mLabelView.setText(text);
        if (Build.VERSION.SDK_INT < 11) {
            mSwitchView.resetLayout();
            mSwitchView.requestLayout();
        }
        isMultiline = false;
        mSwitchView.setChecked(checked);
        needDivider = divider;
        mTextView.setVisibility(GONE);
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) mLabelView.getLayoutParams();
        layoutParams.height = FrameLayout.LayoutParams.MATCH_PARENT;
        layoutParams.topMargin = 0;
        mLabelView.setLayoutParams(layoutParams);
        setWillNotDraw(!divider);
    }

    public void setTextAndValueAndCheck(String label, String text, boolean checked, boolean multiline, boolean divider) {
        mLabelView.setText(label);
        mTextView.setText(text);
        if (Build.VERSION.SDK_INT < 11) {
            mSwitchView.resetLayout();
            mSwitchView.requestLayout();
        }
        mSwitchView.setChecked(checked);
        needDivider = divider;
        mTextView.setVisibility(VISIBLE);
        isMultiline = multiline;
        if (multiline) {
            mTextView.setLines(0);
            mTextView.setMaxLines(0);
            mTextView.setSingleLine(false);
            mTextView.setEllipsize(null);
            mTextView.setPadding(0, 0, 0, AndroidUtilities.dp(11));
        } else {
            mTextView.setLines(1);
            mTextView.setMaxLines(1);
            mTextView.setSingleLine(true);
            mTextView.setEllipsize(TextUtils.TruncateAt.END);
            mTextView.setPadding(0, 0, 0, 0);
        }
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) mLabelView.getLayoutParams();
        layoutParams.height = FrameLayout.LayoutParams.WRAP_CONTENT;
        layoutParams.topMargin = AndroidUtilities.dp(10);
        mLabelView.setLayoutParams(layoutParams);
        setWillNotDraw(!divider);
    }

    public void setChecked(boolean checked) {
        mSwitchView.setChecked(checked);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (needDivider) {
            canvas.drawLine(getPaddingLeft(), getHeight() - 1, getWidth() - getPaddingRight(), getHeight() - 1, paint);
        }
    }
}
