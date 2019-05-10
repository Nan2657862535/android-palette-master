package com.beyondsw.palette.shape;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Environment;
import android.util.Xml;

import org.xmlpull.v1.XmlSerializer;

import java.io.File;
import java.io.FileOutputStream;
import java.io.StringWriter;

public class LineShape extends BaseShape {
    @Override
    public void draw(Canvas canvas) {
        canvas.drawLine(mLastX,mLastY,mEndX,mEndY,paint);
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
        //serializer.startDocument("UTF-8", true);

        serializer.startTag("", "line");
        // serializer.attribute("", "id", book.getId() + "");

        serializer.startTag("", "color");
        serializer.text(paint.getColor()+"");
        serializer.endTag("", "color");

        serializer.startTag("", "DrawSize");
        serializer.text(paint.getStrokeWidth()+"");
        serializer.endTag("", "DrawSize");

        serializer.startTag("", "mLastX");
        serializer.text(mLastX+"");
        serializer.endTag("", "mLastX");

        serializer.startTag("", "mLastY");
        serializer.text(mLastY+"");
        serializer.endTag("", "mLastY");

        serializer.startTag("", "mEndX");
        serializer.text(mEndX+"");
        serializer.endTag("", "mEndX");

        serializer.startTag("", "mEndY");
        serializer.text(mEndY+"");
        serializer.endTag("", "mEndY");


        serializer.endTag("", "line");

        serializer.endDocument();//文档结尾
       // outS.close();
        return null;
    }
}
