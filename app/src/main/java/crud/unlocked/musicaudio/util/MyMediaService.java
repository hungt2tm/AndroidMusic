package crud.unlocked.musicaudio.util;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.NotificationCompat;
import android.widget.RemoteViews;

import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import crud.unlocked.learnvolley.R;
import crud.unlocked.musicaudio.MainActivity;
import crud.unlocked.musicaudio.model.Track;

/**
 * Created by Irelia on 12/28/2016.
 */

public class MyMediaService extends IntentService {

    private static final String TAG = "MyMediaService";
    private static final int NOTIF_ID = 0;
    private static NotificationCompat.Builder mBuilder;
    RemoteViews mContentViews, mBigContentViews;
    private MediaPlayer mMediaPlayer;
    private List<Track> mTrackList;
    private int currentIndex;
    private int loop = MyConfig.MY_ACTION_PLAY_LOOP_OFF;
    private boolean random;
    private Intent myBroadcastMediaPlay;
    private Handler handler = new Handler();
    private boolean isRunnable, streamError, isNotificationClick;
    private PendingIntent mPendingIntent;
    private NotificationManager mNotificationManager;
    private Runnable r = new Runnable() {
        @Override
        public void run() {
            Bundle bundle = new Bundle();
            bundle.putInt("progress", mMediaPlayer.getCurrentPosition());
            bundle.putInt("action", MyConfig.MY_MEDIA_PLAY_PROGRESS);
            myBroadcastMediaPlay.replaceExtras(bundle);
            sendBroadcast(myBroadcastMediaPlay);
            handler.postDelayed(this, 1000);
        }
    };
    private BroadcastReceiver myReceiverMediaControl = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getIntExtra("action", 0)) {
                case MyConfig.MY_NOTIFICATION_MEDIA_PLAY:
                    if (isNotificationClick) {
                        pressPlayPause();
                    }
                    break;
                case MyConfig.MY_NOTIFICATION_MEDIA_NEXT:
                    if (isNotificationClick) {
                        pressNext();
                        updateNotification();
                    }
                    break;
                case MyConfig.MY_NOTIFICATION_MEDIA_PREVIOUS:
                    if (isNotificationClick) {
                        pressPrevious();
                        updateNotification();
                    }
                    break;
                case MyConfig.MY_NOTIFICATION_MEDIA_CLOSE:
                    mNotificationManager.cancel(NOTIF_ID);
                    myBroadcastMediaPlay.putExtra("action", MyConfig.MY_MEDIA_PLAY_CLOSE);
                    sendBroadcast(myBroadcastMediaPlay);
                    stopSelf();
                    break;
            }
        }
    };
    private BroadcastReceiver myReceiverActionPlay = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            switch (intent.getIntExtra("trackAction", -1)) {
                case MyConfig.MY_ACTION_PLAY_PLAY_PAUSE:
                    pressPlayPause();
                    break;
                case MyConfig.MY_ACTION_PLAY_RANDOM_ON:
                    random = true;
                    break;
                case MyConfig.MY_ACTION_PLAY_RANDOM_OFF:
                    random = false;
                    break;
                case MyConfig.MY_ACTION_PLAY_LOOP_OFF:
                    loop = MyConfig.MY_ACTION_PLAY_LOOP_OFF;
                    break;
                case MyConfig.MY_ACTION_PLAY_LOOP_ONE:
                    loop = MyConfig.MY_ACTION_PLAY_LOOP_ONE;
                    break;
                case MyConfig.MY_ACTION_PLAY_LOOP_ALL:
                    loop = MyConfig.MY_ACTION_PLAY_LOOP_ALL;
                    break;
                case MyConfig.MY_ACTION_PLAY_CURRENT_TRACK:
                    changeSelectedTrack(intent.getIntExtra("trackIndex", 0));
                    prepareTrack();
                    break;
                case MyConfig.MY_ACTION_PLAY_CURRENT_SEEK:
                    if (mMediaPlayer != null) {
                        mMediaPlayer.seekTo(intent.getIntExtra("trackProgress", 0) * 1000);
                    }
                    break;
                case MyConfig.MY_ACTION_PLAY_NEXT_TRACK:
                    pressNext();
                    break;
                case MyConfig.MY_ACTION_PLAY_PREVIOUS_TRACK:
                    pressPrevious();
                    break;
                case MyConfig.MY_ACTION_PLAY_REQUEST_TRACK:
                    myBroadcastMediaPlay.putExtra("action", MyConfig.MY_MEDIA_PLAY_RESPONSE_TRACK);
                    myBroadcastMediaPlay.putExtra("track", mTrackList.get(currentIndex));
                    myBroadcastMediaPlay.putExtra("currentTime", mMediaPlayer.getCurrentPosition());
                    myBroadcastMediaPlay.putExtra("isPlaying", mMediaPlayer.isPlaying());
                    sendBroadcast(myBroadcastMediaPlay);
                    break;
                case MyConfig.MY_ACTION_PLAY_SEEK_PROGRESS:
                    mMediaPlayer.seekTo(intent.getIntExtra("progress", 0) * 1000);
                    break;
            }
        }
    };


    public MyMediaService() {
        super(TAG);
    }

    public static Intent newIntent(Context context, ArrayList<Track> trackList, boolean random, int loop) {
        Intent i = new Intent(context, MyMediaService.class);
        Bundle args = new Bundle();
        args.putParcelableArrayList("mTrackList", trackList);
        args.putBoolean("random", random);
        args.putInt("loop", loop);
        i.putExtras(args);
        return i;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        registerReceiver(myReceiverActionPlay, new IntentFilter(MyConfig.MY_ACTION_PLAY));
        registerReceiver(myReceiverMediaControl, new IntentFilter(MyConfig.MY_NOTIFICATION_MEDIA));
        myBroadcastMediaPlay = new Intent(MyConfig.MY_MEDIA_PLAY);
        Bundle args = intent.getExtras();
        currentIndex = args.getInt("currentIndex");
        mTrackList = args.getParcelableArrayList("mTrackList");
        random = args.getBoolean("random");
        loop = args.getInt("loop");
        setMediaPlayer();
        changeSelectedTrack(currentIndex);
        prepareTrack();
        createNotification();
        return START_STICKY;
    }

    @Override
    protected void onHandleIntent(Intent intent) {
    }

    private void createNotification() {
        if (mBuilder == null) {
            mBuilder = new NotificationCompat.Builder(this);
        }

        Intent i = new Intent(this, MainActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pIntent = PendingIntent.getActivity(this, 0, i,
                PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(pIntent);
        mBuilder.setSmallIcon(R.drawable.logo);
        mBuilder.setAutoCancel(false);

        Track track = mTrackList.get(currentIndex);

        mContentViews = new RemoteViews(getPackageName(), R.layout.notification_simple);
        mContentViews.setTextViewText(R.id.notification_title_simple, track.getTitle());
        mContentViews.setTextViewText(R.id.notification_artist_simple, track.getArtist());
        mContentViews.setImageViewResource(R.id.notification_play_simple, R.drawable.notification_pause);

        Intent myBroadcastMediaControl = new Intent(MyConfig.MY_NOTIFICATION_MEDIA);
        //Send Broadcast in notification use pending intent
        myBroadcastMediaControl.putExtra("action", MyConfig.MY_NOTIFICATION_MEDIA_PLAY);
        mPendingIntent = PendingIntent.getBroadcast(this, MyConfig.MY_NOTIFICATION_MEDIA_PLAY, myBroadcastMediaControl, 0);
        mContentViews.setOnClickPendingIntent(R.id.notification_play_simple, mPendingIntent);

        myBroadcastMediaControl.putExtra("action", MyConfig.MY_NOTIFICATION_MEDIA_NEXT);
        mPendingIntent = PendingIntent.getBroadcast(this, MyConfig.MY_NOTIFICATION_MEDIA_NEXT, myBroadcastMediaControl, 0);
        mContentViews.setOnClickPendingIntent(R.id.notification_next_simple, mPendingIntent);

        myBroadcastMediaControl.putExtra("action", MyConfig.MY_NOTIFICATION_MEDIA_PREVIOUS);
        mPendingIntent = PendingIntent.getBroadcast(this, MyConfig.MY_NOTIFICATION_MEDIA_PREVIOUS, myBroadcastMediaControl, 0);
        mContentViews.setOnClickPendingIntent(R.id.notification_previous_simple, mPendingIntent);

        myBroadcastMediaControl.putExtra("action", MyConfig.MY_NOTIFICATION_MEDIA_CLOSE);
        mPendingIntent = PendingIntent.getBroadcast(this, MyConfig.MY_NOTIFICATION_MEDIA_CLOSE, myBroadcastMediaControl, 0);
        mContentViews.setOnClickPendingIntent(R.id.notification_close_simple, mPendingIntent);

        mBuilder.setContent(mContentViews);
        Picasso.with(this)
                .load(mTrackList.get(currentIndex).getArtworkUrl())
                .into(mContentViews, R.id.notification_album_simple, 0, mBuilder.build());

        if (Build.VERSION.SDK_INT >= 16) {
            mBigContentViews = new RemoteViews(getPackageName(), R.layout.notification);
            mBigContentViews.setTextViewText(R.id.notification_title, track.getTitle());
            mBigContentViews.setTextViewText(R.id.notification_artist, track.getArtist());
            mBigContentViews.setImageViewResource(R.id.notification_play, R.drawable.notification_pause);

            //Send Broadcast in notification use pending intent
            myBroadcastMediaControl.putExtra("action", MyConfig.MY_NOTIFICATION_MEDIA_PLAY);
            mPendingIntent = PendingIntent.getBroadcast(this, MyConfig.MY_NOTIFICATION_MEDIA_PLAY, myBroadcastMediaControl, 0);
            mBigContentViews.setOnClickPendingIntent(R.id.notification_play, mPendingIntent);

            myBroadcastMediaControl.putExtra("action", MyConfig.MY_NOTIFICATION_MEDIA_NEXT);
            mPendingIntent = PendingIntent.getBroadcast(this, MyConfig.MY_NOTIFICATION_MEDIA_NEXT, myBroadcastMediaControl, 0);
            mBigContentViews.setOnClickPendingIntent(R.id.notification_next, mPendingIntent);

            myBroadcastMediaControl.putExtra("action", MyConfig.MY_NOTIFICATION_MEDIA_PREVIOUS);
            mPendingIntent = PendingIntent.getBroadcast(this, MyConfig.MY_NOTIFICATION_MEDIA_PREVIOUS, myBroadcastMediaControl, 0);
            mBigContentViews.setOnClickPendingIntent(R.id.notification_previous, mPendingIntent);

            myBroadcastMediaControl.putExtra("action", MyConfig.MY_NOTIFICATION_MEDIA_CLOSE);
            mPendingIntent = PendingIntent.getBroadcast(this, MyConfig.MY_NOTIFICATION_MEDIA_CLOSE, myBroadcastMediaControl, 0);
            mBigContentViews.setOnClickPendingIntent(R.id.notification_close, mPendingIntent);

            mBuilder.setCustomBigContentView(mBigContentViews);
            Picasso.with(this)
                    .load(mTrackList.get(currentIndex).getArtworkUrl())
                    .into(mBigContentViews, R.id.notification_album, 0, mBuilder.build());

        }
        if (mNotificationManager == null) {
            mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        }
        Notification notification = mBuilder.build();
        notification.flags = Notification.FLAG_NO_CLEAR;
        mNotificationManager.notify(NOTIF_ID, notification);
    }

    private void setMediaPlayer() {
        //Media
        mMediaPlayer = new MediaPlayer();
        mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                streamError = false;
                playStreamUrl(mediaPlayer);
            }
        });

        //Complete The Track
        mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                if (!streamError) {
                    if (loop == MyConfig.MY_ACTION_PLAY_LOOP_ONE) {
                        mediaPlayer.start();
                    } else if (loop == MyConfig.MY_ACTION_PLAY_LOOP_ALL) {
                        if (random) {
                            changeSelectedTrack((new Random()).nextInt(mTrackList.size() - currentIndex) + currentIndex);
                            prepareTrack();
                        } else {
                            if (currentIndex + 1 < mTrackList.size()) {
                                changeSelectedTrack(currentIndex + 1);
                                prepareTrack();
                            } else {
                                changeSelectedTrack(0);
                                prepareTrack();
                            }
                        }
                    } else if (loop == MyConfig.MY_ACTION_PLAY_LOOP_OFF) {
                        if (currentIndex == mTrackList.size() - 1) {
                            changeSelectedTrack(-1);
                            mMediaPlayer.stop();
                            mMediaPlayer.reset();
                            //
                            if (isRunnable) {
                                handler.removeCallbacks(r);
                            }
                            //
                            myBroadcastMediaPlay.putExtra("action", MyConfig.MY_MEDIA_PLAY_STOP);
                            sendBroadcast(myBroadcastMediaPlay);
                        } else {
                            if (random) {
                                changeSelectedTrack((new Random()).nextInt(mTrackList.size()));
                                prepareTrack();
                            } else {
                                if (currentIndex + 1 < mTrackList.size()) {
                                    changeSelectedTrack(currentIndex + 1);
                                    prepareTrack();
                                }
                            }
                        }
                    }
                } else {
                    changeSelectedTrack(++currentIndex);
                    prepareTrack();
                }
            }
        });
    }

    private void prepareTrack() {
        Track track = mTrackList.get(currentIndex);
        //Send Broadcast - Standby
        isNotificationClick = false;
        myBroadcastMediaPlay.putExtra("action", MyConfig.MY_MEDIA_PLAY_TRACK_STANDBY);
        myBroadcastMediaPlay.putExtra("maxTime", track.getDuration());
        sendBroadcast(myBroadcastMediaPlay);
        if (isRunnable) {
            handler.removeCallbacks(r);
            isRunnable = false;
        }
        String stream = track.getStreamUrl() + "?client_id=" + MyConfig.CLIENT_ID;
        mMediaPlayer.reset();
        try {
            mMediaPlayer.setDataSource(stream);
            streamError = true;
            mMediaPlayer.prepareAsync();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void playStreamUrl(MediaPlayer mp) {
        if (!mp.isPlaying()) {
            mp.start();
            //Send BroadCast - Start
            myBroadcastMediaPlay.putExtra("action", MyConfig.MY_MEDIA_PLAY_TRACK_START);
            myBroadcastMediaPlay.putExtra("artworkUrl", mTrackList.get(currentIndex).getArtworkUrl());
            sendBroadcast(myBroadcastMediaPlay);
            if (!isRunnable) {
                handler.post(r);
                isRunnable = true;
            }
            isNotificationClick = true;
        }
    }

    private void changeSelectedTrack(int index) {
        currentIndex = index;
        //Send Broadcast
        myBroadcastMediaPlay.putExtra("action", MyConfig.MY_MEDIA_PLAY_INDEX);
        myBroadcastMediaPlay.putExtra("index", currentIndex);
        sendBroadcast(myBroadcastMediaPlay);
    }

    private void pressPlayPause() {
        if (mMediaPlayer.isPlaying()) {
            mMediaPlayer.pause();
            if (isRunnable) {
                handler.removeCallbacks(r);
                isRunnable = false;
                mContentViews.setImageViewResource(R.id.notification_play_simple, R.drawable.notification_play);
                mBigContentViews.setImageViewResource(R.id.notification_play, R.drawable.notification_play);
            }
        } else {
            mMediaPlayer.start();
            if (!isRunnable) {
                handler.post(r);
                isRunnable = true;
                mContentViews.setImageViewResource(R.id.notification_play_simple, R.drawable.notification_pause);
                mBigContentViews.setImageViewResource(R.id.notification_play, R.drawable.notification_pause);
            }
        }
        Notification notification = mBuilder.build();
        notification.flags = Notification.FLAG_NO_CLEAR;
        mNotificationManager.notify(NOTIF_ID, notification);

        Bundle bundle = new Bundle();
        bundle.putInt("action", MyConfig.MY_MEDIA_PLAY_PLAY);
        bundle.putBoolean("isPlaying", isRunnable);
        myBroadcastMediaPlay.replaceExtras(bundle);
        sendBroadcast(myBroadcastMediaPlay);
    }

    private void updateNotification() {
        Track track = mTrackList.get(currentIndex);
        mContentViews.setImageViewResource(R.id.notification_play_simple, R.drawable.notification_pause);
        mBigContentViews.setImageViewResource(R.id.notification_play, R.drawable.notification_pause);
        mContentViews.setTextViewText(R.id.notification_title_simple, track.getTitle());
        mContentViews.setTextViewText(R.id.notification_artist_simple, track.getArtist());
        Picasso.with(this)
                .load(mTrackList.get(currentIndex).getArtworkUrl())
                .into(mContentViews, R.id.notification_album_simple, 0, mBuilder.build());

        mBigContentViews.setTextViewText(R.id.notification_title, track.getTitle());
        mBigContentViews.setTextViewText(R.id.notification_artist, track.getArtist());
        Picasso.with(this)
                .load(mTrackList.get(currentIndex).getArtworkUrl())
                .into(mBigContentViews, R.id.notification_album, 0, mBuilder.build());
        Notification notification = mBuilder.build();
        notification.flags = Notification.FLAG_NO_CLEAR;
        mNotificationManager.notify(NOTIF_ID, notification);
    }

    private void pressPrevious() {
        if (mMediaPlayer != null) {
            if (random) {
                int r;
                if (currentIndex > 3) {
                    r = (new Random()).nextInt(currentIndex);
                } else {
                    r = (new Random()).nextInt(mTrackList.size());
                }
                changeSelectedTrack(r);
                prepareTrack();
            } else {
                if (currentIndex - 1 >= 0) {
                    changeSelectedTrack(currentIndex - 1);
                    prepareTrack();
                } else {
                    changeSelectedTrack(mTrackList.size() - 1);
                    prepareTrack();
                }
            }
        }
    }

    private void pressNext() {
        if (mMediaPlayer != null) {
            if (random) {
                int r;
                if (currentIndex < mTrackList.size() - 3) {
                    r = (new Random()).nextInt(mTrackList.size() - currentIndex) + currentIndex;
                } else {
                    r = (new Random()).nextInt(mTrackList.size());
                }
                changeSelectedTrack(r);
                prepareTrack();
            } else {
                if (currentIndex + 1 < mTrackList.size()) {
                    changeSelectedTrack(currentIndex + 1);
                    prepareTrack();
                } else {
                    changeSelectedTrack(0);
                    prepareTrack();
                }
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mMediaPlayer != null) {
            mMediaPlayer.stop();
        }
        mMediaPlayer.release();
        if (isRunnable) {
            handler.removeCallbacks(r);
        }
        unregisterReceiver(myReceiverActionPlay);
        unregisterReceiver(myReceiverMediaControl);
    }
}
