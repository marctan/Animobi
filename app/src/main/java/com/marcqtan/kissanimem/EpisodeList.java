package com.marcqtan.kissanimem;

import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.widget.FrameLayout;
import android.widget.TextView;

public class EpisodeList extends AppCompatActivity implements EpisodeListAdapter.onItemClicked {

    RecyclerView episode_list;
    EpisodeListAdapter epadapter;
    FrameLayout frame;
    TextView summary;
    Anime anime;

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
        new Utility.getAnimeVideo(this, anime, frame).execute(anime.retrieveEpisodes().get(position).getValue());
        //Toast.makeText(this,"LINK IS " + lists_episode.get(position).getValue(), Toast.LENGTH_SHORT).show();
    }
}
