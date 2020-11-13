/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.thevpc.scholar.adamlan.commands.fr;

import net.thevpc.jeep.JNode;
import net.thevpc.jeep.core.nodes.JNodeElse;
import net.thevpc.scholar.adamlan.AdamLanParser;
import net.thevpc.scholar.adamlan.WeightedExpressionNode;
import net.thevpc.scholar.adamlan.commands.LangCommand;
import net.thevpc.scholar.adamlan.LangSupport;

/**
 *
 * @author thevpc
 */
public class Else_FR extends LangCommand {

    public Else_FR(LangSupport s) {
        super(s);
    }

    @Override
    public WeightedExpressionNode resolve(JNode node, AdamLanParser context) {
        JNode[] args = readWordId("else", toArray(node));
        if (args != null) {
            //should check args?
            JNodeElse w = new JNodeElse();
            return new WeightedExpressionNode(1, w);
        }
        return null;
    }

}
