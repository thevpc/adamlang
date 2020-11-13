/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.thevpc.scholar.adamlan.commands;

import net.thevpc.jeep.JNode;
import net.thevpc.scholar.adamlan.AdamLanParser;
import net.thevpc.scholar.adamlan.LangSupport;
import net.thevpc.scholar.adamlan.utils.AdamLanUtils;
import net.thevpc.scholar.adamlan.CommandResolver;

/**
 *
 * @author thevpc
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

    public static JNode[] toArray(JNode node) {
        return AdamLanUtils.toArray(node);
    }

    public JNode[] removeHead(JNode[] arr) {
        return AdamLanUtils.removeHead(arr);
    }

    public JNode[] readWordId(String wordId, JNode[] arr) {
        return AdamLanUtils.readWordId(wordId,arr,getLangSupport());
    }

}
