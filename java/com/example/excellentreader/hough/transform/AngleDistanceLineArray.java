package com.example.excellentreader.hough.transform;

public class AngleDistanceLineArray {
  public final TrigTable trigTable;
  public final AngleDistanceLine[] lines;

  public AngleDistanceLineArray(TrigTable tt, AngleDistanceLine[] lines) {
    this.trigTable = tt;
    this.lines = lines;
  }
}
