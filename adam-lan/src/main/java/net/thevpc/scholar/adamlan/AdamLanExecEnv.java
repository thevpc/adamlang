/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.thevpc.scholar.adamlan;

/**
 *
 * @author thevpc
 */
public interface AdamLanExecEnv {

    void println(Object msg);

    void print(Object msg);

    Object read(Class type, String title);
}
