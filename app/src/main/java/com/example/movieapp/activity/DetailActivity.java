package com.example.movieapp.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.example.movieapp.R;
import com.example.movieapp.adapters.PagerAdapterDetail;
import com.example.movieapp.api.MovieApi;
import com.example.movieapp.api.RetrofitClient;
import com.example.movieapp.models.Genre;
import com.example.movieapp.object.DetailMovieResponse;
import com.example.movieapp.object.GenresMovie;
import com.example.movieapp.utils.Constants;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DetailActivity extends AppCompatActivity {

    private static final String TAG = "DetailActivity";

    private ImageView img_detail, img_poster_detail, img_genres, img_wishList;
    private TextView textView_release_date, textView_runtime, textView_genre, textView_rating, title_detail;
    private MovieApi movieApi;

    private DatabaseReference historyReference;
    private FirebaseUser currentUser;

    private DetailMovieResponse movieResponse;

    private Button btnWatch;

    private List<Integer> restrictedGenres = Arrays.asList(27, 80, 53, 10752); // Thay thế ID của thể loại cần hạn chế

    private Map<String, Integer> genreIdMap = new HashMap<>();

    interface AgeCallback {
        void onSuccess(int userAge);
        void onError();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        initView();
        addControl();
        getDataFromIntent();

        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            historyReference = FirebaseDatabase.getInstance().getReference("historyList").child(currentUser.getUid());

            getDataFromApiForGenres();
            checkUserAge();
        }
    }

    private void getDataFromApiForGenres() {
        // Gọi API để lấy danh sách thể loại
        MovieApi movieApi = RetrofitClient.getMovieApi(); // Đảm bảo bạn đã có RetrofitClient và MovieApi được định nghĩa

        Call<GenresMovie> call = movieApi.getGenresMovie();

        call.enqueue(new Callback<GenresMovie>() {
            @Override
            public void onResponse(Call<GenresMovie> call, Response<GenresMovie> response) {
                if (response.isSuccessful()) {
                    List<Genre> genresListFromApi = response.body().getGenres();

                    // Sau khi nhận được dữ liệu thể loại, xây dựng bản đồ ánh xạ
                    for (Genre genre : genresListFromApi) {
                        genreIdMap.put(genre.getName().toLowerCase(), genre.getId());
                    }
                } else {
                    // Xử lý khi có lỗi từ server
                }
            }

            @Override
            public void onFailure(Call<GenresMovie> call, Throwable t) {
                // Xử lý khi gặp lỗi kết nối
            }
        });
        // ...
    }

    private void checkUserAge() {
        int ageLimit = 18;

        // Kiểm tra xem movieResponse có null hay không
        if (movieResponse != null) {
            // Lấy ngày sinh của người dùng từ Firebase
            getUserAgeFromFirebase(new AgeCallback() {
                @Override
                public void onSuccess(int userAge) {
                    if (userAge < ageLimit) {
                        // Người dưới 18 tuổi, kiểm tra xem phim thuộc thể loại nào
                        String movieGenres = getFirstGenreName(movieResponse.getGenres());

                        if (movieGenres != null && isRestrictedGenre(movieGenres)) {
                            // Người dưới 18 tuổi và phim thuộc thể loại cấm, hiển thị thông báo
                            showAgeAndGenreRestrictionDialog();
                        } else {
                            // Người dưới 18 tuổi nhưng phim thuộc thể loại không cấm, cho phép xem
                            btnWatch.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    goToPlayVideo();
                                }
                            });
                        }
                    } else {
                        // Người dùng đủ 18 tuổi, cho phép xem bình thường
                        btnWatch.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                goToPlayVideo();
                            }
                        });
                    }
                }

                @Override
                public void onError() {
                    // Xử lý lỗi khi không thể lấy được dữ liệu tuổi từ Firebase
                    // Có thể hiển thị thông báo lỗi hoặc thực hiện các hành động khác
                }
            });
        }
    }
    private void getUserAgeFromFirebase(final AgeCallback callback) {
        DatabaseReference userAgeReference = FirebaseDatabase.getInstance().getReference("users")
                .child(currentUser.getUid())
                .child("age");

        userAgeReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull com.google.firebase.database.DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Object ageValue = dataSnapshot.getValue();
                    if (ageValue instanceof String) {
                        callback.onSuccess(Integer.parseInt((String) ageValue));
                    } else if (ageValue instanceof Long) {
                        callback.onSuccess(((Long) ageValue).intValue());
                    }
                } else {
                    callback.onError(); // Xử lý trường hợp không có dữ liệu tuổi
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                callback.onError(); // Xử lý lỗi nếu có
            }
        });
    }
/*
    private int getUserAgeFromFirebase() {
        final int[] userAge = {-1};

        DatabaseReference userAgeReference = FirebaseDatabase.getInstance().getReference("users")
                .child(currentUser.getUid())
                .child("age");

        userAgeReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull com.google.firebase.database.DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Object ageValue = dataSnapshot.getValue();
                    if (ageValue instanceof String) {
                        userAge[0] = Integer.parseInt((String) ageValue);
                    } else if (ageValue instanceof Long) {
                        userAge[0] = ((Long) ageValue).intValue();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Xử lý lỗi nếu có
            }
        });

        return userAge[0];
    }

 */

    private void showAgeAndGenreRestrictionDialog() {
        // Hiển thị thông báo khi người dùng không đủ 18 tuổi hoặc phim thuộc thể loại cấm
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Age and Genre Restriction");
        builder.setMessage("You must be 18 years or older to watch this content. Additionally, this content contains restricted genres (horror, crime, thriller, war). You may not watch this content.");
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Đóng dialog nếu người dùng bấm OK
                dialog.dismiss();
                // Có thể thêm các xử lý khác sau khi người dùng bấm OK
            }
        });
        builder.show();
    }

    private String getFirstGenreName(List<Genre> genres) {
        if (genres != null && genres.size() > 0) {
            return genres.get(0).getName();
        }
        return null;
    }

    private boolean isRestrictedGenre(String genres) {
        // Thêm log để xem giá trị
        Log.d(TAG, "isRestrictedGenre - Genres: " + genres);
        if (genres != null) {
            String[] genreArray = genres.toLowerCase().split(",");
            for (String genre : genreArray) {
                if (genreIdMap.containsKey(genre.trim())) {
                    String genreName = String.valueOf(genreIdMap.get(genre.trim()));
                    Log.d(TAG, "isRestrictedGenre - GenreName: " + genreName);
                    if (restrictedGenres.contains(Integer.valueOf(genreName))) {
                        Log.d(TAG, "isRestrictedGenre - Restricted Genre Found");
                        return true;  // Phát hiện thể loại hạn chế
                    }
                }
            }
        }
        Log.d(TAG, "isRestrictedGenre - Not Restricted Genre");
        return false;  // Không phải thể loại hạn chế
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

    private void getDataFromIntent() {
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
                            textView_runtime.setText(movieResponse.getRuntime() + "" + " Minutes");

                            String genres = getListGenres(movieResponse.getGenres());
                            if (genres != null) {
                                textView_genre.setText(genres);
                                textView_genre.setVisibility(View.VISIBLE);
                                img_genres.setVisibility(View.VISIBLE);
                            } else {
                                textView_genre.setVisibility(View.GONE);
                                img_genres.setVisibility(View.GONE);
                            }

                            textView_rating.setText(movieResponse.getVoteAverage() + "");
                        }
                    }
                }

                @Override
                public void onFailure(Call<DetailMovieResponse> call, Throwable t) {
                    // Xử lý lỗi nếu có
                }
            });
        } else {
            finish();
        }
    }

    private String getListGenres(List<Genre> genres) {
        if (genres == null) {
            return null;
        }
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < genres.size(); i++) {
            if (i > 0) {
                result.append(",").append(genres.get(i).getName());
            } else {
                result.append(genres.get(i).getName());
            }
        }
        return result.toString();
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
                goToPlayVideo();
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
        // Thực hiện các thao tác cập nhật danh sách mong muốn tại đây
    }

    private void goToPlayVideo() {
        int movieId = getIntent().getExtras().getInt(Constants.MOVIE_ID_KEY, -1);

        if (currentUser != null && movieId != -1) {
            DatabaseReference movieReference = historyReference.child(String.valueOf(movieId));
            movieReference.child("title").setValue(title_detail.getText().toString());
            movieReference.child("posterPath").setValue("https://image.tmdb.org/t/p/w500/" + movieResponse.getPosterPath());
            movieReference.child("release_date").setValue(movieResponse.getReleaseDate());
            movieReference.child("vote_average").setValue(textView_rating.getText().toString());

            String genres = getListGenres(movieResponse.getGenres());
            movieReference.child("genre").setValue(genres);
            if (genres != null) {
                textView_genre.setText(genres);
                textView_genre.setVisibility(View.VISIBLE);
                img_genres.setVisibility(View.VISIBLE);
            } else {
                textView_genre.setVisibility(View.GONE);
                img_genres.setVisibility(View.GONE);
            }

            movieReference.child("runtime").setValue(movieResponse.getRuntime());
            movieReference.child("overview").setValue(movieResponse.getOverview());
            // Thêm các trường khác nếu cần

            // Kiểm tra xem bộ phim thuộc thể loại bị hạn chế hay không
            if (isRestrictedGenre(genres)) {
                // Kiểm tra xem người xem có đủ 18 tuổi hay không
                getUserAgeFromFirebase(new AgeCallback() {
                    @Override
                    public void onSuccess(int userAge) {
                        // Sử dụng userAge như cần
                        if (userAge < 18) {
                            // Người dưới 18 tuổi, hiển thị thông báo và không cho phép xem
                            showAgeAndGenreRestrictionDialog();
                        } else {
                            // Người dưới 18 tuổi nhưng phim thuộc thể loại không cấm, cho phép xem
                            continueToPlayVideo(movieId);
                        }
                    }

                    @Override
                    public void onError() {
                        // Xử lý trường hợp lỗi khi không thể lấy được tuổi người dùng
                        // Có thể hiển thị thông báo lỗi hoặc thực hiện các hành động khác
                    }
                });
            } else {
                // Nếu không có thể loại bị hạn chế, tiếp tục chuyển đến PlayVideoActivity
                continueToPlayVideo(movieId);
            }
        }
    }

    private void continueToPlayVideo(int movieId) {
        // Chuyển đến PlayVideoActivity
        Intent intent = new Intent(this, PlayVideoActivity.class);
        Bundle bundle = new Bundle();
        bundle.putInt(Constants.MOVIE_ID_KEY, movieId);
        intent.putExtras(bundle);
        startActivity(intent);
    }

}
