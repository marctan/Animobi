package com.marcqtan.kissanimem;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.FrameLayout;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by dell on 13/02/2018.
 */

final class Utility {

    public static class GridSpacingItemDecoration extends RecyclerView.ItemDecoration {

        private int spanCount;
        private int spacing;
        private boolean includeEdge;

        GridSpacingItemDecoration(int spanCount, int spacing, boolean includeEdge) {
            this.spanCount = spanCount;
            this.spacing = spacing;
            this.includeEdge = includeEdge;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            int position = parent.getChildAdapterPosition(view); // item position
            int column = position % spanCount; // item column

            if (includeEdge) {
                outRect.left = spacing - column * spacing / spanCount; // spacing - column * ((1f / spanCount) * spacing)
                outRect.right = (column + 1) * spacing / spanCount; // (column + 1) * ((1f / spanCount) * spacing)

                if (position < spanCount) { // top edge
                    outRect.top = spacing;
                }
                outRect.bottom = spacing; // item bottom
            } else {
                outRect.left = column * spacing / spanCount; // column * ((1f / spanCount) * spacing)
                outRect.right = spacing - (column + 1) * spacing / spanCount; // spacing - (column + 1) * ((1f /    spanCount) * spacing)
                if (position >= spanCount) {
                    outRect.top = spacing; // item top
                }
            }
        }
    }

    /**
     * Converting dp to pixel
     */
    static int dpToPx(int dp, Resources resource) {
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, resource.getDisplayMetrics()));
    }

    static List<Anime> parseAllAnimeName(Document doc) {
        Elements animeInfo = doc.select("div.ep-box");
        List<Anime> animelist;

        if (animeInfo.size() ==0){
            return null;
        }
        animelist = new ArrayList<>();
        for(Element info : animeInfo ){
            Anime anime = new Anime();
            Element hrefInfo = info.select("a").first();
            anime.setAnimeName(hrefInfo.text());
            anime.setAnimeLink(hrefInfo.attr("href"));
            anime.setThumbNail(info.select("img").attr("src"));
            anime.setEpisodeCount(info.select("span.ep-no").text());
            animelist.add(anime);
        }
        return animelist;
    }

    static void initCollapsingToolbar(CollapsingToolbarLayout toolbarlayout, AppBarLayout appbarlayout, final String appname) {
        final CollapsingToolbarLayout collapsingToolbar = toolbarlayout;
        collapsingToolbar.setTitle(" ");
        appbarlayout.setExpanded(true);

        // hiding & showing the title when toolbar expanded & collapsed
        appbarlayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            boolean isShow = false;
            int scrollRange = -1;

            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (scrollRange == -1) {
                    scrollRange = appBarLayout.getTotalScrollRange();
                }
                if (scrollRange + verticalOffset == 0) {
                    collapsingToolbar.setTitle(appname);
                    isShow = true;
                } else if (isShow) {
                    collapsingToolbar.setTitle(" ");
                    isShow = false;
                }
            }
        });
    }

    public static class getAnimeEpisode extends AsyncTask<Anime, Void, Anime> {
        Context context;
        FrameLayout frame;

        getAnimeEpisode(Context context, FrameLayout frame) {
            this.context = context;
            this.frame = frame;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            frame.setVisibility(View.VISIBLE);
        }

        @Override
        protected Anime doInBackground(Anime... params) {
            Anime animeSelected = params[0];
            try {
                Document doc = Jsoup.connect(animeSelected.getAnimeLink()).timeout(30000).userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/61.0.3163.100 Safari/537.36 OPR/48.0.2685.50")
                        .followRedirects(true)
                        .get();
                parseAnimeEpisode(animeSelected, doc);
                return animeSelected;
            } catch (IOException e) {
                e.printStackTrace();
                Log.v("getAnimeEpisode()", "Error accessing link");
                return null;
            }
        }

        @Override
        protected void onPostExecute(Anime anime) {
            super.onPostExecute(anime);
            frame.setVisibility(View.GONE);

            if(anime.retrieveEpisodes() == null) {
                Log.v("getAnimeEpisode()", "ERROR on postExecute!!!");
                return;
            }

            Intent i = new Intent(context, EpisodeList.class);
            i.putExtra("anime", anime);
            context.startActivity(i);
        }
    }

    private static void parseAnimeEpisode(Anime anime, Document doc) {
        String episode_list = doc.select("div.ep-list").select("a").get(0).text();
        int latestEpisode = Integer.parseInt(episode_list.substring(8));

        anime.initEpisodeList();

        for (int i = 1; i <= latestEpisode; i++) {
            String episode_name = "Episode " + i;
            String episode_link = anime.getAnimeLink() + "/episode-" + i;
            Map.Entry<String, String> episode_name_link = new AbstractMap.SimpleEntry<>(episode_name,episode_link);

            anime.addEpisodeInfo(episode_name_link);
        }
        anime.setSummary(doc.select("div.some-more-info").select("p").text());
    }

    public static class getAnimeVideo extends AsyncTask<String, Void, List<String>> {
        Context context;
        List<String> ep_names = new ArrayList<String>();
        Anime anime;
        FrameLayout frame;

        public getAnimeVideo(Context context, Anime anime, FrameLayout frame) {
            this.context = context;
            this.anime = anime;
            this.frame = frame;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            frame.setVisibility(View.VISIBLE);
        }

        @Override
        protected List<String> doInBackground(String... params) {
            try {
                Document doc = Jsoup.connect(params[0]).timeout(30000).userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/61.0.3163.100 Safari/537.36 OPR/48.0.2685.50")
                        .followRedirects(true)
                        .get();
                return Utility.getVideoLink(doc, ep_names);
            } catch (IOException e) {
                e.printStackTrace();
                Log.v("getAnimeEpisode()", "Error accessing link");
                return null;
            }
        }

        @Override
        protected void onPostExecute(List<String> quality) {
            super.onPostExecute(quality);
            frame.setVisibility(View.GONE);

            if(quality == null) {
                AlertDialog.Builder adb = new AlertDialog.Builder(context);
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
            } else if (quality.size() == 1) {

                Intent i = new Intent(context, exoactivity.class);
                i.putExtra("vidurl", quality.get(0));
                i.putExtra("animeName", anime.getAnimeName());
                context.startActivity(i);

            } else {
                Intent i = new Intent(context, QualityList.class);
                i.putStringArrayListExtra("vidurl", (ArrayList<String>)quality);
                i.putStringArrayListExtra("ep_name", (ArrayList<String>) ep_names);
                i.putExtra("animeName", anime.getAnimeName());
                context.startActivity(i);
            }
        }
    }

    static List<String> getVideoLink(Document doc, List<String> ep_names) {
        try {
            String vidDirectUrl = doc.select("div#Rapidvideo").select("iframe[src]").attr("src");

            if(vidDirectUrl == "") {
                return null;
            }

            vidDirectUrl = "https://otakustream.tv" + vidDirectUrl;
            Document doc2 = Jsoup.connect(vidDirectUrl).timeout(30000).userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/61.0.3163.100 Safari/537.36 OPR/48.0.2685.50")
                    .followRedirects(true)
                    .get();
            vidDirectUrl = doc2.select("iframe[src]").attr("src");

            Document doc3 = Jsoup.connect(vidDirectUrl).timeout(30000).userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/61.0.3163.100 Safari/537.36 OPR/48.0.2685.50")
                    .followRedirects(true)
                    .get();

            Elements a = doc3.select("a"); //select all quality
            List<String> quality = new ArrayList<String>();
            if(a.size() <= 1) {
                quality.add(doc3.select("video#videojs").select("source[src]").attr("src"));
                return quality;
            }

            for(Element x:a) {
                quality.add(x.select("a[href]").attr("href"));
                ep_names.add(x.text());
            }

            return quality;

        } catch (IOException e) {
            e.printStackTrace();
            Log.v("getAnimeEpisode()", "Error accessing link");
            return null;
        }
    }
}
