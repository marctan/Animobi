package com.marcqtan.kissanimem;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.List;

public class SearchFragment extends Fragment implements SearchListAdapter.onItemClicked {
    List <Anime> animeLists = null;
    RecyclerView rv;
    SearchListAdapter adapter;
    TextView empty;
    FrameLayout frame;

    public SearchFragment() {

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.activity_search_list, container, false);
        rv = rootView.findViewById(R.id.searchRv);
        empty = rootView.findViewById(R.id.empty_view);
        frame = rootView.findViewById(R.id.progressBarContainer);
        adapter = new SearchListAdapter(getActivity(), this);

        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getActivity(), 2);
        rv.setLayoutManager(layoutManager);
        rv.addItemDecoration(new Utility.GridSpacingItemDecoration(2, Utility.dpToPx(10, getResources()), true));
        rv.setItemAnimator(new DefaultItemAnimator());
        rv.setHasFixedSize(true);
        rv.setAdapter(adapter);
        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        new searchAnime().execute(getArguments().getString("searchUrl"));
    }

    @Override
    public void itemClick(int position) {
        Anime animeSelected = animeLists.get(position);
        if(animeSelected.getEpisodeCount().equals("Movie")) {

            MovieFragment movieFrag = new MovieFragment();
            Bundle bundle = new Bundle();
            bundle.putSerializable("anime", animeSelected);

            movieFrag.setArguments(bundle);
            getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.frame_fragmentholder, movieFrag).commit();
         } else {
            new Utility.getAnimeEpisode(getActivity(), frame).execute(animeSelected);
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
