package com.marcqtan.kissanimem;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

/**
 * Created by dell on 25/10/2017.
 */

public class AnimeListAdapter extends RecyclerView.Adapter<AnimeListAdapter.AnimeViewHolder> {

    List<AnimeList> animeLists;
    private OnItemClicked mListener;

    public interface OnItemClicked {
        void onItemClick(int position);
    }

    public AnimeListAdapter(OnItemClicked listener) {
        mListener = listener;
    }

    @Override
    public AnimeViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater li = LayoutInflater.from(parent.getContext());
        View view = li.inflate(R.layout.anime_list, parent, false);
        AnimeViewHolder vh = new AnimeViewHolder(view);

        return vh;
    }

    @Override
    public void onBindViewHolder(AnimeViewHolder holder, int position) {
        AnimeList anime = animeLists.get(position);
        holder.tvAnimeName.setText(anime.getAnimeName());
    }

    @Override
    public int getItemCount() {
        if(animeLists == null) {
            return 0;
        }
        return animeLists.size();
    }

    public void setAnimeData(List<AnimeList> animeLists) {
        this.animeLists = animeLists;
        notifyDataSetChanged();
    }

    class AnimeViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView tvAnimeName;
        public AnimeViewHolder(View v) {
            super(v);
            tvAnimeName = (TextView) v.findViewById(R.id.tv_anime_name);
            v.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            mListener.onItemClick(getAdapterPosition());
        }
    }


}
