/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.thevpc.scholar.adamlan.test;

import java.io.File;
import net.thevpc.scholar.adamlan.AdamLanParserImpl;

/**
 *
 * @author thevpc
 */
public class AdamLanExample {

    public static void main(String[] args) {
        AdamLanParserImpl p = new AdamLanParserImpl();
//        Object e = p.evaluate("say \"are you ok?\"\n write \"Ok\"");
        Object e = p.evaluate(new File("example.al"));
        System.out.println("...THE END...");
//        try {
//            Thread.sleep(30000);
//        } catch (InterruptedException ex) {
//            ex.printStackTrace();
//        }
    }
}
