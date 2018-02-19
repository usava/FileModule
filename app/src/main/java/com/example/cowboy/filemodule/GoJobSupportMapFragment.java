package com.example.cowboy.filemodule;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.SupportMapFragment;

/**
 * Created by Cowboy on 07.02.2018.
 */

public class GoJobSupportMapFragment extends SupportMapFragment {
    public View mOriginalContentView;
    public TouchableWrapper mTouchView;
    private boolean isPause;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        mOriginalContentView = super.onCreateView(inflater, parent, savedInstanceState);
        mTouchView = new TouchableWrapper(getActivity(), this);
        mTouchView.addView(mOriginalContentView);
        return mTouchView;
    }

    @Override
    public View getView() {
        return mOriginalContentView;
    }

    @Override
    public void onPause(){
        isPause = true;
        super.onPause();
    }
    @Override
    public void onResume(){
        isPause = false;
        super.onResume();
    }

    public boolean getPauseStatus(){
        return isPause;
    }
}