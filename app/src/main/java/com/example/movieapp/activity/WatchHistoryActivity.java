package com.example.movieapp.activity;

import static com.google.firebase.database.DatabaseKt.getSnapshots;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.movieapp.R;
import com.example.movieapp.adapters.MovieAdapter;
import com.example.movieapp.models.Genre;
import com.example.movieapp.models.NowPlayingMovie;

import com.example.movieapp.object.DetailMovieResponse;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.util.List;

public class WatchHistoryActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private FirebaseRecyclerAdapter<DetailMovieResponse, MovieViewHolder> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_watch_history);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        String currentUserUid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        Query query = FirebaseDatabase.getInstance().getReference("historyList").child(currentUserUid);

        FirebaseRecyclerOptions<DetailMovieResponse> options = new FirebaseRecyclerOptions.Builder<DetailMovieResponse>()
                .setQuery(query, DetailMovieResponse.class)
                .build();

        adapter = new FirebaseRecyclerAdapter<DetailMovieResponse, MovieViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull MovieViewHolder holder, int position, @NonNull DetailMovieResponse model) {

                holder.bindData(model);
            }

            @NonNull
            @Override
            public MovieViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_watch_history, parent, false);
                return new MovieViewHolder(view);
            }
        };

        recyclerView.setAdapter(adapter);
    }

    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }

    private static class MovieViewHolder extends RecyclerView.ViewHolder {

        ImageView img_history,img_genres_history;
        TextView tv_title_history, tv_rating_history, tv_release_date_history, tv_genre_history, tv_runtime_history,overview_history;

        public MovieViewHolder(@NonNull View itemView) {
            super(itemView);
            img_history = itemView.findViewById(R.id.img_history);
            tv_title_history = itemView.findViewById(R.id.tv_title_history);
        //    tv_rating_history = itemView.findViewById(R.id.tv_rating_history);
       //     tv_release_date_history = itemView.findViewById(R.id.tv_release_date_history);
      //      tv_genre_history = itemView.findViewById(R.id.tv_genre_history);
     //       img_genres_history = itemView.findViewById(R.id.img_genres_history);
            tv_runtime_history = itemView.findViewById(R.id.tv_runtime_history);
            overview_history = itemView.findViewById(R.id.overview_history);

        }

        public void bindData(DetailMovieResponse detailMovieResponse) {
            String posterPath = detailMovieResponse.getPosterPath();
         //   String voteAverage = detailMovieResponse.getVoteAverage() +"";

            if (posterPath != null && !posterPath.isEmpty()) {
                Glide.with(itemView.getContext())
                        .load("https://image.tmdb.org/t/p/w500/" + posterPath)
                        .into(img_history);
            } else {
                img_history.setImageResource(R.drawable._0);
            }

            tv_title_history.setText(detailMovieResponse.getTitle());
            overview_history.setText(detailMovieResponse.getOverview());
      //      tv_rating_history.setText(voteAverage);
     //       tv_release_date_history.setText(detailMovieResponse.getReleaseDate());
/*
            String genres = getListGenres(detailMovieResponse.getGenres());
            if (genres != null) {
                tv_genre_history.setText(genres);
                tv_genre_history.setVisibility(View.VISIBLE);
                img_genres_history.setVisibility(View.VISIBLE);
            } else {
                tv_genre_history.setVisibility(View.GONE);
                img_genres_history.setVisibility(View.GONE);

            }

 */
            tv_runtime_history.setText(detailMovieResponse.getRuntime() +""+" Minutes");



        }

        private String getListGenres(List<Genre> genres) {
            if (genres == null) {
                return null;
            }
            String result = "";
            for (int i = 0; i < genres.size(); i++) {
                if (i > 0) {
                    result += "," + genres.get(i).getName();
                } else {
                    result += genres.get(i).getName();
                }
            }
            return result;
        }

    }
}

