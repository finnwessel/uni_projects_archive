package de.hsflensburg.ctfgame.services.sound;

import android.content.Context;
import android.media.AudioAttributes;
import android.media.SoundPool;

import java.util.HashMap;
import java.util.Map;

import de.hsflensburg.ctfgame.R;

public class SoundManager {
    private static SoundManager instance;
    private final SoundPool soundPool;
    private Map<String, Integer> sounds = new HashMap<String, Integer>();

    private SoundManager(Context context) {

        AudioAttributes audioAttributes = new AudioAttributes.Builder().setUsage(AudioAttributes.USAGE_ASSISTANCE_SONIFICATION)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION).build();

        soundPool = new SoundPool.Builder().setMaxStreams(6).setAudioAttributes(audioAttributes).build();

        sounds.put("lobby_enter", soundPool.load(context, R.raw.lobby_enter, 1));
        sounds.put("lobby_leave", soundPool.load(context, R.raw.lobby_leave, 1));
        sounds.put("game_started", soundPool.load(context, R.raw.game_started, 1));
        sounds.put("game_lose", soundPool.load(context, R.raw.game_lose, 1));
        sounds.put("game_won", soundPool.load(context, R.raw.game_won, 1));
        sounds.put("point_nearby", soundPool.load(context, R.raw.point_nearby, 1));
    }

    public static void createInstance(Context context) {
        if(instance == null) {
            instance = new SoundManager(context);
        }
    }

    public static SoundManager getInstance() {
        return instance;
    }

    public void playSound(String soundName){

        if (sounds.containsKey(soundName)) {
            soundPool.play(sounds.get(soundName), 1, 1, 0, 0, 1);
        }
    }
}
