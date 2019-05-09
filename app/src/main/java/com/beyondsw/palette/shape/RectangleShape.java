package com.beyondsw.palette.shape;

import android.graphics.Canvas;
import android.graphics.Paint;

public class RectangleShape extends BaseShape {
    @Override
    public void draw(Canvas canvas, Paint mPaint) {
        canvas.drawRect(mLastX , mLastY , mEndX , mEndY , mPaint);
    }
}
