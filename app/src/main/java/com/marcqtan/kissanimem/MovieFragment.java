package com.marcqtan.kissanimem;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import org.jsoup.Jsoup;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public class MovieFragment extends Fragment implements Utility.interface2 {

    Anime anime;
    TextView summary;
    FrameLayout frame;
    LinearLayout layout;
    List<String> quality_name = new ArrayList<>();
    ListView list;
    List<String> quality_list;
    Button play;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.activity_movie, container,false);
        summary = rootView.findViewById(R.id.summary);
        frame = rootView.findViewById(R.id.progressBarContainer);
        layout = rootView.findViewById(R.id.layoutContainer);
        play = rootView.findViewById(R.id.play);
        anime = (Anime) getArguments().getSerializable("anime");

        return rootView;
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        new getMovieInfo(this).execute(anime.getAnimeLink());
        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(quality_list == null) {
                    AlertDialog.Builder adb = new AlertDialog.Builder(getActivity());
                    adb.setTitle("No available video stream");
                    adb.setMessage("Sorry");
                    adb.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    AlertDialog ad = adb.create();
                    ad.show();

                    Log.v("Error", "Error fetching video quality url");
                } else if (quality_list.size() == 1) {

                    if(getActivity() == null) {
                        return;
                    }

                    Intent i = new Intent(getActivity(), exoactivity.class);
                    i.putExtra("vidurl", quality_list.get(0));
                    i.putExtra("animeName", anime.getAnimeName());
                    startActivity(i);

                } else {
                    Utility.showBottomSheet(MovieFragment.this, quality_list, quality_name, anime.getAnimeName());
                }
            }
        });
    }

    @Override
    public void showVisibilty() {
        frame.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideVisibility() {
        frame.setVisibility(View.GONE);
    }

    @Override
    public Context getCtx() {
        return getActivity();
    }

    @Override
    public Activity getActvty() {
        return getActivity();
    }

    static class getMovieInfo extends AsyncTask<String, Void, List<String>> {

        private WeakReference<MovieFragment> activity;

        getMovieInfo(MovieFragment activity) {
            this.activity = new WeakReference<MovieFragment>(activity);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            activity.get().frame.setVisibility(View.VISIBLE);
            activity.get().layout.setVisibility(View.GONE);
        }

        @Override
        protected List<String> doInBackground(String... strings) {
            try {
                org.jsoup.nodes.Document doc = Jsoup.connect(strings[0]).timeout(30000).userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/61.0.3163.100 Safari/537.36 OPR/48.0.2685.50")
                        .followRedirects(true)
                        .get();

                activity.get().anime.setSummary(doc.select("div.some-more-info").select("p").text());

                return Utility.getQuality(doc, activity.get().quality_name);
            } catch (IOException e) {
                e.printStackTrace();
                Log.v("getAnimeEpisode()", "Error accessing link");
                return null;
            }
        }

        @Override
        protected void onPostExecute(List<String> quality) {
            super.onPostExecute(quality);
            activity.get().frame.setVisibility(View.GONE);
            activity.get().summary.setText(activity.get().anime.getSummary());
            activity.get().layout.setVisibility(View.VISIBLE);
            activity.get().quality_list = quality;
        }
    }
}
