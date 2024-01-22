package com.example.movieapp.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.movieapp.MyApplication;
import com.example.movieapp.R;
import com.example.movieapp.databinding.MovieListItem2Binding;
import com.example.movieapp.interfaces.OnMovieListener;
import com.example.movieapp.models.NowPlayingMovie;

import java.util.List;

public class MovieAdapter2 extends RecyclerView.Adapter<MovieAdapter2.ViewHolder> {
    private static Context context;
    private static OnMovieListener onMovieListener = null;
    private static List<NowPlayingMovie> mNowList;


    public MovieAdapter2(Context context, OnMovieListener listener) {
        MovieAdapter2.context = context;
        this.onMovieListener = listener;
    }

    private static NowPlayingMovie getItem(int position) {
        return mNowList.get(position);
    }

    public void setData2(List<NowPlayingMovie> mMovieNow2) {
        this.mNowList = mMovieNow2;
        notifyDataSetChanged();
    }

    //  <!-- TODO: click vào imgWishList chưa clear -->

    @NonNull
    @Override
    public MovieAdapter2.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.movie_list_item2, parent, false);
        MovieListItem2Binding movieListItem2Binding = DataBindingUtil.bind(view);
        return new ViewHolder(movieListItem2Binding, onMovieListener);
    }

    @Override
    public void onBindViewHolder(@NonNull MovieAdapter2.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        NowPlayingMovie nowPlayingMovie = mNowList.get(position);
        holder.bindData(nowPlayingMovie);
    }

    @Override
    public int getItemCount() {
        return (mNowList != null) ? mNowList.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private MovieListItem2Binding movieListItem2Binding;
        private OnMovieListener movieListener;

        public ViewHolder(MovieListItem2Binding binding, OnMovieListener listener) {
            super(binding.getRoot());
            movieListItem2Binding = binding;
            movieListener = listener;

            movieListItem2Binding.movieImg2.setOnClickListener(v -> movieListener.onMovieClick(getAbsoluteAdapterPosition()));

            movieListItem2Binding.imgWishlistHome.setOnClickListener(v -> movieListener.onChangeWishList(getAbsoluteAdapterPosition(), getItem(getAbsoluteAdapterPosition())));
        }

        public void bindData(NowPlayingMovie movie) {
            if (movie != null) {
                Log.d("TAG", "bindData: "+movie.getTitle()+" | isWish : "+movie.isWish());
                movieListItem2Binding.setMovie(movie);
                movieListItem2Binding.setReleaseDate(movie);
                Context context = MyApplication.getInstance().getApplicationContext();
                Glide.with(context).load(movie.isWish()
                                ? context.getResources().getDrawable(R.drawable.ic_wish_selected, null)
                                : context.getResources().getDrawable(R.drawable.ic_wish, null))
                        .into(movieListItem2Binding.imgWishlistHome);
            }
        }
    }
}
