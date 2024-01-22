package com.example.movieapp.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.movieapp.R;
import com.example.movieapp.activity.DetailActivity;
import com.example.movieapp.adapters.MovieAdapter2;
import com.example.movieapp.interfaces.OnMovieListener;
import com.example.movieapp.models.NowPlayingMovie;
import com.example.movieapp.resporistories.HomeResporistory;
import com.example.movieapp.ui.HomeFragmentViewModel;
import com.example.movieapp.ui.HomeFragmentViewModelFactory;
import com.example.movieapp.utils.Constants;
import com.example.movieapp.utils.FilterEvent;
import com.google.firebase.database.DatabaseReference;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;


public class NowPlayingFragment extends Fragment {

    private ArrayList<NowPlayingMovie> listDataNow;
    private HomeFragmentViewModel homeFragmentViewModelNow;
    private MovieAdapter2 mMovieAdapterNow;

    private DatabaseReference watchlistRef;
    private OnMovieListener onMovieListener = new OnMovieListener() {
        @Override
        public void onMovieClick(int positon) {
            onClickGoToDetail(positon);
        }

        @Override
        public void onCastClick(int positon) {

        }

        @Override
        public void onSaveClick(NowPlayingMovie nowPlayingMovie) {

        }


        @Override
        public void onChangeWishList(int position, NowPlayingMovie movie) {
            homeFragmentViewModelNow.updateMovieWishList(position, movie);
        }
    };

    public NowPlayingFragment() {
        listDataNow = new ArrayList<>();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EventBus.getDefault().register(this);

    }
    @Override
    public void onDestroy() {
        super.onDestroy();

        // Hủy đăng ký EventBus trong phương thức onDestroy để tránh rò rỉ bộ nhớ
        EventBus.getDefault().unregister(this);
    }

    // Xử lý sự kiện lọc từ HomeFragment
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onFilterEvent(FilterEvent event) {
        int genreId = event.getGenreId();

        filterMoviesByGenre(genreId);
        // Thực hiện việc lọc bộ phim dựa trên genreId
        // Cập nhật danh sách bộ phim trong RecyclerView hoặc làm điều gì đó khác tùy thuộc vào yêu cầu của bạn
    }

    private void filterMoviesByGenre(int genreId) {
        if (listDataNow != null) {
            List<NowPlayingMovie> filteredMovies = new ArrayList<>();

            for (NowPlayingMovie movie : listDataNow) {
                if (movie.getGenreIds() != null && movie.getGenreIds().contains(genreId)) {
                    filteredMovies.add(movie);
                }
            }

            // Cập nhật dữ liệu trong Adapter
            mMovieAdapterNow.setData2(filteredMovies);

            // Thông báo rằng dữ liệu đã thay đổi
            mMovieAdapterNow.notifyDataSetChanged();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_now_playing, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initViewModel();
        initView(view);
    }

    private void initView(View view) {
        RecyclerView recyclerView = view.findViewById(R.id.recyclerViewNowPlaying);
        recyclerView.setHasFixedSize(true);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 2);
        recyclerView.setLayoutManager(gridLayoutManager);

        mMovieAdapterNow = new MovieAdapter2(getActivity(), onMovieListener);
        recyclerView.setAdapter(mMovieAdapterNow);

    }

    private void initViewModel() {
        HomeResporistory homeResporistoryNow = new HomeResporistory();
        HomeFragmentViewModelFactory homeFragmentViewModelFactoryNow = new HomeFragmentViewModelFactory(getActivity().getApplication(), homeResporistoryNow);
        homeFragmentViewModelNow = new ViewModelProvider(this, homeFragmentViewModelFactoryNow).get(HomeFragmentViewModel.class);
        homeFragmentViewModelNow.getListNowPlaying(2);


        homeFragmentViewModelNow.nowListLiveData.observe(getViewLifecycleOwner(), nowPlayingMovies -> {
            if (nowPlayingMovies != null) {
                listDataNow = nowPlayingMovies;
                mMovieAdapterNow.setData2(nowPlayingMovies);
            }
        });
    }

    private void onClickGoToDetail(int positon) {
        if (positon >= 0 && positon < listDataNow.size()) {
            int movieId = listDataNow.get(positon).getId();
            Intent intent = new Intent(getActivity(), DetailActivity.class);
            Bundle bundle = new Bundle();
            bundle.putInt(Constants.MOVIE_ID_KEY, movieId);
            intent.putExtras(bundle);
            startActivity(intent);
        }
    }

}


