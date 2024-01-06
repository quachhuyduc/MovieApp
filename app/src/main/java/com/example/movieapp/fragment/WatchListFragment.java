package com.example.movieapp.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.movieapp.R;
import com.example.movieapp.adapters.MovieAdapter2;
import com.example.movieapp.databinding.MovieListItem2Binding;
import com.example.movieapp.models.NowPlayingMovie;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class WatchListFragment extends Fragment {

    private DatabaseReference watchlistRef;
    private RecyclerView rcv_wishList;
    private FirebaseRecyclerAdapter<NowPlayingMovie, MovieAdapter2.ViewHolder> movieAdapter;

    public WatchListFragment() {
        // Constructor rỗng
    }

   // <!-- TODO: Item chưa hiển thị được trong watchListFragment-->

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_watch_list, container, false);
        rcv_wishList = view.findViewById(R.id.rcv_wishList);
        rcv_wishList.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        rcv_wishList.setLayoutManager(linearLayoutManager);

        // Lấy UID của người dùng hiện tại
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            String userId = user.getUid();

            // Khởi tạo database reference cho watchlist của người dùng
            watchlistRef = FirebaseDatabase.getInstance().getReference("favouritesList").child(userId);

            Query query = FirebaseDatabase.getInstance().getReference().child("favouritesList").child(userId).limitToFirst(50);

            // Thêm ValueEventListener để lắng nghe sự thay đổi trong danh sách watchlist
            FirebaseRecyclerOptions<NowPlayingMovie> options = new FirebaseRecyclerOptions.Builder<NowPlayingMovie>()
                    .setQuery(query, NowPlayingMovie.class)
                    .build();

            movieAdapter = new FirebaseRecyclerAdapter<NowPlayingMovie, MovieAdapter2.ViewHolder>(options) {
                @Override
                protected void onBindViewHolder(@NonNull MovieAdapter2.ViewHolder holder, int position, @NonNull NowPlayingMovie model) {
                    Log.d("TAG", "onBindViewHolder: ");
                    // Bind data to ViewHolder
                    holder.bindData(model);

                    // Bind other data...

                    // Handle item click or other actions...
                }

                @NonNull
                @Override
                public MovieAdapter2.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                    LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
                    MovieListItem2Binding movieListItem2Binding = DataBindingUtil.inflate(layoutInflater, R.layout.movie_list_item2, parent, false);
                    return new MovieAdapter2.ViewHolder(movieListItem2Binding);
                }
            };

            rcv_wishList.setAdapter(movieAdapter);
        }

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (movieAdapter != null) {
            movieAdapter.startListening();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (movieAdapter != null) {
            movieAdapter.stopListening();
        }
    }
}
