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
public class WeightedExpressionNode {

    private int weight;
    private JNode node;

    public WeightedExpressionNode(int weight, JNode node) {
        this.weight = weight;
        this.node = node;
    }

    public int getWeight() {
        return weight;
    }

    public JNode getNode() {
        return node;
    }

}
