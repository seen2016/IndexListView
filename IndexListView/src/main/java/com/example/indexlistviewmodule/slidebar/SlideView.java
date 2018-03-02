package com.example.indexlistviewmodule.slidebar;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Canvas;

import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import com.example.indexlistviewmodule.R;

/**
 * Created by LPF on 2018/2/28.
 */

public class SlideView extends View {

    private int mTextColor=getResources().getColor(R.color.colorPrimary) ;//选中颜色
    private int unSelectColor= getResources().getColor(R.color.colorAccent) ;//未选中颜色
    private int mTextSize = (int) sp2px(10);
    private Paint mPaint;//背景画笔
    /*绘制的列表导航字母*/
    private String words[] = {"A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N",
            "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z", "#"};
    private int touchIndex = 0;
    private Paint wordsPaint;
    private OnTouchLetterListeners onTouchLetterListeners;
    private String TAG = "SlideView";
    private int itemWidth;
    private int itemHeight;
    private int height;
    private int wordWidth;
    private int width;
    private String mCurrentWord;
    private int currentindex = 0;
    private boolean isUP = false;
    private int textStyleIndex = 1;
    private Typeface textStyle;
    private boolean textBold=false;


    public OnTouchLetterListeners getOnTouchLetterListeners() {
        return onTouchLetterListeners;
    }

    public void setOnTouchLetterListeners(OnTouchLetterListeners onTouchLetterListeners) {
        this.onTouchLetterListeners = onTouchLetterListeners;
    }

    public SlideView(Context context) {
        this(context, null);

    }

    public SlideView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    @SuppressLint("ResourceAsColor")
    public SlideView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        // 获取TypedArray
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.SlideView);
        // 获取文字颜色
        mTextColor = typedArray.getColor(R.styleable.SlideView_textColor, mTextColor);
        unSelectColor = typedArray.getColor(R.styleable.SlideView_unSelectColor, unSelectColor);
        // 获取文字大小
        mTextSize = typedArray.getDimensionPixelSize(R.styleable.SlideView_textSize, mTextSize);
        Log.e(TAG, "SlideView: textsize:"+mTextSize);
        textStyleIndex = typedArray.getInt(R.styleable.SlideView_textStyle, textStyleIndex);
        textBold = typedArray.getBoolean(R.styleable.SlideView_textBold, textBold);
        switch (textStyleIndex) {
            case 1:
                textStyle = Typeface.DEFAULT;
                break;
            case 2:
                textStyle = Typeface.DEFAULT_BOLD;
                break;
            case 3:
                textStyle = Typeface.SANS_SERIF;
                break;
            case 4:
                textStyle = Typeface.SERIF;
                break;
            case 5:
                textStyle = Typeface.MONOSPACE;
                break;
        }
        // 回收
        typedArray.recycle();
        mPaint = getPaint(mTextColor, mTextSize, textBold, textStyle);
        wordsPaint = getPaint(mTextColor, mTextSize, textBold, textStyle);
    }

    /**
     * 获得一个画笔
     *
     * @param colorRes  字体颜色
     * @param textsize  字体大小
     * @param isBold    是否为精体
     * @param textStyle 字体样式
     * @return 返回一个画笔
     */
    private Paint getPaint(int colorRes, int textsize, boolean isBold, Typeface textStyle) {
        //自定义属性 颜色 字体大小
        Paint paint = new Paint();
        paint.setTextSize(textsize);
        Typeface font = Typeface.create(textStyle, isBold ? Typeface.BOLD : Typeface.NORMAL);
        paint.setTypeface(font);
        paint.setColor(colorRes);
        return paint;

    }

    /**
     * px转 sp
     *
     * @param sp
     * @return
     */
    private float sp2px(int sp) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp, getResources().getDisplayMetrics());
    }

    /**
     * px 转dp
     *
     * @param dp
     * @return
     */
    private float dp2px(int dp) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, getResources().getDisplayMetrics());
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        //每一个字体的宽度
        //字体宽度
        wordWidth = (int) mPaint.measureText("A");
        //view的总宽度
        width = getPaddingLeft() + getPaddingRight() + wordWidth;
        //view的总高度
        height = MeasureSpec.getSize(heightMeasureSpec);
        //每一个字体的高度
        itemHeight = (height - getPaddingBottom() - getPaddingTop()) / words.length;
        setMeasuredDimension(width, height);
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int letterCenterX = getPaddingLeft();
        /**
         * 获取画笔测量对象
         */
        Paint.FontMetrics fontMetrics = mPaint.getFontMetrics();
        Log.i(TAG, "onDraw: ascent:" + fontMetrics.ascent);
        Log.i(TAG, "onDraw: descent:" + fontMetrics.descent);
        Log.i(TAG, "onDraw: bottom" + fontMetrics.bottom);
        Log.i(TAG, "onDraw: top" + fontMetrics.top);
        for (int i = 0; i < words.length; i++) {
            int letterCenterY = itemHeight / 2 + i * itemHeight + getPaddingTop();
            /**
             * 计算偏移
             */
            int dy = (int) (fontMetrics.bottom - fontMetrics.top / 2 - fontMetrics.bottom);
            /**
             * 计算基线
             */
            int baseline = letterCenterY + dy;

            if (currentindex == i) {
                //绘制文字圆形背景
//                canvas.drawCircle(wordX, letterCenterY, 23, mPaint);
                wordsPaint.setColor(mTextColor);
            } else {
                wordsPaint.setColor(unSelectColor);
            }
            canvas.drawText(words[i], letterCenterX, baseline, wordsPaint);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
//                setBackgroundColor(R.drawable.sidebar_background);
                setBackgroundColor(Color.parseColor("#00000000")); // 背景设置为透明
            case MotionEvent.ACTION_MOVE:
                float currentMoveY = event.getY();
                isUP = true;
                //当前索引下标
                currentindex = (int) (currentMoveY / itemHeight);
                if (currentindex < 0) {
                    currentindex = 0;
                }
                if (currentindex > words.length - 1) {
                    currentindex = words.length - 1;
                }
                mCurrentWord = words[currentindex];
                if (onTouchLetterListeners != null) {
                    //回调按下的字母
                    onTouchLetterListeners.onTouchLetter(mCurrentWord, isUP);
                }
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                isUP = false;
                postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (onTouchLetterListeners != null) {
                            //回调按下的字母
                            onTouchLetterListeners.onTouchLetter(mCurrentWord, isUP);
                        }
                    }
                }, 500);
                break;
        }

        return true;
    }

    @Override
    protected void onDisplayHint(int hint) {
        super.onDisplayHint(hint);
    }


    public interface OnTouchLetterListeners {
        void onTouchLetter(String words, boolean b);
    }

}
