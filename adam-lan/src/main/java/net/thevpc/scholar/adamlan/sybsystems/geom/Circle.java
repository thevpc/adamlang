package net.thevpc.scholar.adamlan.sybsystems.geom;

import net.thevpc.gaming.atom.presentation.layers.LayerDrawingContext;

import java.awt.*;

public class Circle implements GeomComp {

    int x;
    int y;
    int w;
    int h;
    Color c;

    public Circle() {
    }

    public Circle(int x, int y, int w, int h, Color c) {
        this.x = x;
        this.y = y;
        this.w = w;
        this.h = h;
        this.c = c;
    }

    @Override
    public void draw(LayerDrawingContext context) {
        Graphics2D g = context.getGraphics();
        g.setColor(c);
        g.fillOval(x, y, w, h);
    }
}
