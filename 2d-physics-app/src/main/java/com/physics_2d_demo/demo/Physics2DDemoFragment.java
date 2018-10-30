package com.physics_2d_demo.demo;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.physics_2d_demo.R;
import com.physics_2d_demo.SimpleDrawingView;

/*
 * @author Stefan Wyszyński 2009 przeniesiona do androida z J2ME
 */
public class Physics2DDemoFragment extends Fragment {
    private SimpleDrawingView simpleDrawingView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_2d, container, false);
        simpleDrawingView = view.findViewById(R.id.canv);
        return view;
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getActivity().setTitle(getString(R.string.fragment_main_title));
    }

    @Override
    public void onResume() {
        super.onResume();

        if (simpleDrawingView != null) simpleDrawingView.startRendering();
    }

    @Override
    public void onPause() {
        if (simpleDrawingView != null) simpleDrawingView.stopRendering();
        super.onPause();
    }
}
