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
import com.example.movieapp.models.Result;

import java.util.List;

public class KnownForAdapter extends RecyclerView.Adapter<KnownForAdapter.ViewHolder> {

    private List<Result> mResults;

    private Context context;

    private OnMovieListener onMovieListener;

    public KnownForAdapter(Context context, OnMovieListener onMovieListener) {
        this.context = context;
        this.onMovieListener = onMovieListener;
    }


    public void setDataKnownFor(List<Result> mResults){
        this.mResults = mResults;
        notifyDataSetChanged();

    }

    @NonNull
    @Override
    public KnownForAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.movie_list_item,parent,false);

        return new KnownForAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull KnownForAdapter.ViewHolder holder, int position) {
        Glide.with(holder.itemView.getContext())
                .load("https://image.tmdb.org/t/p/w500/" + mResults.get(position)
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
        return (mResults != null && mResults.size() > 0) ? mResults.size() : 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        RecyclerView recyclerView;
        ImageView imageView;

        public ViewHolder(@NonNull View itemView) {

            super(itemView);

            recyclerView = itemView.findViewById(R.id.rcv_known_for);
            imageView = itemView.findViewById(R.id.movie_img);


        }
    }
}
