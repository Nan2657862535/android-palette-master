package com.beyondsw.palette.xmlparser;

import com.beyondsw.palette.drawinginfo.PathDrawingInfo;

import java.io.InputStream;
import java.util.List;

public abstract interface BaseParse {
    public abstract Object parse(InputStream in)throws Exception;
    public abstract String serialize() throws Exception;
}
