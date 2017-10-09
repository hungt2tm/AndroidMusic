package crud.unlocked.musicaudio.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import crud.unlocked.learnvolley.R;
import crud.unlocked.musicaudio.util.Utility;

/**
 * Created by Jarvis on 1/6/2017.
 */

public class FragmentConnection extends Fragment {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View v = inflater.inflate(R.layout.fragment_connection, container, false);
        return v;

    }
}
