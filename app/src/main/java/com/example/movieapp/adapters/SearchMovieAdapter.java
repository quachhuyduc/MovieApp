package com.example.movieapp.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.movieapp.R;
import com.example.movieapp.activity.DetailActivity;
import com.example.movieapp.api.MovieApi;
import com.example.movieapp.api.RetrofitClient;
import com.example.movieapp.models.Genre;
import com.example.movieapp.models.NowPlayingMovie;
import com.example.movieapp.models.Result;
import com.example.movieapp.object.DetailMovieResponse;
import com.example.movieapp.object.GenresMovie;
import com.example.movieapp.ui.SearchFragmentViewModel;
import com.example.movieapp.utils.Constants;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchMovieAdapter extends RecyclerView.Adapter<SearchMovieAdapter.ViewHolder> {

    private List<Result> mResult;

    private OnMovieListener onMovieListener;

    private Context context;
    private MovieApi movieApi;

    private SearchFragmentViewModel searchFragmentViewModel;
    public SearchMovieAdapter(Context context, OnMovieListener listener) {
        this.context = context;
        this.onMovieListener = listener;


    }



    public void setDataSearch(List<Result> mResult) {

        this.mResult = mResult;
        notifyDataSetChanged();
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



        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onMovieListener.onMovieClick(holder.getAdapterPosition());
            }
        });


        if (mResultMovie.isWish()){
            Glide.with(context).load(R.drawable.ic_wish_selected).into(holder.img_wishListSearch);
        }else {
            Glide.with(context).load(R.drawable.wish).into(holder.img_wishListSearch);
        }

        holder.img_wishListSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                onMovieListener.onChangeWishList(position);
            }
        });

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
       //     textView_genre = itemView.findViewById(R.id.textView_genreSearch);
            textView_release_date = itemView.findViewById(R.id.textView_release_date_search);

        }
    }
}
