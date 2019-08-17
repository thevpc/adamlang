/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.vpc.scholar.adamlan.commands.en;

import java.util.Locale;
import net.vpc.scholar.adamlan.AbstractLangSupport;
import net.vpc.scholar.adamlan.LangSupport;
import net.vpc.scholar.adamlan.commands.fr.Assign_FR;
import net.vpc.scholar.adamlan.commands.fr.Else_FR;
import net.vpc.scholar.adamlan.commands.fr.End_FR;
import net.vpc.scholar.adamlan.commands.fr.For_FR;
import net.vpc.scholar.adamlan.commands.fr.If_FR;
import net.vpc.scholar.adamlan.commands.fr.While_FR;

/**
 *
 * @author vpc
 */
public class EnglishLanSupport extends AbstractLangSupport {
    public static final LangSupport INSTANCE=new EnglishLanSupport();

    private EnglishLanSupport() {
        super(Locale.ENGLISH);
        addResolver(new Assign_FR(this));
        addResolver(new While_FR(this));
        addResolver(new If_FR(this));
        addResolver(new For_FR(this));
        addResolver(new End_FR(this));
        addResolver(new Else_FR(this));
    }

    public String[][] getTranslations(String wordId) {
        String[][] found = super.getTranslations(wordId);
        if (found.length == 0) {
            return new String[][]{{wordId}};
        }
        return found;
    }

}
