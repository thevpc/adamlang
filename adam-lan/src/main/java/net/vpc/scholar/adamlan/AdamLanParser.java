/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.vpc.scholar.adamlan;

import java.util.Locale;
import net.vpc.common.jeep.ExpressionManager;

/**
 *
 * @author vpc
 */
public interface AdamLanParser extends ExpressionManager {

//    ExpressionNode parse(String code);

    Locale getLocale();

    Locale detectLocale(String text);

    LangSupport getLangSupport();
    
    AdamLanExecEnv env();

    Thread run(Runnable runnable);
}
