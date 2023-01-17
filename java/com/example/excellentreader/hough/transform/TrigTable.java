package com.example.excellentreader.hough.transform;

public class TrigTable {
  private double[] sin;
  private int quarterStepCount;
  
  protected int actualStepCount(int suggestedCount) {
    return suggestedCount & ~3; // round down to multiple of 4
  }
  
  public TrigTable(int suggestedCount) {
    setStepCount(suggestedCount);
  }
  
  public int getStepCount() {
    return quarterStepCount * 4;
  }
  
  public double sinAtStep(int step) {
    return sin[step];
  }
  
  public double cosAtStep(int step) {
    return sin[step + quarterStepCount];
  }
  
  public double tanAtStep(int step) {
    return sinAtStep(step) / cosAtStep(step);
  }
  
  public double stepSize() {
    return Math.PI / (quarterStepCount * 2);
  }
  
  public int radiansToSteps(double rad) {
    return ((int)(rad / stepSize())) % getStepCount();
  }
  
  public double stepsToRadians(int step) {
    return step * stepSize();
  }
  
  public void setStepCount(int suggestedCount) {
    final int actualCount = actualStepCount(suggestedCount); 
    quarterStepCount = actualCount / 4;
    
    final int tableSize = quarterStepCount * 5;
    sin = new double[tableSize];
    
    for(int i = 0; i < sin.length; ++i) {
      sin[i] = Math.sin(stepsToRadians(i));
    }
    
  }
}
