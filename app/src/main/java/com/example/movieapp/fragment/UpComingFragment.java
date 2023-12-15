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
import com.example.movieapp.interfaces.OnMovieListener;
import com.example.movieapp.adapters.UpComingAdapter;
import com.example.movieapp.models.NowPlayingMovie;
import com.example.movieapp.object.UpComingResponse;
import com.example.movieapp.resporistories.HomeResporistory;
import com.example.movieapp.ui.HomeFragmentViewModel;
import com.example.movieapp.ui.HomeFragmentViewModelFactory;
import com.example.movieapp.utils.Constants;
import com.google.firebase.database.DatabaseReference;

import java.util.List;


public class UpComingFragment extends Fragment {

    private List<NowPlayingMovie> listDataUpComing;
    private HomeFragmentViewModel homeFragmentViewModelNow;
    private UpComingAdapter upComingAdapter;

    private DatabaseReference watchlistRef;



    public UpComingFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_up_coming, container, false);
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
        homeFragmentViewModelNow.getUpComingMovie(2);
        homeFragmentViewModelNow.upComing.observe(getViewLifecycleOwner(), new Observer<UpComingResponse>() {
            @Override
            public void onChanged(UpComingResponse upComingResponse) {
                if (upComingResponse != null) {
                    listDataUpComing = upComingResponse.getResults();
                    upComingAdapter.setDataUpComing(listDataUpComing);
                }
            }
        });
    }

    private void initView(View view) {
        RecyclerView recyclerView = view.findViewById(R.id.rcv_upComing);
        recyclerView.setHasFixedSize(true);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 2);
        recyclerView.setLayoutManager(gridLayoutManager);


        upComingAdapter = new UpComingAdapter(getActivity().getBaseContext(), new OnMovieListener() {
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
        recyclerView.setAdapter(upComingAdapter);
    }
    private void onClickGoToDetail(int positon) {
        int movieId = listDataUpComing.get(positon).getId();
        Intent intent = new Intent(getActivity(), DetailActivity.class);
        Bundle bundle = new Bundle();
        bundle.putInt(Constants.MOVIE_ID_KEY,movieId);
        intent.putExtras(bundle);
        startActivity(intent);
    }
}