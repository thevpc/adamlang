/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.thevpc.scholar.adamlan.commands.fr;

import net.thevpc.jeep.JNode;
import net.thevpc.scholar.adamlan.AdamLanParser;
import net.thevpc.scholar.adamlan.WeightedExpressionNode;
import net.thevpc.scholar.adamlan.commands.LangCommand;
import net.thevpc.scholar.adamlan.LangSupport;
import net.thevpc.jeep.core.nodes.JNodeEnd;

/**
 *
 * @author thevpc
 */
public class End_FR extends LangCommand {

    public End_FR(LangSupport s) {
        super(s);
    }

    @Override
    public WeightedExpressionNode resolve(JNode node, AdamLanParser context) {
        JNode[] args = readWordId("end", toArray(node));
        if (args != null) {
            return new WeightedExpressionNode(1, new JNodeEnd());
        }
        return null;
    }

}
