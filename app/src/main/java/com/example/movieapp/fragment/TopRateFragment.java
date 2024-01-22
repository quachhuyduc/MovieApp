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

import com.example.movieapp.R;
import com.example.movieapp.activity.DetailActivity;
import com.example.movieapp.adapters.TopRatedAdapter;

import com.example.movieapp.interfaces.OnMovieListener;
import com.example.movieapp.models.NowPlayingMovie;
import com.example.movieapp.object.TopRatedResponse;
import com.example.movieapp.object.UpComingResponse;
import com.example.movieapp.resporistories.HomeResporistory;
import com.example.movieapp.ui.HomeFragmentViewModel;
import com.example.movieapp.ui.HomeFragmentViewModelFactory;
import com.example.movieapp.utils.Constants;
import com.google.firebase.database.DatabaseReference;

import java.util.List;

public class TopRateFragment extends Fragment {
    private List<NowPlayingMovie> listDataTopRated;
    private HomeFragmentViewModel homeFragmentViewModelNow;
    private TopRatedAdapter topRatedAdapter;

    private DatabaseReference watchlistRef;


    public TopRateFragment() {
        // Required empty public constructor
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_top_rate, container, false);
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initViewModel();
        initView(view);

    }

    private void initViewModel() {
        HomeResporistory homeResporistoryNow = new HomeResporistory();
        HomeFragmentViewModelFactory homeFragmentViewModelFactoryNow = new HomeFragmentViewModelFactory(getActivity().getApplication(), homeResporistoryNow);
        homeFragmentViewModelNow = new ViewModelProvider(this, homeFragmentViewModelFactoryNow).get(HomeFragmentViewModel.class);
        homeFragmentViewModelNow.getTopRatedMovie(2);
        homeFragmentViewModelNow.topRated.observe(getViewLifecycleOwner(), new Observer<TopRatedResponse>() {
            @Override
            public void onChanged(TopRatedResponse topRatedResponse) {
                if (topRatedResponse != null) {
                    listDataTopRated = topRatedResponse.getResults();
                    topRatedAdapter.setDataTopRated(listDataTopRated);
                }
            }
        });
    }

    private void initView(View view) {
        RecyclerView recyclerView = view.findViewById(R.id.rcv_TopRated);
        recyclerView.setHasFixedSize(true);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 2);
        recyclerView.setLayoutManager(gridLayoutManager);


        topRatedAdapter = new TopRatedAdapter(getActivity().getBaseContext(), new OnMovieListener() {
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

            }
        });
        recyclerView.setAdapter(topRatedAdapter);
    }
    private void onClickGoToDetail(int positon) {
        int movieId = listDataTopRated.get(positon).getId();
        Intent intent = new Intent(getActivity(), DetailActivity.class);
        Bundle bundle = new Bundle();
        bundle.putInt(Constants.MOVIE_ID_KEY,movieId);
        intent.putExtras(bundle);
        startActivity(intent);
    }
}