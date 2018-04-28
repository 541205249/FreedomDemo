package com.jiazy.freedomdemo.frameanimation;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jiazy.freedomdemo.R;


public class PropertyAnimFragment extends Fragment {

    private View mView;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_property_animation, container, false);
        return mView;
    }

    public View getFragmentView(){
        return mView;
    }


}
