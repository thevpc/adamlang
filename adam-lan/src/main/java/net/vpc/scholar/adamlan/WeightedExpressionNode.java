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
public class WeightedExpressionNode {

    private int weight;
    private ExpressionNode node;

    public WeightedExpressionNode(int weight, ExpressionNode node) {
        this.weight = weight;
        this.node = node;
    }

    public int getWeight() {
        return weight;
    }

    public ExpressionNode getNode() {
        return node;
    }

}
