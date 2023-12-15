package com.example.movieapp.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.movieapp.R;
import com.example.movieapp.adapters.MovieAdapter2;
import com.example.movieapp.interfaces.OnMovieListener;
import com.example.movieapp.models.NowPlayingMovie;
import com.example.movieapp.utils.SharedPreferencesUtil;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


public class WatchListFragment extends Fragment {

    private static final String TAG = "WatchListFragment";

    private DatabaseReference watchlistRef;
    private List<NowPlayingMovie> listDataNow;
    private RecyclerView rcv_wishList;
    private MovieAdapter2 movieAdapter2;



    public WatchListFragment() {
        // Constructor rỗng
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        listDataNow = new ArrayList<>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_watch_list, container, false);
        rcv_wishList = view.findViewById(R.id.rcv_wishList);
        rcv_wishList.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        rcv_wishList.setLayoutManager(linearLayoutManager);

        movieAdapter2 = new MovieAdapter2(getContext(), new OnMovieListener() {
            @Override
            public void onMovieClick(int positon) {

            }

            @Override
            public void onCastClick(int positon) {

            }

            @Override
            public void onSaveClick(NowPlayingMovie nowPlayingMovie) {
                saveToWatchList(nowPlayingMovie);
            }

            @Override
            public void onChangeWishList(int position) {

                NowPlayingMovie selectedMovie = listDataNow.get(position);
                selectedMovie.setWish(!selectedMovie.isWish());
                movieAdapter2.notifyItemChanged(position);

                boolean newStatus = selectedMovie.isWish();
                SharedPreferencesUtil.setWishListStatus(requireContext(), selectedMovie.getId(), newStatus);
            }

        });




        // Khởi tạo database reference
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            String currentUserid = user.getUid();
            watchlistRef = FirebaseDatabase.getInstance().getReference("watchlist").child(currentUserid);

            // Thêm ValueEventListener để lắng nghe sự thay đổi trong danh sách watchlist
            watchlistRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    listDataNow.clear(); // Xóa danh sách hiện tại để tránh việc trùng lặp
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        NowPlayingMovie nowPlayingMovie = dataSnapshot.getValue(NowPlayingMovie.class);
                        if (nowPlayingMovie != null) {
                            listDataNow.add(nowPlayingMovie);
                        }
                    }
                    // Cập nhật dữ liệu mới vào adapter và thông báo adapter cập nhật RecyclerView
                    movieAdapter2.setData2(listDataNow);
                    movieAdapter2.notifyDataSetChanged();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.e(TAG, "onCancelled: DatabaseError", error.toException());
                }
            });
        }
        rcv_wishList.setAdapter(movieAdapter2);

        return view;
    }

    private void saveToWatchList(NowPlayingMovie nowPlayingMovie) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            // Nếu đã đăng nhập, lấy UID của người dùng
            String userId = user.getUid();

            // Tạo một child node mới với khóa duy nhất
            String movieKey = watchlistRef.push().getKey();

            // Lưu thông tin của bộ phim vào child node mới
            watchlistRef.child(movieKey).setValue(nowPlayingMovie);

            Log.d(TAG, "Bộ phim đã được lưu vào watchlist");
        } else {
            Log.d(TAG, "Người dùng chưa đăng nhập");
        }
    }
}
