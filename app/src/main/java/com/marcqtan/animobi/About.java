package com.marcqtan.animobi;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

/**
 * Created by Marc Q. Tan on 27/03/2018.
 */

public class About extends Fragment {
    public About(){

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.about,container,false);
        ImageView img = rootView.findViewById(R.id.imageView);
        Glide.with(this).load(R.drawable.cover).into(img);

        return rootView;
    }
}
