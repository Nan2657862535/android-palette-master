package com.beyondsw.palette.xmlparser;

import android.content.res.XmlResourceParser;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;

import android.graphics.Xfermode;
import android.os.Environment;
import android.util.Xml;

import com.beyondsw.palette.drawinginfo.DrawingInfo;
import com.beyondsw.palette.drawinginfo.Point;
import com.beyondsw.palette.drawinginfo.PathDrawingInfo;
import com.beyondsw.palette.shape.BaseShape;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlSerializer;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

public class PullPathDrawingInfoParser implements BaseParse{
   /*
   *    ---- 解析xml
    */
   @Override
    public List<PathDrawingInfo> parse(InputStream in) throws Exception {
/*
* // path路径根据实际项目修改，此次获取SDcard根目录
String path = Environment.getExternalStorageDirectory().toString();
File xmlFlie = new File(path+fileName);
InputStream inputStream = new FileInputStream(xmlFlie);

* */
        List<DrawingInfo> mDrawingInfos = null;
        List<Point> mPoints=new ArrayList<>();
        PathDrawingInfo mPathDrawingInfo=null;
        // XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
        // XmlPullParser parser = factory.newPullParser();
        XmlPullParser parser = Xml.newPullParser(); // 由android.util.Xml创建一个XmlPullParser实例
        parser.setInput(in, "UTF-8"); // 设置输入流 并指明编码方式
        int eventType = parser.getEventType();
        List<Point> mPointList=new ArrayList<>();
        float x = 0,y=0;
       Path mPath;
       Paint mPaint;
        while (eventType != XmlPullParser.END_DOCUMENT) {
            switch (eventType) {
                case XmlPullParser.START_DOCUMENT:
                    mDrawingInfos = new ArrayList<DrawingInfo>();
                    break;
                case XmlPullParser.START_TAG:
                    String shapeMode=parser.getName();
                    switch (shapeMode){
                        case "pathDrawingInfo":
                            mPathDrawingInfo=new PathDrawingInfo();
                            break;

                    }
                    if (parser.getName().equals("pathDrawingInfo")) {
                        mPathDrawingInfo=new PathDrawingInfo();
                    } else if (parser.getName().equals("color")) {
                        eventType = parser.next();
                        mPathDrawingInfo.paint.setColor(Integer.parseInt(parser.getText()));
                    } else if (parser.getName().equals("DrawSize")){
                        mPathDrawingInfo.paint.setStrokeWidth(Float.parseFloat(parser.getText()));
                    }else if (parser.getName().equals("Xfermode")){
                        //mPathDrawingInfo.paint.setXfermode((Xfermode)parser.getText());
                    }else if (parser.getName().contains("pointx")){
                        x=Float.parseFloat(parser.getText());

                    } else if (parser.getName().contains("pointy")){
                        y=Float.parseFloat(parser.getText());
                        mPointList.add(new Point(x,y));
                    }
                    break;
                case XmlPullParser.END_TAG:
                    if (parser.getName().equals("drawinginfo")) {
                        if (mPointList.size()!=0){
                            mPath=pointsToPath(mPointList);
                            mPathDrawingInfo.path=mPath;
                            mPathDrawingInfos.add(mPathDrawingInfo);
                            mPointList.clear();
                        }
                        mPathDrawingInfo = null;
                    }
                    break;
            }
            eventType = parser.next();
        }
        return mPathDrawingInfos;
    }

    @Override
    public String serialize() throws Exception {
        return null;
    }

    private Path pointsToPath(List<Point> pointList) {
       Path mPath=new Path();
       float mLastX=pointList.get(0).x;
       float mLastY=pointList.get(0).y;
        mPath.moveTo(pointList.get(0).x,pointList.get(0).y);
        for (Point point:pointList
             ) {
            mPath.quadTo(mLastX, mLastY, (point.x + mLastX) / 2, (point.y + mLastY) / 2);
            mLastX=point.x;
            mLastY=point.y;
        }
        return mPath;
    }


    /***
     *   ---- 生成xml
     */


    public String serialize(Object mDrawingListObject) throws Exception {
//      XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
//      XmlSerializer serializer = factory.newSerializer();
        List<PathDrawingInfo> mDrawingList=(List<PathDrawingInfo>)mDrawingListObject;
        XmlSerializer serializer = Xml.newSerializer(); //由android.util.Xml创建一个XmlSerializer实例
        File file = new File(Environment.getExternalStorageDirectory().getPath(), "handwriting0.xml");
        FileOutputStream outS = new FileOutputStream(file);
        serializer.setOutput(outS, "utf-8");
        serializer.startDocument("utf-8", true);//文档开头，true表示独立，不需要其他文件依赖
        // serilizer.startTag(null, "smss");//设置节点，第一个是参数是命名空间，第二个是节点名
        StringWriter writer = new StringWriter();
        //serializer.setOutput(writer);   //设置输出方向为writer
        serializer.startDocument("UTF-8", true);
        serializer.startTag("", "drawinglist");
        for (PathDrawingInfo mDrawingInfo : mDrawingList) {
            serializer.startTag("", "drawinginfo");
           // serializer.attribute("", "id", book.getId() + "");

            serializer.startTag("", "color");
            serializer.text(mDrawingInfo.paint.getColor()+"");
            serializer.endTag("", "color");

            serializer.startTag("", "DrawSize");
            serializer.text(mDrawingInfo.paint.getStrokeWidth()+"");
            serializer.endTag("", "DrawSize");

            serializer.startTag("", "Xfermode");
            serializer.text(mDrawingInfo.paint.getXfermode()+"");
            serializer.endTag("", "Xfermode");
            List<Point> mPoints=getpoints(mDrawingInfo);
            int num=0;
            for (Point point:mPoints) {
                serializer.startTag("", "pointx"+num);
                serializer.text(point.x + "");
                serializer.endTag("", "pointx"+num);

                serializer.startTag("", "pointy"+num);
                serializer.text(point.y + "");
                serializer.endTag("", "pointy"+num);
                num++;
            }

            serializer.endTag("", "drawinginfo");
        }
        serializer.endTag("", "drawinglist");
        serializer.endDocument();//文档结尾
         outS.close();
        return writer.toString();
    }

    private List<Point> getpoints(PathDrawingInfo pathDrawingInfo) {
        PathMeasure mPathMeasure = null;
        float mAnimatorValue;
        float[] pos = new float[2];
        float[] tan=new float[2];
        float mLength;
        Matrix mMatrix = null;
        Paint mPaint=pathDrawingInfo.paint;
        Path mPath=pathDrawingInfo.path;
        //PathMeasure
        mPathMeasure = new PathMeasure(mPath, false);
        mLength = mPathMeasure.getLength();
        //得到矩阵
        //mPathMeasure.getMatrix(mLength, mMatrix, PathMeasure.POSITION_MATRIX_FLAG);

        float distance=0;
        List<Point> mPoints=new ArrayList<>();
        float accuracy=0.5f;
        while (distance<mLength){
            //获取在动画某一个时刻点的坐标及正切值
            mPathMeasure.getPosTan(distance ,pos,tan);
            Point mPoint=new Point(pos[0],pos[1]);
            mPoints.add(mPoint);
            distance+=accuracy;
        }

        return mPoints;
    }

}
