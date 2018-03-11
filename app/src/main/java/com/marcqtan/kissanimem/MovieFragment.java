package com.marcqtan.kissanimem;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.Fragment;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AlertDialog;
import android.support.v7.graphics.Palette;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.GlideBitmapDrawable;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.ImageViewTarget;
import com.bumptech.glide.request.target.Target;

import org.jsoup.Jsoup;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public class MovieFragment extends Fragment implements Utility.interface2 {

    Anime anime;
    TextView summary;
    FrameLayout frame;
    static List<String> quality_name = new ArrayList<>();
    ListView list;
    static List<String> quality_list;
    ImageButton play;
    ImageView image;
    NestedScrollView scroll;
    CollapsingToolbarLayout test;
    CoordinatorLayout movieLayout;
    AsyncTask task = null;
    int color;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.activity_movie, container,false);
        summary = rootView.findViewById(R.id.summary);
        frame = rootView.findViewById(R.id.progressBarContainer);
        image = rootView.findViewById(R.id.backdrop);
        //layout = rootView.findViewById(R.id.layoutContainer);
        play = rootView.findViewById(R.id.play);
        anime = (Anime) getArguments().getSerializable("anime");
        scroll = rootView.findViewById(R.id.scroll);
        test = rootView.findViewById(R.id.collapsing_toolbar);
        movieLayout = rootView.findViewById(R.id.movieLayout);

        image.setTransitionName(getArguments().getString("transitionName"));

        Glide.with(getActivity())
                .load(anime.getAnimeThumbnail())
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .dontAnimate()
                .listener(new RequestListener<String, GlideDrawable>() {
                    @Override
                    public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                        getActivity().startPostponedEnterTransition();
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                        getActivity().startPostponedEnterTransition();
                        return false;
                    }
                })
                .into(new ImageViewTarget<GlideDrawable>(image) {
                    @Override
                    protected void setResource(GlideDrawable resource) {
                        setImage(resource);
                        extractColors(resource);
                    }

                    private void setImage(GlideDrawable resource) {
                        image.setImageDrawable(resource.getCurrent());
                    }

                    private void extractColors(GlideDrawable resource) {
                        Bitmap b = ((GlideBitmapDrawable) resource.getCurrent()).getBitmap();
                        Palette p = Palette.from(b).generate();

                        extractBackgroundColor(p);
                    }

                    private void extractBackgroundColor(Palette p) {
                        int defaultColor = getActivity().getResources().getColor(R.color.colorPrimary);


                        //                        int color = p.getDominantColor(defaultColor);
                        //                        int color = p.getMutedColor(defaultColor);
                        //                        int color = p.getLightMutedColor(defaultColor);
                        //                        int color = p.getDarkMutedColor(defaultColor);
                        //                        int color = p.getVibrantColor(defaultColor);
                        //                        int color = p.getLightVibrantColor(defaultColor);
                        color = p.getDarkVibrantColor(defaultColor);
                        scroll.setBackgroundColor(color);
                        test.setContentScrimColor(color);
                    }
                });

        /*Picasso.with(getActivity())
                .load(anime.getAnimeThumbnail())
                .noFade()
                //.memoryPolicy(MemoryPolicy.NO_CACHE,MemoryPolicy.NO_STORE)
                //.networkPolicy(NetworkPolicy.NO_CACHE)
                .into(image, new Callback() {
                    @Override
                    public void onSuccess() {
                        getActivity().supportStartPostponedEnterTransition();
                    }

                    @Override
                    public void onError() {
                        getActivity().supportStartPostponedEnterTransition();
                    }
                });*/

        return rootView;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        postponeEnterTransition();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //setupBackdropHeight();
        if(anime.getSummary() == null) {
            if(quality_list != null) quality_list.clear();
            if(quality_name !=null) quality_name.clear();
            task = new getMovieInfo(this).execute(anime.getAnimeLink());
        } else {
            summary.setText(anime.getSummary());
        }
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
    public void onDestroyView() {
        super.onDestroyView();
        if(task != null) {
            task.cancel(true);
        }
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
            activity.get().quality_list = quality;
        }
    }
}
