package com.rojao.customviewtrainning.widget;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.animation.LinearInterpolator;

import com.rojao.customviewtrainning.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lsc on 2016/10/20 0020.
 *
 * @author lsc
 * @desc 思路:画两条贝塞尔曲线,通过设置点的坐标使其移动,根据可视点加多一个周期就能实现贝塞尔曲线的无缝复位
 */
public class WaveView extends View {

    private static final float DEFAULT_MAX_PROGRESS = 100f;
    private static final int DEFAULT_BG_COLOR = Color.WHITE;
    private static final int DEFAULT_WAVE_COLOR = Color.GREEN;
    private static final String TAG = "debug_waveView";

    private int mBgColor;
    private int mWaveColor;
    private float mMaxProgress;
    private int mRadius;
    private boolean hasMeasure;
    private List<Point> mMainWavePoints = new ArrayList<>();
    private List<Point> mSecondaryPoints = new ArrayList<>();
    private int mWidth;
    private int mHeight;
    private float mMainWaveWidth;
    private float mWaveHeight;
    private int mDy;
    private int mMainWaveDx;

    private Path mMainPath;
    private Path mSecordaryPath;
    private float mSecondaryWaveWidth;

    private Paint mPaint;
    private PorterDuffXfermode mPorterDuffXfermode;
    private float mSpeed;
    private int mSecordaryDx;


    public WaveView(Context context) {
        super(context);
        init(context, null);
    }

    public WaveView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public WaveView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.WaveView);
        mBgColor = typedArray.getColor(R.styleable.WaveView_backgroundColor, DEFAULT_BG_COLOR);
        mWaveColor = typedArray.getColor(R.styleable.WaveView_waveColor, DEFAULT_WAVE_COLOR);
        mMaxProgress = typedArray.getFloat(R.styleable.WaveView_maxProgress, DEFAULT_MAX_PROGRESS);
        mRadius = typedArray.getDimensionPixelSize(R.styleable.WaveView_radius, 250);
        typedArray.recycle();
        mMainPath = new Path();
        mSecordaryPath = new Path();
        mMainPath.setFillType(Path.FillType.EVEN_ODD);
        mPaint = new Paint();
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setAntiAlias(true);
        mPaint.setStrokeWidth(5);
        mPorterDuffXfermode = new PorterDuffXfermode(PorterDuff.Mode.SRC_ATOP);

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);

        if (widthMode == MeasureSpec.EXACTLY || heightMode == MeasureSpec.EXACTLY) {
            width = width <= height ? width : height;
            mRadius = width / 2;
        }

        if (widthMode == MeasureSpec.AT_MOST || heightMode == MeasureSpec.AT_MOST) {
            width = mRadius * 2;
        }

        setMeasuredDimension(width, width);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (!hasMeasure) {
            hasMeasure = true;
            mWidth = w;
            mHeight = h;
            mMainWaveWidth = w / 2f;
            mSecondaryWaveWidth = mMainWaveWidth * 1.25f;
            mWaveHeight = h / 25f;
            mSpeed = mMainWaveWidth / 40f;
            mDy = h;
            initPoints();
            startAnim();
            Log.e(TAG, "onSizeChanged: " + mHeight);
        }
    }

    /**
     * 开启贝塞尔曲线的滑动
     */
    private void startAnim() {
        ValueAnimator valueAnimator = ValueAnimator.ofInt(0, 100);
        valueAnimator.setDuration(500);
        valueAnimator.setInterpolator(new LinearInterpolator());
        valueAnimator.setRepeatCount(ValueAnimator.INFINITE);
        valueAnimator.setRepeatMode(ValueAnimator.INFINITE);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mMainWaveDx = (int) (mMainWaveDx + mSpeed);
                mSecordaryDx = (int) (mSecordaryDx + mSpeed / 2);
                if (mMainWaveDx >= 2 * mMainWaveWidth) {
                    mMainWaveDx = 0;
                }

                if (mSecordaryDx >= 2 * mSecondaryWaveWidth) {
                    mSecordaryDx = 0;
                }

                updatePoints();
                postInvalidate();

            }

        });

        valueAnimator.start();
    }

    private void updatePoints() {
        //首先复位所有的点
        initPoints();

        for (Point p :
                mMainWavePoints) {
            p.x = p.x + mMainWaveDx;
        }
        for (Point p :
                mSecondaryPoints) {
            p.x = p.x + mSecordaryDx;
        }
    }

    private void initPoints() {
        mMainWavePoints.clear();
        mSecondaryPoints.clear();

        for (int i = 0; i < 5; i++) {
            Point mainWavePoint = new Point();
            mainWavePoint.y = mDy;
            mainWavePoint.x = (int) (mMainWaveWidth * (i - 2));
            mMainWavePoints.add(mainWavePoint);

            Point secondaryPoint = new Point();
            secondaryPoint.y = mDy;
            secondaryPoint.x = (int) (mSecondaryWaveWidth * (i - 2));
            mSecondaryPoints.add(secondaryPoint);
        }

    }

    Point wavePoint;


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        mMainPath.reset();
        mSecordaryPath.reset();
        //draw circle
        int saveFlags = Canvas.MATRIX_SAVE_FLAG | Canvas.CLIP_SAVE_FLAG | Canvas.HAS_ALPHA_LAYER_SAVE_FLAG | Canvas.FULL_COLOR_LAYER_SAVE_FLAG | Canvas.CLIP_TO_LAYER_SAVE_FLAG;
        //savelayer之后PorterDuffXfermode才正确使用
        canvas.saveLayer(0, 0, mWidth, mHeight, null, saveFlags);
        mPaint.setColor(mBgColor);
        canvas.drawCircle(mWidth / 2, mWidth / 2, mRadius, mPaint);

        for (int i = 0; i < mMainWavePoints.size(); i++) {
            wavePoint = mMainWavePoints.get(i);
            mMainPath.moveTo(wavePoint.x, mDy);
            if ((i + 1) % 2 == 0 && (i + 1) >= 2) {
                mMainPath.quadTo(wavePoint.x + mMainWaveWidth / 2, mDy - mWaveHeight, wavePoint.x + mMainWaveWidth, mDy);
            } else {
                mMainPath.quadTo(wavePoint.x + mMainWaveWidth / 2, mDy + mWaveHeight, wavePoint.x + mMainWaveWidth, mDy);
            }

        }
        for (int i = 0; i < mSecondaryPoints.size(); i++) {
            wavePoint = mSecondaryPoints.get(i);
            mSecordaryPath.moveTo(wavePoint.x, mDy);
            if ((i + 1) % 2 == 0 && (i + 1) >= 2) {
                mSecordaryPath.quadTo(wavePoint.x + mSecondaryWaveWidth / 2, mDy + mWaveHeight, wavePoint.x + mSecondaryWaveWidth, mDy);
            } else {
                mSecordaryPath.quadTo(wavePoint.x + mSecondaryWaveWidth / 2, mDy - mWaveHeight, wavePoint.x + mSecondaryWaveWidth, mDy);
            }

        }

        mMainPath.lineTo(mWidth, mHeight);
        mMainPath.lineTo(0, mHeight);
        mMainPath.lineTo(0, mDy);
        mMainPath.close();

        mSecordaryPath.lineTo(mWidth, mHeight);
        mSecordaryPath.lineTo(0, mHeight);
        mSecordaryPath.lineTo(0, mDy);
        mSecordaryPath.close();

        mPaint.setXfermode(mPorterDuffXfermode);
        mPaint.setColor(mWaveColor);
        mPaint.setAlpha(100);
        canvas.drawPath(mMainPath, mPaint);
        mPaint.setAlpha(70);
        canvas.drawPath(mSecordaryPath, mPaint);
        mPaint.setXfermode(null);

    }

    private int mLastDy;
    ValueAnimator ProgressvalueAnimator;
    public void setProgress(final float progress) {
        if (ProgressvalueAnimator != null && ProgressvalueAnimator.isRunning()){
            ProgressvalueAnimator.cancel();
        }
        //当次需要移动的大小
        int deltaHeight = mDy - (int) (mHeight *(1f- progress / mMaxProgress));
        //记录上次progress的位置
        mLastDy = mDy;
        ProgressvalueAnimator = ValueAnimator.ofInt(0, deltaHeight);
        ProgressvalueAnimator.setDuration(300);
        ProgressvalueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mDy = mLastDy - (int) animation.getAnimatedValue();
                if (progress == mMaxProgress) {
                    mDy = (int) -mWaveHeight;
                   // animation.end();
                }
            }
        });
        ProgressvalueAnimator.start();

    }

    public void setMaxProgress(int maxProgress) {
        mMaxProgress = maxProgress;
    }
}

