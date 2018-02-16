package com.marcqtan.kissanimem;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.List;

public class SearchList extends AppCompatActivity implements SearchListAdapter.onItemClicked {
    ProgressBar pb;
    List <AnimeList> animeLists = null;
    RecyclerView rv;
    SearchListAdapter adapter;
    TextView empty;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_list);
        pb = findViewById(R.id.progressBar);
        rv = findViewById(R.id.searchRv);
        empty = findViewById(R.id.empty_view);
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
        AnimeList animeSelected = animeLists.get(position);
        if(animeSelected.getEpisodeCount().equals("Movie")) {
            new Utility.getAnimeVideo(pb, this, animeSelected.getAnimeName(), "").execute(animeSelected.getAnimeLink());
         } else {
            new Utility.getAnimeEpisode(pb, this, animeSelected.getAnimeName()).execute(animeSelected);
        }
    }

    private class searchAnime extends AsyncTask<String, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pb.setVisibility(View.VISIBLE);
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
            pb.setVisibility(View.GONE);
        }
    }


}
