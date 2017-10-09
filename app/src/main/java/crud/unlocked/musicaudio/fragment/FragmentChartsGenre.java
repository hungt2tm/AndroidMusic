package crud.unlocked.musicaudio.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.android.volley.toolbox.JsonObjectRequest;

import java.util.ArrayList;
import java.util.List;

import crud.unlocked.learnvolley.R;
import crud.unlocked.musicaudio.adapter.ChartsGenreAdapter;
import crud.unlocked.musicaudio.adapter.SimpleDividerItemDecoration;
import crud.unlocked.musicaudio.model.Genre;
import crud.unlocked.musicaudio.model.Track;
import crud.unlocked.musicaudio.util.SoundCloud;
import crud.unlocked.musicaudio.util.MyConfig;
import crud.unlocked.musicaudio.util.Utility;
import crud.unlocked.musicaudio.util.VolleySingleton;

/**
 * Created by Irelia on 12/13/2016.
 */

public class FragmentChartsGenre extends Fragment {
    public static final String TAG = "FragmentChartsGenre";
    private List<Genre> mGenre = new ArrayList<>();
    private ChartsGenreAdapter mChartGenreAdapter;
    private RecyclerView mRecyclerView;
    private boolean isMusic;
    private boolean isTop;
    private View myView;
    private ProgressBar loader;

    public static FragmentChartsGenre newInstance(boolean isTop, boolean isMusic) {
        FragmentChartsGenre f = new FragmentChartsGenre();
        // Supply index input as an argument.
        Bundle args = new Bundle();
        args.putBoolean("isTop", isTop);
        args.putBoolean("isMusic", isMusic);
        f.setArguments(args);
        return f;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        isTop = args.getBoolean("isTop");
        isMusic = args.getBoolean("isMusic");
        mChartGenreAdapter = new ChartsGenreAdapter(getActivity(), mGenre, new ChartsGenreAdapter.MyRecyclerItemClickListener() {
            @Override
            public void onClickListener(Genre genre, int position) {
                VolleySingleton.getInstance(getActivity()).cancelRequests(TAG);
                ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(isTop?"Top ":"Trending " + genre.getTitle());
                FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.content_main, FragmentChartsGenreDetail.newInstance(isTop, genre.getGenre()));
                ft.addToBackStack(null);
                ft.commit();
            }
        });
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (myView == null) {
            myView = inflater.inflate(R.layout.frame_recycler, container, false);
            loader = (ProgressBar) myView.findViewById(R.id.pb_main_loader);
            mRecyclerView = (RecyclerView) myView.findViewById(R.id.recycler);
            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
            mRecyclerView.setLayoutManager(mLayoutManager);
            mRecyclerView.setItemAnimator(new DefaultItemAnimator());
            mRecyclerView.addItemDecoration(new SimpleDividerItemDecoration(getActivity()));
            mRecyclerView.setAdapter(mChartGenreAdapter);
            update();
        }
        return myView;
    }

    private void update() {
        if (isMusic) {
            requestAllChartsByGenre(MyConfig.HIPHOP_RAP);
            requestAllChartsByGenre(MyConfig.POP);
            requestAllChartsByGenre(MyConfig.CLASSICAL);
            requestAllChartsByGenre(MyConfig.COUNTRY);
            requestAllChartsByGenre(MyConfig.RB_SOUL);
            requestAllChartsByGenre(MyConfig.ALTERNATIVE_ROCK);
            requestAllChartsByGenre(MyConfig.DANCE_EDM);
            requestAllChartsByGenre(MyConfig.PIANO);
            requestAllChartsByGenre(MyConfig.INDIE);
            requestAllChartsByGenre(MyConfig.JAZZ_BLUES);
            requestAllChartsByGenre(MyConfig.ROCK);
            requestAllChartsByGenre(MyConfig.ELECTRONIC);
            requestAllChartsByGenre(MyConfig.HOUSE);
            requestAllChartsByGenre(MyConfig.DANCE_HALL);
            requestAllChartsByGenre(MyConfig.LATIN);
            requestAllChartsByGenre(MyConfig.METAL);
            requestAllChartsByGenre(MyConfig.FOLK_SINGER_SONG_WRITER);
            requestAllChartsByGenre(MyConfig.DRUM_BASS);
            requestAllChartsByGenre(MyConfig.DUBSTEP);
            requestAllChartsByGenre(MyConfig.DEEP_HOUSE);
            requestAllChartsByGenre(MyConfig.DISCO);
            requestAllChartsByGenre(MyConfig.REGGAE);
            requestAllChartsByGenre(MyConfig.REGGAETON);
            requestAllChartsByGenre(MyConfig.SOUND_TRACK);
            requestAllChartsByGenre(MyConfig.TECHNO);
            requestAllChartsByGenre(MyConfig.TRANCE);
            requestAllChartsByGenre(MyConfig.TRAP);
            requestAllChartsByGenre(MyConfig.TRIPHOP);
            requestAllChartsByGenre(MyConfig.WORLD);
            requestAllChartsByGenre(MyConfig.AMBIENT);
        } else {
            requestAllChartsByGenre(MyConfig.AUDIO_BOOKS);
            requestAllChartsByGenre(MyConfig.BUSINESS);
            requestAllChartsByGenre(MyConfig.COMEDY);
            requestAllChartsByGenre(MyConfig.ENTERTAINMENT);
            requestAllChartsByGenre(MyConfig.LEARNING);
            requestAllChartsByGenre(MyConfig.NEWS_POLITICS);
            requestAllChartsByGenre(MyConfig.RELIGION_SPIRITUALITY);
            requestAllChartsByGenre(MyConfig.SCIENCE);
            requestAllChartsByGenre(MyConfig.SPORTS);
            requestAllChartsByGenre(MyConfig.STORYTELLING);
            requestAllChartsByGenre(MyConfig.TECHNOLOGY);
        }
    }

    private void requestAllChartsByGenre(final String style) {
        String url = SoundCloud.buildUrl(isTop ? "top" : "trending", style, "13");
        JsonObjectRequest req = SoundCloud.requestChartsGenres(url, new SoundCloud.TrackListInterface() {
                    @Override
                    public void onSuccess(List<Track> trackList) {
                        Genre genre = new Genre();
                        genre.setTitle(Utility.convertGenreTitle(style));
                        genre.setGenre(style);
                        if (trackList.size() > 2) {
                            genre.setPhotoFirst(trackList.get(0).getArtworkUrl());
                            genre.setPhotoSecond(trackList.get(1).getArtworkUrl());
                            genre.setPhotoThird(trackList.get(2).getArtworkUrl());
                        }else if (trackList.size() == 2) {
                            genre.setPhotoFirst(trackList.get(0).getArtworkUrl());
                            genre.setPhotoSecond(trackList.get(1).getArtworkUrl());
                        }else if (trackList.size() == 1){
                            genre.setPhotoFirst(trackList.get(0).getArtworkUrl());
                        }
                        mGenre.add(genre);
                        if (mGenre.size() == 6) {
                            mChartGenreAdapter.setClickable(true);
                            loader.setVisibility(View.GONE);
                            mChartGenreAdapter.notifyDataSetChanged();
                        }else if (mGenre.size() >6){
                            mChartGenreAdapter.notifyDataSetChanged();
                        }
                    }
                });
        VolleySingleton.getInstance(getActivity()).addToRequestQueue(req, TAG);
    }

    @Override
    public void onDestroy() {
        VolleySingleton.getInstance(getActivity()).cancelRequests(TAG);
        super.onDestroy();
    }
}
