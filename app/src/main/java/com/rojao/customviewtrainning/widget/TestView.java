package com.rojao.customviewtrainning.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by lsc on 2016/10/24 0024.
 *
 * @author lsc
 * @desc ${TODO}
 */
public class TestView extends View {
    private Paint mPaint;
    private PorterDuffXfermode mPorterDuffXfermode = new PorterDuffXfermode(PorterDuff.Mode.SRC_OUT);

    public TestView(Context context) {
        super(context);
        init();
    }

    private void init() {
        mPaint = new Paint();
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setAntiAlias(true);
        setLayerType(LAYER_TYPE_SOFTWARE, null);
    }

    public TestView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public TestView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int saveFlags = Canvas.MATRIX_SAVE_FLAG | Canvas.CLIP_SAVE_FLAG | Canvas.HAS_ALPHA_LAYER_SAVE_FLAG | Canvas.FULL_COLOR_LAYER_SAVE_FLAG | Canvas.CLIP_TO_LAYER_SAVE_FLAG;
        canvas.saveLayer(0, 0, getMeasuredWidth(), getMeasuredHeight(), null, saveFlags);
        canvas.drawARGB(255, 139, 197, 186);
        mPaint.setColor(Color.GREEN);
        canvas.drawRect(0,0,getMeasuredWidth()/2,getMeasuredHeight()/2,mPaint);
        mPaint.setXfermode(mPorterDuffXfermode);
        mPaint.setColor(Color.YELLOW);
        canvas.drawRect(0,getMeasuredHeight()/4,getMeasuredWidth()/2,getMeasuredHeight()*0.75f,mPaint);
//        mPaint.setXfermode(null);

    }

}
