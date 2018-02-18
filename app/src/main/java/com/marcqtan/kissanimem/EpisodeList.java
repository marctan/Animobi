package com.marcqtan.kissanimem;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class EpisodeList extends AppCompatActivity implements EpisodeListAdapter.onItemClicked {

    RecyclerView episode_list;
    EpisodeListAdapter epadapter;
    FrameLayout frame;
    TextView summary;
    Anime anime;
    ListView list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_episode_list);

        episode_list = findViewById(R.id.episodeRV);
        frame = findViewById(R.id.progressBarContainer);
        summary = findViewById(R.id.summary);

        Bundle b = getIntent().getExtras();
        if(b != null) {
            anime = (Anime) b.getSerializable("anime");
        }

        if (anime == null) {
            return;
        }

        summary.setText(anime.getSummary());

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Utility.initCollapsingToolbar((CollapsingToolbarLayout)findViewById(R.id.collapsing_toolbar),(AppBarLayout)findViewById(R.id.appbar), getString(R.string.app_name));

        LinearLayoutManager lm = new LinearLayoutManager(this);

        episode_list.setLayoutManager(lm);
        episode_list.setHasFixedSize(true);

        epadapter = new EpisodeListAdapter(anime.retrieveEpisodes(), this);

        episode_list.setAdapter(epadapter);

    }

    @Override
    public void onClick(int position) {
        //String episodeUrl = lists_episode.get(position).getValue();
        //String episodeName = lists_episode.get(position).getKey();
        new getAnimeVideo().execute(anime.retrieveEpisodes().get(position).getValue());
        //Toast.makeText(this,"LINK IS " + lists_episode.get(position).getValue(), Toast.LENGTH_SHORT).show();
    }

    class getAnimeVideo extends AsyncTask<String, Void, List<String>> {
        List<String> quality_name = new ArrayList<String>();

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
                return Utility.getQuality(doc, quality_name);
            } catch (IOException e) {
                e.printStackTrace();
                Log.v("getAnimeEpisode()", "Error accessing link");
                return null;
            }
        }

        @Override
        protected void onPostExecute(final List<String> quality) {
            super.onPostExecute(quality);
            frame.setVisibility(View.GONE);

            if(quality == null) {
                AlertDialog.Builder adb = new AlertDialog.Builder(EpisodeList.this);
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

                Intent i = new Intent(EpisodeList.this, exoactivity.class);
                i.putExtra("vidurl", quality.get(0));
                i.putExtra("animeName", anime.getAnimeName());
                startActivity(i);

            } else {
                Utility.showBottomSheet(EpisodeList.this, EpisodeList.this, list, quality, quality_name, frame, anime.getAnimeName());
            }
        }
    }
}
