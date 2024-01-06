package com.example.movieapp.ui;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.example.movieapp.models.NowPlayingMovie;
import com.example.movieapp.object.ListNowPlayingResponse;
import com.example.movieapp.object.PopularResponse;
import com.example.movieapp.object.TopRatedResponse;
import com.example.movieapp.object.UpComingResponse;
import com.example.movieapp.resporistories.HomeResporistory;
import com.example.movieapp.utils.ApiStatus;
import com.example.movieapp.utils.Constants;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragmentViewModel extends AndroidViewModel {

    public MutableLiveData<PopularResponse> popular = new MutableLiveData<>();
    public MutableLiveData<UpComingResponse> upComing = new MutableLiveData<>();
    public MutableLiveData<TopRatedResponse> topRated = new MutableLiveData<>();
    public DatabaseReference favouritesRef;
    public DatabaseReference favouritesListRef;
    public DatabaseReference allWatchListRef;
    public MutableLiveData<ArrayList<NowPlayingMovie>> nowListLiveData = new MutableLiveData<>();
    private ApiStatus wishListApiStatus = ApiStatus.ERROR;
    private ApiStatus nowListApiStatus = ApiStatus.ERROR;
    private HomeResporistory homeResporistory;
    // Các phương thức và khai báo khác...
    private FirebaseUser user;
    private FirebaseDatabase database;
    private String currentUserid;
    private ArrayList<NowPlayingMovie> nowList = new ArrayList<>();
    private ArrayList<NowPlayingMovie> wishList = new ArrayList<>();

    public HomeFragmentViewModel(@NonNull Application application, HomeResporistory homeResporistory) {
        super(application);
        this.homeResporistory = homeResporistory;
        initData();
    }

    private void initData() {
        user = FirebaseAuth.getInstance().getCurrentUser();
        currentUserid = user.getUid();

        database = FirebaseDatabase.getInstance();
        favouritesRef = database.getReference(Constants.DB_FAVOURITES);
        allWatchListRef = favouritesRef.child(Constants.DB_ALL_WATCH_LIST);
        favouritesListRef = database.getReference(Constants.DB_FAVOURITES_LIST).child(currentUserid);

        loadWishList();
    }

    private void loadWishList() {
        wishList = new ArrayList<>();
        wishListApiStatus = ApiStatus.REQUEST;
        favouritesListRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.getChildrenCount() > 0) {
                    for (DataSnapshot child : snapshot.getChildren()) {
                        wishList.add(child.getValue(NowPlayingMovie.class));
                    }
                    wishListApiStatus = ApiStatus.SUCCESS;
                    updateData();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("TAG", "onCancelled: " + error.getMessage());
                wishListApiStatus = ApiStatus.ERROR;
                updateData();
            }
        });
    }

    private void updateData() {
        if (wishListApiStatus != ApiStatus.REQUEST && nowListApiStatus != ApiStatus.REQUEST) {
            postNowPlayingData(nowList, wishList);
        }
    }

    public ArrayList<NowPlayingMovie> getWishList() {
        return wishList;
    }

    public void updateMovieWishList(int position, NowPlayingMovie movie) {
        favouritesListRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //Kiểm tra wishlist của user có data hay chưa nếu có rồi thì for để get list còn chưa thì push value luôn
                boolean isExisted = false;
                if (snapshot.getChildrenCount() > 0) {
                    for (DataSnapshot child : snapshot.getChildren()) {
                        NowPlayingMovie index = child.getValue(NowPlayingMovie.class);
                        int id = index.getId();
                        if (id == movie.getId()) {
                            isExisted = true;
                            String removeKey = child.getKey();
                            favouritesListRef.child(removeKey).removeValue().addOnCompleteListener(task -> {
                                Log.d("TAG", "onDataChange: remove " + movie.getId() + " success ");
                                wishList.removeIf(mv -> mv.getId() == id);//Update wishList
                                updateData();
                            }).addOnFailureListener(e -> {
                                Log.d("TAG", "onDataChange: " + e.getMessage());
                            });

                            break;
                        }
                    }
                    //Nếu movie chưa có trong mảng thì push value mới
                    if (!isExisted) {
                        addNewWish(movie);
                    }
                } else {
                    addNewWish(movie);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("TAG", "onCancelled: " + error.getMessage());
            }
        });
    }

    private void addNewWish(NowPlayingMovie movie) {
        favouritesListRef.push().setValue(movie).addOnCompleteListener(task -> {
            Log.d("TAG", "onComplete: add wish success");
            wishList.add(movie);
            updateData();
        }).addOnFailureListener(e -> {
            Log.d("TAG", "onComplete: add wish failure" + e.getMessage());
            updateData();
        });
    }


    public void getListNowPlaying(int page) {
        nowListApiStatus = ApiStatus.ERROR;
        homeResporistory.getListNowPlaying(page).enqueue(new Callback<ListNowPlayingResponse>() {
            @Override
            public void onResponse(Call<ListNowPlayingResponse> call, Response<ListNowPlayingResponse> response) {
                if (response.isSuccessful()) {
                    nowList = (ArrayList<NowPlayingMovie>) response.body().getResults();
                }
                nowListApiStatus = ApiStatus.SUCCESS;
                updateData();
            }

            @Override
            public void onFailure(Call<ListNowPlayingResponse> call, Throwable t) {
                nowList = null;
                nowListApiStatus = ApiStatus.ERROR;
                updateData();
            }
        });
    }

    private void postNowPlayingData(ArrayList<NowPlayingMovie> nowList, ArrayList<NowPlayingMovie> wishList) {
        if (nowList == null || nowList.isEmpty()) {
            nowListLiveData.postValue(null);
            return;
        }

        ArrayList<NowPlayingMovie> result = new ArrayList<>();
        result.addAll(nowList);

        //Reset wish field
        for (NowPlayingMovie movie : result) {
            movie.setWish(false);
        }

        if (wishList != null && !wishList.isEmpty()) {
            for (int i = 0; i < wishList.size(); i++) {
                final int id = wishList.get(i).getId();
                //Tìm kiếm trong list nowPlayingMovies nếu trùng id thì set wish = true
                result.stream().filter(movie -> movie.getId() == id).findFirst().ifPresent(moviePresent -> moviePresent.setWish(true));
            }
        }

        nowListLiveData.postValue(result);
    }

    public void getPopularMovie(int page) {
        homeResporistory.getPopularMovie(page).enqueue(new Callback<PopularResponse>() {
            @Override
            public void onResponse(Call<PopularResponse> call, Response<PopularResponse> response) {
                if (response.isSuccessful()) {
                    popular.postValue(response.body());
                }
            }

            @Override
            public void onFailure(Call<PopularResponse> call, Throwable t) {
                popular.postValue(null);
            }
        });
    }

    public void getUpComingMovie(int page) {
        homeResporistory.getUpcomingMovie(page).enqueue(new Callback<UpComingResponse>() {
            @Override
            public void onResponse(Call<UpComingResponse> call, Response<UpComingResponse> response) {
                if (response.isSuccessful()) {
                    upComing.postValue(response.body());
                }
            }

            @Override
            public void onFailure(Call<UpComingResponse> call, Throwable t) {
                upComing.postValue(null);
            }
        });
    }

    public void getTopRatedMovie(int page) {
        homeResporistory.getTopRatedMovie(page).enqueue(new Callback<TopRatedResponse>() {
            @Override
            public void onResponse(Call<TopRatedResponse> call, Response<TopRatedResponse> response) {
                if (response.isSuccessful()) {
                    topRated.postValue(response.body());
                }
            }

            @Override
            public void onFailure(Call<TopRatedResponse> call, Throwable t) {
                topRated.postValue(null);
            }
        });
    }


}
