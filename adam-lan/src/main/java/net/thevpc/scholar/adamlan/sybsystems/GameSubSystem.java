/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.thevpc.scholar.adamlan.sybsystems;

import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.geom.Path2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import net.thevpc.jeep.JContext;
import net.thevpc.jeep.JNode;
import net.thevpc.gaming.atom.engine.DefaultGameEngine;
import net.thevpc.gaming.atom.engine.DefaultSceneEngine;
import net.thevpc.gaming.atom.model.ViewDimension;
import net.thevpc.gaming.atom.presentation.DefaultGame;
import net.thevpc.gaming.atom.presentation.DefaultScene;
import net.thevpc.gaming.atom.presentation.layers.DefaultLayer;
import net.thevpc.gaming.atom.presentation.layers.FillScreenColorLayer;
import net.thevpc.gaming.atom.presentation.layers.Layer;
import net.thevpc.gaming.atom.presentation.layers.LayerDrawingContext;
import net.thevpc.gaming.atom.util.AtomUtils;
import net.thevpc.scholar.adamlan.AdamLanFunctionsUtils;
import net.thevpc.scholar.adamlan.sybsystems.geom.*;
import net.thevpc.scholar.adamlan.sybsystems.geom.Polygon;
import net.thevpc.scholar.adamlan.utils.ExpressionNodeList;

/**
 * @author thevpc
 */
public class GameSubSystem {

    DefaultGameEngine engine;
    DefaultGame game;
    DefaultSceneEngine sceneEngine;
    DefaultScene scene;
    Font currentFont = new Font("arial", 0, 32);
    private Color currentColor = Color.BLACK;
    int consoleX = 0;
    int consoleY = 10;
    int fontHeight = 10;
    private int screenWidth = 800;
    private int screenHeight = 600;
    FontRenderContext c = new FontRenderContext(null, true, true);

    private List<GeomComp> comps = new ArrayList<GeomComp>();

    private void prepare() {
        if (engine == null) {
            engine = new DefaultGameEngine();
//            sceneEngine = new DefaultSceneEngine("default");
//            sceneEngine.setModel(new DefaultSceneEngineModel(50, 50));
//            engine.addSceneEngine(sceneEngine);
            game = new DefaultGame(engine);
            scene = new DefaultScene("default", new ViewDimension(screenWidth, screenHeight));
            scene.addLayer(new FillScreenColorLayer(Color.WHITE));
            fontHeight = (int) currentFont.getStringBounds("AW", c).getHeight();
            consoleY = fontHeight;
            scene.addLayer(new DefaultLayer() {
                {
                    setLayer(Layer.SCREEN_BACKGROUND_LAYER + 1);
                }

                @Override
                public void draw(LayerDrawingContext context) {
                    ViewDimension sceneSize = scene.getSceneSize();
                    Graphics2D g = context.getGraphics();
                    g.setColor(Color.LIGHT_GRAY);
                    for (int i = 0; i < sceneSize.getWidth(); i += 10) {
                        g.drawLine(i, 0, i, sceneSize.getHeight());
                    }
                    for (int i = 0; i < sceneSize.getHeight(); i += 10) {
                        g.drawLine(0, i, sceneSize.getWidth(), i);
                    }
                    g.setColor(Color.GRAY);
                    for (int i = 0; i < sceneSize.getWidth(); i += 100) {
                        g.drawLine(i, 0, i, sceneSize.getHeight());
                        g.drawString(String.valueOf(i), i, 10);
                    }
                    for (int i = 0; i < sceneSize.getHeight(); i += 100) {
                        g.drawLine(0, i, sceneSize.getWidth(), i);
                        if (i > 0) {
                            g.drawString(String.valueOf(i), 0, i);
                        }
                    }
                    synchronized (comps) {
                        for (GeomComp comp : comps) {
                            comp.draw(context);
                        }
                    }
                }

            });
            game.addScene(scene);
            scene.setTitle("Adam");
            game.start();
        }
    }

    public void draw(String text) {
        prepare();
        StringTokenizer t = new StringTokenizer(text, "\n", true);
        while (t.hasMoreTokens()) {
            String s = t.nextToken();
            if (s.equalsIgnoreCase("\n")) {
                consoleY += fontHeight;
                consoleX = 0;
            } else {
                Rectangle2D stringBounds = currentFont.getStringBounds(text, c);
                draw(new GeomText(text, consoleX, consoleY, currentFont, currentColor));
                consoleX = (int) (consoleX + stringBounds.getWidth());
                if (consoleX > screenWidth) {
                    consoleX = 0;
                    consoleY += fontHeight;
                }
            }
            if (consoleY > screenHeight) {
                consoleY = 0;
                consoleX = 0;
            }
        }
    }

    public void draw(GeomComp c) {
        prepare();
        synchronized (comps) {
            comps.add(c);
        }
    }

    public void draw(JContext it, JNode... args) {
        prepare();
        if (args.length == 0) {
            return;
        }
        ExpressionNodeList li = new ExpressionNodeList(it, args);
        if (li.removeLocalizedVar("circle")) {
            Point xy = AdamLanFunctionsUtils.readPointPos(it,li);
            Point wh = AdamLanFunctionsUtils.readPointLen(it,li);
            int x = xy.x;
            int y = xy.y;
            int w = wh.x;
            int h = wh.y;
            Color c = AdamLanFunctionsUtils.readColor(it,li);
            draw(new Circle(xy.x, xy.y, wh.x, wh.y <= 0 ? wh.x : wh.y, c));
        } else if (li.removeLocalizedVar("ellipse") || li.removeLocalizedVar("oval")) {
            Point xy = AdamLanFunctionsUtils.readPointPos(it,li);
            Point wh = AdamLanFunctionsUtils.readPointLen(it,li);
            int x = xy.x;
            int y = xy.y;
            int w = wh.x;
            int h = wh.y;
            Color c = AdamLanFunctionsUtils.readColor(it,li);
            draw(new Circle(x, y, w, h <= 0 ? w : h, c));
        } else if (li.removeLocalizedVar("square")) {
            Point xy = AdamLanFunctionsUtils.readPointPos(it,li);
            Point wh = AdamLanFunctionsUtils.readPointLen(it,li);
            int x = xy.x;
            int y = xy.y;
            int w = wh.x;
            int h = wh.y;
            Color c = AdamLanFunctionsUtils.readColor(it,li);
            draw(new net.thevpc.scholar.adamlan.sybsystems.geom.Rectangle(x, y, w, h <= 0 ? w : h, c));
        } else if (li.removeLocalizedVar("rectangle")) {
            Point xy = AdamLanFunctionsUtils.readPointPos(it,li);
            Point wh = AdamLanFunctionsUtils.readPointLen(it,li);
            int x = xy.x;
            int y = xy.y;
            int w = wh.x;
            int h = wh.y;
            Color c = AdamLanFunctionsUtils.readColor(it,li);
            draw(new net.thevpc.scholar.adamlan.sybsystems.geom.Rectangle(x, y, w, h <= 0 ? w : h, c));
        } else if (li.removeLocalizedVar("line")) {
            Point xy = AdamLanFunctionsUtils.readPointPos(it,li);
            Point wh = AdamLanFunctionsUtils.readPointPos(it,li);
            int x = xy.x;
            int y = xy.y;
            int w = wh.x;
            int h = wh.y;
            Color c = AdamLanFunctionsUtils.readColor(it,li);
            draw(new Line(x, y, w, h, c));
        } else if (li.removeLocalizedVar("triangle")) {
            List<Point> all = new ArrayList<Point>();
            for (int i = 0; i < 3; i++) {
                all.add(AdamLanFunctionsUtils.readPointPos(it,li));
            }
            Color c = AdamLanFunctionsUtils.readColor(it,li);
            draw(new net.thevpc.scholar.adamlan.sybsystems.geom.Polygon(all.toArray(new Point[all.size()]), c));
        } else if (li.removeLocalizedVar("polygon")) {
            int nn = !li.isEmpty() ? li.evalAndRemoveHeadInt() : AtomUtils.randomInt(3, 12);
            Point xy = AdamLanFunctionsUtils.readPointPos(it,li);
            int x = xy.x;
            int y = xy.y;
            int w = !li.isEmpty() ? li.evalAndRemoveHeadInt() : AdamLanFunctionsUtils.__randomW();
            Color c = AdamLanFunctionsUtils.readColor(it,li);

            draw(createPolygon(nn, x, y, w, c));
        } else if (li.removeLocalizedVar("pentagon")) {
            Point xy = AdamLanFunctionsUtils.readPointPos(it,li);
            int x = xy.x;
            int y = xy.y;
            int w = !li.isEmpty() ? li.evalAndRemoveHeadInt() : AdamLanFunctionsUtils.__randomW();
            Color c = AdamLanFunctionsUtils.readColor(it,li);
            draw(createPolygon(5, x, y, w, c));
        } else if (li.removeLocalizedVar("hexagon")) {
            Point xy = AdamLanFunctionsUtils.readPointPos(it,li);
            int x = xy.x;
            int y = xy.y;
            int w = !li.isEmpty() ? li.evalAndRemoveHeadInt() : AdamLanFunctionsUtils.__randomW();
            Color c = AdamLanFunctionsUtils.readColor(it,li);
            draw(createPolygon(6, x, y, w, c));
        } else if (li.removeLocalizedVar("heptagon")) {
            Point xy = AdamLanFunctionsUtils.readPointPos(it,li);
            int x = xy.x;
            int y = xy.y;
            int w = !li.isEmpty() ? li.evalAndRemoveHeadInt() : AdamLanFunctionsUtils.__randomW();
            Color c = AdamLanFunctionsUtils.readColor(it,li);
            draw(createPolygon(7, x, y, w, c));
        } else if (li.removeLocalizedVar("octogon")) {
            Point xy = AdamLanFunctionsUtils.readPointPos(it,li);
            int x = xy.x;
            int y = xy.y;
            int w = !li.isEmpty() ? li.evalAndRemoveHeadInt() : AdamLanFunctionsUtils.__randomW();
            Color c = AdamLanFunctionsUtils.readColor(it,li);
            draw(createPolygon(8, x, y, w, c));
        } else if (li.removeLocalizedVar("nanogon")) {
            Point xy = AdamLanFunctionsUtils.readPointPos(it,li);
            int x = xy.x;
            int y = xy.y;
            int w = !li.isEmpty() ? li.evalAndRemoveHeadInt() : AdamLanFunctionsUtils.__randomW();
            Color c = AdamLanFunctionsUtils.readColor(it,li);
            draw(createPolygon(9, x, y, w, c));
        } else if (li.removeLocalizedVar("decagon")) {
            Point xy = AdamLanFunctionsUtils.readPointPos(it,li);
            int x = xy.x;
            int y = xy.y;
            int w = !li.isEmpty() ? li.evalAndRemoveHeadInt() : AdamLanFunctionsUtils.__randomW();
            Color c = AdamLanFunctionsUtils.readColor(it,li);
            draw(createPolygon(10, x, y, w, c));
        } else if (li.removeLocalizedVar("star")) {
            Point xy = AdamLanFunctionsUtils.readPointPos(it,li);
            int x = xy.x;
            int y = xy.y;
            int w = !li.isEmpty() ? li.evalAndRemoveHeadInt() : AdamLanFunctionsUtils.__randomW();
            Color c = AdamLanFunctionsUtils.readColor(it,li);
            int rays = AtomUtils.randomInt(3, 20);
            double startAngle = AtomUtils.randomDouble(0, Math.PI);
            draw(new GShape(
                    createStar(x, y, w / 2, w, 5, startAngle),
                    c
            ));
        } else if (li.removeLocalizedVar("sun")) {
            Point xy = AdamLanFunctionsUtils.readPointPos(it,li);
            int x = xy.x;
            int y = xy.y;
            int w = !li.isEmpty() ? li.evalAndRemoveHeadInt() : AdamLanFunctionsUtils.__randomW();
            Color c = AdamLanFunctionsUtils.readColor(it,li);
            int rays = AtomUtils.randomInt(30, 50);
            double startAngle = AtomUtils.randomDouble(0, Math.PI);
            draw(new GShape(
                    createStar(x, y, 4 * w / 5, w, rays, startAngle),
                    c
            ));
        }
    }

    private Polygon createPolygon(int nn, int x, int y, int w, Color c) {
        List<Point> all = new ArrayList<Point>();
        for (int i = 0; i < nn; i++) {
            int x0 = x + (int) (w * Math.cos(2 * Math.PI / nn * i));
            int y0 = y + (int) (w * Math.sin(2 * Math.PI / nn * i));
            all.add(new Point(x0, y0));
        }
        return new Polygon(all.toArray(new Point[all.size()]), c);
    }

    private static Shape createStar(double centerX, double centerY,
                                    double innerRadius, double outerRadius, int numRays,
                                    double startAngleRad) {
        Path2D path = new Path2D.Double();
        double deltaAngleRad = Math.PI / numRays;
        for (int i = 0; i < numRays * 2; i++) {
            double angleRad = startAngleRad + i * deltaAngleRad;
            double ca = Math.cos(angleRad);
            double sa = Math.sin(angleRad);
            double relX = ca;
            double relY = sa;
            if ((i & 1) == 0) {
                relX *= outerRadius;
                relY *= outerRadius;
            } else {
                relX *= innerRadius;
                relY *= innerRadius;
            }
            if (i == 0) {
                path.moveTo(centerX + relX, centerY + relY);
            } else {
                path.lineTo(centerX + relX, centerY + relY);
            }
        }
        path.closePath();
        return path;
    }
}
