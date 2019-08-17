/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.vpc.scholar.adamlan.sybsystems;

import javax.sound.sampled.AudioInputStream;

import marytts.LocalMaryInterface;
import marytts.exceptions.MaryConfigurationException;
import marytts.modules.synthesis.Voice;
import marytts.util.data.audio.AudioPlayer;
import net.vpc.common.jeep.ExpressionEvaluator;
import net.vpc.scholar.adamlan.AdamLanFunctionsUtils;
import net.vpc.scholar.adamlan.AdamLanParser;

import java.util.Locale;

/**
 * @author vpc
 */
public class AdamLanTextToSpeechSubSystem {

    private LocalMaryInterface marytts = null;

    public AdamLanTextToSpeechSubSystem() {
    }

    public void speak(ExpressionEvaluator it, String inputText) {
        try {
            if (inputText == null) {
                inputText = "";
            }
            inputText = inputText.trim();
            if (inputText.isEmpty()) {
                return;
            }

            // init mary
            if (marytts == null) {
                try {
                    String v = System.getProperty("java.version");
                    if ("11-internal".equals(v)) {
                        System.setProperty("java.version", "11.0");
                    }
                    marytts = new LocalMaryInterface();
                } catch (MaryConfigurationException e) {
                    e.printStackTrace();
                    System.err.println("Could not initialize MaryTTS interface: " + e.getMessage());
                    throw e;
                }
            }
            for (Voice voice : Voice.getAvailableVoices()) {
                System.out.println(voice);
            }

            AdamLanParser eval = AdamLanFunctionsUtils.__evaluator(it);
            Locale loc = eval.detectLocale(inputText);
            if (loc != null) {
                if(loc.toString().equals("en")){
                    loc=new Locale("EN","us");
                }
                marytts.setLocale(loc);
            } else {
                marytts.setLocale(Locale.ENGLISH);
            }
            // synthesize
            AudioInputStream audio = null;
            try {
                audio = marytts.generateAudio(inputText);

                AudioPlayer p = new AudioPlayer(audio);
                p.start();
                p.join();
//                Clip clip = AudioSystem.getClip();
//                clip.open(audio);
//                clip.start();
//                System.out.println("SAY : "+inputText); 
            } catch (Exception e) {
                System.err.println("Synthesis failed: " + e.getMessage());
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            //
        }
    }
}
