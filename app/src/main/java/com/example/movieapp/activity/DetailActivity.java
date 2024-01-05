package com.example.movieapp.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.example.movieapp.R;
import com.example.movieapp.adapters.PagerAdapterDetail;
import com.example.movieapp.api.MovieApi;
import com.example.movieapp.api.RetrofitClient;
import com.example.movieapp.interfaces.IItemClickListener;
import com.example.movieapp.models.Genre;
import com.example.movieapp.models.Video;
import com.example.movieapp.object.DetailMovieResponse;
import com.example.movieapp.utils.Constants;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DetailActivity extends AppCompatActivity {

    private static final String TAG = "DetailActivity";



    private ImageView img_detail, img_poster_detail,img_genres,img_wishList;
    private TextView textView_release_date, textView_runtime, textView_genre, textView_rating, title_detail;
    private MovieApi movieApi;

    private DatabaseReference historyReference;
    private FirebaseUser currentUser;

    private DetailMovieResponse movieResponse;

    private Button btnWatch;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        initView();
        addControl();
        GetDataFromIntent();

        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if(currentUser != null){
            historyReference = FirebaseDatabase.getInstance().getReference("historyList").child(currentUser.getUid());
        }
    }

    private void addControl() {
        ViewPager pagerDetail = findViewById(R.id.view_pager_detail);
        TabLayout tabLayout = findViewById(R.id.tab_layout_detail);
        FragmentManager manager = getSupportFragmentManager();
        PagerAdapterDetail pagerAdapterDetail = new PagerAdapterDetail(manager);
        pagerDetail.setAdapter(pagerAdapterDetail);
        tabLayout.setupWithViewPager(pagerDetail);
        pagerDetail.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.setTabsFromPagerAdapter(pagerAdapterDetail);
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(pagerDetail));
    }

    private void GetDataFromIntent() {
        Intent intent = getIntent();
        int movieID = intent.getExtras().getInt(Constants.MOVIE_ID_KEY, -1);
        if (movieID != -1) {
            movieApi = RetrofitClient.getMovieApi();
            movieApi.getDetailMovie(movieID, Constants.API_KEY).enqueue(new Callback<DetailMovieResponse>() {
                @Override
                public void onResponse(Call<DetailMovieResponse> call, Response<DetailMovieResponse> response) {
                    if (response.isSuccessful()) {
                        if (response.code() == 200) {
                            movieResponse = response.body();

                            Glide.with(DetailActivity.this).load("https://image.tmdb.org/t/p/w500/" + movieResponse.getBackdropPath()).into(img_detail);
                            Glide.with(DetailActivity.this).load("https://image.tmdb.org/t/p/w500/" + movieResponse.getPosterPath()).into(img_poster_detail);
                            title_detail.setText(response.body().getTitle());
                            textView_release_date.setText(movieResponse.getReleaseDate());
                            textView_runtime.setText(movieResponse.getRuntime()+""+" Minutes");

                            String genres = getListGenres(movieResponse.getGenres());
                            if(genres != null){
                                textView_genre.setText(genres);
                                textView_genre.setVisibility(View.VISIBLE);
                                img_genres.setVisibility(View.VISIBLE);
                            }else{
                                textView_genre.setVisibility(View.GONE);
                                img_genres.setVisibility(View.GONE);
                            }

                            textView_rating.setText(movieResponse.getVoteAverage() + "");


                        }
                    }
                }

                @Override
                public void onFailure(Call<DetailMovieResponse> call, Throwable t) {
                    Log.d(TAG, "onFailure: " + t.getMessage());
                }
            });
        } else {
            finish();
        }

    }

    private String getListGenres(List<Genre> genres) {
        if(genres == null){
            return null;
        }
        String result = "";
        for (int i = 0; i<genres.size();i++){
            if(i >0){
                result += ","+genres.get(i).getName();
            }else{
                result+= genres.get(i).getName();
            }
        }
        return result;
    }

    private void initView() {
        img_detail = findViewById(R.id.img_detail);
        img_genres = findViewById(R.id.img_genres);
        img_poster_detail = findViewById(R.id.img_poster_detail);
        img_wishList = findViewById(R.id.img_wishList);
        textView_release_date = findViewById(R.id.textView_release_date);
        textView_runtime = findViewById(R.id.textView_runtime);
        textView_genre = findViewById(R.id.textView_genre);
        textView_rating = findViewById(R.id.textView_rating);
        title_detail = findViewById(R.id.title_detail);

        btnWatch = findViewById(R.id.btnWatch);

        btnWatch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

             goToPLayVideo();
            }
        });
        img_wishList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateWishList();

            }
        });

    }

    private void updateWishList() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("favouritesList");
    }

    private void goToPLayVideo() {
        int movieId = getIntent().getExtras().getInt(Constants.MOVIE_ID_KEY, -1);

        if (currentUser != null && movieId != -1) {
    //        historyReference.child(String.valueOf(movieId)).setValue(true);

            // Lưu thêm thông tin về bộ phim trong Firebase
            DatabaseReference movieReference = historyReference.child(String.valueOf(movieId));
            movieReference.child("title").setValue(title_detail.getText().toString());
            movieReference.child("posterPath").setValue("https://image.tmdb.org/t/p/w500/" + movieResponse.getPosterPath());
            movieReference.child("release_date").setValue(movieResponse.getReleaseDate());
            movieReference.child("vote_average").setValue(textView_rating.getText().toString());
            String genres = getListGenres(movieResponse.getGenres());
            movieReference.child("genre").setValue(genres);
            if(genres != null){
                textView_genre.setText(genres);
                textView_genre.setVisibility(View.VISIBLE);
                img_genres.setVisibility(View.VISIBLE);
            }else{
                textView_genre.setVisibility(View.GONE);
                img_genres.setVisibility(View.GONE);
            }

            movieReference.child("runtime").setValue(movieResponse.getRuntime());
            movieReference.child("overview").setValue(movieResponse.getOverview());
            // Thêm các trường khác nếu cần

            // Chuyển đến PlayVideoActivity
            Intent intent = new Intent(this, PlayVideoActivity.class);
            Bundle bundle = new Bundle();
            bundle.putInt(Constants.MOVIE_ID_KEY, movieId);
            intent.putExtras(bundle);
            startActivity(intent);
        }
    }
}