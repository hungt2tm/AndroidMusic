package crud.unlocked.musicaudio.util;

import android.net.Uri;

/**
 * Created by Jarvis on 12/4/2016.
 */

public class MyConfig {
        //Send BroadcastReceiver
        //Action Play
        public static final String MY_ACTION_PLAY = "crud.unlocked.musicaudio.MY_ACTION_PLAY";
        //Play Pause
        public static final int MY_ACTION_PLAY_PLAY_PAUSE = 1;
        //Random On
        public static final int MY_ACTION_PLAY_RANDOM_ON = 2;
        //Random Off
        public static final int MY_ACTION_PLAY_RANDOM_OFF = 3;
        //Loop One
        public static final int MY_ACTION_PLAY_LOOP_ONE = 4;
        //Loop All
        public static final int MY_ACTION_PLAY_LOOP_ALL = 5;
        //Loop Off
        public static final int MY_ACTION_PLAY_LOOP_OFF = 6;
        //Current Track
        public static final int MY_ACTION_PLAY_CURRENT_TRACK = 7;
        //Current Seek
        public static final int MY_ACTION_PLAY_CURRENT_SEEK = 8;
        //Next Track
        public static final int MY_ACTION_PLAY_NEXT_TRACK = 9;
        //Previous Track
        public static final int MY_ACTION_PLAY_PREVIOUS_TRACK = 10;
        //Request Track
        public static final int MY_ACTION_PLAY_REQUEST_TRACK = 0;
        //Send Progress Seek Bar
        public static final int MY_ACTION_PLAY_SEEK_PROGRESS = 11;

        //Send BroadcastReceiver
        //Media Play
        public static final String MY_MEDIA_PLAY = "crud.unlocked.musicaudio.MY_MEDIA_PLAY";
        //Track Standby or Prepare
        public static final int MY_MEDIA_PLAY_TRACK_STANDBY = 1;
        //Track Started or PrepareAsync
        public static final int MY_MEDIA_PLAY_TRACK_START = 2;
        //Track Index
        public static final int MY_MEDIA_PLAY_INDEX = 3;
        //Media Stop
        public static final int MY_MEDIA_PLAY_STOP = 4;
        //Response Track
        public static final int MY_MEDIA_PLAY_RESPONSE_TRACK = 5;
        //Media Progress
        public static final int MY_MEDIA_PLAY_PROGRESS = 6;
        //Media Play Pause
        public static final int MY_MEDIA_PLAY_PLAY = 7;
        //Media Play Close
        public static final int MY_MEDIA_PLAY_CLOSE = 8;

        //Notification Action
        public static final String MY_NOTIFICATION_MEDIA = "crud.unlocked.musicaudio.MY_NOTIFICATION";
        //Media Play Pause
        public static final int MY_NOTIFICATION_MEDIA_PLAY = 1;
        //Media Next
        public static final int MY_NOTIFICATION_MEDIA_NEXT= 2;
        //Media Previous
        public static final int MY_NOTIFICATION_MEDIA_PREVIOUS = 3;
        //Media Close
        public static final int MY_NOTIFICATION_MEDIA_CLOSE = 4;



        //SoundCloud Api
        public static final String CLIENT_ID = "eX0kxSjLqW2IxyyXQ1BgK9rQflG2JKOO";
        public static final String CLIENT_SECRET = "fDeqdGjplVeYB0Cn7EL2xvDesR1UKjY9";
        public static final String REDIRECT_URI = "soundcloud.com/hungt2tm/soundplaying/";
        public static final String API_URL = "https://api.soundcloud.com";
        public static final String APIV2_URL = "https://api-v2.soundcloud.com";

        public static final Uri ENDPOINT = Uri
                .parse("https://api.soundcloud.com/")
                .buildUpon()
                .appendQueryParameter("client_id", CLIENT_ID)
                .build();

        public static final Uri ENDPOINT_V2 = Uri
                .parse(API_URL)
                .buildUpon()
                .appendQueryParameter("client_id", CLIENT_ID)
                .build();

        public static final String PATH_CHARTS = "charts";
        public static final String PATH_TRACKS = "tracks";
        public static final String PATH_PLAYLIST = "playlists";

        //Kind
        public static final String KIND_TRENDING = "trending";
        public static final String KIND_TOP = "top";

        //Music
        public static final String ALL_MUSIC = "soundcloud:genres:all-music";
        public static final String ALL_AUDIO = "soundcloud:genres:all-audio";

        public static final String ALTERNATIVE_ROCK = "soundcloud:genres:alternativerock";
        public static final String AMBIENT = "soundcloud:genres:ambient";
        public static final String CLASSICAL = "soundcloud:genres:classical";
        public static final String COUNTRY = "soundcloud:genres:country";
        public static final String DANCE_EDM = "soundcloud:genres:danceedm";
        public static final String DANCE_HALL =  "soundcloud:genres:dancehall";
        public static final String DEEP_HOUSE = "soundcloud:genres:deephouse";
        public static final String DISCO = "soundcloud:genres:disco";
        public static final String DRUM_BASS = "soundcloud:genres:drumbass";
        public static final String DUBSTEP = "soundcloud:genres:dubstep";
        public static final String ELECTRONIC = "soundcloud:genres:electronic";
        public static final String FOLK_SINGER_SONG_WRITER = "soundcloud:genres:folksingersongwriter";
        public static final String HIPHOP_RAP = "soundcloud:genres:hiphoprap";
        public static final String HOUSE = "soundcloud:genres:house";
        public static final String INDIE = "soundcloud:genres:indie";
        public static final String JAZZ_BLUES = "soundcloud:genres:jazzblues";
        public static final String LATIN = "soundcloud:genres:latin";
        public static final String METAL = "soundcloud:genres:metal";
        public static final String PIANO = "soundcloud:genres:piano";
        public static final String POP = "soundcloud:genres:pop";
        public static final String RB_SOUL = "soundcloud:genres:rbsoul";
        public static final String REGGAE = "soundcloud:genres:reggae";
        public static final String REGGAETON = "soundcloud:genres:reggaeton";
        public static final String ROCK = "soundcloud:genres:rock";
        public static final String SOUND_TRACK = "soundcloud:genres:soundtrack";
        public static final String TECHNO = "soundcloud:genres:techno";
        public static final String TRANCE = "soundcloud:genres:trance";
        public static final String TRAP = "soundcloud:genres:trap";
        public static final String TRIPHOP = "soundcloud:genres:triphop";
        public static final String WORLD = "soundcloud:genres:world";
        //Audio
        public static final String AUDIO_BOOKS = "soundcloud:genres:audiobooks";
        public static final String BUSINESS = "soundcloud:genres:business";
        public static final String COMEDY = "soundcloud:genres:comedy";
        public static final String ENTERTAINMENT = "soundcloud:genres:entertainment";
        public static final String LEARNING = "soundcloud:genres:learning";
        public static final String NEWS_POLITICS = "soundcloud:genres:newspolitics";
        public static final String RELIGION_SPIRITUALITY = "soundcloud:genres:religionspirituality";
        public static final String SCIENCE = "soundcloud:genres:science";
        public static final String SPORTS = "soundcloud:genres:sports";
        public static final String STORYTELLING = "soundcloud:genres:storytelling";
        public static final String TECHNOLOGY = "soundcloud:genres:technology";

}
