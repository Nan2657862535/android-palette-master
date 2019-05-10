package com.beyondsw.palette.shape;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Environment;
import android.util.Xml;

import com.beyondsw.palette.drawinginfo.Point;

import org.xmlpull.v1.XmlSerializer;

import java.io.File;
import java.io.FileOutputStream;
import java.io.StringWriter;

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
    @Override
    public String serialize(XmlSerializer serializer) throws Exception {

        //XmlSerializer serializer = Xml.newSerializer(); //由android.util.Xml创建一个XmlSerializer实例
        File file = new File(Environment.getExternalStorageDirectory().getPath(), "handwriting1.xml");
        //FileOutputStream outS = new FileOutputStream(file,true);
        //serializer.setOutput(outS, "utf-8");
        //serializer.startDocument("utf-8", true);//文档开头，true表示独立，不需要其他文件依赖
        // serilizer.startTag(null, "smss");//设置节点，第一个是参数是命名空间，第二个是节点名
        StringWriter writer = new StringWriter();
        //serializer.setOutput(writer);   //设置输出方向为writer
       // serializer.startDocument("UTF-8", true);

        serializer.startTag("", "circle");
        // serializer.attribute("", "id", book.getId() + "");

        serializer.startTag("", "color");
        serializer.text(paint.getColor()+"");
        serializer.endTag("", "color");

        serializer.startTag("", "DrawSize");
        serializer.text(paint.getStrokeWidth()+"");
        serializer.endTag("", "DrawSize");

        serializer.startTag("", "midpointx");
        serializer.text(midpointx+"");
        serializer.endTag("", "midpointx");

        serializer.startTag("", "midpointy");
        serializer.text(midpointy+"");
        serializer.endTag("", "midpointy");

        serializer.startTag("", "radius");
        serializer.text(radius+"");
        serializer.endTag("", "radius");

        serializer.endTag("", "circle");

        serializer.endDocument();//文档结尾
       // outS.close();
        return null;
    }
}
