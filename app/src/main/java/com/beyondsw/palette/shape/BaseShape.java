package com.beyondsw.palette.shape;

import android.graphics.Canvas;
import android.graphics.Paint;

import com.beyondsw.palette.drawinginfo.DrawingInfo;

public class BaseShape extends DrawingInfo{
    protected float mLastX;
    protected float mLastY;
    protected float mEndX;
    protected float mEndY;

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

    @Override
    public void draw(Canvas canvas) {

    }
}
