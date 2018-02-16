package com.marcqtan.kissanimem;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.FrameLayout;
import android.widget.ProgressBar;

import java.util.ArrayList;
import java.util.Map;

public class EpisodeList extends AppCompatActivity implements EpisodeListAdapter.onItemClicked {

    RecyclerView episode_list;
    EpisodeListAdapter epadapter;
    FrameLayout frame;
    ArrayList<Map.Entry<String, String>> lists_episode;

    String episodeName = null;
    String animeName = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_episode_list);

        episode_list = (RecyclerView) findViewById(R.id.episodeRV);
        frame = findViewById(R.id.progressBarContainer);

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
        new Utility.getAnimeVideo(this, animeName, episodeName, frame).execute(episodeUrl);
        //Toast.makeText(this,"LINK IS " + lists_episode.get(position).getValue(), Toast.LENGTH_SHORT).show();
    }
}
