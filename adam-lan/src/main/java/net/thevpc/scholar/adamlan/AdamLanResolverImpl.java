/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.thevpc.scholar.adamlan;

import net.thevpc.jeep.*;
import net.thevpc.jeep.impl.functions.JSignature;
import net.thevpc.jeep.util.JTypeUtils;

/**
 *
 * @author thevpc
 */
class AdamLanResolverImpl implements JResolver {

    public AdamLanResolverImpl() {
    }

    @Override
    public JFunction resolveFunction(String name, JTypePattern[] argTypes, JContext context) {
        AdamLanParser p = (AdamLanParser) context;
        String n = p.getLangSupport().translateWord(name);
        if (n != null && !n.equals(name)) {
            //TODO caller info should not be null!!
            JFunction JFunction = context.functions().findFunctionMatchOrNull(
                    JSignature.of(n, JTypeUtils.typesOrError(argTypes)),null);
            if(JFunction !=null){
                return JFunction;
            }
        }
        return null;
    }

    @Override
    public JVar resolveVariable(String name, JContext context) {
        //variable not found, define implicitly
        return context.vars().declareVar(name, Object.class, null);
    }

}
