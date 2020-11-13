/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.thevpc.scholar.adamlan.sybsystems.music;

import net.thevpc.common.strings.StringUtils;
import net.thevpc.scholar.adamlan.utils.StringBuilder2;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

/**
 * @author thevpc
 */
public class MusicParser {

    @FunctionalInterface
    interface ValConsumer {

        void consume(Val v);
    }

    class Val {

        List<MusicNote> list = new ArrayList<>();
        MusicContext context;
        int note = -1000;
        int alt = 0;
        double time = 1;
        private ValConsumer consumer;

        void consume() {
            if (getConsumer() != null) {
                getConsumer().consume(this);
            }
            setConsumer(null);
            note = -1000;
            alt = 0;
            time = 1;
        }

        public ValConsumer getConsumer() {
            return consumer;
        }

        public Val setConsumer(ValConsumer consumer) {
            this.consumer = consumer;
            return this;
        }
    }

    public MusicNote[] parse(String music, MusicContext context) {
        if (music == null) {
            music = "";
        }
        music = StringUtils.normalizeString(music);
        ValConsumer noteConsumer = x -> x.list.add(new MusicNote(
                    x.context.trans + x.note + x.alt + 12 * x.context.octave + 60,
                    x.context.instrument, x.time * 60 / (x.context.tempo <= 0 ? 100 : x.context.tempo)));
        ;
        ValConsumer noteConsumer2 = x -> x.list.add(new MusicNote(x.context.trans + x.note + x.alt, x.context.instrument, x.time * 60 / (x.context.tempo == 0 ? 100 : x.context.tempo)));
        ValConsumer pauseConsumer = x -> x.list.add(new MusicNote(-1, x.context.instrument, x.time * 60 / (x.context.tempo == 0 ? 100 : x.context.tempo)));
        StringTokenizer t = new StringTokenizer(music, " ,|");
        Val v = new Val();
        v.context = context;
        while (t.hasMoreElements()) {
            v.consume();
            StringBuilder2 e = new StringBuilder2(t.nextToken());
            e = e.toLowerCase();
            if (e.toString().equals("0") || e.toString().equals("reinitialiser") || e.toString().equals("init")) {
                context.instrument = 0;
                context.octave = 0;
                context.tempo = 100;
                e.clear();
            }
            while (!e.isEmpty()) {
                if (e.deleteHead("<")) {
                    v.context.octave--;
                } else if (e.deleteHead(">")) {
                    v.context.octave++;
                } else if (e.deleteHead("do")) {
                    v.consume();
                    v.alt = 0;
                    if (e.deleteHead("b")) {
                        v.alt--;
                    }
                    v.note = 0;
                    v.setConsumer(noteConsumer);
                } else if (e.deleteHead("re")) {
                    v.consume();
                    v.alt = 0;
                    if (e.deleteHead("b")) {
                        v.alt--;
                    }
                    v.note = 2;
                    v.setConsumer(noteConsumer);
                } else if (e.deleteHead("mi")) {
                    v.consume();
                    v.alt = 0;
                    if (e.deleteHead("b")) {
                        v.alt--;
                    }
                    v.note = 4;
                    v.setConsumer(noteConsumer);
                } else if (e.deleteHead("fa")) {
                    v.consume();
                    v.alt = 0;
                    if (e.deleteHead("b")) {
                        v.alt--;
                    }
                    v.note = 5;
                    v.setConsumer(noteConsumer);
                } else if (e.deleteHead("sol")) {
                    v.consume();
                    v.alt = 0;
                    if (e.deleteHead("b")) {
                        v.alt--;
                    }
                    v.note = 7;
                    v.setConsumer(noteConsumer);
                } else if (e.deleteHead("la")) {
                    v.consume();
                    v.alt = 0;
                    if (e.deleteHead("b")) {
                        v.alt--;
                    }
                    v.note = 9;
                    v.setConsumer(noteConsumer);
                } else if (e.deleteHead("si")) {
                    v.consume();
                    v.alt = 0;
                    if (e.deleteHead("b")) {
                        v.alt--;
                    }
                    v.note = 11;
                    v.setConsumer(noteConsumer);
                } else if (e.deleteHead("instrument") || e.deleteHead("i")) {
                    v.consume();
                    Integer ii = e.readInt();
                    if (ii != null) {
                        v.context.instrument = ii;
                    }
                } else if (e.deleteHead("node") || e.deleteHead("n")) {
                    v.consume();
                    String tt = "";
                    if(e.deleteHead("+")) {
                        tt = "+";
                    }else if(e.deleteHead("-")){
                        tt="-";
                    }
                    Integer ii = e.readInt();
                    if (ii != null) {
                        if (tt.equals("+")) {
                            v.context.trans = ii;
                        }else if (tt.equals("-")) {
                            v.context.trans = -ii;
                        } else {
                            v.setConsumer(noteConsumer2);
                            v.note = ii;
                        }
                    }
                } else if (e.deleteHead("tempo") || e.deleteHead("t")) {
                    v.consume();
                    Integer ii = e.readInt();
                    if (ii != null) {
                        v.context.tempo = ii;
                    }
                } else if (e.deleteHead("pause") || e.deleteHead("silence") || e.deleteHead("_")) {
                    v.consume();
                    v.note = -1;
                    v.alt = 0;
                    v.setConsumer(pauseConsumer);
                } else if (e.deleteHead("#")) {
                    v.alt++;
                } else if (e.deleteHead("-")) {
                    v.alt--;
                } else if (e.startsWith("'")) {
                    v.time = 0;
                    for (int i = 5; i > 0; i--) {
                        if (e.deleteHead(StringUtils.fillString('\'', i))) {
                            v.time = 1.0 / (1 << i);
                            break;
                        }
                    }
                    if (v.time == 0) {
                        e = e.deleteHead(1);
                        v.time = 1;
                    }
                } else if (e.startsWith("!")) {
                    v.time = 0;
                    for (int i = 5; i > 0; i--) {
                        if (e.deleteHead(StringUtils.fillString('!', i))) {
                            v.time = 1.0 * (1 << i);
                            break;
                        }
                    }
                    if (v.time == 0) {
                        e = e.deleteHead(1);
                        v.time = 1;
                    }
                } else if (e.isDigit(0)) {
                    v.time = 0;
                    Double r = e.readDouble();
                    if (r != null) {
                        v.time = r;
                        if (e.deleteHead("/")) {
                            Double q = e.readDouble();
                            if (q == null) {
                                q = 1.0;
                            }
                            v.time = r / q;
                        }
                    }
                    if (v.time == 0) {
                        e = e.deleteHead(1);
                        v.time = 1;
                    }
                } else if (e.deleteHead("/")) {
                    v.time = 0;
                    Double r = e.readDouble();
                    if (r != null) {
                        v.time = 1.0 / r;
                    }
                    if (v.time == 0) {
                        e = e.deleteHead(1);
                        v.time = 1;
                    }
                } else if (e.deleteHead("*")) {
                    v.time = 0;
                    Double r = e.readDouble();
                    if (r != null) {
                        v.time = r;
                    } else {
                        e = e.deleteHead(1);
                        v.time = 1;
                    }
                }else{
                    e = e.deleteHead(1);
                }
            }
        }
        v.consume();
        return v.list.toArray(new MusicNote[v.list.size()]);
    }
}
