/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.thevpc.scholar.adamlan;

import java.util.Locale;
import net.thevpc.jeep.Jeep;

/**
 *
 * @author thevpc
 */
public interface AdamLanParser extends Jeep {

//    JNode parse(String code);

    Locale getLocale();

    Locale detectLocale(String text);

    LangSupport getLangSupport();
    
    AdamLanExecEnv env();

    Thread run(Runnable runnable);
}
