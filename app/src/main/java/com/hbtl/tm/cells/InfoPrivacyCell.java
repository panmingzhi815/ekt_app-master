/*
 * This is the source code of Telegram for Android v. 3.x.x.
 * It is licensed under GNU GPL v. 2 or later.
 * You should have received a copy of the license in this archive (see LICENSE).
 *
 * Copyright Nikolai Kudashov, 2013-2016.
 */

package com.hbtl.tm.cells;

import android.content.Context;
import android.text.method.LinkMovementMethod;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.hbtl.tm.components.LayoutHelper;
import com.hbtl.tm.components.Theme;
import com.hbtl.tm.AndroidUtilities;
import com.hbtl.tm.LocaleController;

public class InfoPrivacyCell extends FrameLayout {

    private TextView textView;

    public InfoPrivacyCell(Context context) {
        this(context, null);
    }

    public InfoPrivacyCell(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public InfoPrivacyCell(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        initAttrs(context, attrs);
    }

    private void initAttrs(Context context, AttributeSet attrs) {
        textView = new TextView(context);
        textView.setTextColor(0xff808080);
        textView.setLinkTextColor(Theme.MSG_LINK_TEXT_COLOR);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
        textView.setGravity(LocaleController.isRTL ? Gravity.RIGHT : Gravity.LEFT);
        textView.setPadding(0, AndroidUtilities.dp(10), 0, AndroidUtilities.dp(17));
        textView.setMovementMethod(LinkMovementMethod.getInstance());
        addView(textView, LayoutHelper.createFrame(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT, (LocaleController.isRTL ? Gravity.RIGHT : Gravity.LEFT) | Gravity.TOP, 17, 0, 17, 0));
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(widthMeasureSpec), MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
    }

    public void setText(CharSequence text) {
        textView.setText(text);
    }

    public void setTextColor(int color) {
        textView.setTextColor(color);
    }
}
