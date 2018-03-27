package com.marcqtan.animobi;

import android.content.Context;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.CardView;
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
 * Created by Marc Q. Tan on 25/10/2017.
 */
public class AnimeListAdapter extends RecyclerView.Adapter<AnimeListAdapter.AnimeViewHolder> {

    private List<Anime> animeLists;
    private OnItemClicked mListener;
    private Context mContext;

    public interface OnItemClicked {
        void onItemClick(int position, ImageView v);
    }

    AnimeListAdapter(OnItemClicked listener, Context mContext) {
        this.mContext = mContext;
        mListener = listener;
    }

    @Override
    public AnimeViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater li = LayoutInflater.from(parent.getContext());
        View view = li.inflate(R.layout.cardviewlayout, parent, false);

        return new AnimeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final AnimeViewHolder holder, final int position) {
        //setScaleAnimation(holder.itemView);
        final Anime anime = animeLists.get(position);
        holder.tvAnimeName.setText(anime.getAnimeName());
        holder.tvEpisodeCount.setText(anime.getEpisodeCount());
        //Glide.with(mContext).load(anime.getAnimeThumbnail()).into(holder.thumbNail);
        //Glide.with(mContext).load(R.drawable.testcover).into(holder.thumbNail);

        Picasso.with(mContext)
                .load(anime.getAnimeThumbnail())
                .into(holder.thumbNail, new Callback() {
                    @Override
                    public void onSuccess() {
                        holder.tvAnimeName.setVisibility(View.VISIBLE);
                        holder.tvEpisodeCount.setVisibility(View.VISIBLE);
                        holder.thumbNail.setVisibility(View.VISIBLE);
                        setScaleAnimation(holder.itemView,position);
                    }

                    @Override
                    public void onError() {

                    }
                });

        ViewCompat.setTransitionName(holder.thumbNail, anime.getAnimeName());

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

    private void setScaleAnimation(View view, int position) {
        if (position >= animeLists.size()) {
            return;
        }

        Animation animation = AnimationUtils.loadAnimation(mContext, R.anim.item_animation_fall_down);
        view.animate().setStartDelay(300);
        view.startAnimation(animation);
    }

    @Override
    public int getItemCount() {
        if(animeLists == null) {
            return 0;
        }
        return animeLists.size();
    }

    void setAnimeData(List<Anime> animeLists) {
        this.animeLists = animeLists;
        notifyDataSetChanged();
    }

    class AnimeViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView tvAnimeName;
        ImageView thumbNail;
        TextView tvEpisodeCount;

        CardView cardView;
        AnimeViewHolder(View v) {
            super(v);
            tvAnimeName = v.findViewById(R.id.anime_name);
            thumbNail = v.findViewById(R.id.thumbnail);
            cardView = v.findViewById(R.id.card_view);
            tvEpisodeCount = v.findViewById(R.id.ep_count);
            v.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            mListener.onItemClick(getAdapterPosition(), thumbNail);
        }
    }


}
