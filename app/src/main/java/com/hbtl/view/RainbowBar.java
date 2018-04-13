package com.hbtl.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import com.hbtl.ekt.R;
import com.hbtl.utils.UiUtils;

/**
 * Created by 亚飞 on 2015-12-05.
 * 参考 教你搞定Android自定义View http://www.jianshu.com/p/84cee705b0d3
 */
public class RainbowBar extends View {

    //progress bar color
    int barColor = Color.parseColor("#1E88E5");
    //every bar segment width
    int hSpace = UiUtils.dpToPx(80, getResources());
    //every bar segment height
    int vSpace = UiUtils.dpToPx(4, getResources());
    //space among bars
    int space = UiUtils.dpToPx(10, getResources());
    float startX = 0;
    float delta = 10f;
    Paint mPaint;

    public RainbowBar(Context context) {
        super(context);
    }

    public RainbowBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RainbowBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        initAttrs(context, attrs);
    }

    private void initAttrs(Context context, AttributeSet attrs) {
        //read custom attrs
        TypedArray t = context.obtainStyledAttributes(attrs, R.styleable.rainbowbar, 0, 0);
        hSpace = t.getDimensionPixelSize(R.styleable.rainbowbar_rainbowbar_hspace, hSpace);
        vSpace = t.getDimensionPixelOffset(R.styleable.rainbowbar_rainbowbar_vspace, vSpace);
        barColor = t.getColor(R.styleable.rainbowbar_rainbowbar_color, barColor);
        t.recycle();   // we should always recycle after used
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setColor(barColor);
        mPaint.setStrokeWidth(vSpace);
    }

    //draw be invoke numbers.
    int index = 0;

//    private int getMySize(int defaultSize, int measureSpec) {
//        int mySize = defaultSize;
//
//        int mode = MeasureSpec.getMode(measureSpec);
//        int size = MeasureSpec.getSize(measureSpec);
//
//        switch (mode) {
//            case MeasureSpec.UNSPECIFIED: {//如果没有指定大小,就设置为默认大小
//                mySize = defaultSize;
//                break;
//            }
//            case MeasureSpec.AT_MOST: {//如果测量模式是最大取值为size
//                //我们将大小取最大值,你也可以取其他值
//                mySize = size;
//                break;
//            }
//            case MeasureSpec.EXACTLY: {//如果是固定的大小,那就不要去改变它
//                mySize = size;
//                break;
//            }
//        }
//        return mySize;
//    }

//    @Override
//    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
//        int width = getMySize(defalutSize, widthMeasureSpec);
//        int height = getMySize(defalutSize, heightMeasureSpec);
//
//        if (width < height) {
//            height = width;
//        } else {
//            width = height;
//        }
//
//        setMeasuredDimension(width, height);
//    }

//    private int mWidth = 500, mHeight = 450;
//    @Override
//    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
//
//        // width
//        int widthSpecMode = MeasureSpec.getMode(widthMeasureSpec);
//        int widthSpecSize = MeasureSpec.getSize(widthMeasureSpec);
//
//        // height
//        int heightSpecMode = MeasureSpec.getMode(heightMeasureSpec);
//        int heightSpecSize = MeasureSpec.getSize(heightMeasureSpec);
//
//        // calculate
//        int finalWidth = mWidth > widthSpecSize ? mWidth : widthSpecSize;
//        int finalheight = mHeight > heightSpecSize ? mHeight : heightSpecSize;
//
//        //view支持wrap_content
//        if (widthSpecMode == MeasureSpec.AT_MOST && heightSpecMode == MeasureSpec.AT_MOST) {
//            setMeasuredDimension(finalWidth, finalheight);
//        } else if (widthSpecMode == MeasureSpec.AT_MOST) {
//            setMeasuredDimension(finalWidth, heightSpecSize);
//        } else if (heightSpecMode == MeasureSpec.AT_MOST) {
//            setMeasuredDimension(widthSpecSize, finalheight);
//        }
//    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //get screen width
        float sw = this.getMeasuredWidth();
        if (startX >= sw + (hSpace + space) - (sw % (hSpace + space))) {
            startX = 0;
        } else {
            startX += delta;
        }
        float start = startX;
        // draw latter parse
        while (start < sw) {
            canvas.drawLine(start, 5, start + hSpace, 5, mPaint);
            start += (hSpace + space);
        }

        start = startX - space - hSpace;

        // draw front parse
        while (start >= -hSpace) {
            canvas.drawLine(start, 5, start + hSpace, 5, mPaint);
            start -= (hSpace + space);
        }
//        if (index >= 700000) {
//            index = 0;
//        }
        invalidate();
    }
}
