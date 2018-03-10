package com.marcqtan.kissanimem;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.transition.Fade;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.List;

public class SearchResultsFragment extends Fragment implements SearchListAdapter.onItemClicked, Utility.interface1 {

    List <Anime> animeLists = null;
    RecyclerView rv;
    SearchListAdapter adapter;
    TextView empty;
    FrameLayout frame;
    AsyncTask task = null;

    public SearchResultsFragment() {

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
        if(animeLists != null && animeLists.size() > 0) {
            adapter.setData(animeLists);
        } else {
            task = new searchAnime(this).execute(getArguments().getString("searchUrl"));
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if(task != null) {
            task.cancel(true);
        }
    }
    @Override
    public void itemClick(int position, ImageView image) {
        Anime animeSelected = animeLists.get(position);
        if(animeSelected.getEpisodeCount().equals("Movie")) {

            MovieFragment movieFrag = new MovieFragment();
            Bundle bundle = new Bundle();
            bundle.putSerializable("anime", animeSelected);
            bundle.putString("transitionName", ViewCompat.getTransitionName(image));

            movieFrag.setArguments(bundle);

            movieFrag.setSharedElementEnterTransition(new DetailsTransition());
            movieFrag.setEnterTransition(new Fade());
            setSharedElementReturnTransition(new DetailsTransition());
            setExitTransition(new Fade());

            getActivity().getSupportFragmentManager().beginTransaction()
                    .addSharedElement(image,ViewCompat.getTransitionName(image))
                    .replace(R.id.frame_fragmentholder, movieFrag).addToBackStack(null).commit();
         } else {
            EpisodeListFragment episodeList = new EpisodeListFragment();
            Bundle bundle = new Bundle();
            bundle.putSerializable("anime", animeSelected);
            bundle.putString("transitionName", ViewCompat.getTransitionName(image));
            episodeList.setArguments(bundle);

            episodeList.setSharedElementEnterTransition(new DetailsTransition());
            episodeList.setEnterTransition(new Fade());
            setSharedElementReturnTransition(new DetailsTransition());
            setExitTransition(new Fade());

            getActivity().getSupportFragmentManager()
                    .beginTransaction().
                    addSharedElement(image, ViewCompat.getTransitionName(image)).
                    replace(R.id.frame_fragmentholder, episodeList).addToBackStack(null).commit();
        }
    }

    @Override
    public void showVisibilty() {
        frame.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideVisibility() {
        frame.setVisibility(View.GONE);
    }

    @Override
    public FragmentActivity getFragActivity() {
        return getActivity();
    }

    private static class searchAnime extends AsyncTask<String, Void, Void> {
        private WeakReference<SearchResultsFragment> activity;

        searchAnime(SearchResultsFragment activity) {
            this.activity = new WeakReference<SearchResultsFragment>(activity);
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            activity.get().frame.setVisibility(View.VISIBLE);
        }

        @Override
        protected Void doInBackground(String... strings) {
            try{
                Document doc = Jsoup.connect(strings[0]).timeout(30000).userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/61.0.3163.100 Safari/537.36 OPR/48.0.2685.50")
                        .followRedirects(true)
                        .get();

                activity.get().animeLists = Utility.parseAllAnimeName(doc);

            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if(activity.get().animeLists == null || activity.get().animeLists.size() <= 0) {
                activity.get().rv.setVisibility(View.GONE);
                activity.get().empty.setVisibility(View.VISIBLE);
            }  else if (activity.get().animeLists.size() > 0){
                activity.get().rv.setVisibility(View.VISIBLE);
                activity.get().empty.setVisibility(View.GONE);
                activity.get().adapter.setData(activity.get().animeLists);
            }
            activity.get().frame.setVisibility(View.GONE);
        }
    }


}
