package com.marcqtan.kissanimem;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class EpisodeList extends AppCompatActivity implements EpisodeListAdapter.onItemClicked {

    RecyclerView episode_list;
    EpisodeListAdapter epadapter;
    ArrayList<Map.Entry<String, String>> lists_episode;

    List<String> ep_names = null;
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

    public class getAnimeVideo extends AsyncTask<String, Void, List<String>> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pb.setVisibility(View.VISIBLE);
        }

        @Override
        protected List<String> doInBackground(String... params) {
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
        protected void onPostExecute(List<String> quality) {
            super.onPostExecute(quality);
            pb.setVisibility(View.GONE);
            if(quality == null) {
                AlertDialog.Builder adb = new AlertDialog.Builder(EpisodeList.this);
                adb.setTitle("No available video stream");
                adb.setMessage("Sorry");
                adb.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        finish(); // take out user from this activity. you can skip this
                    }
                });
                AlertDialog ad = adb.create();
                ad.show();

                Log.v("Error", "Error fetching video quality url");
            } else if (quality.size() == 1) {

                Intent i = new Intent(EpisodeList.this, exoactivity.class);
                i.putExtra("vidurl", quality.get(0));
                i.putExtra("episodeName", episodeName);
                i.putExtra("animeName", animeName);
                startActivity(i);

            } else {
                Intent i = new Intent(EpisodeList.this, QualityList.class);
                i.putStringArrayListExtra("vidurl", (ArrayList<String>)quality);
                i.putStringArrayListExtra("ep_name", (ArrayList<String>) ep_names);
                i.putExtra("episodeName", episodeName);
                i.putExtra("animeName", animeName);
                startActivity(i);
            }
        }
    }

    private List<String> getVideoLink(Document doc) {
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
            ep_names = new ArrayList<>();
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
