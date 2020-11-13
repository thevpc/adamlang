/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.thevpc.scholar.adamlan.ide.editor;

import net.thevpc.scholar.adamlan.AdamLanExecEnv;

/**
 *
 * @author thevpc
 */
public class NbAdamLanExecContext implements AdamLanExecEnv {

    @Override
    public void println(Object msg) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void print(Object msg) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Object read(Class type, String title) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

//    PrintStream out;
//
//    @Override
//    public PrintStream out() {
//        if (out == null) {
//            OutputWriter w = IOProvider.getDefault().getIO("AdamLAn", true).getOut();
//            OutputStream os = new WriterOutputStream(w);
//            out = new PrintStream(os);
//        }
//        return out;
//    }
    

}
