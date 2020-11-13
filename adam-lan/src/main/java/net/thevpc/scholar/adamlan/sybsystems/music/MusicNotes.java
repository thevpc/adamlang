/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.thevpc.scholar.adamlan.sybsystems.music;

/**
 *
 * @author thevpc
 */
public class MusicNotes {
    private int channel;
    private MusicNote[] nodes;

    public MusicNotes(int channel, MusicNote[] nodes) {
        this.channel = channel;
        this.nodes = nodes;
    }

    public int getChannel() {
        return channel;
    }

    public MusicNote[] getNodes() {
        return nodes;
    }
    
}
