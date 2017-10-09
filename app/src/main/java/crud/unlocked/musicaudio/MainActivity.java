package crud.unlocked.musicaudio;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.telecom.ConnectionRequest;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import crud.unlocked.learnvolley.R;
import crud.unlocked.musicaudio.fragment.FragmentCharts;
import crud.unlocked.musicaudio.fragment.FragmentChartsGenre;
import crud.unlocked.musicaudio.fragment.FragmentChartsGenreDetail;
import crud.unlocked.musicaudio.fragment.FragmentConnection;
import crud.unlocked.musicaudio.fragment.FragmentHome;
import crud.unlocked.musicaudio.fragment.FragmentSearch;
import crud.unlocked.musicaudio.fragment.FragmentSearchPlaylist;
import crud.unlocked.musicaudio.fragment.FragmentSearchPlaylistDetail;
import crud.unlocked.musicaudio.fragment.FragmentSearchTrack;
import crud.unlocked.musicaudio.model.Track;
import crud.unlocked.musicaudio.util.MyConfig;
import crud.unlocked.musicaudio.util.MyMediaService;
import crud.unlocked.musicaudio.util.MySharedPreferences;
import crud.unlocked.musicaudio.util.Utility;
import crud.unlocked.musicaudio.util.VolleySingleton;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private DrawerLayout mDrawerLayout;
    private boolean isSearchCommit = false;
    //PlayMusic
    private RelativeLayout rlMainPlayMusic;
    private TextView currentTime, maxTime;
    private ImageView play, next, previous, imgAlbum, repeat, shuffle;
    private SeekBar seekBar;
    private String loop = "off";
    private boolean random;
    private Intent myIntentActionPlay;

    //Rotate image add 0.5 degree after 50 millisecond
    private float mDegree;
    private Handler handler = new Handler();
    private boolean isRunnable;

    private Runnable runnableRotation = new Runnable() {
        @Override
        public void run() {
            //mDegree = mDegree + 0.5f;
            mDegree = mDegree + 0.5f;
            imgAlbum.setRotation(mDegree);
            handler.postDelayed(this, 50);
        }
    };
    private boolean mBroadcastRegistered = false;
    private BroadcastReceiver myReceiverMediaPlay = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getIntExtra("action", -1)) {
                case MyConfig.MY_MEDIA_PLAY_TRACK_STANDBY:
                    getTrackStandBy(intent.getLongExtra("maxTime", 0));
                    break;
                case MyConfig.MY_MEDIA_PLAY_TRACK_START:
                    getTrackStart(intent.getStringExtra("artworkUrl"));
                    break;
                case MyConfig.MY_MEDIA_PLAY_STOP:
                    getMediaStop();
                    break;
                case MyConfig.MY_MEDIA_PLAY_RESPONSE_TRACK:
                    Track track = intent.getParcelableExtra("track");
                    maxTime.setText(Utility.convertDuration(track.getDuration()));
                    seekBar.setMax((int) (track.getDuration() / 1000));
                    Picasso.with(MainActivity.this)
                            .load(track.getArtworkUrl())
                            .placeholder(R.drawable.music_placeholder)
                            .into(imgAlbum);
                    int progress = intent.getIntExtra("currentTime", 0);
                    seekBar.setProgress(progress / 1000);
                    currentTime.setText(Utility.convertDuration(progress));
                    mDegree = progress / 100;
                    imgAlbum.setRotation(mDegree);
                    if (intent.getBooleanExtra("isPlaying", false)) {
                        setRunnable(true);
                        play.setBackgroundResource(R.drawable.ic_pause_circle_black);
                    } else {
                        setRunnable(false);
                        play.setBackgroundResource(R.drawable.ic_play_circle_black);
                    }
                    break;
                case MyConfig.MY_MEDIA_PLAY_PROGRESS:
                    int currentPosition = intent.getIntExtra("progress", 0);
                    seekBar.setProgress(currentPosition / 1000);
                    currentTime.setText(Utility.convertDuration((long) currentPosition));
                    break;
                case MyConfig.MY_MEDIA_PLAY_PLAY:
                    if (intent.getBooleanExtra("isPlaying", false)) {
                        setRunnable(true);
                        play.setBackgroundResource(R.drawable.ic_pause_circle_black);
                    } else {
                        setRunnable(false);
                        play.setBackgroundResource(R.drawable.ic_play_circle_black);
                    }
                    break;
                case MyConfig.MY_MEDIA_PLAY_CLOSE:
                    getMediaStop();
                    rlMainPlayMusic.setVisibility(View.GONE);
                    //finish();
                    break;
            }
        }
    };

    private void setRunnable(boolean isRun) {
        if (isRun) {
            runOnUiThread(runnableRotation);
        } else {
            handler.removeCallbacks(runnableRotation);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initializeViews();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        rlMainPlayMusic = (RelativeLayout) findViewById(R.id.rlMainPlayMusic);
        myIntentActionPlay = new Intent(MyConfig.MY_ACTION_PLAY);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this,
                mDrawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                requestCancelAll();
                int id = item.getItemId();

                if (id == R.id.nav_mymusic) {
                    FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                    if (Utility.checkConnection(getApplicationContext())) {
                        ft.replace(R.id.content_main, new FragmentHome());
                    } else {
                        ft.replace(R.id.content_main, new FragmentConnection());
                    }
                    ft.commit();
                } else if (id == R.id.nav_new_hot) {
                    FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                    if (Utility.checkConnection(getApplicationContext())) {
                        ft.replace(R.id.content_main, FragmentCharts.newInstance(false));
                    } else {
                        ft.replace(R.id.content_main, new FragmentConnection());
                    }
                    ft.commit();
                } else if (id == R.id.nav_top50) {
                    FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                    if (Utility.checkConnection(getApplicationContext())) {
                        ft.replace(R.id.content_main, FragmentCharts.newInstance(true));
                    } else {
                        ft.replace(R.id.content_main, new FragmentConnection());
                    }
                    ft.commit();
                } else if (id == R.id.nav_exit) {
                    showAlertDialog();
                }
                mDrawerLayout.closeDrawer(GravityCompat.START);
                return true;
            }
        });

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        if (Utility.checkConnection(getApplicationContext())) {
            ft.replace(R.id.content_main, new FragmentHome());
        } else {
            ft.replace(R.id.content_main, new FragmentConnection());
        }

        ft.commit();
    }

    public void showAlertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Exit MusicAudio?");
        builder.setMessage("Are you sure you want to quit?");
        builder.setCancelable(false);
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Intent exit = new Intent(MyConfig.MY_NOTIFICATION_MEDIA);
                exit.putExtra("action", MyConfig.MY_NOTIFICATION_MEDIA_CLOSE);
                sendBroadcast(exit);
                onPause();
                finish();
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();

    }

    private void requestCancelAll() {
        VolleySingleton.getInstance(this).cancelRequests(FragmentChartsGenre.TAG);
        VolleySingleton.getInstance(this).cancelRequests(FragmentChartsGenreDetail.TAG);
        VolleySingleton.getInstance(this).cancelRequests(FragmentSearchTrack.TAG);
        VolleySingleton.getInstance(this).cancelRequests(FragmentSearchPlaylist.TAG);
        VolleySingleton.getInstance(this).cancelRequests(FragmentSearchPlaylistDetail.TAG);
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        final MenuItem item = menu.findItem(R.id.mn_search);
        final SearchView search = (SearchView) MenuItemCompat.getActionView(item);
        search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (isSearchCommit) {
                    isSearchCommit = false;
                    FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                    if(Utility.checkConnection(getApplicationContext())){
                        ft.replace(R.id.content_main, FragmentSearch.newInstance(query));
                    }else ft.replace(R.id.content_main, new FragmentConnection());

                    ft.addToBackStack(null);
                    ft.commit();

                    //Hide keyboard
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(search.getWindowToken(), 0);

                    MenuItemCompat.collapseActionView(item);
                    return true;
                } else {
                    isSearchCommit = true;
                }
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    private void initializeViews() {
        rlMainPlayMusic = (RelativeLayout) findViewById(R.id.rlMainPlayMusic);
        currentTime = (TextView) findViewById(R.id.txtCurrentTime);
        maxTime = (TextView) findViewById(R.id.txtMaxTime);
        next = (ImageView) findViewById(R.id.imgNext);
        play = (ImageView) findViewById(R.id.imgPlayPause);
        previous = (ImageView) findViewById(R.id.imgPrevious);
        imgAlbum = (ImageView) findViewById(R.id.imgAlbum);
        repeat = (ImageView) findViewById(R.id.imgLoop);
        if (MySharedPreferences.getStoredPlayLoop(this) == MyConfig.MY_ACTION_PLAY_LOOP_ONE) {
            repeat.setImageResource(R.drawable.ic_repeat_one);
        } else if (MySharedPreferences.getStoredPlayLoop(this) == MyConfig.MY_ACTION_PLAY_LOOP_ALL) {
            repeat.setImageResource(R.drawable.ic_repeat_all);
        } else if (MySharedPreferences.getStoredPlayLoop(this) == MyConfig.MY_ACTION_PLAY_LOOP_OFF) {
            repeat.setImageResource(R.drawable.ic_repeat_off);
        }
        shuffle = (ImageView) findViewById(R.id.imgRanDom);
        if (MySharedPreferences.getStoredPlayRandom(this)) {
            shuffle.setImageResource(R.drawable.ic_shuffle_on);
        } else {
            shuffle.setImageResource(R.drawable.ic_shuffle_off);
        }
        seekBar = (SeekBar) findViewById(R.id.seekBar);
        //Control Play
        pressPlayPause();
        pressNext();
        pressPrevious();
        //Loop
        pressLoop();
        pressShuffle();
        //Handle Seek Bar
        handlerSeekBar();
    }

    private void handlerSeekBar() {
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    currentTime.setText(Utility.convertDuration(progress * 1000));
                    Bundle bundle = new Bundle();
                    bundle.putInt("trackAction", MyConfig.MY_ACTION_PLAY_SEEK_PROGRESS);
                    bundle.putInt("progress", progress);
                    myIntentActionPlay.replaceExtras(bundle);
                    sendBroadcast(myIntentActionPlay);
                    mDegree = progress * 10;
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    private void pressShuffle() {
        shuffle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                if (random) {
                    shuffle.setImageResource(R.drawable.ic_shuffle_off);
                    random = false;
                    bundle.putInt("trackAction", MyConfig.MY_ACTION_PLAY_RANDOM_OFF);
                    MySharedPreferences.setStorePlayRandom(MainActivity.this, false);
                } else {
                    shuffle.setImageResource(R.drawable.ic_shuffle_on);
                    random = true;
                    bundle.putInt("trackAction", MyConfig.MY_ACTION_PLAY_RANDOM_ON);
                    MySharedPreferences.setStorePlayRandom(MainActivity.this, true);
                }
                myIntentActionPlay.replaceExtras(bundle);
                sendBroadcast(myIntentActionPlay);
            }
        });
    }

    private void pressLoop() {
        repeat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                if (loop.equals("off")) {
                    repeat.setImageResource(R.drawable.ic_repeat_one);
                    loop = "one";
                    bundle.putInt("trackAction", MyConfig.MY_ACTION_PLAY_LOOP_ONE);
                    MySharedPreferences.setStoredPlayLoop(MainActivity.this, MyConfig.MY_ACTION_PLAY_LOOP_ONE);
                } else if (loop.equals("one")) {
                    repeat.setImageResource(R.drawable.ic_repeat_all);
                    loop = "all";
                    bundle.putInt("trackAction", MyConfig.MY_ACTION_PLAY_LOOP_ALL);
                    MySharedPreferences.setStoredPlayLoop(MainActivity.this, MyConfig.MY_ACTION_PLAY_LOOP_ALL);
                } else {
                    repeat.setImageResource(R.drawable.ic_repeat_off);
                    loop = "off";
                    bundle.putInt("trackAction", MyConfig.MY_ACTION_PLAY_LOOP_OFF);
                    MySharedPreferences.setStoredPlayLoop(MainActivity.this, MyConfig.MY_ACTION_PLAY_LOOP_OFF);
                }
                myIntentActionPlay.replaceExtras(bundle);
                sendBroadcast(myIntentActionPlay);
            }
        });
    }

    private void pressPlayPause() {
        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                bundle.putInt("trackAction", MyConfig.MY_ACTION_PLAY_PLAY_PAUSE);
                myIntentActionPlay.replaceExtras(bundle);
                sendBroadcast(myIntentActionPlay);
                if (isRunnable) {
                    play.setBackgroundResource(R.drawable.ic_play_circle_black);
                    setRunnable(false);
                } else {
                    play.setBackgroundResource(R.drawable.ic_pause_circle_black);
                    setRunnable(true);

                }
            }
        });
    }

    private void pressNext() {
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                bundle.putInt("trackAction", MyConfig.MY_ACTION_PLAY_NEXT_TRACK);
                myIntentActionPlay.replaceExtras(bundle);
                sendBroadcast(myIntentActionPlay);
            }
        });
    }

    private void pressPrevious() {
        previous.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                bundle.putInt("trackAction", MyConfig.MY_ACTION_PLAY_PREVIOUS_TRACK);
                myIntentActionPlay.replaceExtras(bundle);
                sendBroadcast(myIntentActionPlay);
            }
        });
    }

    private void getMediaStop() {
        play.setBackgroundResource(R.drawable.ic_play_circle_black);
        seekBar.setProgress(0);
        currentTime.setText("00:00");
        maxTime.setText("00:00");
        if (isRunnable) {
            setRunnable(false);
        }
    }

    private void getTrackStandBy(long duration) {
        //Not Click button when load track
        next.setClickable(false);
        play.setClickable(false);
        previous.setClickable(false);
        if (isRunnable) {
            setRunnable(false);
        }
        play.setBackgroundResource(R.drawable.ic_play_circle_black);
        seekBar.setMax((int) (duration / 1000));
        seekBar.setProgress(0);
        currentTime.setText("00:00");
        maxTime.setText(Utility.convertDuration(duration));
    }

    private void getTrackStart(String artworkUrl) {
        play.setBackgroundResource(R.drawable.ic_pause_circle_black);

        //Stream Ready To Play (Music is loaded)
        play.setClickable(true);
        previous.setClickable(true);
        next.setClickable(true);

        Picasso.with(this)
                .load(artworkUrl)
                .placeholder(R.drawable.music_placeholder)
                .into(imgAlbum);

        if (!isRunnable) {
            setRunnable(true);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (Utility.isMyServiceRunning(this, MyMediaService.class)) {
            //Show PlayMusicLayout
            if (rlMainPlayMusic.getVisibility() == View.GONE || rlMainPlayMusic.getVisibility() == View.INVISIBLE) {
                rlMainPlayMusic.setVisibility(View.VISIBLE);
            }
            Bundle bundle = new Bundle();
            bundle.putInt("trackAction", MyConfig.MY_ACTION_PLAY_REQUEST_TRACK);
            myIntentActionPlay.replaceExtras(bundle);
            sendBroadcast(myIntentActionPlay);
        } else {
            rlMainPlayMusic.setVisibility(View.GONE);
        }
        if (!mBroadcastRegistered) {
            registerReceiver(myReceiverMediaPlay, new IntentFilter(MyConfig.MY_MEDIA_PLAY));
            mBroadcastRegistered = true;
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mBroadcastRegistered) {
            unregisterReceiver(myReceiverMediaPlay);
            mBroadcastRegistered = false;
        }
        setRunnable(false);
    }
}
