package net.vpc.scholar.adamlan.sybsystems.geom;

import net.vpc.gaming.atom.presentation.layers.LayerDrawingContext;

import java.awt.*;

public class GeomText implements GeomComp {

    String text;
    int x;
    int y;
    Font font;
    Color c;

    public GeomText(String text, int x, int y, Font font, Color c) {
        this.text = text==null?"":text;
        this.x = x;
        this.y = y;
        this.font = font;
        this.c = c;
    }

    @Override
    public void draw(LayerDrawingContext context) {
        Graphics2D g = context.getGraphics();
        g.setFont(font);
        g.setColor(c);
        g.drawString(text,x,y);
    }
}
