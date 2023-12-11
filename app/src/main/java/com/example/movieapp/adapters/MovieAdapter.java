package com.example.movieapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.movieapp.R;
import com.example.movieapp.models.NowPlayingMovie;

import java.util.List;

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.ViewHolder> {


    private List<NowPlayingMovie> mMovieNow;

    private OnMovieListener onMovieListener;

    private Context context;

    public MovieAdapter(Context context, OnMovieListener listener) {
        this.context = context;
        this.onMovieListener = listener;
    }

    public void setData(List<NowPlayingMovie> mMovieNow) {

        this.mMovieNow = mMovieNow;
        notifyDataSetChanged();
    }


    @NonNull
    @Override
    public MovieAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.movie_list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MovieAdapter.ViewHolder holder, int position) {

        Glide.with(holder.itemView.getContext())
                .load("https://image.tmdb.org/t/p/w500/" + mMovieNow.get(position)
                        .getPosterPath())
                .into((holder).imageView);
        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onMovieListener.onMovieClick(holder.getAdapterPosition());
            }
        });
    }


    @Override
    public int getItemCount() {
        return (mMovieNow != null && mMovieNow.size() > 0) ? mMovieNow.size() : 0;
    }



    public class ViewHolder extends RecyclerView.ViewHolder {

        RecyclerView recyclerView;
        ImageView imageView;

        public ViewHolder(@NonNull View itemView) {

            super(itemView);

            recyclerView = itemView.findViewById(R.id.recycler_nowPlaying);
            imageView = itemView.findViewById(R.id.movie_img);


        }
    }





}
