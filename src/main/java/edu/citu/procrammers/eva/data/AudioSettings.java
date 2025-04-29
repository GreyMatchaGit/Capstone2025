package edu.citu.procrammers.eva.data;

import java.io.Serializable;

public class AudioSettings implements Serializable {
    private static final long serialVersionUID = 1L;

    public double musicVolume;
    public double sfxVolume;

    public AudioSettings(double musicVolume, double sfxVolume) {
        this.musicVolume = musicVolume;
        this.sfxVolume = sfxVolume;
    }
}
