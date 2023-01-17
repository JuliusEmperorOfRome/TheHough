package com.example.excellentreader.hough.transform;
import java.util.Collections;
import java.util.ArrayList;

public class HoughLines {
  public static class Builder {
    private int theta, w, h;
    private double quant;
    
    public Builder() {
      theta = 0;
      w = 0;
      h = 0;
      quant = 1;
    }
    
    public Builder thetaStepCount(int tsc) {
      theta = tsc;
      return this;
    }
    
    public Builder width(int w_) {
      w = w_;
      return this;
    }
    
    public Builder height(int h_) {
      h = h_;
      return this;
    }
    
    public Builder quantisationFactor(double qF) {
      quant = qF;
      return this;
    }
    
    public HoughLines build() {
      if(theta <= 0) {
        throw new RuntimeException("HoughLines.Builder.build: must set theta step count(to a positive value)");
      }
      if(w <= 0) {
        throw new RuntimeException("HoughLines.Builder.build: must set width(to a positive value)");
      }
      if(h <= 0) {
        throw new RuntimeException("HoughLines.Builder.build: must set height(to a positive value)");
      }
      if(quant <= 0) {
        throw new RuntimeException("HoughLines.Builder.build: quantisation factor must be positive");
      }
      return new HoughLines(this);
    }
  }
  
  private TrigTable tt;
  private int houghDomain[/*angle*/][/*distance*/];
  private double quantisationFactor;
  private int halfWidth, halfHeight;

  private HoughLines(Builder bd) {
    this.tt = new TrigTable(bd.theta);
    this.halfWidth = (bd.w + 1) / 2; /*ceil div instead of floor div*/
    this.halfHeight = (bd.h + 1) / 2;
    this.quantisationFactor = bd.quant;
    
    double maxDist = Math.sqrt(halfWidth * halfWidth + halfHeight * halfHeight);
    this.houghDomain = new int[tt.getStepCount()][(int)Math.ceil(maxDist * quantisationFactor)];
  }
  
  //prepare for an image of the same size
  public void reset() {
    for(int i = 0; i < houghDomain.length; ++i) {
      for(int j = 0; j < houghDomain[i].length; ++i) {
        houghDomain[i][j] = 0;
      }
    }
  }

  public void addPoint(int x, int y) {
    for (int theta = 0; theta < tt.getStepCount(); ++theta) {
      int r = (int)(((x - halfWidth) * tt.cosAtStep(theta) + (y - halfHeight) * tt.sinAtStep(theta)) * quantisationFactor);

      if (r < 0 || r >= houghDomain[0].length) {/*can't store in argument domain*/}
      else {
        houghDomain[theta][r]++;
      }
    }
  }
  
  public AngleDistanceLineArray getLines() {
    return getLines(0.1);
  }
  
  public AngleDistanceLineArray getLines(double domainFraction) {
    int tenthTheta = (int)(houghDomain.length * domainFraction);
    int tenthDistance = (int)(houghDomain[0].length * domainFraction);
    return getLines(tenthTheta, tenthDistance);
  }

  public AngleDistanceLineArray getLines(int thetaLocalSize, int distanceLocalSize) {
    return getLines(thetaLocalSize, distanceLocalSize, 1);
  }

  public AngleDistanceLineArray getLines(int thetaLocalSize, int distanceLocalSize, int threshold) {
    ArrayList<AngleDistanceLine> lines = new ArrayList<AngleDistanceLine>();

    final int maxTheta = houghDomain.length;
    final int maxR = houghDomain[0].length;

    for (int theta = 0; theta < maxTheta; ++theta) {
      for (int r = 0; r < maxR; ++r) {

        final int value = houghDomain[theta][r];
        //not enough coresponding points to consider
        if (value < threshold) {
          continue;
        }
        
        if(isLocalMax(theta, r, thetaLocalSize, distanceLocalSize)) {
          lines.add(new AngleDistanceLine(theta, r / quantisationFactor, value));
        }
      }
    }

    Collections.sort(lines, Collections.reverseOrder());
    return new AngleDistanceLineArray(this.tt, lines.toArray(new AngleDistanceLine[lines.size()]));
  }

  public PointDirectionLine[] toPointDirectionLines(AngleDistanceLineArray lines) {
    if(this.tt != lines.trigTable) {
      throw new IllegalArgumentException("HoughLines.toPointDirectionLineArray: the AngleDistanceLineArray must have been constructed by the called HoughLines instance");
    }

    PointDirectionLine[] ret = new PointDirectionLine[lines.lines.length];
    for(int i = 0; i < ret.length; ++i) {
      AngleDistanceLine adLine = lines.lines[i];
      double cos = tt.cosAtStep(adLine.theta);
      double sin = tt.sinAtStep(adLine.theta);
      double x = halfWidth + cos * adLine.distance;
      double y = halfHeight + sin * adLine.distance;
      ret[i] = new PointDirectionLine(x, y, -sin, cos);
    }
    return ret;
  }

  private boolean isLocalMax(int theta, int r, int thetaLocalSize, int distanceLocalSize) {
    final int maxTheta = houghDomain.length;
    final int maxR = houghDomain[0].length;
    int value = houghDomain[theta][r];

    int rMin = (r - distanceLocalSize < 0)? 0 : (r - distanceLocalSize);
    int rMax = (r + distanceLocalSize >= maxR)? maxR - 1 : r + distanceLocalSize;

    for (int y = rMin; y <= rMax; ++y) {
      for (int xOffset = -thetaLocalSize; xOffset <= thetaLocalSize; ++xOffset) {
        int x = theta + xOffset;
        if (x >= maxTheta) {
          x -= maxTheta;
        } else if (x < 0) {
          x += maxTheta;
        }

        if (houghDomain[x][y] > value) {
          return false;
        }
      }
    }
    return true;
  }
}
