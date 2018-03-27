package com.marcqtan.animobi;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.BottomSheetDialog;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by Marc Q. Tan on 13/02/2018.
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
        interface1 i;
        ImageView image = null;
        String name = "";
        private WeakReference<EpisodeListFragment> activity;

        getAnimeEpisode(interface1 i, ImageView image, String name) {
            this.i = i;
            this.image = image;
            this.name = name;
        }

        getAnimeEpisode(EpisodeListFragment e){
            activity = new WeakReference<>(e);

        }

        getAnimeEpisode(interface1 i) {
            this.i = i;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            activity.get().frame.setVisibility(View.VISIBLE);
        }

        @Override
        protected Anime doInBackground(Anime... params) {
            Anime animeSelected = params[0];
            try {
                Document doc = Jsoup.connect(animeSelected.getAnimeLink()).timeout(30000).userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/61.0.3163.100 Safari/537.36 OPR/48.0.2685.50")
                        .followRedirects(true)
                        .get();
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
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
            //i.hideVisibility();
            activity.get().frame.setVisibility(View.GONE);
            if(anime.retrieveEpisodes() == null) {
                Log.v("getAnimeEpisode()", "ERROR on postExecute!!!");
                return;
            }

            activity.get().epadapter.setEpisodeListData(anime.retrieveEpisodes());

            /*EpisodeListFragment episodeList = new EpisodeListFragment();
            Bundle bundle = new Bundle();
            bundle.putSerializable("anime", anime);
            bundle.putString("transitionName", name);
            episodeList.setArguments(bundle);

            if(name == "") {
                i.getFragActivity().getSupportFragmentManager().beginTransaction().replace(R.id.frame_fragmentholder,episodeList).addToBackStack(null).commit();
            } else {
                //i.getFragActivity().getSupportFragmentManager().beginTransaction().addSharedElement(image, name).replace(R.id.frame_fragmentholder,episodeList).addToBackStack(null).commit();
            }*/
        }
    }

    private static void parseAnimeEpisode(Anime anime, Document doc) {
        String episode_list = doc.select("div.ep-list").select("a").get(0).text();
        int latestEpisode = Integer.parseInt(episode_list.substring(8));

        anime.initEpisodeList();

        for (int i = 1; i <= latestEpisode; i++) {
            String episode_name = "Episode " + i;
            String episode_link = anime.getAnimeLink() + "episode-" + i;
            Map.Entry<String, String> episode_name_link = new AbstractMap.SimpleEntry<>(episode_name,episode_link);

            anime.addEpisodeInfo(episode_name_link);
        }
        anime.setSummary(doc.select("div.some-more-info").select("p").text());
    }

    static List<String> getQuality(Document doc, List<String> quality_name) {
        try {
            String vidDirectUrl = doc.select("div#Rapidvideo").select("iframe[src]").attr("src");

            if(vidDirectUrl.isEmpty()) {
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

            for (Element x : a) {
                quality.add(x.select("a[href]").attr("href"));
                quality_name.add(x.text());
            }
            return quality;

        } catch (IOException e) {
            e.printStackTrace();
            Log.v("getAnimeEpisode()", "Error accessing link");
            return null;
        }
    }
    public static class getVideo extends AsyncTask<String, Void, String> {
        String animeName;
        interface2 i;

        getVideo(interface2 i, String animeName) {
            this.i = i;
            this.animeName = animeName;
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            i.showVisibilty();
        }

        @Override
        protected String doInBackground(String... params) {
            try {
                Document doc = Jsoup.connect(params[0]).timeout(30000).userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/61.0.3163.100 Safari/537.36 OPR/48.0.2685.50")
                        .followRedirects(true)
                        .get();
                return getVideoLink(doc);
            } catch (IOException e) {
                e.printStackTrace();
                Log.v("getAnimeEpisode()", "Error accessing link");
                return null;
            }
        }

        @Override
        protected void onPostExecute(String video) {
            super.onPostExecute(video);
            i.hideVisibility();
            if(video == null) {
                Log.v("Error", "Error fetching video quality url");
            } else {
                if(i.getCtx() == null) {
                    return;
                }
                Intent intent = new Intent(i.getCtx(), exoactivity.class);
                intent.putExtra("vidurl", video);
                intent.putExtra("animeName", animeName);
                i.getCtx().startActivity(intent);
            }
        }
    }

    private static String getVideoLink(Document doc) {
        return doc.select("video#videojs").select("source[src]").attr("src");
    }

    static void showBottomSheet(final interface2 i, final List<String> quality_list, List<String> quality_name, final String animeName){
        final BottomSheetDialog dialog = new BottomSheetDialog(i.getCtx());
        View parentView = i.getActvty().getLayoutInflater().inflate(R.layout.bottom_sheet_layout,null);
        dialog.setContentView(parentView);
        ListView list = dialog.findViewById(R.id.list);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(i.getCtx(),
                R.layout.custom_list_layout, android.R.id.text1, quality_name);
        list.setAdapter(adapter);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

                dialog.hide();
                String itemValue = quality_list.get(position);
                new Utility.getVideo(i, animeName).execute(itemValue);

            }
        });
        dialog.show();
    }

    public interface interface1{
        void showVisibilty();
        void hideVisibility();
        FragmentActivity getFragActivity();
    }

    public interface interface2{
        void showVisibilty();
        void hideVisibility();
        Context getCtx();
        Activity getActvty();
    }
}
