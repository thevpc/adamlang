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
public class MusicNote {

    private int note;
    private int instrument;
    private double time;

    public MusicNote(int note, int instrument, double time) {
        this.setNote(note);
        this.setInstrument(instrument);
        this.setTime(time);
    }

    public int getNote() {
        return note;
    }

    public int getInstrument() {
        return instrument;
    }

    public double getTime() {
        return time;
    }

    public MusicNote setNote(int note) {
        this.note = note;
        return this;
    }

    public MusicNote setInstrument(int instrument) {
        this.instrument = instrument;
        return this;
    }

    public MusicNote setTime(double time) {
        this.time = time;
        return this;
    }
}
