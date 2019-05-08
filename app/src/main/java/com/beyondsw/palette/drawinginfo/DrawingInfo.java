package com.beyondsw.palette.drawinginfo;

import android.graphics.Canvas;
import android.graphics.Paint;

public  abstract class DrawingInfo {
    public Paint paint;
   public abstract void draw(Canvas canvas);
}
