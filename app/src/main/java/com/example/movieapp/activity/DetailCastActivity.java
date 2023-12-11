package com.example.movieapp.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.movieapp.R;
import com.example.movieapp.adapters.ImagesCastAdapter;
import com.example.movieapp.adapters.KnownForAdapter;
import com.example.movieapp.adapters.OnMovieListener;
import com.example.movieapp.api.MovieApi;
import com.example.movieapp.api.RetrofitClient;
import com.example.movieapp.models.Cast;
import com.example.movieapp.models.NowPlayingMovie;
import com.example.movieapp.models.Profile;
import com.example.movieapp.models.Result;
import com.example.movieapp.object.CastImagesResponse;
import com.example.movieapp.object.KnownForResponse;
import com.example.movieapp.utils.Constants;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DetailCastActivity extends AppCompatActivity {

    private ImageView cast_backdrop_detail,cast_img_detail;
    private TextView cast_name_detail,cast_character,cast_biogoraphy,cast_known_for_department,cast_popularity
            ,cast_genre,cast_birth,cast_place_of_birth,cast_also_known_as;
    private MovieApi movieApi;

    private ImagesCastAdapter imagesCastAdapter;
    private KnownForAdapter knownForAdapter;

    private List<Profile> mProfiles;
    private List<Result> mResults;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_cast);
        initView();
        GetDataFromIntent();
    }

    private void GetDataFromIntent() {
        Intent intent = getIntent();
        int personID = intent.getExtras().getInt(Constants.PERSON_ID_KEY,-1);
        if(personID != -1){
            movieApi = RetrofitClient.getMovieApi();
            movieApi.getDetailCast(personID).enqueue(new Callback<Cast>() {
                @Override
                public void onResponse(Call<Cast> call, Response<Cast> response) {
                    if (response.isSuccessful()) {
                        if (response.code() == 200) {
                            Glide.with(DetailCastActivity.this).load("https://image.tmdb.org/t/p/w500/" + response.body().getProfilePath()).into(cast_img_detail);

                            cast_name_detail.setText(response.body().getName());
                //            cast_character.setText(response.body().getCharacter());
                            cast_biogoraphy.setText(response.body().getBiography());



                            cast_known_for_department.setText("Known For: " + response.body().getKnownForDepartment());
                            cast_popularity.setText("Popularity: "+ response.body().getPopularity()+" ");
                            cast_place_of_birth.setText("Place of birth: " + response.body().getPlaceOfBirth());

                            cast_birth.setText("Birth: " + response.body().getBirthday());
                        }
                    }
                }

                @Override
                public void onFailure(Call<Cast> call, Throwable t) {
                    Log.d("TAG", "onFailure: " + t.getMessage());
                }
            });

            movieApi.getImagesCast(personID).enqueue(new Callback<CastImagesResponse>() {
                @Override
                public void onResponse(Call<CastImagesResponse> call, Response<CastImagesResponse> response) {
                    if(response.isSuccessful()){
                        if(response.code() == 200){

                            mProfiles    = response.body().getProfiles();
                            imagesCastAdapter.setDataImages(mProfiles);
                        }
                    }
                }

                @Override
                public void onFailure(Call<CastImagesResponse> call, Throwable t) {
                    Log.d("TAG", "onFailure: " + t.getMessage());
                }
            });
            movieApi.getKnownForCast(personID).enqueue(new Callback<KnownForResponse>() {
                @Override
                public void onResponse(Call<KnownForResponse> call, Response<KnownForResponse> response) {
                    if(response.isSuccessful()){
                        if(response.code() == 200){


                            mResults    = response.body().getResults();
                            knownForAdapter.setDataKnownFor(mResults);
                        }
                    }
                }

                @Override
                public void onFailure(Call<KnownForResponse> call, Throwable t) {
                    Log.d("TAG", "onFailure: " + t.getMessage());
                }
            });
        }else {
            finish();
        }


    }




    private void initView() {

        RecyclerView recyclerView = findViewById(R.id.rcv_cast_backdrop_detail);
        //   LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setHasFixedSize(true);
        //    recyclerView.setLayoutManager(layoutManager);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        imagesCastAdapter = new ImagesCastAdapter(getBaseContext());
        recyclerView.setAdapter(imagesCastAdapter);

        RecyclerView recyclerView1 = findViewById(R.id.rcv_known_for);
        //   LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView1.setHasFixedSize(true);
        //    recyclerView.setLayoutManager(layoutManager);
        recyclerView1.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        knownForAdapter = new KnownForAdapter(getBaseContext(), new OnMovieListener() {
            @Override
            public void onMovieClick(int positon) {
                onClickGotoDetail(positon);
            }

            @Override
            public void onCastClick(int positon) {

            }

            @Override
            public void onSaveClick(NowPlayingMovie nowPlayingMovie) {

            }


            @Override
            public void onChangeWishList(int position) {

            }
        });
        recyclerView1.setAdapter(knownForAdapter);

   //     cast_backdrop_detail = findViewById(R.id.cast_backdrop_detail);
        cast_img_detail = findViewById(R.id.cast_img_detail);
        cast_name_detail = findViewById(R.id.cast_name_detail);
    //      cast_character = findViewById(R.id.cast_character);
        cast_biogoraphy = findViewById(R.id.cast_biogoraphy);
        cast_known_for_department = findViewById(R.id.cast_known_for_department);
        cast_popularity = findViewById(R.id.cast_popularity);
   //     cast_genre = findViewById(R.id.cast_genre);
        cast_birth = findViewById(R.id.cast_birth);
        cast_place_of_birth = findViewById(R.id.cast_place_of_birth);

   //     cast_also_known_as = findViewById(R.id.cast_also_known_as);

        cast_biogoraphy.setNestedScrollingEnabled(false);

    }

    private void onClickGotoDetail(int positon) {

        int movieId = mResults.get(positon).getId();
        Intent intent = new Intent(this, DetailActivity.class);
        Bundle bundle = new Bundle();
        bundle.putInt(Constants.MOVIE_ID_KEY, movieId);
        intent.putExtras(bundle);
        startActivity(intent);
    }
}