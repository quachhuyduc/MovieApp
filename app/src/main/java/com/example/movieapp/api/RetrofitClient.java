package com.example.movieapp.api;

import com.chuckerteam.chucker.api.ChuckerInterceptor;
import com.example.movieapp.MyApplication;
import com.example.movieapp.utils.Constants;

import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {

    public static Retrofit getInstances() {
        if (retrofit == null) {
            HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
            loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            Interceptor interceptor = chain -> {
                Request request = chain.request();
                HttpUrl originalHttpUrl = chain.request().url();
                HttpUrl newUrl = originalHttpUrl.newBuilder()
                        .addQueryParameter("api_key", Constants.API_KEY).build();
                Request newRequest;
                newRequest = request.newBuilder()
                        .url(newUrl)
                        .addHeader("accept", "application/json").build();
//                        .addHeader("api_key", Constants.API_KEY).build();
//                        .addHeader("Authorization","Bearer eyJhbGciOiJIUzI1NiJ9.eyJhdWQiOiJmN2FkZWNmM2U1MWViNDIyNTljOWZiZjU4OTYxZjVjZCIsInN1YiI6IjY0YTY3MWEwY2FlNjMyMDBjODdkOTlmMSIsInNjb3BlcyI6WyJhcGlfcmVhZCJdLCJ2ZXJzaW9uIjoxfQ.NkwZPPPRiFNCTEiwVBlOyFt6zt2Cq1Z9SLZKaOkG9RY").build();
;
                return chain.proceed(newRequest);
            };
            OkHttpClient okHttpClient = new OkHttpClient.Builder().addInterceptor(interceptor).addInterceptor(loggingInterceptor).addInterceptor(new ChuckerInterceptor(MyApplication.getInstance()))
                    .build();
            Retrofit.Builder retrofitBuilder = new Retrofit.Builder().baseUrl(Constants.BASE_URL).client(okHttpClient).addConverterFactory(GsonConverterFactory.create());
            return retrofitBuilder.build();
        }
        return retrofit;
    }

    private static Retrofit retrofit;

    private static MovieApi movieApi = getInstances().create(MovieApi.class);

    public static MovieApi getMovieApi() {
        return getInstances().create(MovieApi.class);
    }


}
