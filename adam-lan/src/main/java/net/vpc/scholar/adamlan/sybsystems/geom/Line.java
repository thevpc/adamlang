package net.vpc.scholar.adamlan.sybsystems.geom;

import net.vpc.gaming.atom.presentation.layers.LayerDrawingContext;

import java.awt.*;

public class Line implements GeomComp {

    int x;
    int y;
    int x2;
    int y2;
    Color c;

    public Line() {
    }

    public Line(int x, int y, int w, int h, Color c) {
        this.x = x;
        this.y = y;
        this.x2 = w;
        this.y2 = h;
        this.c = c;
    }

    @Override
    public void draw(LayerDrawingContext context) {
        Graphics2D g = context.getGraphics();
        g.setColor(c);
        g.drawLine(x, y, x2, y2);
    }
}
