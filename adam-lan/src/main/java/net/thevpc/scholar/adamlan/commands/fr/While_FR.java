/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.thevpc.scholar.adamlan.commands.fr;

import net.thevpc.jeep.JNode;
import net.thevpc.jeep.core.nodes.JDefaultNode;
import net.thevpc.jeep.core.nodes.JNodeWhile;
import net.thevpc.scholar.adamlan.AdamLanParser;
import net.thevpc.scholar.adamlan.WeightedExpressionNode;
import net.thevpc.scholar.adamlan.commands.LangCommand;
import net.thevpc.scholar.adamlan.LangSupport;

/**
 *
 * @author thevpc
 */
public class While_FR extends LangCommand {

    public While_FR(LangSupport s) {
        super(s);
    }

    @Override
    public WeightedExpressionNode resolve(JNode node, AdamLanParser context) {
        JNode[] arr = toArray(node);
        JNode[] arr2 = readWordId("while", arr);
        if (arr2 == null) {
            return null;
        }
        arr = arr2;
        if (arr.length == 0) {
            return null;
        }
        JNodeWhile w = new JNodeWhile();
        w.setCondition((JDefaultNode) arr[0]);
        return new WeightedExpressionNode(1, w);
    }

}
