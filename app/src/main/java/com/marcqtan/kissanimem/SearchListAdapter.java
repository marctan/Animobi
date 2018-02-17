package com.marcqtan.kissanimem;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;

/**
 * Created by dell on 12/02/2018.
 */

public class SearchListAdapter extends RecyclerView.Adapter<SearchListAdapter.ViewHolder> {

    private List<Anime> anime_list = null;
    private Context m_context;
    private onItemClicked m_listener;

    public interface onItemClicked {
        void itemClick(int position);
    }

    @Override
    public SearchListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.search_cardview,parent,false));
    }

    @Override
    public void onBindViewHolder(SearchListAdapter.ViewHolder holder, int position) {
        Anime anime = anime_list.get(position);
        holder.title.setText(anime.getAnimeName());
        holder.epcount.setText(anime.getEpisodeCount());
        Glide.with(m_context).load(anime.getAnimeThumbnail()).into(holder.thumbnail);
    }

    @Override
    public int getItemCount() {
        if(anime_list == null) {
            return 0;
        }
        return anime_list.size();
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
            m_listener.itemClick(getAdapterPosition());
        }
    }
}
