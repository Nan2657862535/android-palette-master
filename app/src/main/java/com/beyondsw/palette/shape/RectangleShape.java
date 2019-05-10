package com.beyondsw.palette.shape;

import android.graphics.Canvas;
import android.graphics.Paint;

public class RectangleShape extends BaseShape {

    public void draw(Canvas canvas) {
        canvas.drawRect(mLastX , mLastY , mEndX , mEndY , paint);
    }
}
