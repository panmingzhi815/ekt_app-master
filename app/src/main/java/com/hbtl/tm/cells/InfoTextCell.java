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
import android.view.View;
import android.widget.TextView;

import com.hbtl.ekt.R;
import com.hbtl.tm.components.FrameLayoutFixed;
import com.hbtl.tm.components.LayoutHelper;
import com.hbtl.tm.AndroidUtilities;
import com.hbtl.tm.LocaleController;

public class InfoTextCell extends FrameLayoutFixed {

    private TextView mLabelView;
    private TextView mTextView;

    private static Paint paint;
    private boolean needDivider;
    private boolean multiline;

    private String cLabel = "";
    private String cText = "";
    private boolean vDivider = true;

    public InfoTextCell(Context context) {
        this(context, null);
    }

    public InfoTextCell(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public InfoTextCell(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        initAttrs(context, attrs);
    }

    private void initAttrs(Context context, AttributeSet attrs) {
        if (paint == null) {
            paint = new Paint();
            paint.setColor(0xffd9d9d9);
            paint.setStrokeWidth(1);
        }

        mTextView = new TextView(context);
        mTextView.setTextColor(0xff212121);
        mTextView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
        mTextView.setLines(1);
        mTextView.setMaxLines(1);
        mTextView.setSingleLine(true);
        mTextView.setGravity((LocaleController.isRTL ? Gravity.RIGHT : Gravity.LEFT) | Gravity.CENTER_VERTICAL);
        addView(mTextView, LayoutHelper.createFrame(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT, (LocaleController.isRTL ? Gravity.RIGHT : Gravity.LEFT) | Gravity.TOP, 17, 10, 17, 0));

        mLabelView = new TextView(context);
        mLabelView.setTextColor(0xff8a8a8a);
        mLabelView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 13);
        mLabelView.setGravity(LocaleController.isRTL ? Gravity.RIGHT : Gravity.LEFT);
        mLabelView.setLines(1);
        mLabelView.setMaxLines(1);
        mLabelView.setSingleLine(true);
        //valueTextView.setPadding(0, AndroidUtilities.dp(20), 0, AndroidUtilities.dp(20));
        addView(mLabelView, LayoutHelper.createFrame(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT, (LocaleController.isRTL ? Gravity.RIGHT : Gravity.LEFT) | Gravity.TOP, 17, 35, 17, 0));

        TypedArray t = context.obtainStyledAttributes(attrs, R.styleable.CoamView, 0, 0);
        // 读取属性...
        cLabel = t.getString(R.styleable.CoamView_cLabel);
        cText = t.getString(R.styleable.CoamView_cText);
        vDivider = t.getBoolean(R.styleable.CoamView_vDivider, true);
        // 设置默认值...
        this.setLabelText(cLabel, cText, vDivider);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (!multiline) {
            super.onMeasure(widthMeasureSpec, View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(64) + (needDivider ? 1 : 0), View.MeasureSpec.EXACTLY));
        } else {
            super.onMeasure(widthMeasureSpec, View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        }
    }

    public void setMultilineDetail(boolean value) {
        multiline = value;

        if (value) {
            mTextView.setLines(0);
            mTextView.setMaxLines(0);
            mTextView.setSingleLine(false);
            mTextView.setPadding(0, 0, 0, AndroidUtilities.dp(12));
        } else {
            mTextView.setLines(1);
            mTextView.setMaxLines(1);
            mTextView.setSingleLine(true);
            mTextView.setPadding(0, 0, 0, 0);
        }
    }

    public void setLabel(String label) {
        mLabelView.setText(label);
    }

    public void setText(String text) {
        mTextView.setText(text);

        if (text != null)
            mTextView.setVisibility(VISIBLE);
        else
            mTextView.setVisibility(INVISIBLE);
    }

    public void setLabelText(String label, String text, boolean divider) {
        this.setLabel(label);
        this.setText(text);

        needDivider = divider;

        // 自定义调整多行隐藏问题
        // [textview.getLineCount always 0 in android](http://stackoverflow.com/questions/3528790/textview-getlinecount-always-0-in-android)
        mTextView.post(new Runnable() {

            @Override
            public void run() {
                if (mTextView.getLineCount() <= 1) return;
                int ss = mTextView.getLineHeight() * (mTextView.getLineCount() - 1);
                //Timber.i("OOOOOOOOOOOO|ss:" + ss + "|getLineHeight():" + textView.getLineHeight() + "|getLineCount():" + textView.getLineCount() + "|textView.getHeight():" + textView.getHeight() + "|text:" + text);
                mLabelView.setPadding(0, ss, 0, AndroidUtilities.dp(12));
                invalidate();
            }
        });

        setWillNotDraw(!divider);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (needDivider) {
            canvas.drawLine(getPaddingLeft(), getHeight() - 1, getWidth() - getPaddingRight(), getHeight() - 1, paint);
        }
    }
}
