package com.example.movieapp.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.movieapp.R;
import com.example.movieapp.models.NowPlayingMovie;

import java.util.List;

// WatchlistAdapter.java
public class WatchListAdapter extends RecyclerView.Adapter<WatchListAdapter.ViewHolder> {

    private List<NowPlayingMovie> movies;

    public WatchListAdapter(List<NowPlayingMovie> movies) {
        this.movies = movies;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_watchlist, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        NowPlayingMovie movie = movies.get(position);

        // Hiển thị thông tin bộ phim trong ViewHolder
        Glide.with(holder.itemView.getContext())
                .load("https://image.tmdb.org/t/p/w500/" + movies.get(position).getPosterPath())
                .into((holder).movieImageView);

        holder.movieTitleTextView.setText(movie.getTitle());
    }

    @Override
    public int getItemCount() {
        return movies.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView movieImageView;
        public TextView movieTitleTextView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            movieImageView = itemView.findViewById(R.id.movieImageView);
            movieTitleTextView = itemView.findViewById(R.id.movieTitleTextView);
        }
    }
}

