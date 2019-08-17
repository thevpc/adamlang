package net.vpc.scholar.adamlan;

import net.vpc.common.jeep.*;
import net.vpc.common.jeep.nodes.ExpressionNodeFunctionCall;
import net.vpc.common.jeep.nodes.ExpressionNodeLiteral;
import net.vpc.common.jeep.nodes.ExpressionNodeVariableName;
import net.vpc.gaming.atom.util.AtomUtils;
import net.vpc.scholar.adamlan.utils.AdamLanUtils;
import net.vpc.scholar.adamlan.utils.ExpressionNodeList;

import java.awt.*;
import java.lang.reflect.Field;

public class AdamLanFunctionsUtils {
    public static AdamLanParser __evaluator(ExpressionEvaluator it) {
        return (AdamLanParser) (it.getExpressionManager());
    }

    public static AdamLanParser __evaluator() {
        return (AdamLanParser) ExpressionEvaluators.getExpressionEvaluator();
    }

    public static Point readPointPos(ExpressionEvaluator it, ExpressionNodeList li) {
//        AdamLanParser ev = (AdamLanParser) it.getExpressionManager();
        ExpressionNode xy = li.peek();
        if (xy == null) {
            int x = __randomX();
            int y = __randomY();
            return new Point(x, y);
        }
        if (xy instanceof ExpressionNodeFunctionCall && ((ExpressionNodeFunctionCall) xy).getName().equals(",")) {
            ExpressionNodeFunctionCall popped = (ExpressionNodeFunctionCall) li.pop();
            ExpressionNode op1 = popped.getOperand(0);
            ExpressionNode op2 = popped.getOperand(1);
            int x = (Integer) AdamLanUtils.convert(op1.evaluate(it), Integer.class);
            int y = (Integer) AdamLanUtils.convert(op2.evaluate(it), Integer.class);
            return new Point(x, y);
        } else {
            int x = !li.isEmpty() ? li.evalAndRemoveHeadInt() : __randomX();
            int y = !li.isEmpty() ? li.evalAndRemoveHeadInt() : __randomY();
            return new Point(x, y);
        }
    }

    public static Color readColor(ExpressionEvaluator it, ExpressionNodeList li) {
        Color c = null;

        ExpressionNode h = li.peek();
        if (h != null) {
            h = li.pop();
            Object s = null;
            if (h instanceof ExpressionNodeLiteral) {
                Object value = ((ExpressionNodeLiteral) h).getValue();
                s = value;
            } else if (h instanceof ExpressionNodeVariableName) {
                Object value = it.getVariableValue(((ExpressionNodeVariableName) h).getName());
                if (value == null) {
                    //not declared!
                    s = ((ExpressionNodeVariableName) h).getName();
                } else {
                    s = value;
                }
            } else if (h instanceof ExpressionNodeFunctionCall && ((ExpressionNodeFunctionCall) h).getName().equals(",")) {
                ExpressionNodeFunctionCall popped = (ExpressionNodeFunctionCall) h;
                double x = (Integer) AdamLanUtils.convert(popped.getOperand(0).evaluate(it), Double.class);
                double y = popped.getOperandsCount() < 1 ? 0 : (Integer) AdamLanUtils.convert(popped.getOperand(1).evaluate(it), Double.class);
                double z = popped.getOperandsCount() < 2 ? 0 : (Integer) AdamLanUtils.convert(popped.getOperand(2).evaluate(it), Double.class);
                double t = popped.getOperandsCount() < 3 ? 0 : (Integer) AdamLanUtils.convert(popped.getOperand(3).evaluate(it), Double.class);
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
                    String uc = ((AdamLanParser) (it.getExpressionManager())).getLangSupport().translateWord(s.toString());
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

    public static Point readPointLen(ExpressionEvaluator it, ExpressionNodeList li) {
        ExpressionNode xy = li.peek();
        if (xy == null) {
            int x = __randomW();
            int y = __randomH();
            return new Point(x, y);
        }
        if (xy instanceof ExpressionNodeFunctionCall && ((ExpressionNodeFunctionCall) xy).getName().equals(",")) {
            ExpressionNodeFunctionCall popped = (ExpressionNodeFunctionCall) li.pop();
            int x = (Integer) AdamLanUtils.convert(popped.getOperand(0).evaluate(it), Integer.class);
            int y = (Integer) AdamLanUtils.convert(popped.getOperand(1).evaluate(it), Integer.class);
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
