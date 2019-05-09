package com.beyondsw.palette.shape;

import android.graphics.Canvas;
import android.graphics.Paint;

public class LineShape extends BaseShape {
    @Override
    public void draw(Canvas canvas, Paint mPaint) {
        canvas.drawLine(mLastX,mLastY,mEndX,mEndY,mPaint);
    }
}
