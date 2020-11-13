/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.thevpc.scholar.adamlan.commands.fr;

import net.thevpc.jeep.JNode;
import net.thevpc.jeep.core.nodes.JDefaultNode;
import net.thevpc.jeep.core.nodes.JNodeFor;
import net.thevpc.scholar.adamlan.AdamLanParser;
import net.thevpc.scholar.adamlan.utils.AdamLanUtils;
import net.thevpc.scholar.adamlan.WeightedExpressionNode;
import net.thevpc.scholar.adamlan.commands.LangCommand;
import net.thevpc.scholar.adamlan.LangSupport;

/**
 * for i=48 to 36 end
 *
 * @author thevpc
 */
public class For_FR extends LangCommand {

    public For_FR(LangSupport s) {
        super(s);
    }

    @Override
    public WeightedExpressionNode resolve(JNode node, AdamLanParser context) {
        JNode[] arr = toArray(node);
        JNode[] arr2 = readWordId("for", arr);
        if (arr2 == null) {
            return null;
        }
        arr = arr2;
        JNodeFor w = new JNodeFor();
        if (AdamLanUtils.isAssign(arr[0])) {
            w.setName(AdamLanUtils.getAssignVar(arr[0]));
            w.setFrom((JDefaultNode) AdamLanUtils.getAssignExpr(arr[0]));
            arr = removeHead(arr);
        } else if (AdamLanUtils.isVar(arr[0])) {
            w.setName(AdamLanUtils.getVarName(arr[0]));
            arr = removeHead(arr);
            arr2 = readWordId("from", arr);
            if (arr2 == null || arr2.length == 0) {
                return null;
            }
            arr = arr2;
            w.setFrom((JDefaultNode) arr[0]);
            arr = removeHead(arr);
        } else {
            return null;
        }
        arr2 = readWordId("to", arr);
        if (arr2 == null || arr2.length == 0) {
            return null;
        }
        arr = arr2;
        w.setTo((JDefaultNode) arr[0]);
        arr = removeHead(arr);
        if (arr.length == 0) {
            return new WeightedExpressionNode(1, w);
        } else {
            arr2 = readWordId("by", arr);
            if (arr2 == null || arr2.length == 0) {
                return null;
            }
            w.setBy((JDefaultNode) arr2[0]);
            return new WeightedExpressionNode(1, w);
        }
    }

}
