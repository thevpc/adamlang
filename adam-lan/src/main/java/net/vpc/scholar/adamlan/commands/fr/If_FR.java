/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.vpc.scholar.adamlan.commands.fr;

import net.vpc.common.jeep.ExpressionNode;
import net.vpc.common.jeep.nodes.ExpressionNodeIf;
import net.vpc.scholar.adamlan.AdamLanParser;
import net.vpc.scholar.adamlan.WeightedExpressionNode;
import net.vpc.scholar.adamlan.commands.LangCommand;
import net.vpc.scholar.adamlan.LangSupport;

/**
 *
 * @author vpc
 */
public class If_FR extends LangCommand {

    public If_FR(LangSupport s) {
        super(s);
    }

    @Override
    public WeightedExpressionNode resolve(ExpressionNode node, AdamLanParser context) {
        ExpressionNode[] arr = toArray(node);
        ExpressionNode[] arr2 = readWordId("if", arr);
        if (arr2 == null) {
            return null;
        }
        arr = arr2;
        if (arr.length == 0) {
            return null;
        }
        ExpressionNodeIf w = new ExpressionNodeIf();
        w.setCondition(arr[0]);
        return new WeightedExpressionNode(1, w);
    }

}
