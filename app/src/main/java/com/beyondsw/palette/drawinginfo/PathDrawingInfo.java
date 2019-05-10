package com.beyondsw.palette.drawinginfo;

import android.graphics.Canvas;
import android.graphics.Path;
import android.os.Environment;
import android.util.Xml;

import com.beyondsw.palette.xmlparser.BaseParse;

import org.xmlpull.v1.XmlSerializer;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.List;

public  class PathDrawingInfo extends  DrawingInfo implements BaseParse {

  public  Path path;
  public List<Point> mPoints;

    @Override
    public void draw(Canvas canvas) {
        canvas.drawPath(path, paint);
    }

    @Override
    public Object parse(InputStream in) throws Exception {
        return null;
    }

    @Override
    public String serialize() throws Exception {

        XmlSerializer serializer = Xml.newSerializer(); //由android.util.Xml创建一个XmlSerializer实例
        File file = new File(Environment.getExternalStorageDirectory().getPath(), "handwriting0.xml");
        FileOutputStream outS = new FileOutputStream(file);
        serializer.setOutput(outS, "utf-8");
        serializer.startDocument("utf-8", true);//文档开头，true表示独立，不需要其他文件依赖
        // serilizer.startTag(null, "smss");//设置节点，第一个是参数是命名空间，第二个是节点名
        StringWriter writer = new StringWriter();
        //serializer.setOutput(writer);   //设置输出方向为writer
        serializer.startDocument("UTF-8", true);

            serializer.startTag("", "pathdrawinginfo");
            // serializer.attribute("", "id", book.getId() + "");

            serializer.startTag("", "color");
            serializer.text(paint.getColor()+"");
            serializer.endTag("", "color");

            serializer.startTag("", "DrawSize");
            serializer.text(paint.getStrokeWidth()+"");
            serializer.endTag("", "DrawSize");

            serializer.startTag("", "Xfermode");
            serializer.text(paint.getXfermode()+"");
            serializer.endTag("", "Xfermode");

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

            serializer.endTag("", "pathdrawinginfo");

        serializer.endDocument();//文档结尾
        outS.close();
        return null;
    }
}