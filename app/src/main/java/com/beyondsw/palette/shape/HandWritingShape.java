package com.beyondsw.palette.shape;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Xfermode;

import com.beyondsw.palette.PaletteView;
import com.beyondsw.palette.drawinginfo.DrawingInfo;

import java.util.List;

public class HandWritingShape extends BaseShape {
    private Bitmap mBufferBitmap;
    private Canvas mBufferCanvas;
    private Path mPath;
    private static final int MAX_CACHE_STEP = 20;

    private List<DrawingInfo> mDrawingList;
    private List<DrawingInfo> mRemovedList;

    private Xfermode mXferModeClear;
    private Xfermode mXferModeDraw;

    private PaletteView.Mode mMode = PaletteView.Mode.DRAW;
    public enum Mode {
        DRAW,
        ERASER
    }

    public HandWritingShape(){
        if (mPath == null) {
            mPath = new Path();
        }
    }
}
