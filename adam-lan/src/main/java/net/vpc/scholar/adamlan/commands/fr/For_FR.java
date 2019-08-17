/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.vpc.scholar.adamlan.commands.fr;

import net.vpc.common.jeep.ExpressionNode;
import net.vpc.common.jeep.nodes.ExpressionNodeFor;
import net.vpc.scholar.adamlan.AdamLanParser;
import net.vpc.scholar.adamlan.utils.AdamLanUtils;
import net.vpc.scholar.adamlan.WeightedExpressionNode;
import net.vpc.scholar.adamlan.commands.LangCommand;
import net.vpc.scholar.adamlan.LangSupport;

/**
 * for i=48 to 36 end
 *
 * @author vpc
 */
public class For_FR extends LangCommand {

    public For_FR(LangSupport s) {
        super(s);
    }

    @Override
    public WeightedExpressionNode resolve(ExpressionNode node, AdamLanParser context) {
        ExpressionNode[] arr = toArray(node);
        ExpressionNode[] arr2 = readWordId("for", arr);
        if (arr2 == null) {
            return null;
        }
        arr = arr2;
        ExpressionNodeFor w = new ExpressionNodeFor();
        if (AdamLanUtils.isAssign(arr[0])) {
            w.setName(AdamLanUtils.getAssignVar(arr[0]));
            w.setFrom(AdamLanUtils.getAssignExpr(arr[0]));
            arr = removeHead(arr);
        } else if (AdamLanUtils.isVar(arr[0])) {
            w.setName(AdamLanUtils.getVarName(arr[0]));
            arr = removeHead(arr);
            arr2 = readWordId("from", arr);
            if (arr2 == null || arr2.length == 0) {
                return null;
            }
            arr = arr2;
            w.setFrom(arr[0]);
            arr = removeHead(arr);
        } else {
            return null;
        }
        arr2 = readWordId("to", arr);
        if (arr2 == null || arr2.length == 0) {
            return null;
        }
        arr = arr2;
        w.setTo(arr[0]);
        arr = removeHead(arr);
        if (arr.length == 0) {
            return new WeightedExpressionNode(1, w);
        } else {
            arr2 = readWordId("by", arr);
            if (arr2 == null || arr2.length == 0) {
                return null;
            }
            w.setBy(arr2[0]);
            return new WeightedExpressionNode(1, w);
        }
    }

}
