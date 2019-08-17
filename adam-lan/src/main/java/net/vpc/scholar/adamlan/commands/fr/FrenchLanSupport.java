/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.vpc.scholar.adamlan.commands.fr;

import java.util.Locale;
import net.vpc.scholar.adamlan.AbstractLangSupport;
import net.vpc.scholar.adamlan.LangSupport;

/**
 *
 * @author vpc
 */
public class FrenchLanSupport extends AbstractLangSupport {
    public static final LangSupport INSTANCE=new FrenchLanSupport();
    private FrenchLanSupport() {
        super(Locale.FRENCH);
        addResolver(new Assign_FR(this));
        addResolver(new While_FR(this));
        addResolver(new If_FR(this));
        addResolver(new For_FR(this));
        addResolver(new End_FR(this));
        addResolver(new Else_FR(this));
    }

}
