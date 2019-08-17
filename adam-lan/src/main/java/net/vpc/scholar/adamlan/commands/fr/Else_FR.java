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
import net.vpc.common.jeep.nodes.ExpressionNodeElse;

/**
 *
 * @author vpc
 */
public class Else_FR extends LangCommand {

    public Else_FR(LangSupport s) {
        super(s);
    }

    @Override
    public WeightedExpressionNode resolve(ExpressionNode node, AdamLanParser context) {
        ExpressionNode[] args = readWordId("else", toArray(node));
        if (args != null) {
            //should check args?
            ExpressionNodeElse w = new ExpressionNodeElse();
            return new WeightedExpressionNode(1, w);
        }
        return null;
    }

}
