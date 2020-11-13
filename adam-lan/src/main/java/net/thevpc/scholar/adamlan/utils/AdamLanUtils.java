/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.thevpc.scholar.adamlan.utils;

import java.awt.Color;
import java.lang.reflect.Field;
import java.util.List;

import net.thevpc.jeep.*;
import net.thevpc.jeep.core.nodes.JNodeFunctionCall;
import net.thevpc.jeep.core.nodes.JNodeVarName;
import net.thevpc.common.strings.StringUtils;
import net.thevpc.common.util.Convert;
import net.thevpc.common.util.DoubleParserConfig;
import net.thevpc.common.util.FloatParserConfig;
import net.thevpc.common.util.IntegerParserConfig;
import net.thevpc.common.util.LongParserConfig;
import net.thevpc.common.util.Utils;
import net.thevpc.scholar.adamlan.LangSupport;

/**
 *
 * @author thevpc
 */
public class AdamLanUtils {

    public static Object convert(Object o, Class type) {
        if (type != null && type != Object.class && type != Void.class && type != Void.TYPE) {
            if (type == String.class) {
                if (o == null) {
                    return null;
                }
                return o.toString();
            } else if (type == Color.class) {
                if (o == null) {
                    return null;
                }
                String h = o.toString();
                try {
                    Field f = Color.class.getDeclaredField(h.toUpperCase());
                    return f.get(null);
                } catch (Exception ex) {
                    //
                }
                return null;
            } else if (type == Integer.class || type == Integer.TYPE) {
                o = Convert.toInt(o, IntegerParserConfig.LENIENT_F);
            } else if (type == Long.class || type == Long.TYPE) {
                o = Convert.toLong(o, LongParserConfig.LENIENT_F);
            } else if (type == Double.class || type == Double.TYPE) {
                o = Convert.toDouble(o, DoubleParserConfig.LENIENT);
            } else if (type == Float.class || type == Float.TYPE) {
                o = Convert.toFloat(o, FloatParserConfig.LENIENT);
            } else {
                o = null;
            }
        }
        return o;
    }

    public static String getVarName(JNode node) {
        return ((JNodeVarName) node).getName();
    }

    public static boolean isVar(JNode node) {
        return node instanceof JNodeVarName;
    }

    public static boolean isAnyVarCI(JNode node, String... names) {
        for (int i = 0; i < names.length; i++) {
            String name = names[i];
            if (isVarCI(node, name)) {
                return true;
            }
        }
        return false;
    }

    public static boolean isVarsCI(List<JNode> node, String... names) {
        for (int i = 0; i < names.length; i++) {
            String name = names[i];
            if (i >= node.size()) {
                return false;
            }
            if (!isVarCI(node.get(i), name)) {
                return false;
            }
        }
        return true;
    }

    public static boolean isVarsCI(JNode[] node, String... names) {
        for (int i = 0; i < names.length; i++) {
            String name = names[i];
            if (i >= node.length) {
                return false;
            }
            if (!isVarCI(node[i], name)) {
                return false;
            }
        }
        return true;
    }

    public static boolean isVarsCI(JToken[] node, String... names) {
        for (int i = 0; i < names.length; i++) {
            String name = names[i];
            if (i >= node.length) {
                return false;
            }
            if (!isVarCI(node[i], name)) {
                return false;
            }
        }
        return true;
    }

    public static boolean isVarCI(JNode node, String name) {
        return node instanceof JNodeVarName
                && StringUtils.normalizeString(((JNodeVarName) node).getName().toLowerCase())
                        .equals(StringUtils.normalizeString(name.toLowerCase()));
    }

    public static boolean isVarCI(JToken node, String name) {
        return node.def.ttype== JTokenType.TT_IDENTIFIER
                && StringUtils.normalizeString(node.image.toLowerCase())
                        .equals(StringUtils.normalizeString(name.toLowerCase()));
    }

    public static JNode[] readWordId(String wordId, JNode[] arr, LangSupport langSupport) {
        String[][] translations = langSupport.getTranslations(wordId);
        for (int i = 0; i < translations.length; i++) {
            String[] translation = translations[i];
            if (AdamLanUtils.isVarsCI(arr, translation)) {
                JNode[] t = new JNode[arr.length - translation.length];
                System.arraycopy(arr, translation.length, t, 0, t.length);
                return t;
            }
        }
        return null;
    }

    public static JToken[] readWordId(String wordId, JToken[] arr, LangSupport langSupport) {
        String[][] translations = langSupport.getTranslations(wordId);
        for (int i = 0; i < translations.length; i++) {
            String[] translation = translations[i];
            if (AdamLanUtils.isVarsCI(arr, translation)) {
                JToken[] t = new JToken[arr.length - translation.length];
                System.arraycopy(arr, translation.length, t, 0, t.length);
                return t;
            }
        }
        return null;
    }

    public static JNode[] removeHead(JNode[] arr) {
        JNode[] t = new JNode[arr.length - 1];
        System.arraycopy(arr, 1, t, 0, t.length);
        return t;
    }

    public static JNode[] toArray(JNode node) {
        if (node == null) {
            return new JNode[0];
        }
        JNode[] n = Utils.ifType(node, JNodeFunctionCall.class, op -> {
            if(op.is("")) {
                return op.getArgs();
//                if (op.isUnary()) {
//                    JNode[] a = op.getOperands();
//                    if (a.length == 1 && a[0] instanceof JNodeArray) {
//                        List<JNode> ok = new ArrayList<JNode>();
//                        for (JNode value : ((JNodeArray) a[0]).getValues()) {
//                            ok.addAll(Arrays.asList(toArray(value)));
//                        }
//                        return ok.toArray(new JNode[ok.size()]);
//                    }
//                    return a;
//                }else{
//                    return op.getOperands();
//                }
            }
            return null;
        });
        if (n == null) {
            n = new JNode[]{node};
        }
        return n;
    }

    public static JNode getAssignExpr(JNode node) {
        return Utils.ifType(node, JNodeFunctionCall.class, (JNodeFunctionCall op) -> {
            if (op.isBinary("=")) {
                return Utils.ifType(op.get(0), JNodeVarName.class, (JNodeVarName v) -> {
                    return op.get(1);
                });
            }
            return null;
        });
    }

    public static String getAssignVar(JNode node) {
        return Utils.ifType(node, JNodeFunctionCall.class, (JNodeFunctionCall op) -> {
            if (op.isBinary("=")) {
                return Utils.ifType(op.get(0), JNodeVarName.class, (JNodeVarName v) -> {
                    return v.getName();
                });
            }
            return null;
        });
    }

    public static boolean isAssign(JNode node) {
        return Utils.ifType(node, JNodeFunctionCall.class, (JNodeFunctionCall op) -> {
            if (op.isBinary("=")) {
                return Utils.ifType(op.get(0), JNodeVarName.class, (JNodeVarName v) -> {
                    return true;
                });
            }
            return false;
        });
    }
}
