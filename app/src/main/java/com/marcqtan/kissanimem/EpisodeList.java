package com.marcqtan.kissanimem;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Map;

public class EpisodeList extends AppCompatActivity implements EpisodeListAdapter.onItemClicked {

    RecyclerView episode_list;
    EpisodeListAdapter epadapter;
    ArrayList<Map.Entry<String, String>> lists_episode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_episode_list);

        episode_list = (RecyclerView) findViewById(R.id.episodeRV);
        LinearLayoutManager lm = new LinearLayoutManager(this);

        episode_list.setLayoutManager(lm);
        episode_list.setHasFixedSize(true);

        lists_episode = (ArrayList<Map.Entry<String, String>>) getIntent().getSerializableExtra("episode_lists");

        epadapter = new EpisodeListAdapter(lists_episode, this);

        episode_list.setAdapter(epadapter);

    }

    @Override
    public void onClick(int position) {
        Toast.makeText(this,"LINK IS " + lists_episode.get(position).getValue(), Toast.LENGTH_SHORT).show();
    }
    
}
