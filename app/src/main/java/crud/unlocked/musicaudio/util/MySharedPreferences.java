package crud.unlocked.musicaudio.util;

import android.content.Context;
import android.preference.PreferenceManager;

/**
 * Created by Irelia on 1/1/2017.
 */

public class MySharedPreferences {
    private static final String PREF_PLAY_RANDOM = "playRandom";
    private static final String PREF_PLAY_LOOP = "playLoop";

    public static boolean getStoredPlayRandom(Context context){
        return PreferenceManager.getDefaultSharedPreferences(context).getBoolean(PREF_PLAY_RANDOM, false);
    }

    public static int getStoredPlayLoop(Context context){
        return PreferenceManager.getDefaultSharedPreferences(context).getInt(PREF_PLAY_LOOP, MyConfig.MY_ACTION_PLAY_LOOP_OFF);
    }

    public static void setStorePlayRandom(Context context, boolean random){
        PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .putBoolean(PREF_PLAY_RANDOM, random)
                .apply();
    }

    public static void setStoredPlayLoop(Context context, int loop){
        PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .putInt(PREF_PLAY_LOOP, loop)
                .apply();
    }
}
