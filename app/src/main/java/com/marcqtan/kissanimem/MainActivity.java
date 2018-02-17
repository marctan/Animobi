package com.marcqtan.kissanimem;

import android.content.Intent;
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
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements AnimeListAdapter.OnItemClicked {

    RecyclerView animelist;
    String animeListUrl = "https://otakustream.tv/anime/";
    String animeTrendingUrl = "https://otakustream.tv/trending-animes";

    AnimeListAdapter animeAdapter;
    List<Anime> anime_list = null;
    EditText query;
    FrameLayout frame;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Utility.initCollapsingToolbar((CollapsingToolbarLayout)findViewById(R.id.collapsing_toolbar),(AppBarLayout)findViewById(R.id.appbar), getString(R.string.app_name));

        animelist = findViewById(R.id.animelist);
        animeAdapter = new AnimeListAdapter(this, this);
        query = findViewById(R.id.searchET);
        frame = findViewById(R.id.progressBarContainer);

        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(this, 2);
        animelist.setLayoutManager(layoutManager);
        animelist.addItemDecoration(new Utility.GridSpacingItemDecoration(2, Utility.dpToPx(10, getResources()), true));
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

    @Override
    public void onItemClick(int position) {
        Anime animeSelected = anime_list.get(position);
        new Utility.getAnimeEpisode(this, frame).execute(animeSelected);
    }

    public void search(View view) {
        String searchUrl = "https://otakustream.tv/?s=" + query.getText();
        Intent i = new Intent(this, SearchList.class);
        i.putExtra("searchUrl", searchUrl);
        startActivity(i);
    }

    private class getAnimeList extends AsyncTask<String, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            frame.setVisibility(View.VISIBLE);
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
            frame.setVisibility(View.GONE);
        }
    }

    public void parseTrendingAnimeName(Document doc) {
        Elements animeInfo = doc.select("article.article-block");
        for(Element info : animeInfo ){
            Anime anime = new Anime();
            Element hrefInfo = info.select("h3").first();
            anime.setAnimeName(hrefInfo.text());
            anime.setAnimeLink(hrefInfo.select("a").attr("href"));
            anime.setThumbNail(info.select("img").attr("src"));
            anime.setEpisodeCount(info.select("span.ep-no").text()); //TODO
            anime_list.add(anime);
        }
    }
}
