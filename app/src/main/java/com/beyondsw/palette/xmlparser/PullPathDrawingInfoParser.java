package com.beyondsw.palette.xmlparser;

import android.content.res.XmlResourceParser;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;

import android.util.Xml;

import com.beyondsw.palette.drawinginfo.Point;
import com.beyondsw.palette.drawinginfo.PathDrawingInfo;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlSerializer;

import java.io.InputStream;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

public class PullPathDrawingInfoParser {
   /*
   *    ---- 解析xml
    */
    public List<PathDrawingInfo> parse(InputStream in) throws Exception {
/*
* // path路径根据实际项目修改，此次获取SDcard根目录
String path = Environment.getExternalStorageDirectory().toString();
File xmlFlie = new File(path+fileName);
InputStream inputStream = new FileInputStream(xmlFlie);

* */
        List<PathDrawingInfo> mPathDrawingInfos = null;
        List<Point> mPoints=new ArrayList<>();
        PathDrawingInfo mPathDrawingInfo=null;
        // XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
        // XmlPullParser parser = factory.newPullParser();
        XmlPullParser parser = Xml.newPullParser(); // 由android.util.Xml创建一个XmlPullParser实例
        parser.setInput(in, "UTF-8"); // 设置输入流 并指明编码方式
        int eventType = parser.getEventType();
        while (eventType != XmlPullParser.END_DOCUMENT) {
            switch (eventType) {
                case XmlPullParser.START_DOCUMENT:
                    mPathDrawingInfos = new ArrayList<PathDrawingInfo>();
                    break;
                case XmlPullParser.START_TAG:
                    if (parser.getName().equals("drawinginfo")) {
                        mPathDrawingInfo=new PathDrawingInfo();
                    } else if (parser.getName().equals("color")) {
                        eventType = parser.next();
                        mPathDrawingInfo.paint.setColor(Integer.parseInt(parser.getText()));
                    }else if (parser.getName().contains("pointx")){

                    }
                    break;
                case XmlPullParser.END_TAG:
                    if (parser.getName().equals("drawinginfo")) {
                        mPathDrawingInfos.add(mPathDrawingInfo);
                        mPathDrawingInfo = null;
                    }
                    break;
            }
            eventType = parser.next();
        }
        return mPathDrawingInfos;
    }

    /***
     *   ---- 生成xml
     */

    public String serialize(List<PathDrawingInfo> mDrawingList) throws Exception {
//      XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
//      XmlSerializer serializer = factory.newSerializer();

        XmlSerializer serializer = Xml.newSerializer(); //由android.util.Xml创建一个XmlSerializer实例
        StringWriter writer = new StringWriter();
        serializer.setOutput(writer);   //设置输出方向为writer
        serializer.startDocument("UTF-8", true);
        serializer.startTag("", "drawinglist");
        for (PathDrawingInfo mDrawingInfo : mDrawingList) {
            serializer.startTag("", "drawinginfo");
           // serializer.attribute("", "id", book.getId() + "");

            serializer.startTag("", "color");
            serializer.text(mDrawingInfo.paint.getColor()+"");
            serializer.endTag("", "color");

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
        serializer.endDocument();

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
        mPathMeasure.getMatrix(mLength, mMatrix, PathMeasure.POSITION_MATRIX_FLAG);

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
