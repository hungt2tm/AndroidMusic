package crud.unlocked.musicaudio.util;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

/**
 * Created by Irelia on 12/16/2016.
 */

public class Utility {
    public static String convertDuration(long duration){
        long minutes = (duration / 1000) / 60;
        long seconds = (duration / 1000) % 60;
        String converted = String.format("%d:%02d", minutes, seconds);
        return converted;
    }

    public static String convertThousandNumber(int number){
        int thoudsand = (number / 1000);
        String converted = String.format("%,dk", thoudsand);
        return converted;
    }

    public static String convertGenreTitle(String style){
        String title = "";
            if (style == MyConfig.ALTERNATIVE_ROCK) {
                title = "Alternative Rock";
            } else if (style.equals(MyConfig.AMBIENT)) {
                title = "Ambient";
            } else if (style.equals(MyConfig.CLASSICAL)) {
                title = "Classical";
            } else if (style.equals(MyConfig.COUNTRY)) {
                title = "Country";
            } else if (style.equals(MyConfig.DANCE_EDM)) {
                title = "Dance & EDM";
            } else if (style.equals(MyConfig.DANCE_HALL)) {
                title = "Dancehall";
            } else if (style.equals(MyConfig.DEEP_HOUSE)) {
                title = "Deep House";
            } else if (style.equals(MyConfig.DISCO)) {
                title = "Disco";
            } else if (style.equals(MyConfig.DRUM_BASS)) {
                title = "Drum & Bass";
            } else if (style.equals(MyConfig.DUBSTEP)) {
                title = "Dubstep";
            } else if (style.equals(MyConfig.ELECTRONIC)) {
                title = "Electronic";
            } else if (style.equals(MyConfig.FOLK_SINGER_SONG_WRITER)) {
                title = "Folk & Singer Songwriter";
            } else if (style.equals(MyConfig.HIPHOP_RAP)) {
                title = "Hip-hop Rap";
            } else if (style.equals(MyConfig.HOUSE)) {
                title = "House";
            } else if (style == MyConfig.INDIE) {
                title = "Indie";
            } else if (style.equals(MyConfig.JAZZ_BLUES)) {
                title = "Jazz & Blues";
            } else if (style.equals(MyConfig.LATIN)) {
                title = "Latin";
            } else if (style.equals(MyConfig.METAL)) {
                title = "Metal";
            } else if (style.equals(MyConfig.PIANO)) {
                title = "Piano";
            } else if (style.equals(MyConfig.POP)) {
                title = "Pop";
            } else if (style.equals(MyConfig.RB_SOUL)) {
                title = "R&B & Soul";
            } else if (style.equals(MyConfig.REGGAE)) {
                title = "Reggae";
            } else if (style.equals(MyConfig.REGGAETON)) {
                title = "Reggaeton";
            } else if (style.equals(MyConfig.ROCK)) {
                title = "Rock";
            } else if (style.equals(MyConfig.SOUND_TRACK)) {
                title = "Soundtrack";
            } else if (style.equals(MyConfig.TECHNO)) {
                title = "Techno";
            } else if (style.equals(MyConfig.TRANCE)) {
                title = "Trance";
            } else if (style.equals(MyConfig.TRAP)) {
                title = "Trap";
            } else if (style.equals(MyConfig.TRIPHOP)) {
                title = "Triphop";
            } else if (style == MyConfig.WORLD) {
                title = "World";
            }else if (style.equals(MyConfig.AUDIO_BOOKS)) {
                title = "Audiobooks";
            } else if (style.equals(MyConfig.BUSINESS)) {
                title = "Business";
            } else if (style.equals(MyConfig.COMEDY)) {
                title = "Comedy";
            } else if (style.equals(MyConfig.ENTERTAINMENT)) {
                title = "Entertainment";
            } else if (style.equals(MyConfig.LEARNING)) {
                title = "Learning";
            } else if (style == MyConfig.NEWS_POLITICS) {
                title = "New & Politics";
            } else if (style.equals(MyConfig.RELIGION_SPIRITUALITY)) {
                title = "Religion & Spirituality";
            } else if (style.equals(MyConfig.SCIENCE)) {
                title = "Science";
            } else if (style.equals(MyConfig.SPORTS)) {
                title = "Sports";
            } else if (style.equals(MyConfig.STORYTELLING)) {
                title = "Storytelling";
            } else if (style == MyConfig.TECHNOLOGY) {
                title = "Technology";
            }
        return title;
    }

    public static boolean isMyServiceRunning(FragmentActivity fragmentActivity, Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) fragmentActivity.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }
    public static boolean checkConnection(Context context){
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if(connectivityManager != null) {
            NetworkInfo mobile = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
            NetworkInfo wifi = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            if (mobile.isAvailable() || wifi.isAvailable()) {
                return true;
            }
        }
        return false;
    }
}
