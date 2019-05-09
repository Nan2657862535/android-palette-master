package com.beyondsw.palette.shape;

import android.graphics.Canvas;
import android.graphics.Paint;

public abstract class BaseShape {
    protected float mLastX;
    protected float mLastY;
    protected float mEndX;
    protected float mEndY;
    public abstract void draw(Canvas canvas, Paint mPaint);

    public void setLastX(float lastX) {
        mLastX = lastX;
    }

    public void setLastY(float lastY) {
        mLastY = lastY;
    }

    public void setEndX(float endX) {
        mEndX = endX;
    }

    public void setEndY(float endY) {
        mEndY = endY;
    }
}
