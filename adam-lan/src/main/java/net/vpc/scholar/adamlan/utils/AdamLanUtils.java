/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.vpc.scholar.adamlan.utils;

import java.awt.Color;
import java.lang.reflect.Field;
import java.util.List;

import net.vpc.common.jeep.*;
import net.vpc.common.jeep.nodes.ExpressionNodeFunctionCall;
import net.vpc.common.jeep.nodes.ExpressionNodeVariableName;
import net.vpc.common.strings.StringUtils;
import net.vpc.common.util.Convert;
import net.vpc.common.util.DoubleParserConfig;
import net.vpc.common.util.FloatParserConfig;
import net.vpc.common.util.IntegerParserConfig;
import net.vpc.common.util.LongParserConfig;
import net.vpc.common.util.Utils;
import net.vpc.scholar.adamlan.LangSupport;

/**
 *
 * @author vpc
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

    public static String getVarName(ExpressionNode node) {
        return ((ExpressionNodeVariableName) node).getName();
    }

    public static boolean isVar(ExpressionNode node) {
        return node instanceof ExpressionNodeVariableName;
    }

    public static boolean isAnyVarCI(ExpressionNode node, String... names) {
        for (int i = 0; i < names.length; i++) {
            String name = names[i];
            if (isVarCI(node, name)) {
                return true;
            }
        }
        return false;
    }

    public static boolean isVarsCI(List<ExpressionNode> node, String... names) {
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

    public static boolean isVarsCI(ExpressionNode[] node, String... names) {
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

    public static boolean isVarsCI(ExpressionStreamTokenizer.Token[] node, String... names) {
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

    public static boolean isVarCI(ExpressionNode node, String name) {
        return node instanceof ExpressionNodeVariableName
                && StringUtils.normalizeString(((ExpressionNodeVariableName) node).getName().toLowerCase())
                        .equals(StringUtils.normalizeString(name.toLowerCase()));
    }

    public static boolean isVarCI(ExpressionStreamTokenizer.Token node, String name) {
        return node.ttype==ExpressionStreamTokenizer.TT_WORD
                && StringUtils.normalizeString(node.image.toLowerCase())
                        .equals(StringUtils.normalizeString(name.toLowerCase()));
    }

    public static ExpressionNode[] readWordId(String wordId, ExpressionNode[] arr, LangSupport langSupport) {
        String[][] translations = langSupport.getTranslations(wordId);
        for (int i = 0; i < translations.length; i++) {
            String[] translation = translations[i];
            if (AdamLanUtils.isVarsCI(arr, translation)) {
                ExpressionNode[] t = new ExpressionNode[arr.length - translation.length];
                System.arraycopy(arr, translation.length, t, 0, t.length);
                return t;
            }
        }
        return null;
    }

    public static ExpressionStreamTokenizer.Token[] readWordId(String wordId, ExpressionStreamTokenizer.Token[] arr, LangSupport langSupport) {
        String[][] translations = langSupport.getTranslations(wordId);
        for (int i = 0; i < translations.length; i++) {
            String[] translation = translations[i];
            if (AdamLanUtils.isVarsCI(arr, translation)) {
                ExpressionStreamTokenizer.Token[] t = new ExpressionStreamTokenizer.Token[arr.length - translation.length];
                System.arraycopy(arr, translation.length, t, 0, t.length);
                return t;
            }
        }
        return null;
    }

    public static ExpressionNode[] removeHead(ExpressionNode[] arr) {
        ExpressionNode[] t = new ExpressionNode[arr.length - 1];
        System.arraycopy(arr, 1, t, 0, t.length);
        return t;
    }

    public static ExpressionNode[] toArray(ExpressionNode node) {
        if (node == null) {
            return new ExpressionNode[0];
        }
        ExpressionNode[] n = Utils.ifType(node, ExpressionNodeFunctionCall.class, op -> {
            if(op.is("")) {
                return op.getOperands();
//                if (op.isUnary()) {
//                    ExpressionNode[] a = op.getOperands();
//                    if (a.length == 1 && a[0] instanceof ExpressionNodeArray) {
//                        List<ExpressionNode> ok = new ArrayList<ExpressionNode>();
//                        for (ExpressionNode value : ((ExpressionNodeArray) a[0]).getValues()) {
//                            ok.addAll(Arrays.asList(toArray(value)));
//                        }
//                        return ok.toArray(new ExpressionNode[ok.size()]);
//                    }
//                    return a;
//                }else{
//                    return op.getOperands();
//                }
            }
            return null;
        });
        if (n == null) {
            n = new ExpressionNode[]{node};
        }
        return n;
    }

    public static ExpressionNode getAssignExpr(ExpressionNode node) {
        return Utils.ifType(node, ExpressionNodeFunctionCall.class, (ExpressionNodeFunctionCall op) -> {
            if (op.isBinary("=")) {
                return Utils.ifType(op.get(0), ExpressionNodeVariableName.class, (ExpressionNodeVariableName v) -> {
                    return op.get(1);
                });
            }
            return null;
        });
    }

    public static String getAssignVar(ExpressionNode node) {
        return Utils.ifType(node, ExpressionNodeFunctionCall.class, (ExpressionNodeFunctionCall op) -> {
            if (op.isBinary("=")) {
                return Utils.ifType(op.get(0), ExpressionNodeVariableName.class, (ExpressionNodeVariableName v) -> {
                    return v.getName();
                });
            }
            return null;
        });
    }

    public static boolean isAssign(ExpressionNode node) {
        return Utils.ifType(node, ExpressionNodeFunctionCall.class, (ExpressionNodeFunctionCall op) -> {
            if (op.isBinary("=")) {
                return Utils.ifType(op.get(0), ExpressionNodeVariableName.class, (ExpressionNodeVariableName v) -> {
                    return true;
                });
            }
            return false;
        });
    }
}
