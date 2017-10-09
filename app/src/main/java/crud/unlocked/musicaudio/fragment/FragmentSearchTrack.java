package crud.unlocked.musicaudio.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Point;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.android.volley.toolbox.JsonArrayRequest;

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

public class FragmentSearchTrack extends Fragment {

    public static final String TAG = "FragmentSearchTrack";
    private String mQuery;
    private RecyclerView mRecycler;
    private TrackListAdapter mAdapter;
    private ArrayList<Track> mTrackList;
    private int currentIndex;
    private ProgressBar pb_main_loader;
    private RelativeLayout rlMainPlayMusic;
    private View myView;
    private Intent myMediaService;
    private Intent myBroadcastActionPlay;
    private boolean mBroadcastRegistered = false;
    private boolean isFirstLaunch = true;
    private LinearLayoutManager linearLayoutManager;
    private FrameLayout flContentMain;


    //Create Fragment with bundle arguments
    public static FragmentSearchTrack newInstance(String query) {
        FragmentSearchTrack f = new FragmentSearchTrack();
        // Supply index input as an argument.
        Bundle args = new Bundle();
        args.putString("query", query);
        f.setArguments(args);
        return f;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Get bundle arguments
        Bundle args = getArguments();
        mQuery = args.getString("query");
        mTrackList = new ArrayList<>();
    }

    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (myView == null) {
            rlMainPlayMusic = (RelativeLayout) getActivity().findViewById(R.id.rlMainPlayMusic);
            mTrackList = new ArrayList<>();
            myBroadcastActionPlay = new Intent(MyConfig.MY_ACTION_PLAY);

            myView = inflater.inflate(R.layout.frame_recycler, container, false);
            flContentMain = (FrameLayout) myView.findViewById(R.id.fl_content_main);
            pb_main_loader = (ProgressBar) myView.findViewById(R.id.pb_main_loader);
            linearLayoutManager = new LinearLayoutManager(getContext());
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
                        }
                    }
                }
            });
            mAdapter.setSelectedPosition(-1);
            mRecycler.setAdapter(mAdapter);
            requestTrackListByQuery(mQuery);
        }
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Search Result");
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

    private void requestTrackListByQuery(String query) {
        pb_main_loader.setVisibility(View.VISIBLE);
        String url = MyConfig.ENDPOINT.buildUpon()
                .appendPath(MyConfig.PATH_TRACKS)
                .appendQueryParameter("q", query)
                .appendQueryParameter("filter", "public")
                .appendQueryParameter("limit", "50").build().toString();
        JsonArrayRequest requestTrackList = SoundCloud.requestTrackListBySearch(url, new SoundCloud.TrackListInterface() {
            @Override
            public void onSuccess(List<Track> trackList) {
                if (trackList.isEmpty()) {
                    flContentMain.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.no_result));
                } else {
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
        VolleySingleton.getInstance(getActivity()).addToRequestQueue(requestTrackList, TAG);
    }
}
