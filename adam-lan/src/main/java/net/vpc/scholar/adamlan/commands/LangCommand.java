/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.vpc.scholar.adamlan.commands;

import net.vpc.common.jeep.ExpressionNode;
import net.vpc.scholar.adamlan.AdamLanParser;
import net.vpc.scholar.adamlan.LangSupport;
import net.vpc.scholar.adamlan.utils.AdamLanUtils;
import net.vpc.scholar.adamlan.CommandResolver;

/**
 *
 * @author vpc
 */
public abstract class LangCommand implements CommandResolver {

    private LangSupport langSupport;

    public LangCommand(LangSupport lang) {
        this.langSupport = lang;
    }

    public LangSupport getLangSupport() {
        return langSupport;
    }

    @Override
    public boolean isEnabled(AdamLanParser context) {
        return context.getLocale().getLanguage().equalsIgnoreCase(langSupport.getLocale().getLanguage());
    }

    public static ExpressionNode[] toArray(ExpressionNode node) {
        return AdamLanUtils.toArray(node);
    }

    public ExpressionNode[] removeHead(ExpressionNode[] arr) {
        return AdamLanUtils.removeHead(arr);
    }

    public ExpressionNode[] readWordId(String wordId, ExpressionNode[] arr) {
        return AdamLanUtils.readWordId(wordId,arr,getLangSupport());
    }

}
