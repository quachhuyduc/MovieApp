package com.example.movieapp.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

import com.example.movieapp.R;
import com.example.movieapp.adapters.CastAdapter;
import com.example.movieapp.adapters.OnMovieListener;
import com.example.movieapp.adapters.VideoAdapter;
import com.example.movieapp.api.MovieApi;
import com.example.movieapp.api.RetrofitClient;
import com.example.movieapp.models.NowPlayingMovie;
import com.example.movieapp.models.Video;
import com.example.movieapp.object.CastResponse;
import com.example.movieapp.object.VideoResponse;
import com.example.movieapp.utils.Constants;
import com.example.movieapp.utils.PlayYTView;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.PlayerConstants;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.utils.YouTubePlayerUtils;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PlayVideoActivity extends AppCompatActivity {


    private PlayYTView wvDemo;
    private Button btnForward;
    private Button btnPlayOrPause;
    private Button btnPrevious;

    private VideoAdapter videoAdapter;

    private List<Video> mVideos;

    private MovieApi movieApi;
    private YouTubePlayerView youTubePlayerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_video);
        initView();
        GetDataFromIntent();



    }

    private void GetDataFromIntent() {

        Intent intent = getIntent();
        int movieID = intent.getExtras().getInt(Constants.MOVIE_ID_KEY, -1);
        if (movieID != -1) {
            movieApi = RetrofitClient.getMovieApi();
            movieApi.getVideoMovie(movieID).enqueue(new Callback<VideoResponse>() {
                @Override
                public void onResponse(Call<VideoResponse> call, Response<VideoResponse> response) {
                    if (response.isSuccessful()) {
                        if (response.code() == 200) {
                            String key = response.body().getVideos().get(0).getKey();
                            mVideos = response.body().getVideos();
                            videoAdapter.setDataVideo(mVideos);

                            wvDemo.loadVideoID(key);
                       /*
                            youTubePlayerView.addYouTubePlayerListener(new AbstractYouTubePlayerListener() {
                                @Override
                                public void onReady(@NonNull YouTubePlayer youTubePlayer) {
                                    youTubePlayer.loadVideo(key, 0);
                                }
                            });

                        */
                        }
                    }
                }

                @Override
                public void onFailure(Call<VideoResponse> call, Throwable t) {
                    Log.d("TAG", "onFailure: " + t.getMessage());
                }
            });
        } else {
            finish();
        }
    }

    private void initView() {
  //      youTubePlayerView = findViewById(R.id.youtube_player_view);
        wvDemo = findViewById(R.id.wvDemo);
        wvDemo.setAutoPlay(true);

        btnForward = findViewById(R.id.btnForward);
        btnPlayOrPause = findViewById(R.id.btnPlayOrPause);
        btnPrevious = findViewById(R.id.btnPrevious);

        btnPrevious.setOnClickListener(v -> wvDemo.onPrevious());
        btnPlayOrPause.setOnClickListener(v -> {
            wvDemo.changeState();
            btnPlayOrPause.setText(wvDemo.getState());
        });
        btnForward.setOnClickListener(v -> wvDemo.onForward());

        RecyclerView rcvVideos = findViewById(R.id.rcvVideos);
        rcvVideos.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getBaseContext(),LinearLayoutManager.VERTICAL,false);
        rcvVideos.setLayoutManager(linearLayoutManager);

        videoAdapter = new VideoAdapter(getBaseContext(), new OnMovieListener() {
            @Override
            public void onMovieClick(int positon) {

             onClickGoToPlayAnotherVideo(positon);
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
        rcvVideos.setAdapter(videoAdapter);


    }


    private void onClickGoToPlayAnotherVideo(int positon) {
            String videoId = mVideos.get(positon).getKey();
               wvDemo.loadVideoID(videoId);

    }


}
