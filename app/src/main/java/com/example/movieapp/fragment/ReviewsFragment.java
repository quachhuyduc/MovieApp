package com.example.movieapp.fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.movieapp.R;
import com.example.movieapp.adapters.CastAdapter;
import com.example.movieapp.adapters.ReviewAdapter;
import com.example.movieapp.interfaces.OnMovieListener;
import com.example.movieapp.models.AuthorDetails;
import com.example.movieapp.models.Cast;
import com.example.movieapp.models.NowPlayingMovie;
import com.example.movieapp.models.Review;
import com.example.movieapp.object.CastResponse;
import com.example.movieapp.object.ReviewsResponse;
import com.example.movieapp.resporistories.CastResporistory;
import com.example.movieapp.resporistories.ReviewResposistory;
import com.example.movieapp.ui.CastFragmentViewModel;
import com.example.movieapp.ui.CastFragmentViewModelFactory;
import com.example.movieapp.ui.ReviewFragmentViewModel;
import com.example.movieapp.ui.ReviewFragmentViewModelFactory;
import com.example.movieapp.utils.Constants;

import java.util.List;


public class ReviewsFragment extends Fragment {


    private List<Review> reviewsData;


    private ReviewFragmentViewModel reviewFragmentViewModel;

    private ReviewAdapter reviewAdapter;

    public ReviewsFragment() {

    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_reviews, container, false);
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView(view);
        initViewModel();
    }

    private void initViewModel() {
        ReviewResposistory reviewResposistory = new ReviewResposistory();
        ReviewFragmentViewModelFactory factory = new ReviewFragmentViewModelFactory(getActivity().getApplication(),reviewResposistory);
        reviewFragmentViewModel = new ViewModelProvider(this,factory).get(ReviewFragmentViewModel.class);


        Intent intent = getActivity().getIntent();
        int movieID = intent.getExtras().getInt(Constants.MOVIE_ID_KEY,-1);
        if(movieID != -1){
            reviewFragmentViewModel.getListReview(movieID);
        }
        else {
            getActivity().finish();
        }

        reviewFragmentViewModel.reviewsResponse.observe(getViewLifecycleOwner(), new Observer<ReviewsResponse>() {
            @Override
            public void onChanged(ReviewsResponse reviewsResponse) {
                if(reviewsResponse != null){
                    reviewsData = reviewsResponse.getResults();

                    reviewAdapter.setDataReview(reviewsData);
                }
            }
        });
    }


    private void initView(View view) {

        RecyclerView recyclerView = view.findViewById(R.id.rcv_Reviews);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(),LinearLayoutManager.VERTICAL,false);
        recyclerView.setLayoutManager(linearLayoutManager);

        reviewAdapter = new ReviewAdapter(getActivity().getBaseContext());

        recyclerView.setAdapter(reviewAdapter);


    }
}