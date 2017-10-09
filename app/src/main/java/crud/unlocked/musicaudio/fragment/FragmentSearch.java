package crud.unlocked.musicaudio.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import crud.unlocked.learnvolley.R;
import crud.unlocked.musicaudio.adapter.MyViewPagerAdapter;

/**
 * Created by Irelia on 12/21/2016.
 */

public class FragmentSearch extends Fragment {

    private TabLayout tabLayout;
    private ViewPager viewPager;
    private View myView;
    private String mQuery;

    public static FragmentSearch newInstance(String query) {
        FragmentSearch f = new FragmentSearch();
        Bundle args = new Bundle();
        args.putString("query", query);
        f.setArguments(args);
        return f;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (myView == null) {
            mQuery = getArguments().getString("query");
            myView = inflater.inflate(R.layout.fragment_tabpager, container, false);
            viewPager = (ViewPager) myView.findViewById(R.id.viewPager);
            viewPager.setOffscreenPageLimit(2);
            setUpViewPager(viewPager);
            tabLayout = (TabLayout) myView.findViewById(R.id.tabLayout);
            tabLayout.setupWithViewPager(viewPager);
        }
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Search Result");
        return myView;
    }

    private void setUpViewPager(ViewPager viewPager) {
        MyViewPagerAdapter adapter = new MyViewPagerAdapter(getChildFragmentManager());
        adapter.addFragment(FragmentSearchTrack.newInstance(mQuery), "Track");
        adapter.addFragment(FragmentSearchPlaylist.newInstance(mQuery), "Playlist");
        viewPager.setAdapter(adapter);
    }
}
