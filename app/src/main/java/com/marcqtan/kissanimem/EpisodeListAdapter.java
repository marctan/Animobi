package com.marcqtan.kissanimem;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Map;


/**
 * Created by dell on 26/10/2017.
 */

public class EpisodeListAdapter extends RecyclerView.Adapter<EpisodeListAdapter.EpisodeViewHolder> {
    private ArrayList<Map.Entry<String, String>> m_episodes;
    private onItemClicked m_listener;

    public interface onItemClicked {
        void onClick(int position);
    }

    @Override
    public EpisodeViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater li = LayoutInflater.from(parent.getContext());
        View v = li.inflate(R.layout.episode_list,parent, false);
        return new EpisodeViewHolder(v);
    }

    @Override
    public void onBindViewHolder(EpisodeViewHolder holder, int position) {
        holder.episode_list.setText(m_episodes.get(position).getKey());
    }

    @Override
    public int getItemCount() {
        if (m_episodes == null) return 0;

        return m_episodes.size();
    }

    EpisodeListAdapter(ArrayList<Map.Entry<String, String>> episodes, onItemClicked listener) {
        m_episodes = episodes;
        m_listener = listener;
    }

    public class EpisodeViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView episode_list;
        EpisodeViewHolder(View itemView) {
            super(itemView);
            episode_list = itemView.findViewById(R.id.tv_episode_list);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            m_listener.onClick(getAdapterPosition());
        }
    }

    void setEpisodeListData(ArrayList<Map.Entry<String, String>> episodes) {
        this.m_episodes = episodes;
        notifyDataSetChanged();
    }
}
