/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.vpc.scholar.adamlan.commands.fr;

import net.vpc.common.jeep.ExpressionNode;
import net.vpc.common.jeep.nodes.ExpressionNodeAssign;
import net.vpc.common.jeep.nodes.ExpressionNodeFunctionCall;
import net.vpc.common.jeep.nodes.ExpressionNodeVariableName;
import net.vpc.common.util.Utils;
import net.vpc.scholar.adamlan.AdamLanParser;
import net.vpc.scholar.adamlan.WeightedExpressionNode;
import net.vpc.scholar.adamlan.commands.LangCommand;
import net.vpc.scholar.adamlan.LangSupport;

/**
 *
 * @author vpc
 */
public class Assign_FR extends LangCommand {

    public Assign_FR(LangSupport s) {
        super(s);
    }


    @Override
    public WeightedExpressionNode resolve(ExpressionNode node, AdamLanParser context) {
        ExpressionNode[] arr = toArray(node);
        ExpressionNode[] args = readWordId("let", arr);
        if (args == null) {
            args = arr;
        }
        if (args.length == 1) {
            return Utils.ifType(args[0], ExpressionNodeFunctionCall.class, op -> {
                if (op.isBinary("=")) {
                    return Utils.ifType(op.get(0), ExpressionNodeVariableName.class, v -> {
                        return new WeightedExpressionNode(1, new ExpressionNodeAssign(v.getName(), op.get(1)));
                    });
                }
                return null;
            });
        }
        return null;
    }

}
