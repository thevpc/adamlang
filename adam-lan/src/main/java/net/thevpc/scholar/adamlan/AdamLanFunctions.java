/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.thevpc.scholar.adamlan;

import java.lang.annotation.ElementType;
import java.util.Arrays;

import net.thevpc.jeep.*;
import net.thevpc.jeep.core.JNodeUtils2;
import net.thevpc.jeep.core.imports.PlatformHelperImports;
import net.thevpc.jeep.core.nodes.JNodeLiteral;
import net.thevpc.jeep.core.nodes.JNodeVarName;
import net.thevpc.jeep.core.eval.JEvaluableValue;
import net.thevpc.jeep.impl.functions.DefaultJInvokeContext;
import net.thevpc.jeep.util.JInvokeUtils;
import net.thevpc.jeep.util.JTypeUtils;
import net.thevpc.jeep.util.JeepPlatformUtils;
import net.thevpc.scholar.adamlan.sybsystems.*;
import net.thevpc.common.util.Convert;

/**
 *
 * @author thevpc
 */
@JeepImported(ElementType.TYPE)
public class AdamLanFunctions {

    private static AdamLanTextToSpeechSubSystem marytts = null;
    private static AdamLanMusicSubSystem music = null;
    private static GameSubSystem game = null;

    public static int randomInt(int min,int max) {
        return (int) (Math.random() * (max+1-min))+min;
    }

    public static void draw(JContext it, JNode... args) {
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


    public static void increment(JNode arg, JInvokeContext ee) {
        String varName = null;
        if (arg instanceof JNodeVarName) {
            varName = ((JNodeVarName) arg).getName();
        } else {
            varName = String.valueOf(ee.evaluate(arg));
        }
        JNodeVarName varNode = new JNodeVarName(varName);
        Object oldValue = ee.getContext().vars().getValue(varName,ee);
        Object incArg = JNodeUtils2.getIncDefaultValue(oldValue);
        JType t = ee.getContext().types().typeOf(incArg);
        if(t==null){
            t= JTypeUtils.forObject(ee.getContext().types());
        }
        //TODO caller info is null
        Object newValue = ee.getContext().functions().evaluate(null,"+", JNodeUtils2.getEvaluatables(varNode, new JNodeLiteral(incArg)));
        ee.getContext().vars().setValue(varName, newValue,ee);
    }

    public static void decrement(JInvokeContext it, JNode arg) {
        String varName = null;
//        AdamLanParser ee = AdamLanFunctionsUtils.__evaluator();
        if (arg instanceof JNodeVarName) {
            varName = ((JNodeVarName) arg).getName();
        } else {
            varName = String.valueOf(it.evaluate(arg));
        }
        JNodeVarName varNode = new JNodeVarName(varName);
        Object oldValue = it.getContext().vars().getValue(varName,it);
        Object incArg = JNodeUtils2.getIncDefaultValue(oldValue);
        JType t = it.getContext().types().typeOf(incArg);
        if(t==null){
            t= JTypeUtils.forObject(it.getContext().types());
        }
        Object newValue = it.getContext().functions().evaluate(null,"-", JNodeUtils2.getEvaluatables(varNode,new JNodeLiteral(incArg)));
        it.getContext().vars().setValue(varName, newValue,it);
    }

    public static Object read(JInvokeContext it, JNode... args) {
        String vn;
        String r = null;
        JVar variableDef = null;
        if (args.length == 0) {
            vn = "variable";
        } else {
            vn = String.valueOf(((JNodeVarName) args[0]).getName());
            r = vn;
            variableDef = it.getContext().vars().get(r);
        }
        Class tt = null;
        if (r != null) {
            if (variableDef != null) {
                try {
                    tt = Class.forName(variableDef.type().getName());
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
        AdamLanParser manager = (AdamLanParser) it.getContext().manager();
        if (tt == null) {
            tt = Object.class;
        }
        Object o = manager.env().read(tt, "Entrer " + vn);
        if (r != null) {
            it.getContext().vars().setValue(r, o,it);
        }
        return o;
    }

    public static void play(JContext it, Object... args) {
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

    public static boolean say(JContext it, Object... args) {
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
        AdamLanParser e = AdamLanFunctionsUtils.__evaluator();
        JType[] cc = new JType[args.length];
        for (int i = 0; i < cc.length; i++) {
            cc[i] = e.types().forName(args[i].getClass().getName());
        }
        JMethod m = JInvokeUtils.getMatchingMethod(e.types().forName(PlatformHelperImports.class.getName()), name,
                JTypePattern.of(cc,null));
        JEvaluable[] eargs=new JEvaluable[args.length];
        JType[] etypes=new JType[args.length];
        for (int i = 0; i < eargs.length; i++) {
            eargs[i]=new JEvaluableValue(args[i],e.types().typeOf(args[i]));
            etypes[i]=e.types().typeOf(args[i]);
        }
        if (m != null) {
            return m.invoke(new DefaultJInvokeContext(
                    e,
                    null,
                    null,
                     eargs,
                    m.getName(),
                    null
            ));
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
