package com.example.excellentreader.hough.transform;

public class PointDirectionLine {
  public final double x, y;
  public final double dirVecX, dirVecY;

  public PointDirectionLine(double x_, double y_, double dirX, double dirY) {
    x = x_;
    y = y_;
    dirVecX = dirX;
    dirVecY = dirY;
  }
}
