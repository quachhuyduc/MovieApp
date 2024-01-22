package com.example.movieapp.api;

import androidx.lifecycle.LiveData;

import com.example.movieapp.models.Cast;
import com.example.movieapp.object.CastImagesResponse;
import com.example.movieapp.object.CastResponse;
import com.example.movieapp.object.DetailMovieResponse;
import com.example.movieapp.object.GenresMovie;
import com.example.movieapp.object.KnownForResponse;
import com.example.movieapp.object.ListNowPlayingResponse;

import com.example.movieapp.object.MovieSearchResponse;
import com.example.movieapp.object.PopularResponse;
import com.example.movieapp.object.ReviewsResponse;
import com.example.movieapp.object.SearchPersonResponse;
import com.example.movieapp.object.TopRatedResponse;
import com.example.movieapp.object.UpComingResponse;
import com.example.movieapp.object.VideoResponse;
import com.google.gson.JsonObject;

import org.jetbrains.annotations.NotNull;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface MovieApi {

    @GET("/3/search/movie")
    Call<MovieSearchResponse> searchMovie (
            @Query("api_key") String key,
            @Query("query") String query

    );
    @GET("/3/search/person")
    Call<SearchPersonResponse> searchPerson (

            @Query("query") String query

    );

    @GET("/3/movie/now_playing")
    Call<ListNowPlayingResponse> getNowPlayingMovie(
            @Query("page") int page
    );
    @GET("/3/movie/popular")
    Call<PopularResponse> getPopularMovie(
            @Query("page") int page
    );
    @GET("/3/movie/upcoming")
    Call<UpComingResponse> getUpcomingMovie(
            @Query("page") int page
    );
    @GET("/3/movie/top_rated")
    Call<TopRatedResponse> getTopRatedMovie(
            @Query("page") int page
    );
    @GET("/3/movie/{movie_id}")
    Call<DetailMovieResponse> getDetailMovie(
            @Path("movie_id") int movie_id,
            @Query("api_key") String api_key
    );
    @GET("/3/movie/{movie_id}/reviews")
    Call<ReviewsResponse> getReviewsMovie(
            @Path("movie_id") int movie_id
    );
    @GET("/3/movie/{movie_id}/credits")
    Call<CastResponse> getCastMovie(
            @Path("movie_id") int movie_id,
            @Query("api_key") String api_key
    );

    @GET("/3/person/{person_id}")
    Call<Cast> getDetailCast(
          @Path("person_id") int person_id
    );
    @GET("/3/person/{person_id}/images")
    Call<CastImagesResponse> getImagesCast(
            @Path("person_id") int person_id
    );
    @GET("/3/person/{person_id}/movie_credits")
    Call<KnownForResponse> getKnownForCast(
            @Path("person_id") int person_id
    );

    @GET("/3/movie/{movie_id}/videos")
    Call<VideoResponse> getVideoMovie(
            @Path("movie_id") int movie_id

    );

    @GET("/3/genre/movie/list")
    Call<GenresMovie> getGenresMovie(
    );

}
