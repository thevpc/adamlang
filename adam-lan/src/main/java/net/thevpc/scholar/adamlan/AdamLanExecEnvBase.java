/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.thevpc.scholar.adamlan;

import net.thevpc.scholar.adamlan.utils.AdamLanUtils;
import java.io.PrintStream;
import javax.swing.JOptionPane;

/**
 *
 * @author thevpc
 */
public class AdamLanExecEnvBase implements AdamLanExecEnv {

    @Override
    public void println(Object msg) {
        out().println(msg);
    }

    @Override
    public void print(Object msg) {
        out().print(msg);
    }

    @Override
    public Object read(Class type, String title) {
        if (type == null) {
            type = String.class;
        }
        Object o = JOptionPane.showInputDialog(null, title, title, JOptionPane.QUESTION_MESSAGE);
        return AdamLanUtils.convert(o, type);
    }

    public PrintStream out() {
        return System.out;
    }

}
