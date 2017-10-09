package crud.unlocked.musicaudio.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
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
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.toolbox.JsonArrayRequest;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.ArrayList;
import java.util.List;

import crud.unlocked.learnvolley.R;
import crud.unlocked.musicaudio.MainActivity;
import crud.unlocked.musicaudio.adapter.SimpleDividerItemDecoration;
import crud.unlocked.musicaudio.adapter.TrackListAdapter;
import crud.unlocked.musicaudio.model.Playlist;
import crud.unlocked.musicaudio.model.Track;
import crud.unlocked.musicaudio.util.MyConfig;
import crud.unlocked.musicaudio.util.MyMediaService;
import crud.unlocked.musicaudio.util.MySharedPreferences;
import crud.unlocked.musicaudio.util.SoundCloud;
import crud.unlocked.musicaudio.util.Utility;
import crud.unlocked.musicaudio.util.VolleySingleton;

/**
 * Created by Irelia on 12/25/2016.
 */

public class FragmentSearchPlaylistDetail extends Fragment {
    public static final String TAG = "SearchPlaylistDetail";
    private LinearLayoutManager linearLayoutManager;
    private RelativeLayout rlMainPlayMusic, rlBanner;
    private FrameLayout flContentMain;
    private Playlist mPlaylist;
    private View myView;
    //Playlist detail
    private TextView txtDetailTitle, txtDetailArtist, txtDetailDuration, txtDetailLikes;
    private ImageView btnDetailPlay, btnDetailMore, btnDetailLike;
    private RecyclerView mRecycler;
    private TrackListAdapter mAdapter;
    private ArrayList<Track> mTrackList;
    private int currentIndex;
    private ProgressBar pb_main_loader;
    private Intent myMediaService;
    private Intent myBroadcastActionPlay;
    private boolean mBroadcastRegistered = false;
    private boolean isFirstLaunch = true;

    //Create Fragment with bundle arguments
    public static FragmentSearchPlaylistDetail newInstance(Playlist playlist) {
        FragmentSearchPlaylistDetail f = new FragmentSearchPlaylistDetail();
        // Supply index input as an argument.
        Bundle args = new Bundle();
        args.putParcelable("playlist", playlist);
        f.setArguments(args);
        return f;
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (myView == null) {
            //Get bundle arguments
            Bundle args = getArguments();
            mPlaylist = args.getParcelable("playlist");
            mTrackList = new ArrayList<>();
            rlMainPlayMusic = (RelativeLayout) getActivity().findViewById(R.id.rlMainPlayMusic);
            myBroadcastActionPlay = new Intent(MyConfig.MY_ACTION_PLAY);

            myView = inflater.inflate(R.layout.fragment_playlist_detail, container, false);
            initializeDetailView();
            pb_main_loader = (ProgressBar) myView.findViewById(R.id.pb_main_loader);
            flContentMain = (FrameLayout) myView.findViewById(R.id.fl_content_main);
            mRecycler = (RecyclerView) myView.findViewById(R.id.recycler);
            linearLayoutManager = new LinearLayoutManager(getContext());
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
            requestTrackListByTrackUri(mPlaylist.getTracksUri());
        }
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Playlist");
        return myView;
    }

    private void initializeDetailView() {
        rlBanner = (RelativeLayout) myView.findViewById(R.id.rlBanner);
        Picasso.with(getActivity())
                .load(mPlaylist.getArtworkUrl().replace("large.jpg", "t300x300.jpg"))
                .into(new Target() {
                    @Override
                    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                        rlBanner.setBackground(new BitmapDrawable(getResources(), bitmap));
                    }

                    @Override
                    public void onBitmapFailed(Drawable errorDrawable) {
                        Log.d(TAG, "Bitmap Failed");
                    }

                    @Override
                    public void onPrepareLoad(Drawable placeHolderDrawable) {
                        Log.d(TAG, "Prepare Load");
                    }
                });
        txtDetailTitle = (TextView) myView.findViewById(R.id.playlist_detail_title);
        txtDetailTitle.setText(mPlaylist.getTitle());
        txtDetailArtist = (TextView) myView.findViewById(R.id.playlist_detail_artist);
        txtDetailArtist.setText(mPlaylist.getArtist());
        txtDetailDuration = (TextView) myView.findViewById(R.id.playlist_detail_track_duration);
        txtDetailDuration.setText(String.valueOf(mPlaylist.getTrackCount()) + (mPlaylist.getTrackCount() > 1 ? " tracks, " : " track, ")
                + Utility.convertDuration(mPlaylist.getDuration()));
        txtDetailLikes = (TextView) myView.findViewById(R.id.playlist_detail_like);
        txtDetailLikes.setText(String.valueOf(mPlaylist.getLikesCount()));
        btnDetailPlay = (ImageView) myView.findViewById(R.id.playlist_detail_play);
        btnDetailPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!Utility.isMyServiceRunning(getActivity(), MyMediaService.class)) {
                    getActivity().startService(myMediaService.putExtra("currentIndex", 0));
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
                        getActivity().startService(myMediaService.putExtra("currentIndex", 0));
                        if (!mBroadcastRegistered) {
                            getActivity().registerReceiver(myRecyclerReceiverMediaPlay, new IntentFilter(MyConfig.MY_MEDIA_PLAY));
                            mBroadcastRegistered = true;
                        }
                        isFirstLaunch = false;
                    } else {
                        //Broadcast Receiver
                        myBroadcastActionPlay.putExtra("trackAction", MyConfig.MY_ACTION_PLAY_CURRENT_TRACK);
                        myBroadcastActionPlay.putExtra("trackIndex", 0);
                        getActivity().sendBroadcast(myBroadcastActionPlay);
                    }
                }
            }
        });
        btnDetailLike = (ImageView) myView.findViewById(R.id.playlist_detail_image_like);
        btnDetailLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        btnDetailMore = (ImageView) myView.findViewById(R.id.playlist_detail_more);
        btnDetailMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
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

    private void requestTrackListByTrackUri(String uri) {
        pb_main_loader.setVisibility(View.VISIBLE);
        String url = Uri.parse(uri).buildUpon().appendQueryParameter("client_id", MyConfig.CLIENT_ID).build().toString();
        JsonArrayRequest requestTrackList = SoundCloud.requestTrackListBySearch(url, new SoundCloud.TrackListInterface() {
            @Override
            public void onSuccess(List<Track> trackList) {
                if (trackList.isEmpty()) {
                    flContentMain.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.emty_list));
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
