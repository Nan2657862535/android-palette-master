package com.beyondsw.palette.shape;

import android.graphics.Canvas;
import android.graphics.Paint;

import com.beyondsw.palette.drawinginfo.DrawingInfo;

import org.xmlpull.v1.XmlSerializer;

import java.io.FileOutputStream;

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

    @Override
    public String serialize(XmlSerializer serializer) throws Exception {
        return null;
    }

}
