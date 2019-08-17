/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.vpc.scholar.adamlan;

import java.lang.reflect.Method;
import java.util.Arrays;

import net.vpc.common.jeep.*;
import net.vpc.common.jeep.nodes.ExpressionNodeLiteral;
import net.vpc.common.jeep.nodes.ExpressionNodeVariableName;
import net.vpc.scholar.adamlan.sybsystems.*;
import net.vpc.common.util.Convert;

/**
 *
 * @author vpc
 */
public class AdamLanFunctions {

    private static AdamLanTextToSpeechSubSystem marytts = null;
    private static AdamLanMusicSubSystem music = null;
    private static GameSubSystem game = null;

    public static int randomInt(int min,int max) {
        return (int) (Math.random() * (max+1-min))+min;
    }

    public static void draw(ExpressionEvaluator it, ExpressionNode... args) {
        if (args.length == 0) {
            return;
        }
        getConsole().draw(it,args);
    }

    private static GameSubSystem getConsole() {
        if (game == null) {
            game = new GameSubSystem();
        }
        return game;
    }


    public static void increment(ExpressionNode arg, ExpressionEvaluator ee) {
        String varName = null;
        if (arg instanceof ExpressionNodeVariableName) {
            varName = ((ExpressionNodeVariableName) arg).getName();
        } else {
            varName = String.valueOf(arg.evaluate(ee));
        }
        ExpressionNodeVariableName varNode = new ExpressionNodeVariableName(varName, Object.class);
        Object oldValue = ee.getVariableValue(varName);
        Object incArg = JeepUtils.getIncDefaultValue(oldValue);
        Object newValue = ee.evaluateFunction("+", varNode, new ExpressionNodeLiteral(incArg));
        ee.setVariableValue(varName, newValue);
    }

    public static void decrement(ExpressionEvaluator it, ExpressionNode arg) {
        String varName = null;
//        AdamLanParser ee = AdamLanFunctionsUtils.__evaluator();
        if (arg instanceof ExpressionNodeVariableName) {
            varName = ((ExpressionNodeVariableName) arg).getName();
        } else {
            varName = String.valueOf(arg.evaluate(it));
        }
        ExpressionNodeVariableName varNode = new ExpressionNodeVariableName(varName, Object.class);
        Object oldValue = it.getVariableValue(varName);
        Object incArg = JeepUtils.getIncDefaultValue(oldValue);
        Object newValue = it.evaluateFunction("-", varNode, new ExpressionNodeLiteral(incArg));
        it.setVariableValue(varName, newValue);
    }

    public static Object read(ExpressionEvaluator it, ExpressionNode... args) {
        String vn;
        String r = null;
        Variable variableDef = null;
        if (args.length == 0) {
            vn = "variable";
        } else {
            vn = String.valueOf(((ExpressionNodeVariableName) args[0]).getName());
            r = vn;
            variableDef = it.getExpressionManager().getVariable(r);
        }
        Class tt = null;
        if (r != null) {
            if (variableDef != null) {
                tt = variableDef.getEffectiveType(it);
            }
        }
        if (tt == null) {
            tt = Object.class;
        }
        Object o = ((AdamLanParser)it.getExpressionManager()).env().read(tt, "Entrer " + vn);
        if (r != null) {
            it.setVariableValue(r, o);
        }
        return o;
    }

    public static void play(ExpressionEvaluator it, Object... args) {
        if (music == null) {
            music = new AdamLanMusicSubSystem();
        }
        String[] all = new String[args.length];
        for (int i = 0; i < all.length; i++) {
            all[i] = String.valueOf(args[i]);
        }
        music.play(it,all);
    }

    public static void write(Object... args) {
        AdamLanParser e = AdamLanFunctionsUtils.__evaluator();
        StringBuilder sb = new StringBuilder();
        for (Object t : args) {
            if (t != null) {
                if (sb.length() > 0) {
                    sb.append(" ");
                }
                sb.append(t);
            }
        }
        sb.append("\n");
        getConsole().draw(sb.toString());
//        e.env().println(sb);
    }

    public static void wait(Object... args) {
        try {
            int count = 0;
            for (Object t : args) {
                if (t instanceof Number) {
                    int ii = Convert.toInt(t);
                    if (ii > 0) {
                        count = ii;
                    }
                }
                break;
            }
            if (count < 0) {
                count = 1;
            }
            Thread.sleep(1000 * count);
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
    }

    public static boolean say(ExpressionEvaluator it, Object... args) {
        StringBuilder sb = new StringBuilder();
        for (Object t : args) {
            if (t != null) {
                if (sb.length() > 0) {
                    sb.append(" ");
                }
                sb.append(t);
            }
        }
        try {
            String inputText = sb.toString();

            // init mary
            if (marytts == null) {
                marytts = new AdamLanTextToSpeechSubSystem();
            }
            marytts.speak(it,inputText);
        } catch (Exception ex) {
            ex.printStackTrace();
            //
            return false;
        }
        return true;
    }

    private static Object _PlatformHelper_invoke(String name, Object... args) {
        Class[] cc = new Class[args.length];
        for (int i = 0; i < cc.length; i++) {
            cc[i] = args[i].getClass();
        }
        Method m = JeepPlatformUtils.getMatchingMethod(PlatformHelper.class, name, cc);
        if (m != null) {
            try {
                m.setAccessible(true);
                return m.invoke(null, args);
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        }
        System.err.println("Not found " + name + "(" + Arrays.deepToString(args) + ")");
        return null;
    }

    public static Object add(Object a, Object b) {
        if (a == null) {
            return b;
        }
        if (b == null) {
            return a;

        }
        return _PlatformHelper_invoke("add", a, b);
    }

    public static Object sub(Object a, Object b) {
        if (a == null) {
            return b;
        }
        if (b == null) {
            return a;

        }
        return _PlatformHelper_invoke("sub", a, b);
    }

    public static Object compare(Object a, Object b) {
        if (a == null && b == null) {
            return 0;
        }
        if (a == null) {
            return -1;
        }
        if (b == null) {
            return 1;

        }
        return _PlatformHelper_invoke("compare", a, b);
    }
}
