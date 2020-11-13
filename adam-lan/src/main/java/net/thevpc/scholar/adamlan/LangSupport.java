/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.thevpc.scholar.adamlan;

import java.util.Locale;

/**
 *
 * @author thevpc
 */
public interface LangSupport {

    Locale getLocale();

    CommandResolver[] getResolvers();

    int getSupportLevel(String code);

    String translateWord(String localizedWord);

    String[][] getTranslations(String wordId);
}
