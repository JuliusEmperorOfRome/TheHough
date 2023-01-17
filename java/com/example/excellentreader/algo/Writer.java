package com.example.excellentreader.algo;

public class Writer {
    //should be overridden
    protected String getTextBoundedBy(Rect r) {
        return "XXX";
    }
    public String writeToString(Algo.Rects rects) {
        String output = "";
        for(int i = 0; i < rects.rects.size(); ++i) {
            if(i != 0 && i % rects.nRectsInLine == 0) {
                output += '\n';
            }
            output += getTextBoundedBy(rects.rects.get(i));
            output += '\t';
        }
        return output;
    }
}
