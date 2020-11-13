/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.thevpc.scholar.adamlan.sybsystems;

import net.thevpc.jeep.JContext;
import net.thevpc.common.strings.StringUtils;
import net.thevpc.scholar.adamlan.sybsystems.music.MusicContext;
import net.thevpc.scholar.adamlan.sybsystems.music.MusicNote;
import net.thevpc.scholar.adamlan.sybsystems.music.MusicNotes;
import net.thevpc.scholar.adamlan.sybsystems.music.MusicParser;

import javax.sound.midi.Instrument;
import javax.sound.midi.MidiChannel;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Soundbank;
import javax.sound.midi.Synthesizer;

/**
 * @author thevpc 1
 */
public class AdamLanMusicSubSystem {

    //    public static void main(String[] args) {
//        AdamLanMusicSubSystem a = new AdamLanMusicSubSystem();
//        a.play("i15 do1.5 re/2 i19 mi- do");
//        try {
//            Thread.sleep(3000);
//        } catch (InterruptedException ex) {
//            Logger.getLogger(AdamLanMusicSubSystem.class.getName()).log(Level.SEVERE, null, ex);
//        }
//    }
    private Synthesizer synthesizer;
    private Instrument[] instruments;
    private MidiChannel[] midiChannels;
    private int instrumentIndex;
    private MusicContext[] context;

    public AdamLanMusicSubSystem() {
        try {
            synthesizer = MidiSystem.getSynthesizer();
            synthesizer.open();

            this.midiChannels = synthesizer.getChannels();

            Soundbank bank = synthesizer.getDefaultSoundbank();

            synthesizer.loadAllInstruments(bank);

            this.instruments = synthesizer.getAvailableInstruments();
//            for (int i = 0; i < instruments.length; i++) {
//                System.out.println(i + " : " + this.instruments[i].getName());
//            }
            synthesizer.loadAllInstruments(synthesizer.getDefaultSoundbank());
            synthesizer.getChannels()[0].programChange(instrumentIndex);
            context = new MusicContext[midiChannels.length];
            for (int i = 0; i < context.length; i++) {
                context[i] = new MusicContext();
            }
        } catch (MidiUnavailableException ex) {
            ex.printStackTrace();
        }
    }

    public Synthesizer getSynthesizer() {
        return synthesizer;
    }

    public Instrument[] getInstruments() {
        return instruments;
    }

    public MidiChannel[] getMidiChannels() {
        return midiChannels;
    }

    public int getInstrumentIndex() {
        return instrumentIndex;
    }


    public void changeInstrument(int instrument, int channel) {
        if (instrument < 0 || instrument >= instruments.length) {
            instrument = 0;
        }
        synthesizer.getChannels()[channel].programChange(instrument);
        instrumentIndex = instrument;
    }

    public void play(JContext it, String... text) {
        MusicParser p = new MusicParser();
        MusicNotes[] all = new MusicNotes[text.length];
        for (int i = 0; i < all.length; i++) {
            all[i] = new MusicNotes(i, p.parse(text[i], context[i]));
        }
        play(it,all);
    }

    //    public void play(MusicNotes all) {
//        for (int i = 0; i < all.getNodes().length; i++) {
//            play(all.getNodes()[i], all.getChannel());
//        }
//    }
    public void play(JContext it, MusicNotes... all) {
        if (all.length == 0) {
            return;
        }
        if (all.length == 1) {
            for (int i = 0; i < all[0].getNodes().length; i++) {
                play(all[0].getNodes()[i], all[0].getChannel());
            }
            return;
        }
        Thread[] t = new Thread[all.length];
        for (int i = 0; i < all.length; i++) {
            final int j = i;
            t[i] = new Thread(new Runnable() {
                @Override
                public void run() {
                    for (int i = 0; i < all[j].getNodes().length; i++) {
                        play(all[j].getNodes()[i], all[j].getChannel());
                    }
//                    System.out.println("END "+j);
                }
            });
            t[i].start();
        }
        for (int i = 0; i < t.length; i++) {
            try {
                t[i].join();
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
        }
    }

    public void play(MusicNote n, int channel) {
        play(n.getNote(), (long) (n.getTime() * 1000), n.getInstrument(), channel);
    }

    public static final String[] notes = {"Do", "Re-", "Re", "Mi-","Mi", "Fa", "Sol-","Sol", "La-", "La", "Si-", "Si"};

    public void play(int noteNumber, long millis, int instrument, int channel) {
        if (instrument < 0 || instrument >= instruments.length) {
            instrument = 0;
        }
        if (channel < 0 || channel >= midiChannels.length) {
            channel = 0;
        }
        System.out.println("play #"+(channel+1)+" " + noteNumber+"="+ StringUtils.expand(notes[noteNumber % 12]," ",4) + " for " + StringUtils.expand(millis+""," ",6) + " using " + instrument+"=(" + instruments[instrument].getName() + ")");
        if (millis <= 0) {
            millis = 1000;
        }
        if (channel >= midiChannels.length) {
            channel = 0;
        }
        if (instrument >= 0) {
            changeInstrument(instrument, channel);
        }
        midiChannels[channel].noteOn(noteNumber, 600);
        try {
            Thread.sleep(millis);
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
        midiChannels[channel].noteOff(noteNumber, 600);
    }
}
