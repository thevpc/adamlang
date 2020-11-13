/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.thevpc.scholar.adamlan.commands.fr;

import net.thevpc.jeep.JNode;
import net.thevpc.jeep.core.nodes.JNodeAssign;
import net.thevpc.jeep.core.nodes.JNodeFunctionCall;
import net.thevpc.jeep.core.nodes.JNodeVarName;
import net.thevpc.common.util.Utils;
import net.thevpc.scholar.adamlan.AdamLanParser;
import net.thevpc.scholar.adamlan.WeightedExpressionNode;
import net.thevpc.scholar.adamlan.commands.LangCommand;
import net.thevpc.scholar.adamlan.LangSupport;

/**
 *
 * @author thevpc
 */
public class Assign_FR extends LangCommand {

    public Assign_FR(LangSupport s) {
        super(s);
    }


    @Override
    public WeightedExpressionNode resolve(JNode node, AdamLanParser context) {
        JNode[] arr = toArray(node);
        JNode[] args = readWordId("let", arr);
        if (args == null) {
            args = arr;
        }
        if (args.length == 1) {
            return Utils.ifType(args[0], JNodeFunctionCall.class, op -> {
                if (op.isBinary("=")) {
                    return Utils.ifType(op.get(0), JNodeVarName.class, v -> {
                        return new WeightedExpressionNode(1, new JNodeAssign(
                                new JNodeVarName(v.getName()), op.get(1)));
                    });
                }
                return null;
            });
        }
        return null;
    }

}
