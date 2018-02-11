package com.marcqtan.kissanimem;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.bumptech.glide.Glide;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements AnimeListAdapter.OnItemClicked {

    RecyclerView animelist;
    String animeListUrl = "https://otakustream.tv/anime/";
    String animeTrendingUrl = "https://otakustream.tv/trending-animes";

    AnimeListAdapter animeAdapter;
    ProgressBar pb;
    List<AnimeList> anime_list = null;
    String animeName = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        initCollapsingToolbar();

        animelist = (RecyclerView) findViewById(R.id.animelist);
        pb = (ProgressBar)findViewById(R.id.progressBar);
        animeAdapter = new AnimeListAdapter(this, this);

        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(this, 2);
        animelist.setLayoutManager(layoutManager);
        animelist.addItemDecoration(new GridSpacingItemDecoration(2, dpToPx(10), true));
        animelist.setItemAnimator(new DefaultItemAnimator());
        animelist.setHasFixedSize(true);
        animelist.setAdapter(animeAdapter);

        try {
            Glide.with(this).load(R.drawable.cover).into((ImageView) findViewById(R.id.backdrop));
        } catch (Exception e) {
            e.printStackTrace();
        }

        new getAnimeList().execute(animeTrendingUrl);
    }

    private void initCollapsingToolbar() {
        final CollapsingToolbarLayout collapsingToolbar =
                (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        collapsingToolbar.setTitle(" ");
        AppBarLayout appBarLayout = (AppBarLayout) findViewById(R.id.appbar);
        appBarLayout.setExpanded(true);

        // hiding & showing the title when toolbar expanded & collapsed
        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            boolean isShow = false;
            int scrollRange = -1;

            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (scrollRange == -1) {
                    scrollRange = appBarLayout.getTotalScrollRange();
                }
                if (scrollRange + verticalOffset == 0) {
                    collapsingToolbar.setTitle(getString(R.string.app_name));
                    isShow = true;
                } else if (isShow) {
                    collapsingToolbar.setTitle(" ");
                    isShow = false;
                }
            }
        });
    }

    @Override
    public void onItemClick(int position) {
        AnimeList animeSelected = anime_list.get(position);
        animeName = animeSelected.getAnimeName();
        new getAnimeEpisode().execute(animeSelected);
        //Toast.makeText(this, anime_list.get(position).getAnimeLink(), Toast.LENGTH_LONG).show();
    }

    private class getAnimeList extends AsyncTask<String, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pb.setVisibility(View.VISIBLE);
        }

        @Override
        protected Void doInBackground(String... params) {
            try {
                Document doc = Jsoup.connect(params[0]).timeout(30000).userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/61.0.3163.100 Safari/537.36 OPR/48.0.2685.50")
                        .followRedirects(true)
                        .get();
                anime_list = new ArrayList<>();
                parseTrendingAnimeName(doc);
                Elements page = doc.select("div.wp-pagenavi").select("a");
                for(int i = 0; i < page.size() - 1;i++) {
                    String pageUrl = page.get(i).attr("href");
                    doc = Jsoup.connect(pageUrl).timeout(30000).userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/61.0.3163.100 Safari/537.36 OPR/48.0.2685.50")
                            .followRedirects(true)
                            .get();
                    parseTrendingAnimeName(doc);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void v) {
            super.onPostExecute(v);
            if(anime_list != null) {
                animeAdapter.setAnimeData(anime_list);
            } else {
                Log.v("ERROR HERE", "ERROR!!!");
            }
            pb.setVisibility(View.GONE);
        }
    }

    private class getAnimeEpisode extends AsyncTask<AnimeList, Void, ArrayList<Map.Entry<String,String>>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pb.setVisibility(View.VISIBLE);
        }

        @Override
        protected ArrayList<Map.Entry<String,String>> doInBackground(AnimeList... params) {
            AnimeList animeSelected = params[0];
            try {
                Document doc = Jsoup.connect(animeSelected.getAnimeLink()).timeout(30000).userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/61.0.3163.100 Safari/537.36 OPR/48.0.2685.50")
                        .followRedirects(true)
                        .get();
                parseAnimeEpisode(animeSelected, doc);
                return animeSelected.retrieveEpisodes();
            } catch (IOException e) {
                e.printStackTrace();
                Log.v("getAnimeEpisode()", "Error accessing link");
                return null;
            }
        }

        @Override
        protected void onPostExecute(ArrayList<Map.Entry<String,String>> episodes) {
            super.onPostExecute(episodes);
            pb.setVisibility(View.GONE);

            if(episodes == null) {
                Log.v("getAnimeEpisode()", "ERROR on postExecute!!!");
                return;
            }

            Intent i = new Intent(MainActivity.this, EpisodeList.class);
            i.putExtra("episode_lists", episodes);
            i.putExtra("animename", animeName);
            startActivity(i);
        }
    }

    public void parseAllAnimeName(Document doc) {
        Elements animeInfo = doc.select("div.ep-box");
        anime_list = new ArrayList<>();

        for(Element info : animeInfo ){
            AnimeList anime = new AnimeList();
            Element hrefInfo = info.select("a").first();
            anime.setAnimeName(hrefInfo.text());
            anime.setAnimeLink(hrefInfo.attr("href"));
            anime.setThumbNail(info.select("img").attr("src"));
            anime_list.add(anime);
        }
    }

    public void parseTrendingAnimeName(Document doc) {
        Elements animeInfo = doc.select("article.article-block");
        for(Element info : animeInfo ){
            AnimeList anime = new AnimeList();
            Element hrefInfo = info.select("h3").first();
            anime.setAnimeName(hrefInfo.text());
            anime.setAnimeLink(hrefInfo.select("a").attr("href"));
            anime.setThumbNail(info.select("img").attr("src"));
            anime_list.add(anime);
        }
    }

    public void parseAnimeEpisode(AnimeList anime, Document doc) {
        String episode_list = doc.select("div.ep-list").select("a").get(0).text();
        int latestEpisode = Integer.parseInt(episode_list.substring(8));

        anime.initEpisodeList();

        for (int i = 1; i <= latestEpisode; i++) {
            String episode_name = "Episode " + i;
            String episode_link = anime.getAnimeLink() + "/episode-" + i;
            Map.Entry<String, String> episode_name_link = new AbstractMap.SimpleEntry<>(episode_name,episode_link);

            anime.addEpisodeInfo(episode_name_link);
        }
    }

    public class GridSpacingItemDecoration extends RecyclerView.ItemDecoration {

        private int spanCount;
        private int spacing;
        private boolean includeEdge;

        public GridSpacingItemDecoration(int spanCount, int spacing, boolean includeEdge) {
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
    private int dpToPx(int dp) {
        Resources r = getResources();
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics()));
    }
}
