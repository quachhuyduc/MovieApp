package com.example.movieapp.fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

// import com.example.movieapp.Pagination;
import com.example.movieapp.activity.DetailActivity;
import com.example.movieapp.R;
import com.example.movieapp.adapters.MovieAdapter2;
import com.example.movieapp.adapters.OnMovieListener;
import com.example.movieapp.models.NowPlayingMovie;
import com.example.movieapp.models.Result;
import com.example.movieapp.object.ListNowPlayingResponse;
import com.example.movieapp.resporistories.HomeResporistory;
import com.example.movieapp.ui.HomeFragmentViewModel;
import com.example.movieapp.ui.HomeFragmentViewModelFactory;
import com.example.movieapp.utils.Constants;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;


public class NowPlayingFragment extends Fragment {

    private List<NowPlayingMovie> listDataNow;
    private HomeFragmentViewModel homeFragmentViewModelNow;
    private MovieAdapter2 mMovieAdapterNow;

    public NowPlayingFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

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

        recyclerView.setAdapter(mMovieAdapterNow);







        mMovieAdapterNow = new MovieAdapter2(getActivity().getBaseContext(), new OnMovieListener() {
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
            public void onChangeWishList(int position) {

            }
        });
        recyclerView.setAdapter(mMovieAdapterNow);

    }



    private void initViewModel() {
        HomeResporistory homeResporistoryNow = new HomeResporistory();
        HomeFragmentViewModelFactory homeFragmentViewModelFactoryNow = new HomeFragmentViewModelFactory(getActivity().getApplication(), homeResporistoryNow);
        homeFragmentViewModelNow = new ViewModelProvider(this, homeFragmentViewModelFactoryNow).get(HomeFragmentViewModel.class);
        homeFragmentViewModelNow.getListNowPlaying(2);
        homeFragmentViewModelNow.nowPlaying.observe(getViewLifecycleOwner(), new Observer<ListNowPlayingResponse>() {
            @Override
            public void onChanged(ListNowPlayingResponse listNowPlayingResponse) {
                if (listNowPlayingResponse != null) {
                    listDataNow = listNowPlayingResponse.getResults();
                    mMovieAdapterNow.setData2(listDataNow);
                }
            }
        });
    }

    private void onClickGoToDetail(int positon) {
        int movieId = listDataNow.get(positon).getId();
        Intent intent = new Intent(getActivity(), DetailActivity.class);
        Bundle bundle = new Bundle();
        bundle.putInt(Constants.MOVIE_ID_KEY,movieId);
        intent.putExtras(bundle);
        startActivity(intent);
    }


}


