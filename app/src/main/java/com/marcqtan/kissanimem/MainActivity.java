package com.marcqtan.kissanimem;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static android.R.id.list;
import static android.R.id.message;

public class MainActivity extends AppCompatActivity implements AnimeListAdapter.OnItemClicked {

    RecyclerView animelist;
    String animeListUrl = "https://ww3.gogoanime.io/anime-list.html";
    String rootUrl = "https://ww3.gogoanime.io";
    AnimeListAdapter animeAdapter;
    ProgressBar pb;
    List<AnimeList> anime_list = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        animelist = (RecyclerView) findViewById(R.id.animelist);
        pb = (ProgressBar)findViewById(R.id.progressBar);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        animelist.setLayoutManager(layoutManager);

        animelist.setHasFixedSize(true);
        animeAdapter = new AnimeListAdapter(this);
        animelist.setAdapter(animeAdapter);

        new getAnimeList().execute(animeListUrl);
    }

    public void showVid(View v){
        Intent i = new Intent(this, StreamAnime.class);
        startActivity(i);
    }

    @Override
    public void onItemClick(int position) {
        AnimeList animeSelected = anime_list.get(position);
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
                parseAnimeName(doc);
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

    private class getAnimeEpisode extends AsyncTask<AnimeList, Void, ArrayList<String>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected ArrayList<String> doInBackground(AnimeList... params) {
            AnimeList animeSelected = params[0];
            try {
                Document doc = Jsoup.connect(animeSelected.getAnimeLink()).timeout(30000).userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/61.0.3163.100 Safari/537.36 OPR/48.0.2685.50")
                        .followRedirects(true)
                        .get();
                parseAnimeEpisode(animeSelected, doc);
            } catch (IOException e) {
                e.printStackTrace();
                Log.v("getAnimeEpisode()", "Error accessing link");
            }
            //return params[0].retrieveEpisodes();
            return null;
        }

        @Override
        protected void onPostExecute(ArrayList<String> strings) {
            super.onPostExecute(strings);
            //Log.v("episode", Arrays.toString(strings.toArray()));
        }
    }

    public void parseAnimeName(Document doc) {
        Elements allanime = doc.select("div.anime_list_body").select("ul.listing");
        Elements animeInfo = allanime.select("li");
        anime_list = new ArrayList<>();

        for(Element info : animeInfo ){
            AnimeList anime = new AnimeList();
            anime.setAnimeName(info.text());
            anime.setAnimeLink(rootUrl + info.select("a[href]").attr("href"));
            anime_list.add(anime);
        }
    }

    public void parseAnimeEpisode(AnimeList anime, Document doc) {
        Elements episode_info = doc.select("div.anime_info_episodes_next");
        Elements movie_id = episode_info.select("input.movie_id");
        Elements episode_page = doc.select("ul#episode_page").select("li");
        String ep_start = episode_page.select("a[href]").attr("ep_start");
        String ep_end = episode_page.select("a[href]").attr("ep_end");
        String movieId = movie_id.attr("value");

        String list_episode_url = rootUrl + "/load-list-episode?ep_start=" + ep_start + "&ep_end=" + ep_end + "&id=" + movieId;

        try {
            Document doc2 = Jsoup.connect(list_episode_url).timeout(30000).userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/61.0.3163.100 Safari/537.36 OPR/48.0.2685.50")
                    .followRedirects(true)
                    .get();

            Elements episode_related = doc2.select("ul#episode_related");
            anime.initEpisodeList();
            for (Element s : episode_related.select("li")) {
                String episode_name = s.select("div.name").text();
                String episode_link = rootUrl + s.select("a[href]").attr("href").substring(1);//substring to remove space from beginning.
                Map.Entry<String, String> episode_name_link = new AbstractMap.SimpleEntry<>(episode_name,episode_link);

                anime.addEpisodeInfo(episode_name_link);
            }

            //Log.v("test", anime.retrieveEpisodes().get(0).getValue().toString());

        } catch (IOException e) {
            e.printStackTrace();
            Log.v("parseAnimeEpisode()", "Error accessing link");
        }
    }
}
