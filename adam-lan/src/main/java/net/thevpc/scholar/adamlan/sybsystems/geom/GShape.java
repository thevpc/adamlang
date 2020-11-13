package net.thevpc.scholar.adamlan.sybsystems.geom;

import net.thevpc.gaming.atom.presentation.layers.LayerDrawingContext;

import java.awt.*;

public class GShape implements GeomComp {

    java.awt.Shape shape;
    Color c;

    public GShape() {
    }

    public GShape(java.awt.Shape shape, Color c) {
        this.shape = shape;
        this.c = c;
    }

    @Override
    public void draw(LayerDrawingContext context) {
        Graphics2D g = context.getGraphics();
        g.setColor(c);
        g.fill(shape);
    }
}
