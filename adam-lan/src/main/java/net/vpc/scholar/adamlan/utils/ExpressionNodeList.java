/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.vpc.scholar.adamlan.utils;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.vpc.common.jeep.ExpressionNode;
import net.vpc.common.jeep.ExpressionEvaluator;
import net.vpc.scholar.adamlan.AdamLanParser;

/**
 *
 * @author vpc
 */
public class ExpressionNodeList {

    private List<ExpressionNode> list = new ArrayList<>();
    private ExpressionEvaluator ev;

    public ExpressionNodeList(ExpressionEvaluator ev, ExpressionNode[] all) {
        this.ev = ev;
        this.list.addAll(Arrays.asList(all));
    }

    public ExpressionNode pop() {
        return list.remove(0);
    }

    public ExpressionNode peek() {
        if(list.isEmpty()){
            return null;
        }
        return list.get(0);
    }

    public Object evalAndRemoveHead() {
        ExpressionNode i = list.remove(0);
        return i.evaluate(ev);
    }

    public int evalAndRemoveHeadInt() {
        return (Integer) AdamLanUtils.convert(evalAndRemoveHead(), Integer.class);
    }

    public long evalAndRemoveHeadLong() {
        return (Integer) AdamLanUtils.convert(evalAndRemoveHead(), Long.class);
    }

    public String evalAndRemoveHeadString() {
        return (String) AdamLanUtils.convert(evalAndRemoveHead(), String.class);
    }

    public Color evalAndRemoveHeadColor() {
        return (Color) AdamLanUtils.convert(evalAndRemoveHead(), Color.class);
    }

    public boolean isEmpty() {
        return list.isEmpty();
    }
    
    public boolean removeLocalizedVar(String name) {
        AdamLanParser ee = (AdamLanParser)ev.getExpressionManager();
        String[][] name2 = ee.getLangSupport().getTranslations(name);
        for (int i = 0; i < name2.length; i++) {
            if(AdamLanUtils.isVarsCI(list, name2[i])){
                for (int j = 0; j < name2[i].length; j++) {
                    list.remove(i);
                }
                return true;
            }
        }
        return false;
    }

    public boolean removeAnyVar(String... names) {
        if (isAnyVar(names)) {
            list.remove(0);
            return true;
        }
        return false;
    }

    public boolean isAnyVar(String... names) {
        return AdamLanUtils.isAnyVarCI(list.get(0), names);
    }
}
