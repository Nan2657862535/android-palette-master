package com.beyondsw.palette;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Xfermode;
import android.os.Build;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;

import com.beyondsw.palette.Utils.DimenUtils;
import com.beyondsw.palette.drawinginfo.DrawingInfo;
import com.beyondsw.palette.drawinginfo.PathDrawingInfo;
import com.beyondsw.palette.drawinginfo.Point;
import com.beyondsw.palette.shape.BaseShape;
import com.beyondsw.palette.shape.CircleShape;
import com.beyondsw.palette.shape.LineShape;
import com.beyondsw.palette.shape.RectangleShape;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wensefu on 17-3-21.
 */
public class PaletteView extends View {

    private Paint mPaint;
    private Path mPath;
    private float mLastX;
    private float mLastY;
    private float mEndX;
    private float mEndY;
    private Bitmap mBufferBitmap;
    private Canvas mBufferCanvas;
    private BaseShape mBaseShape;

    private static final int MAX_CACHE_STEP = 20;

    private List<DrawingInfo> mDrawingList;
    private List<DrawingInfo> mRemovedList;

    private Xfermode mXferModeClear;
    private Xfermode mXferModeDraw;
    private int mDrawSize;
    private int mEraserSize;
    private int mPenAlpha = 255;

    private boolean mCanEraser;

    private Callback mCallback;

    public enum Mode {
        DRAW,
        ERASER
    }

    public enum ShapeMode{
        LINE,
        CIRCLE,
        RECTANGLE,
        HANDWRITING
    }

    private Mode mMode = Mode.DRAW;
    private ShapeMode mShapeMode=ShapeMode.HANDWRITING;

    public void setShapeMode(ShapeMode shapeMode) {
        mShapeMode = shapeMode;
    }

    public PaletteView(Context context) {
        super(context);
        init();
    }

    public PaletteView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public PaletteView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public List<DrawingInfo> getDrawingList() {
        return mDrawingList;
    }

    public interface Callback {
        void onUndoRedoStatusChanged();
    }

    public void setCallback(Callback callback){
        mCallback = callback;
    }

    public void setDrawingList(List<PathDrawingInfo> drawingList) {
        mDrawingList.clear();
        for (PathDrawingInfo mPathDrawingInfo:drawingList
             ) {
            mDrawingList.add(mPathDrawingInfo);
            mBufferCanvas.drawPath(mPathDrawingInfo.path,mPathDrawingInfo.paint);
        }
    }

    private void init() {
        setDrawingCacheEnabled(true);
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setFilterBitmap(true);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mDrawSize = DimenUtils.dp2pxInt(3);
        mEraserSize = DimenUtils.dp2pxInt(30);
        mPaint.setStrokeWidth(mDrawSize);
        mPaint.setColor(0XFF000000);
        mXferModeDraw = new PorterDuffXfermode(PorterDuff.Mode.SRC);
        mXferModeClear = new PorterDuffXfermode(PorterDuff.Mode.CLEAR);
        mPaint.setXfermode(mXferModeDraw);
    }

    private void initBuffer(){
        mBufferBitmap = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
        mBufferCanvas = new Canvas(mBufferBitmap);
    }




    public Mode getMode() {
        return mMode;
    }

    public void setMode(Mode mode) {
        if (mode != mMode) {
            mMode = mode;
            if (mMode == Mode.DRAW) {
                mPaint.setXfermode(mXferModeDraw);
                mPaint.setStrokeWidth(mDrawSize);
            } else {
                mPaint.setXfermode(mXferModeClear);
                mPaint.setStrokeWidth(mEraserSize);
            }
        }
    }


    public void setEraserSize(int size) {
        mEraserSize = size;
    }

    public void setPenRawSize(int size) {
        mDrawSize = size;
        if(mMode == Mode.DRAW){
            mPaint.setStrokeWidth(mDrawSize);
        }
    }

    public void setPenColor(int color) {
        mPaint.setColor(color);
    }

    private void reDraw(){
        if (mDrawingList != null) {
            mBufferBitmap.eraseColor(Color.TRANSPARENT);
            for (DrawingInfo drawingInfo : mDrawingList) {
                drawingInfo.draw(mBufferCanvas);
            }
            invalidate();
        }
    }

    public int getPenColor(){
        return mPaint.getColor();
    }

    public int getPenSize(){
        return mDrawSize;
    }

    public int getEraserSize(){
        return mEraserSize;
    }

    public void setPenAlpha(int alpha){
        mPenAlpha = alpha;
        if(mMode == Mode.DRAW){
            mPaint.setAlpha(alpha);
        }
    }

    public int getPenAlpha(){
        return mPenAlpha;
    }

    public boolean canRedo() {
        return mRemovedList != null && mRemovedList.size() > 0;
    }

    public boolean canUndo(){
        return mDrawingList != null && mDrawingList.size() > 0;
    }

    public void redo() {
        int size = mRemovedList == null ? 0 : mRemovedList.size();
        if (size > 0) {
            DrawingInfo info = mRemovedList.remove(size - 1);
            mDrawingList.add(info);
            mCanEraser = true;
            reDraw();
            if (mCallback != null) {
                mCallback.onUndoRedoStatusChanged();
            }
        }
    }

    public void undo() {
        int size = mDrawingList == null ? 0 : mDrawingList.size();
        if (size > 0) {
            DrawingInfo info = mDrawingList.remove(size - 1);
            if (mRemovedList == null) {
                mRemovedList = new ArrayList<>(MAX_CACHE_STEP);
            }
            if (size == 1) {
                mCanEraser = false;
            }
            mRemovedList.add(info);
            reDraw();
            if (mCallback != null) {
                mCallback.onUndoRedoStatusChanged();
            }
        }
    }

    public void clear() {
        if (mBufferBitmap != null) {
            if (mDrawingList != null) {
                mDrawingList.clear();
            }
            if (mRemovedList != null) {
                mRemovedList.clear();
            }
            mCanEraser = false;
            mBufferBitmap.eraseColor(Color.TRANSPARENT);
            invalidate();
            if (mCallback != null) {
                mCallback.onUndoRedoStatusChanged();
            }
        }
    }

    public Bitmap buildBitmap() {
        Bitmap bm = getDrawingCache();
        Bitmap result = Bitmap.createBitmap(bm);
        destroyDrawingCache();
        return result;
    }

    private void saveDrawingPath(){
        if (mDrawingList == null) {
            mDrawingList = new ArrayList<>(MAX_CACHE_STEP);
        } else if (mDrawingList.size() == MAX_CACHE_STEP) {
            mDrawingList.remove(0);
        }
        if (mShapeMode!=ShapeMode.HANDWRITING){
            mBaseShape.draw(mBufferCanvas);
            mDrawingList.add(mBaseShape);
            return;
        }

        Path cachePath = new Path(mPath);
        Paint cachePaint = new Paint(mPaint);
        PathDrawingInfo info = new PathDrawingInfo();
        info.path = cachePath;
        info.paint = cachePaint;
        info.mPoints=mPoints;
        mPoints.clear();
        info.setShapeMode("pathDrawingInfo");
        mDrawingList.add(info);
        mCanEraser = true;
        if (mCallback != null) {
            mCallback.onUndoRedoStatusChanged();
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        switch (mShapeMode){
            case LINE:
            case CIRCLE:
            case RECTANGLE:
                mBaseShape.paint=mPaint;
                mBaseShape.draw(canvas);
                break;
        }

        if (mBufferBitmap != null) {
            canvas.drawBitmap(mBufferBitmap, 0, 0, null);
        }

    }

    private List<Point> mPoints=new ArrayList<>();
    @SuppressWarnings("all")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if(!isEnabled()){
            return false;
        }
        final int action = event.getAction() & MotionEvent.ACTION_MASK;
        final float x = event.getX();
        final float y = event.getY();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                mLastX = x;
                mLastY = y;
                switch (mShapeMode){
                    case LINE:
                        mBaseShape=new LineShape();
                        mBaseShape.setShapeMode("line");
                        break;
                    case CIRCLE:
                        mBaseShape=new CircleShape();
                        mBaseShape.setShapeMode("circle");
                        break;
                    case RECTANGLE:
                        mBaseShape=new RectangleShape();
                        mBaseShape.setShapeMode("rectangle");
                        break;
                    case HANDWRITING:
                        if (mPath == null) {
                            mPath = new Path();
                        }
                        mPath.moveTo(x,y);
                        mPoints.add(new Point(x,y));
                        break;
                }
                //if (mBaseShape==null) break;
                if (mShapeMode!=ShapeMode.HANDWRITING){
                    mBaseShape.setLastX(mLastX);
                    mBaseShape.setLastY(mLastY);
                }


               /* mLastX = x;
                mLastY = y;
                if (mPath == null) {
                    mPath = new Path();
                }
                mPath.moveTo(x,y);*/
                break;
            case MotionEvent.ACTION_MOVE:
                mEndX=x;
                mEndY=y;
                if (mBufferBitmap == null) {
                    initBuffer();
                }
                switch (mShapeMode){
                    case LINE:
                    case RECTANGLE:
                    case CIRCLE:
                        mBaseShape.setEndX(mEndX);
                        mBaseShape.setEndY(mEndY);
                        break;
                    case HANDWRITING:
                        //这里终点设为两点的中心点的目的在于使绘制的曲线更平滑，如果终点直接设置为x,y，效果和lineto是一样的,实际是折线效果
                        mPath.quadTo(mLastX, mLastY, (x + mLastX) / 2, (y + mLastY) / 2);
                        mPoints.add(new Point(x,y));
                        if (mMode == Mode.ERASER && !mCanEraser) {
                            break;
                        }
                        mBufferCanvas.drawPath(mPath,mPaint);

                        mLastX = x;
                        mLastY = y;
                        break;
                }
                invalidate();
                break;

               /* mEndX=x;
                mEndY=y;
                //这里终点设为两点的中心点的目的在于使绘制的曲线更平滑，如果终点直接设置为x,y，效果和lineto是一样的,实际是折线效果
                mPath.quadTo(mLastX, mLastY, (x + mLastX) / 2, (y + mLastY) / 2);
                if (mBufferBitmap == null) {
                    initBuffer();
                }
                if (mMode == Mode.ERASER && !mCanEraser) {
                    break;
                }
                mBufferCanvas.drawPath(mPath,mPaint);
                invalidate();
                mLastX = x;
                mLastY = y;
                break;*/
            case MotionEvent.ACTION_UP:
                if (mShapeMode!=ShapeMode.HANDWRITING){
                    mBaseShape.setEndX(mEndX);
                    mBaseShape.setEndY(mEndY);
                }
                    if (mMode == Mode.DRAW || mCanEraser) {
                        saveDrawingPath();
                    }
                    if (mShapeMode==ShapeMode.HANDWRITING)
                        mPath.reset();
                break;
        }
        return true;
    }
}
