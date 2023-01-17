package com.example.excellentreader.hough.transform;

public class AngleDistanceLine implements Comparable<AngleDistanceLine> {
  public final int theta;
  public final double distance;
  public final int score;

  public AngleDistanceLine(int theta, double distance, int score) {
    this.theta = theta;
    this.distance = distance;
    this.score = score;
  }

  public int compareTo(AngleDistanceLine ln) {
    return score - ln.score;
  }
}
