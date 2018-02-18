package com.marcqtan.kissanimem;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.List;

public class SearchList extends AppCompatActivity implements SearchListAdapter.onItemClicked {
    List <Anime> animeLists = null;
    RecyclerView rv;
    SearchListAdapter adapter;
    TextView empty;
    FrameLayout frame;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_list);
        rv = findViewById(R.id.searchRv);
        empty = findViewById(R.id.empty_view);
        frame = findViewById(R.id.progressBarContainer);
        adapter = new SearchListAdapter(this,this);

        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(this, 2);
        rv.setLayoutManager(layoutManager);
        rv.addItemDecoration(new Utility.GridSpacingItemDecoration(2, Utility.dpToPx(10, getResources()), true));
        rv.setItemAnimator(new DefaultItemAnimator());
        rv.setHasFixedSize(true);
        rv.setAdapter(adapter);

        new searchAnime().execute(getIntent().getStringExtra("searchUrl"));
    }

    @Override
    public void itemClick(int position) {
        Anime animeSelected = animeLists.get(position);
        if(animeSelected.getEpisodeCount().equals("Movie")) {
            Intent intent = new Intent(this, MovieActivity.class);
            intent.putExtra("anime", animeSelected);
            startActivity(intent);
            //new Utility.getAnimeVideo(this, animeSelected, frame).execute(animeSelected.getAnimeLink());
         } else {
            new Utility.getAnimeEpisode(this, frame).execute(animeSelected);
        }
    }

    private class searchAnime extends AsyncTask<String, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            frame.setVisibility(View.VISIBLE);
        }

        @Override
        protected Void doInBackground(String... strings) {
            try{
                Document doc = Jsoup.connect(strings[0]).timeout(30000).userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/61.0.3163.100 Safari/537.36 OPR/48.0.2685.50")
                        .followRedirects(true)
                        .get();

                animeLists = Utility.parseAllAnimeName(doc);

            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if(animeLists == null || animeLists.size() <= 0) {
                rv.setVisibility(View.GONE);
                empty.setVisibility(View.VISIBLE);
            }  else if (animeLists.size() > 0){
                rv.setVisibility(View.VISIBLE);
                empty.setVisibility(View.GONE);
                adapter.setData(animeLists);
            }
            frame.setVisibility(View.GONE);
        }
    }


}
