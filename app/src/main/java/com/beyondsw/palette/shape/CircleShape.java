package com.beyondsw.palette.shape;

import android.graphics.Canvas;
import android.graphics.Paint;

public class CircleShape extends BaseShape {
    float midpointx;
    float midpointy;
    float radius;
    @Override
    public void draw(Canvas canvas) {
        midpointx=(mLastX+mEndX)/2;
        midpointy=(mLastY+mEndY)/2;
        radius=(float) (0.5*Math.sqrt(Math.abs(mLastX-mEndX)*Math.abs(mLastX-mEndX)+Math.abs(mLastY-mEndY)*Math.abs(mLastY-mEndY)));
        canvas.drawCircle(midpointx , midpointy , radius,paint);
    }
}
