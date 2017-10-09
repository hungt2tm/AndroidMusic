package crud.unlocked.musicaudio.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Point;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.android.volley.toolbox.JsonObjectRequest;

import java.util.ArrayList;
import java.util.List;

import crud.unlocked.learnvolley.R;
import crud.unlocked.musicaudio.adapter.SimpleDividerItemDecoration;
import crud.unlocked.musicaudio.adapter.TrackListAdapter;
import crud.unlocked.musicaudio.model.Track;
import crud.unlocked.musicaudio.util.MyConfig;
import crud.unlocked.musicaudio.util.MyMediaService;
import crud.unlocked.musicaudio.util.MySharedPreferences;
import crud.unlocked.musicaudio.util.SoundCloud;
import crud.unlocked.musicaudio.util.Utility;
import crud.unlocked.musicaudio.util.VolleySingleton;

/**
 * Created by Irelia on 12/17/2016.
 */

public class FragmentChartsGenreDetail extends Fragment {

    public static final String TAG = "FragmentChartsGenreDetail";
    private RecyclerView mRecycler;
    private TrackListAdapter mAdapter;
    private ArrayList<Track> mTrackList;
    private int currentIndex;
    private ProgressBar pb_main_loader;
    private boolean isTop;
    private String mGenre;
    private View myView;
    private Intent myMediaService;
    private Intent myBroadcastActionPlay;
    private boolean mBroadcastRegistered = false;
    private boolean isFirstLaunch = true;
    private LinearLayoutManager linearLayoutManager;
    private RelativeLayout rlMainPlayMusic;

    //Create Fragment with bundle arguments
    public static FragmentChartsGenreDetail newInstance(boolean isTop, String genre) {
        FragmentChartsGenreDetail f = new FragmentChartsGenreDetail();
        // Supply index input as an argument.
        Bundle args = new Bundle();
        args.putBoolean("isTop", isTop);
        args.putString("genre", genre);
        f.setArguments(args);
        return f;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (myView == null) {
            //Get bundle arguments
            Bundle args = getArguments();
            isTop = args.getBoolean("isTop");
            mGenre = args.getString("genre");
            mTrackList = new ArrayList<>();

            rlMainPlayMusic = (RelativeLayout) getActivity().findViewById(R.id.rlMainPlayMusic);
            mTrackList = new ArrayList<>();
            myBroadcastActionPlay = new Intent(MyConfig.MY_ACTION_PLAY);

            myView = inflater.inflate(R.layout.frame_recycler, container, false);
            linearLayoutManager = new LinearLayoutManager(getContext());
            pb_main_loader = (ProgressBar) myView.findViewById(R.id.pb_main_loader);
            mRecycler = (RecyclerView) myView.findViewById(R.id.recycler);
            mRecycler.setLayoutManager(linearLayoutManager);
            mRecycler.setItemAnimator(new DefaultItemAnimator());
            mRecycler.addItemDecoration(new SimpleDividerItemDecoration(getActivity()));
            mAdapter = new TrackListAdapter(getContext(), mTrackList, new TrackListAdapter.RecyclerItemClickListener() {
                @Override
                public void onClickListener(Track track, int position) {
                    //Check MediaService
                    //If false, not start again
                    if (!Utility.isMyServiceRunning(getActivity(), MyMediaService.class)) {
                        getActivity().startService(myMediaService.putExtra("currentIndex", position));
                        Log.d("Error", Utility.isMyServiceRunning(getActivity(), MyMediaService.class) ? "true" : "false");
                        //Show PlayMusicLayout
                        if (rlMainPlayMusic.getVisibility() == View.GONE || rlMainPlayMusic.getVisibility() == View.INVISIBLE) {
                            rlMainPlayMusic.setVisibility(View.VISIBLE);
                        }
                        if (!mBroadcastRegistered) {
                            getActivity().registerReceiver(myRecyclerReceiverMediaPlay, new IntentFilter(MyConfig.MY_MEDIA_PLAY));
                            mBroadcastRegistered = true;
                        }
                        isFirstLaunch = false;
                    } else {
                        if (isFirstLaunch) {
                            getActivity().stopService(myMediaService);
                            getActivity().startService(myMediaService.putExtra("currentIndex", position));
                            if (!mBroadcastRegistered) {
                                getActivity().registerReceiver(myRecyclerReceiverMediaPlay, new IntentFilter(MyConfig.MY_MEDIA_PLAY));
                                mBroadcastRegistered = true;
                            }
                            isFirstLaunch = false;
                        } else {
                            //Broadcast Receiver
                            myBroadcastActionPlay.putExtra("trackAction", MyConfig.MY_ACTION_PLAY_CURRENT_TRACK);
                            myBroadcastActionPlay.putExtra("trackIndex", position);
                            getActivity().sendBroadcast(myBroadcastActionPlay);
                            Log.d("PlayMusic", rlMainPlayMusic.getHeight() + "");
                        }
                    }
                }
            });
            mAdapter.setSelectedPosition(-1);
            mRecycler.setAdapter(mAdapter);
            requestChartsByGenre(mGenre);
        }
        return myView;
    }

    private BroadcastReceiver myRecyclerReceiverMediaPlay = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getIntExtra("action", -1)) {
                case MyConfig.MY_MEDIA_PLAY_TRACK_STANDBY:
                    getTrackStandBy();
                    break;
                case MyConfig.MY_MEDIA_PLAY_TRACK_START:
                    getTrackStart();
                    break;
                case MyConfig.MY_MEDIA_PLAY_INDEX:
                    currentIndex = intent.getIntExtra("index", currentIndex);
                    mAdapter.setSelectedPosition(currentIndex);
                    break;
                case MyConfig.MY_MEDIA_PLAY_PLAY:
                    if (intent.getBooleanExtra("isPlaying", false)) {
                        mAdapter.setPause(false);
                        mAdapter.notifyItemChanged(currentIndex);

                    } else {
                        mAdapter.setPause(true);
                        mAdapter.notifyItemChanged(currentIndex);
                    }
                    break;
                case MyConfig.MY_MEDIA_PLAY_CLOSE:
                    mAdapter.setSelectedPosition(-1);
                    mAdapter.notifyDataSetChanged();
                    break;
            }
        }
    };

    private void getTrackStandBy() {
        //Not Click button when load track
        mAdapter.setPause(true);
        mAdapter.setClickable(false);
        mAdapter.notifyDataSetChanged();
    }

    private void getTrackStart() {
        mAdapter.setPause(false);
        mAdapter.setClickable(true);
        mAdapter.notifyDataSetChanged();
        if (mRecycler.findViewHolderForLayoutPosition(currentIndex) != null) {
            if (mRecycler.findViewHolderForLayoutPosition(currentIndex).itemView.getY() < 0) {
                linearLayoutManager.scrollToPositionWithOffset(currentIndex, 0);
            } else {
                Display display = getActivity().getWindowManager().getDefaultDisplay();
                Point size = new Point();
                display.getSize(size);
                if (mRecycler.findViewHolderForLayoutPosition(currentIndex).itemView.getY() > size.y - 216) {
                    linearLayoutManager.scrollToPositionWithOffset(currentIndex, 0);
                }
            }

        } else {
            linearLayoutManager.scrollToPositionWithOffset(currentIndex, 0);
        }

    }

    private void requestChartsByGenre(String genre) {
        pb_main_loader.setVisibility(View.VISIBLE);
        String url = SoundCloud.buildUrl(isTop ? "top" : "trending", genre, "50");
        JsonObjectRequest trackRequest = SoundCloud.requestTrackListByGenre(url, new SoundCloud.TrackListInterface() {
            @Override
            public void onSuccess(List<Track> trackList) {
                if (!trackList.isEmpty()) {
                    mTrackList.addAll(trackList);
                    myMediaService = MyMediaService.newIntent(getActivity(),
                            mTrackList,
                            MySharedPreferences.getStoredPlayRandom(getActivity()),
                            MySharedPreferences.getStoredPlayLoop(getActivity()));
                    mAdapter.notifyDataSetChanged();
                }
                pb_main_loader.setVisibility(View.GONE);
            }
        });
        VolleySingleton.getInstance(getActivity()).addToRequestQueue(trackRequest, TAG);
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mBroadcastRegistered) {
            getActivity().unregisterReceiver(myRecyclerReceiverMediaPlay);
            mBroadcastRegistered = false;
        }
        isFirstLaunch = true;
        mAdapter.setSelectedPosition(-1);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onDestroy() {
        VolleySingleton.getInstance(getActivity()).cancelRequests(TAG);
        super.onDestroy();
    }
}
