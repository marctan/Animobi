package com.marcqtan.kissanimem;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;

/**
 * Created by Marc Q. Tan on 25/10/2017.
 */

public class AnimeListAdapter extends RecyclerView.Adapter<AnimeListAdapter.AnimeViewHolder> {

    List<AnimeList> animeLists;
    private OnItemClicked mListener;
    private Context mContext;


    public interface OnItemClicked {
        void onItemClick(int position);
    }

    public AnimeListAdapter(OnItemClicked listener, Context mContext) {
        this.mContext = mContext;
        mListener = listener;
    }

    @Override
    public AnimeViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater li = LayoutInflater.from(parent.getContext());
        View view = li.inflate(R.layout.cardviewlayout, parent, false);
        AnimeViewHolder vh = new AnimeViewHolder(view);

        return vh;
    }

    @Override
    public void onBindViewHolder(AnimeViewHolder holder, int position) {
        AnimeList anime = animeLists.get(position);
        holder.tvAnimeName.setText(anime.getAnimeName());
        Glide.with(mContext).load(anime.getAnimeThumbnail()).into(holder.thumbNail);

        /*holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onCardSelected(position, holder.thumbnail);
            }
        });

        holder.thumbNail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onCardSelected(position, holder.thumbnail);
            }
        });*/
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
        ImageView thumbNail;
        public CardView cardView;
        public AnimeViewHolder(View v) {
            super(v);
            tvAnimeName = (TextView) v.findViewById(R.id.anime_name);
            thumbNail = (ImageView) v.findViewById(R.id.thumbnail);
            cardView = (CardView) v.findViewById(R.id.card_view);
            v.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            mListener.onItemClick(getAdapterPosition());
        }
    }


}
