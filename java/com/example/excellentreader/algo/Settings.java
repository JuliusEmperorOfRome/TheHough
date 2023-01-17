package com.example.excellentreader.algo;

public class Settings {
  public int maxTiltDegrees;
  public int minPointsInLine;
  public int imageWidth;
  public int imageHeight;
  public int distanceNeigbourhoodSize;
  
  public static Settings defaultSettings(int w, int h) {
    Settings s = new Settings();
    
    s.maxTiltDegrees = 15;
    
    int minDim = w > h ? h : w;
    s.minPointsInLine = minDim / 2;
    
    s.imageWidth = w;
    s.imageHeight = h;
    
    return s;
  }
}
