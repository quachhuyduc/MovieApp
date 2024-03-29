package com.example.movieapp.adapters;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.movieapp.R;
import com.example.movieapp.interfaces.OnMovieListener;
import com.example.movieapp.models.Genre;
import com.example.movieapp.models.Result;

import java.util.ArrayList;
import java.util.List;

public class SearchMovieAdapter extends RecyclerView.Adapter<SearchMovieAdapter.ViewHolder> {




    private List<Result> mResult;

    private OnMovieListener onMovieListener;



    private List<Genre> genresList = new ArrayList<>();


    private Context context;



    public SearchMovieAdapter(Context context, OnMovieListener listener) {
        this.context = context;
        this.onMovieListener = listener;


    }



    public void setDataSearch(List<Result> mResult) {

        this.mResult = mResult;
        notifyDataSetChanged();
    }


    public void setGenresList(List<Genre> genresList) {
        this.genresList = genresList != null ? genresList : new ArrayList<>();  // Đảm bảo genresList không bao giờ là null
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_search, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {


        Result mResultMovie = mResult.get(position);

        Glide.with(holder.itemView.getContext())
                .load("https://image.tmdb.org/t/p/w500/" + mResult.get(position)
                        .getPosterPath())
                .into((holder).imageView);

        holder.tv_search.setText(mResult.get(position).getTitle());
        holder.textView_rating.setText(mResult.get(position).getVoteAverage() + "");



        holder.textView_release_date.setText(mResult.get(position).getReleaseDate());

        List<Integer> genreIds = mResultMovie.getGenreIds();
        if (genreIds != null && !genreIds.isEmpty()) {
            List<String> genreNames = getGenreNames(genreIds);
            holder.textView_genre.setText(TextUtils.join(", ", genreNames));
        } else {
            holder.textView_genre.setText("");
        }



        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onMovieListener.onMovieClick(holder.getAdapterPosition());
            }
        });


        if (mResultMovie.isWish()){
            Glide.with(context).load(R.drawable.ic_wish_selected).into(holder.img_wishListSearch);
        }else {
            Glide.with(context).load(R.drawable.ic_wish).into(holder.img_wishListSearch);
        }

        holder.img_wishListSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //        onMovieListener.onChangeWishList(position);
            }
        });

    }
    private List<String> getGenreNames(List<Integer> genreIds) {
        List<String> genreNames = new ArrayList<>();
        for (Integer genreId : genreIds) {
            String name = findGenreNameById(genreId);
            if (name != null) {
                genreNames.add(name);
            }
        }
        return genreNames;
    }

    private String findGenreNameById(int genreId) {
        for (Genre genre : genresList) {
            if (genre.getId() == genreId) {
                return genre.getName();
            }
        }
        return null;
    }





    @Override
    public int getItemCount() {
        return (mResult != null && mResult.size() > 0) ? mResult.size() : 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView tv_search,textView_rating,textView_genre,textView_release_date;
        RecyclerView recyclerView;
        ImageView imageView,img_wishListSearch;

        EditText editText;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            recyclerView = itemView.findViewById(R.id.rcv_search);
            imageView = itemView.findViewById(R.id.img_search);
            img_wishListSearch = itemView.findViewById(R.id.img_wishListSearch);
            editText = itemView.findViewById(R.id.edtSearch);
            tv_search = itemView.findViewById(R.id.tv_Search);
            textView_rating = itemView.findViewById(R.id.textView_rating_search);
            textView_genre = itemView.findViewById(R.id.textView_genreSearch);
            textView_release_date = itemView.findViewById(R.id.textView_release_date_search);

        }

    }
}
