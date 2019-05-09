package com.beyondsw.palette.xmlparser;

import com.beyondsw.palette.drawinginfo.PathDrawingInfo;

import java.io.InputStream;
import java.util.List;

public abstract class BaseParse {
    public abstract Object parse(InputStream in)throws Exception;
    public abstract String serialize(Object mDrawingList) throws Exception;
}
