/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.vpc.scholar.adamlan;

import net.vpc.common.jeep.ExpressionNode;

/**
 *
 * @author vpc
 */
public interface CommandResolver {

    boolean isEnabled(AdamLanParser context);

    WeightedExpressionNode resolve(ExpressionNode node, AdamLanParser context);
}
