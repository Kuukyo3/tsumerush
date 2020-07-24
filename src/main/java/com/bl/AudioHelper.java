package com.bl;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.util.EnumMap;

public class AudioHelper {
    public enum AudioFile {
        CORRECT, INCORRECT, VICTORY, SNAP
    }

    public static BufferedInputStream accessFile(AudioFile file) {
        String resource = "";
        switch (file) {
            case CORRECT:
                resource = "sounds/Correct.wav";
                break;
            case INCORRECT:
                resource = "sounds/Incorrect.wav";
                break;
            case VICTORY:
                resource = "sounds/Victory.wav";
                break;
            case SNAP:
                resource = "sounds/pieceSnap.wav";
                break;
        }

        InputStream input = AudioHelper.class.getResourceAsStream("/res/" + resource);
        if (input == null) {
            // this is how we load file within editor (eg eclipse)
            input = AudioHelper.class.getClassLoader().getResourceAsStream(resource);
        }

        return new BufferedInputStream(input);
    }
}