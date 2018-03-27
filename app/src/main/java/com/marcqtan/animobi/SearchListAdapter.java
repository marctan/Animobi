package com.marcqtan.animobi;

import android.content.Context;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by Marc Q. Tan on 12/02/2018.
 */

public class SearchListAdapter extends RecyclerView.Adapter<SearchListAdapter.ViewHolder> {

    private List<Anime> anime_list = null;
    private Context m_context;
    private onItemClicked m_listener;
    public interface onItemClicked {
        void itemClick(int position, ImageView image);
    }

    @Override
    public SearchListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.cardviewlayout,parent,false));
    }

    @Override
    public void onBindViewHolder(final SearchListAdapter.ViewHolder holder, final int position) {
        Anime anime = anime_list.get(position);
        holder.title.setText(anime.getAnimeName());
        holder.epcount.setText(anime.getEpisodeCount());
        Picasso.with(m_context)
                .load(anime.getAnimeThumbnail())
                .into(holder.thumbnail, new Callback() {
                    @Override
                    public void onSuccess() {
                        holder.title.setVisibility(View.VISIBLE);
                        holder.epcount.setVisibility(View.VISIBLE);
                        holder.thumbnail.setVisibility(View.VISIBLE);
                        setAnimation(holder.itemView, position);
                    }

                    @Override
                    public void onError() {

                    }
                });
        ViewCompat.setTransitionName(holder.thumbnail, anime.getAnimeName());
    }

    @Override
    public int getItemCount() {
        if(anime_list == null) {
            return 0;
        }
        return anime_list.size();
    }

    private void setAnimation(View view, int position) {
        if (position >= anime_list.size()) {
            return;
        }

        Animation animation = AnimationUtils.loadAnimation(m_context, R.anim.item_animation_fall_down);
        view.animate().setStartDelay(300);
        view.startAnimation(animation);
    }

    SearchListAdapter(Context m_context, onItemClicked m_listener) {
        this.m_context = m_context;
        this.m_listener = m_listener;
    }

    public void setData(List<Anime> list){
        anime_list = list;
        notifyDataSetChanged();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        TextView title;
        ImageView thumbnail;
        TextView epcount;
        ViewHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.anime_name);
            thumbnail = itemView.findViewById(R.id.thumbnail);
            epcount = itemView.findViewById(R.id.ep_count);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            m_listener.itemClick(getAdapterPosition(), thumbnail);
        }
    }
}
