package com.beyondsw.palette.shape;

import android.graphics.Canvas;
import android.graphics.Paint;

public class CircleShape extends BaseShape {
    @Override
    public void draw(Canvas canvas, Paint mPaint) {
        double midpointx=(mLastX+mEndX)*0.5;
        double midpointy=(mLastY+mEndY)*0.5;
        double radius=0.5*Math.sqrt(Math.abs(mLastX-mEndX)*Math.abs(mLastX-mEndX)+Math.abs(mLastY-mEndY)*Math.abs(mLastY-mEndY));
        canvas.drawCircle((float)midpointx , (float)midpointy , (float)radius,mPaint);
    }
}
