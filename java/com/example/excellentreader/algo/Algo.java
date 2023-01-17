package com.example.excellentreader.algo;

import com.example.excellentreader.hough.transform.HoughLines;
import com.example.excellentreader.hough.transform.PointDirectionLine;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class Algo {
  private final HoughLines hl;
  private final Settings settings;

  public Algo(Settings s) {
    this.settings = s;
    if (this.settings.maxTiltDegrees > 45) {
      this.settings.maxTiltDegrees = 45;
    }
    this.hl = new HoughLines.Builder().thetaStepCount(80).width(s.imageWidth).height(s.imageHeight).build();
  }

  public Algo(int w, int h) {
    this(Settings.defaultSettings(w, h));
  }

  public HoughLines getHoughLines() {
    return hl;
  }

  private static class Point {
    public double x, y;
  }

  private Point intersect(PointDirectionLine a, PointDirectionLine b) {
    //BEGIN http://www.java2s.com/example/java-utility-method/line-intersect/intersection-line2d-a-line2d-b-94184.html
    double x1 = a.x, y1 = a.y, x2 = a.x + a.dirVecX, y2 = a.y + a.dirVecY;
    double x3 = b.x, y3 = b.y, x4 = b.x + b.dirVecX, y4 = b.y + b.dirVecY;
    double d = (x1 - x2) * (y3 - y4) - (y1 - y2) * (x3 - x4);
    
    /*assume never happens*/
    /* if (d == 0) { return null; } */

    double xi = ((x3 - x4) * (x1 * y2 - y1 * x2) - (x1 - x2) * (x3 * y4 - y3 * x4)) / d;
    double yi = ((y3 - y4) * (x1 * y2 - y1 * x2) - (y1 - y2) * (x3 * y4 - y3 * x4)) / d;
    //END
    
    Point p = new Point();
    p.x = xi;
    p.y = yi;
    return p;
  }


  public static class Rects {
    ArrayList<Rect> rects;
    int nRectsInLine;
  }
  public Rects getRects() {
    PointDirectionLine[] lines = hl.toPointDirectionLines(hl.getLines(8, 15, settings.minPointsInLine));
    double cos = Math.cos(Math.toRadians((double)settings.maxTiltDegrees));

    ArrayList<PointDirectionLine> verticalLines = new ArrayList<>();
    ArrayList<PointDirectionLine> horizontalLines = new ArrayList<>();

    for (PointDirectionLine line : lines) {
      if (Math.abs(line.dirVecY) > cos) {
        verticalLines.add(line);
      } else if (Math.abs(line.dirVecX) > cos) {
        horizontalLines.add(line);
      }
    }

    verticalLines.sort(Comparator.comparingDouble(a -> a.x)
    );

    horizontalLines.sort(Comparator.comparingDouble(a -> a.y)
    );

    ArrayList<Rect> rects = new ArrayList<>();
    
    for(int vIdx = 0; vIdx < verticalLines.size() - 1; ++vIdx) {
      for(int hIdx = 0; hIdx < horizontalLines.size() - 1; ++hIdx) {
        Point i11 = intersect(verticalLines.get(vIdx), horizontalLines.get(hIdx));
        Point i12 = intersect(verticalLines.get(vIdx), horizontalLines.get(hIdx + 1));
        Point i21 = intersect(verticalLines.get(vIdx + 1), horizontalLines.get(hIdx));
        Point i22 = intersect(verticalLines.get(vIdx + 1), horizontalLines.get(hIdx + 1));
        List<Double> xs = Arrays.asList(i11.x, i12.x, i21.x, i22.x);
        List<Double> ys = Arrays.asList(i11.y, i12.y, i21.y, i22.y);
        Rect r = new Rect();
        r.xmax = Collections.max(xs);
        r.ymax = Collections.max(ys);
        r.xmin = Collections.min(xs);
        r.ymin = Collections.min(ys);
        rects.add(r);
      }
    }
    Rects rs = new Rects();
    rs.rects = rects;
    rs.nRectsInLine = verticalLines.size() - 1;
    return rs;
  }
}
