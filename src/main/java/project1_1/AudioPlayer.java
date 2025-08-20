package project1_1;

import java.io.File;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;

public class AudioPlayer {
    private Clip clip;
    private boolean isMuted = false;

    public AudioPlayer(String audioFilePath) {
        try {
            AudioInputStream ais = AudioSystem.getAudioInputStream(new File(audioFilePath));
            clip = AudioSystem.getClip();
            clip.open(ais);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void play() {
        if (!isMuted) {
            clip.start();
        }
    }

    public void stop() {
        clip.stop();
    }

    public void mute() {
        if (isMuted) {
            clip.start();
        } else {
            clip.stop();
        }
        isMuted = !isMuted;
    }

    public boolean isMuted() {
        return isMuted;
    }
}