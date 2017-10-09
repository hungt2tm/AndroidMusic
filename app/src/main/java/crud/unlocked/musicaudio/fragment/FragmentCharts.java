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
 * Created by Irelia on 12/15/2016.
 */

public class FragmentCharts extends Fragment {
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private boolean isTop;
    private View myView;

    public static FragmentCharts newInstance(boolean isTop) {
        FragmentCharts f = new FragmentCharts();
        // Supply index input as an argument.
        Bundle args = new Bundle();
        args.putBoolean("isTop", isTop);
        f.setArguments(args);
        return f;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        isTop = getArguments().getBoolean("isTop");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (myView == null) {
            myView = inflater.inflate(R.layout.fragment_tabpager, container, false);
            viewPager = (ViewPager) myView.findViewById(R.id.viewPager);
            viewPager.setOffscreenPageLimit(2);
            setUpViewPager(viewPager);
            tabLayout = (TabLayout) myView.findViewById(R.id.tabLayout);
            tabLayout.setupWithViewPager(viewPager);
        }
        if (isTop){
            ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Top Playing");
        }else {
            ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("New & Hot");
        }
        return myView;
    }

    private void setUpViewPager(ViewPager viewPager) {
        MyViewPagerAdapter adapter = new MyViewPagerAdapter(getChildFragmentManager());
        FragmentChartsGenre audio = FragmentChartsGenre.newInstance(isTop, false);
        FragmentChartsGenre music = FragmentChartsGenre.newInstance(isTop, true);
        adapter.addFragment(music, "Music");
        adapter.addFragment(audio, "Audio");
        viewPager.setAdapter(adapter);
    }
}
