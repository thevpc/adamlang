/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.vpc.scholar.adamlan;

import net.vpc.common.jeep.*;

/**
 *
 * @author vpc
 */
class AdamLanResolverImpl extends AbstractExpressionEvaluatorResolver {

    public AdamLanResolverImpl() {
    }

    @Override
    public Function resolveFunction(String name, ExpressionNode[] args, ExpressionManager context) {
        AdamLanParser p = (AdamLanParser) context;
        String n = p.getLangSupport().translateWord(name);
        if (n != null && !n.equals(name)) {
            Function function = context.findFunction(n, args);
            if(function!=null){
                return function;
            }
        }
        return null;
    }

    @Override
    public Variable resolveVariable(String name, ExpressionManager context) {
        //variable not found, define implicitely
        return context.declareVar(name, Object.class, null);
    }

}
