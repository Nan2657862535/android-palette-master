package com.beyondsw.palette.drawinginfo;

import android.graphics.Canvas;
import android.graphics.Paint;

import com.beyondsw.palette.xmlparser.BaseParse;

import org.xmlpull.v1.XmlSerializer;

import java.io.FileOutputStream;

public  abstract class DrawingInfo {
    private String shapeMode;

    public String getShapeMode() {
        return shapeMode;
    }

    public void setShapeMode(String shapeMode) {
        this.shapeMode = shapeMode;
    }

    public Paint paint;
   public abstract void draw(Canvas canvas);
    public abstract String serialize(XmlSerializer serializer)throws Exception;
}
