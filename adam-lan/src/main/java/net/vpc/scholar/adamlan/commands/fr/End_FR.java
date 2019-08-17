/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.vpc.scholar.adamlan.commands.fr;

import net.vpc.common.jeep.ExpressionNode;
import net.vpc.scholar.adamlan.AdamLanParser;
import net.vpc.scholar.adamlan.WeightedExpressionNode;
import net.vpc.scholar.adamlan.commands.LangCommand;
import net.vpc.scholar.adamlan.LangSupport;
import net.vpc.common.jeep.nodes.ExpressionNodeEnd;

/**
 *
 * @author vpc
 */
public class End_FR extends LangCommand {

    public End_FR(LangSupport s) {
        super(s);
    }

    @Override
    public WeightedExpressionNode resolve(ExpressionNode node, AdamLanParser context) {
        ExpressionNode[] args = readWordId("end", toArray(node));
        if (args != null) {
            return new WeightedExpressionNode(1, new ExpressionNodeEnd());
        }
        return null;
    }

}
