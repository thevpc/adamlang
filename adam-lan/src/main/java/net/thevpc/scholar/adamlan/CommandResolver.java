/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.thevpc.scholar.adamlan;

import net.thevpc.jeep.JNode;

/**
 *
 * @author thevpc
 */
public interface CommandResolver {

    boolean isEnabled(AdamLanParser context);

    WeightedExpressionNode resolve(JNode node, AdamLanParser context);
}
