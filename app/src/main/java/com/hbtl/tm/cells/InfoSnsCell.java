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
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
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

public class InfoSnsCell extends FrameLayoutFixed {

    private TextView mInfoView;
    private TextView mLabelView;

    private static Paint paint;
    private boolean needDivider;
    private boolean multiline;
    private String snsBindWay;// QQ WeChat WeiBo
    private String snsBindId;// true false
    //private Boolean ifSnsBind;// true false

    private int errorTop = AndroidUtilities.dp(3);
    private int errorLeft = AndroidUtilities.dp(9);
    private static Drawable errorDrawable;

    private String cLabel = "";
    private String cText = "";
    private boolean vDivider = true;

    public InfoSnsCell(Context context) {
        this(context, null);
    }

    public InfoSnsCell(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public InfoSnsCell(Context context, AttributeSet attrs, int defStyleAttr) {
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
        mInfoView.setTextColor(0xff212121);
        mInfoView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
        mInfoView.setLines(1);
        mInfoView.setMaxLines(1);
        mInfoView.setSingleLine(true);
        mInfoView.setGravity((LocaleController.isRTL ? Gravity.RIGHT : Gravity.LEFT) | Gravity.CENTER_VERTICAL);
        addView(mInfoView, LayoutHelper.createFrame(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT, (LocaleController.isRTL ? Gravity.RIGHT : Gravity.LEFT) | Gravity.TOP, 17, 10, 17, 0));

        mLabelView = new TextView(context);
        mLabelView.setTextColor(0xff8a8a8a);
        mLabelView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 13);
        mLabelView.setGravity(LocaleController.isRTL ? Gravity.RIGHT : Gravity.LEFT);
        mLabelView.setLines(1);
        mLabelView.setMaxLines(1);
        mLabelView.setSingleLine(true);
        mLabelView.setPadding(0, 0, 0, 0);
        addView(mLabelView, LayoutHelper.createFrame(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT, (LocaleController.isRTL ? Gravity.RIGHT : Gravity.LEFT) | Gravity.TOP, 17, 35, 17, 0));

        errorDrawable = getResources().getDrawable(R.drawable.dialogs_warning);
        int messageWidth = getMeasuredWidth() - AndroidUtilities.dp(AndroidUtilities.leftBaseline + 16);

        int w = errorDrawable.getIntrinsicWidth() + AndroidUtilities.dp(8);
//        messageWidth -= w;
//        if (!LocaleController.isRTL) {
        errorLeft = getMeasuredWidth() - errorDrawable.getIntrinsicWidth() - AndroidUtilities.dp(110);
//        } else {
//            errorLeft = AndroidUtilities.dp(11);
//            messageLeft += w;
//        }

        TypedArray t = context.obtainStyledAttributes(attrs, R.styleable.CoamView, 0, 0);
        // 读取属性...
        cLabel = t.getString(R.styleable.CoamView_cLabel);
        cText = t.getString(R.styleable.CoamView_cText);
        vDivider = t.getBoolean(R.styleable.CoamView_vDivider, true);
        // 设置默认值...
        this.setLabelInfo(cLabel, cText, vDivider);
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
            mLabelView.setLines(0);
            mLabelView.setMaxLines(0);
            mLabelView.setSingleLine(false);
            mLabelView.setPadding(0, 0, 0, AndroidUtilities.dp(12));
        } else {
            mLabelView.setLines(1);
            mLabelView.setMaxLines(1);
            mLabelView.setSingleLine(true);
            mLabelView.setPadding(0, 0, 0, 0);
        }
    }

    public void setLabelInfo(String label, String text, boolean divider) {
        mInfoView.setText(text);
        mLabelView.setText(label);
        needDivider = divider;
        setWillNotDraw(!divider);
    }

    public void setBindInfo(String snsBindWay, String snsBindId) {
        this.snsBindWay = snsBindWay;
        this.snsBindId = snsBindId;
    }

    // 绘制 Icon 问题
    private int iconDrawPadding;
    private int iconDrawWidth;
    private int iconDrawHeight;

    @Override
    protected void onDraw(Canvas canvas) {
        ///setDrawableBounds(errorDrawable, errorLeft, errorTop);
        errorDrawable.setBounds(errorLeft, errorTop, errorLeft + errorDrawable.getIntrinsicWidth(), errorTop + errorDrawable.getIntrinsicHeight());
        errorDrawable.draw(canvas);

        snsBindWay = "WeChat";
        int snsBindRes = 0;
        switch (snsBindWay) {
            case "QQ":
                if (snsBindId != null && !"NULL".equals(snsBindId)) {
                    snsBindRes = R.drawable.sns_zj_qq_select;
                } else {
                    snsBindRes = R.drawable.sns_zj_qq_none;
                }
                break;
            case "WeChat":
                if (snsBindId != null && !"NULL".equals(snsBindId)) {
                    snsBindRes = R.drawable.sns_zj_weixin_select;
                } else {
                    snsBindRes = R.drawable.sns_zj_weixin_none;
                }
                break;
            case "WeiBo":
                if (snsBindId != null && !"NULL".equals(snsBindId)) {
                    snsBindRes = R.drawable.sns_zj_weibo_select;
                } else {
                    snsBindRes = R.drawable.sns_zj_weibo_none;
                }
                break;
            default:
                break;
        }
        Bitmap bg = BitmapFactory.decodeResource(getContext().getResources(), snsBindRes);
        //canvas.drawBitmap(bg, new Rect(100,100,150,150), new Rect(0,0,50,50) , null);
        //canvas.drawBitmap(bg, getMeasuredWidth() - errorDrawable.getIntrinsicWidth() - AndroidUtilities.dp(12), AndroidUtilities.dp(12), null);
        // 定义矩阵对象
        Matrix matrix = new Matrix();
        // 缩放原图
//        matrix.postScale(AndroidUtilities.dp(10) / bg.getWidth(), AndroidUtilities.dp(30) / bg.getHeight());
//        matrix.postScale(0.5f, 0.5f);
        //bmp.getWidth(), bmp.getHeight()分别表示缩放后的位图宽高
        Bitmap dstbmp = Bitmap.createBitmap(bg, 0, 0, bg.getWidth(), bg.getHeight(), matrix, true);
//        // 在画布上绘制旋转后的位图
//        //放在坐标为60,460的位置
//        int mw = getMeasuredWidth();
//        int au = AndroidUtilities.dp(60);
//        Timber.i("############### mw:" + mw + "|au:" + au);
//        //canvas.drawBitmap(dstbmp, mw - au, AndroidUtilities.dp(2), null);

        // 计算 sns 图标 周边空白
        iconDrawWidth = bg.getWidth();
        iconDrawHeight = bg.getHeight();
        iconDrawPadding = (getHeight() - bg.getHeight()) / 2;
        canvas.drawBitmap(dstbmp, getMeasuredWidth() - bg.getWidth() - iconDrawPadding, iconDrawPadding, null);
//                Picasso.with(getContext())
//                .load("https://dn-lonal.qbox.me/Home/public//Resources/index/images/CheckRealTimePosition.jpg")
////                    .placeholder(R.drawable.icon_category_leaderboard_raster)
//                .placeholder(avatarDrawable)
//                .error(R.drawable.icon_category_tvmovies_raster)
//                .into(avatarImage);

        if (needDivider) {
            canvas.drawLine(getPaddingLeft(), getHeight() - 1, getWidth() - getPaddingRight(), getHeight() - 1, paint);
        }
    }
}
