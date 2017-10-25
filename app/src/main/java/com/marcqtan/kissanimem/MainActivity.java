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
import java.util.ArrayList;
import java.util.List;

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
        Toast.makeText(this, anime_list.get(position).getAnimeLink(), Toast.LENGTH_LONG).show();
    }

    private class getAnimeList extends AsyncTask<String, Void, List<AnimeList>> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pb.setVisibility(View.VISIBLE);
        }

        @Override
        protected List<AnimeList> doInBackground(String... params) {
            try {
                Document doc = Jsoup.connect(params[0]).timeout(30000).userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/61.0.3163.100 Safari/537.36 OPR/48.0.2685.50")
                        .followRedirects(true)
                        .get();
                parseAnimeName(doc);
                return anime_list;

//                org.jsoup.Connection.Response usage = Jsoup.connect(params[0])
//                        .header("accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8")
//                        .header("accept-encoding", "gzip, deflate")
//                        .header("accept-language", "en-US,en;q=0.8")
//                        //.header("cache-control", "max-age=0")
//                        .header("user-agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/61.0.3163.100 Safari/537.36 OPR/48.0.2685.50")
//                        .header("upgrade-insecure-requests", "1")
//                        .ignoreHttpErrors(true)
//                        .followRedirects(true)
//                        .method(Connection.Method.GET)
//                        .timeout(30000)
//                        .execute();
//                Log.v("GSOAP", usage.parse().title());
//                return null;
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(List<AnimeList> animeLists) {
            super.onPostExecute(animeLists);
            if(animeLists != null) {
                animeAdapter.setAnimeData(animeLists);
            } else {
                Log.v("ERROR HERE", "ERROR!!!");
            }
            pb.setVisibility(View.GONE);
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

}
