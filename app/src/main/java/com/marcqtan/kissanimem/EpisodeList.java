package com.marcqtan.kissanimem;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Map;

public class EpisodeList extends AppCompatActivity implements EpisodeListAdapter.onItemClicked {

    RecyclerView episode_list;
    EpisodeListAdapter epadapter;
    ArrayList<Map.Entry<String, String>> lists_episode;
    ProgressBar pb;
    String episodeName = null;
    String animeName = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_episode_list);

        episode_list = (RecyclerView) findViewById(R.id.episodeRV);
        pb = (ProgressBar) findViewById(R.id.progressBar);

        LinearLayoutManager lm = new LinearLayoutManager(this);

        episode_list.setLayoutManager(lm);
        episode_list.setHasFixedSize(true);

        lists_episode = (ArrayList<Map.Entry<String, String>>) getIntent().getSerializableExtra("episode_lists");
        animeName = getIntent().getStringExtra("animename");

        epadapter = new EpisodeListAdapter(lists_episode, this);

        episode_list.setAdapter(epadapter);

    }

    @Override
    public void onClick(int position) {
        String episodeUrl = lists_episode.get(position).getValue();
        episodeName = lists_episode.get(position).getKey();
        new getAnimeVideo().execute(episodeUrl);
        //Toast.makeText(this,"LINK IS " + lists_episode.get(position).getValue(), Toast.LENGTH_SHORT).show();
    }

    public class getAnimeVideo extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pb.setVisibility(View.VISIBLE);
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
        protected void onPostExecute(String url) {
            super.onPostExecute(url);
            pb.setVisibility(View.GONE);
            if(url == null) {
                Log.v("Error", "Error fetching video url");
            } else {
                Intent i = new Intent(EpisodeList.this, StreamAnime.class);
                i.putExtra("vidurl", url);
                i.putExtra("episodeName", episodeName);
                i.putExtra("animeName", animeName);
                startActivity(i);
            }
        }
    }

    private String getVideoLink(Document doc) {
        Elements el = doc.select("div.play-video").select("iframe[src]");
        String vidUrl = "https:" + el.attr("src");
        String vidDirectUrl = null;
        try {
            Document doc2 = Jsoup.connect(vidUrl).timeout(30000).userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/61.0.3163.100 Safari/537.36 OPR/48.0.2685.50")
                    .followRedirects(true)
                    .get();

            Elements vidPlayer = doc2.select("video#my-video-player");
            for(Element vidSource : vidPlayer.select("source")){
                if(vidSource.select("source[label]").attr("label").toString().equals("360")) {
                    vidDirectUrl = vidSource.select("source[src]").attr("src");
                } else if (vidSource.select("source[label]").attr("label").toString().equals("480")) {
                    vidDirectUrl = vidSource.select("source[src]").attr("src");
                }
            }

            if(vidDirectUrl == null) { //if there's no 360 or 480
                vidDirectUrl = vidPlayer.select("source").select("source[src]").attr("src");
            }
            return vidDirectUrl;

        } catch (IOException e) {
            e.printStackTrace();
            Log.v("getAnimeEpisode()", "Error accessing link");
            return null;
        }
    }

}
