package crud.unlocked.musicaudio.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.android.volley.toolbox.JsonArrayRequest;

import java.util.ArrayList;
import java.util.List;

import crud.unlocked.learnvolley.R;
import crud.unlocked.musicaudio.MainActivity;
import crud.unlocked.musicaudio.adapter.PlaylistAdapter;
import crud.unlocked.musicaudio.adapter.SimpleDividerItemDecoration;
import crud.unlocked.musicaudio.model.Playlist;
import crud.unlocked.musicaudio.model.Track;
import crud.unlocked.musicaudio.util.MyConfig;
import crud.unlocked.musicaudio.util.SoundCloud;
import crud.unlocked.musicaudio.util.VolleySingleton;

/**
 * Created by Irelia on 12/24/2016.
 */

public class FragmentSearchPlaylist extends Fragment {
    public static final String TAG = "FragmentSearchPlaylist";
    private RecyclerView mRecycler;
    private PlaylistAdapter mAdapter;
    private List<Playlist> mPlaylist;
    private String mQuery;
    private ProgressBar pb_main_loader;
    private FrameLayout flContentMain;
    private View myView;

    public static FragmentSearchPlaylist newInstance(String query) {
        FragmentSearchPlaylist f = new FragmentSearchPlaylist();
        // Supply index input as an argument.
        Bundle args = new Bundle();
        args.putString("query", query);
        f.setArguments(args);
        return f;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (myView == null) {
            //Get bundle arguments
            Bundle args = getArguments();
            mQuery = args.getString("query");
            mPlaylist = new ArrayList<>();
            myView = inflater.inflate(R.layout.frame_recycler, container, false);
            pb_main_loader = (ProgressBar) myView.findViewById(R.id.pb_main_loader);
            flContentMain = (FrameLayout) myView.findViewById(R.id.fl_content_main);
            mRecycler = (RecyclerView) myView.findViewById(R.id.recycler);
            mRecycler.setLayoutManager(new LinearLayoutManager(getActivity()));
            mRecycler.setItemAnimator(new DefaultItemAnimator());
            mRecycler.addItemDecoration(new SimpleDividerItemDecoration(getActivity()));
            mAdapter = new PlaylistAdapter(getActivity(), mPlaylist, new PlaylistAdapter.RecyclerItemClickListener() {
                @Override
                public void onClickListener(Playlist playlist, int position) {
                    VolleySingleton.getInstance(getActivity()).cancelRequests(TAG);
                    FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                    ft.replace(R.id.content_main, FragmentSearchPlaylistDetail.newInstance(playlist));
                    ft.addToBackStack("null");
                    ft.commit();
                }
            });
            mRecycler.setAdapter(mAdapter);
            requestPlaylistByQuery(mQuery);
        }
        return myView;
    }

    private void requestPlaylistByQuery(String query) {
        pb_main_loader.setVisibility(View.VISIBLE);
        String url = MyConfig.ENDPOINT.buildUpon()
                .appendPath(MyConfig.PATH_PLAYLIST)
                .appendQueryParameter("q", query)
                .appendQueryParameter("limit", "50").build().toString();
        JsonArrayRequest requestPlaylist = SoundCloud.requestPlaylistBySearch(url, new SoundCloud.PlaylistsInterface() {
            @Override
            public void onSuccess(List<Playlist> playlists) {
                if (playlists.isEmpty()) {
                    flContentMain.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.no_result));
                }else {
                    mPlaylist.addAll(playlists);
                    mAdapter.notifyDataSetChanged();
                    pb_main_loader.setVisibility(View.GONE);
                }
            }
        });
        VolleySingleton.getInstance(getActivity()).addToRequestQueue(requestPlaylist, TAG);
    }

    @Override
    public void onDestroy() {
        VolleySingleton.getInstance(getActivity()).cancelRequests(TAG);
        super.onDestroy();
    }
}
