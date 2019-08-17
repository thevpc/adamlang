package net.vpc.scholar.adamlan.sybsystems.geom;

import net.vpc.gaming.atom.presentation.layers.LayerDrawingContext;

import java.awt.*;

public class Polygon implements GeomComp {

    Point[] points;
    Color c;

    public Polygon() {
    }

    public Polygon(Point[] points, Color c) {
        this.points = points;
        this.c = c;
    }

    @Override
    public void draw(LayerDrawingContext context) {
        Graphics2D g = context.getGraphics();
        g.setColor(c);
        int[] x = new int[points.length];
        int[] y = new int[points.length];
        for (int i = 0; i < points.length; i++) {
            Point point = points[i];
            x[i] = point.x;
            y[i] = point.y;
        }
        g.fillPolygon(x, y, x.length);
    }
}
