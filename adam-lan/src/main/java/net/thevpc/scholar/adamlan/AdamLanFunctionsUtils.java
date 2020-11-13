package net.thevpc.scholar.adamlan;

import net.thevpc.jeep.*;
import net.thevpc.jeep.core.nodes.JNodeFunctionCall;
import net.thevpc.jeep.core.nodes.JNodeVarName;
import net.thevpc.jeep.core.nodes.JNodeLiteral;
import net.thevpc.gaming.atom.util.AtomUtils;
import net.thevpc.scholar.adamlan.utils.AdamLanUtils;
import net.thevpc.scholar.adamlan.utils.ExpressionNodeList;

import java.awt.*;
import java.lang.reflect.Field;

public class AdamLanFunctionsUtils {
    public static AdamLanParser __evaluator(JContext it) {
        return (AdamLanParser) (it.manager());
    }

    public static AdamLanParser __evaluator() {
        return (AdamLanParser) JSharedContext.getCurrent();
    }

    public static Point readPointPos(JContext it, ExpressionNodeList li) {
//        AdamLanParser ev = (AdamLanParser) it.getExpressionManager();
        JNode xy = li.peek();
        if (xy == null) {
            int x = __randomX();
            int y = __randomY();
            return new Point(x, y);
        }
        if (xy instanceof JNodeFunctionCall && ((JNodeFunctionCall) xy).getName().equals(",")) {
            JNodeFunctionCall popped = (JNodeFunctionCall) li.pop();
            JNode op1 = popped.getOperand(0);
            JNode op2 = popped.getOperand(1);
            int x = (Integer) AdamLanUtils.convert(it.evaluate(op1), Integer.class);
            int y = (Integer) AdamLanUtils.convert(it.evaluate(op2), Integer.class);
            return new Point(x, y);
        } else {
            int x = !li.isEmpty() ? li.evalAndRemoveHeadInt() : __randomX();
            int y = !li.isEmpty() ? li.evalAndRemoveHeadInt() : __randomY();
            return new Point(x, y);
        }
    }

    public static Color readColor(JContext it, ExpressionNodeList li) {
        Color c = null;

        JNode h = li.peek();
        if (h != null) {
            h = li.pop();
            Object s = null;
            if (h instanceof JNodeLiteral) {
                Object value = ((JNodeLiteral) h).getValue();
                s = value;
            } else if (h instanceof JNodeVarName) {
                //TODO FIX ME context cant be null!
                Object value = it.vars().getValue(((JNodeVarName) h).getName(),null);
                if (value == null) {
                    //not declared!
                    s = ((JNodeVarName) h).getName();
                } else {
                    s = value;
                }
            } else if (h instanceof JNodeFunctionCall && ((JNodeFunctionCall) h).getName().equals(",")) {
                JNodeFunctionCall popped = (JNodeFunctionCall) h;
                double x = (Integer) AdamLanUtils.convert(it.evaluate(popped.getOperand(0)), Double.class);
                double y = popped.getOperandsCount() < 1 ? 0 : (Integer) AdamLanUtils.convert(it.evaluate(popped.getOperand(1)), Double.class);
                double z = popped.getOperandsCount() < 2 ? 0 : (Integer) AdamLanUtils.convert(it.evaluate(popped.getOperand(2)), Double.class);
                double t = popped.getOperandsCount() < 3 ? 0 : (Integer) AdamLanUtils.convert(it.evaluate(popped.getOperand(3)), Double.class);
                if (x >= 1 || y >= 1 || z >= 1 || t >= 1) {
                    c = new Color((int) x, (int) y, (int) z);
                } else {
                    if (t <= 0) {
                        t = 1;
                    }
                    c = new Color((float) x, (float) y, (float) z, (float) t);
                }
            }
            if (c == null && s != null) {
                if (s instanceof Number) {
                    c = new Color(((Number) s).intValue());
                } else if (s instanceof Color) {
                    c = (Color) s;
                } else if (s instanceof String) {
                    String uc = ((AdamLanParser) (it.manager())).getLangSupport().translateWord(s.toString());
                    try {
                        Field f = Color.class.getDeclaredField(uc.toLowerCase());
                        c = (Color) f.get(null);
                    } catch (Exception ex) {
                        //
                    }
                    if (c == null) {
                        uc = s.toString();
                        try {
                            Field f = Color.class.getDeclaredField(uc.toLowerCase());
                            c = (Color) f.get(null);
                        } catch (Exception ex) {
                            //
                        }
                    }
                }
            }
        }
        if (c == null) {
            c = __randomColor();
        }
        return c;
    }

    public static Point readPointLen(JContext it, ExpressionNodeList li) {
        JNode xy = li.peek();
        if (xy == null) {
            int x = __randomW();
            int y = __randomH();
            return new Point(x, y);
        }
        if (xy instanceof JNodeFunctionCall && ((JNodeFunctionCall) xy).getName().equals(",")) {
            JNodeFunctionCall popped = (JNodeFunctionCall) li.pop();
            int x = (Integer) AdamLanUtils.convert(it.evaluate(popped.getOperand(0)), Integer.class);
            int y = (Integer) AdamLanUtils.convert(it.evaluate(popped.getOperand(1)), Integer.class);
            return new Point(x, y);
        } else {
            int x = !li.isEmpty() ? li.evalAndRemoveHeadInt() : __randomW();
            int y = !li.isEmpty() ? li.evalAndRemoveHeadInt() : __randomH();
            return new Point(x, y);
        }
    }

    public static Color __randomColor() {
        Color c = new Color(AtomUtils.randomFloat(0, 1), AtomUtils.randomFloat(0, 1), AtomUtils.randomFloat(0, 1), AtomUtils.randomFloat(0.3f, 1));
        return c;
    }

    public static int __randomX() {
        return AtomUtils.randomInt(0, 60)*10;
    }

    public static int __randomY() {
        return AtomUtils.randomInt(0, 40)*10;
    }

    public static int __randomW() {
        return AtomUtils.randomInt(10, 30)*10;
    }

    public static int __randomH() {
        return AtomUtils.randomInt(10, 30)*10;
    }

}
