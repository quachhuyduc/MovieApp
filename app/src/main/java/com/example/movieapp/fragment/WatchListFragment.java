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

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.movieapp.R;
import com.example.movieapp.adapters.MovieAdapter2;
import com.example.movieapp.adapters.OnMovieListener;
import com.example.movieapp.adapters.SearchMovieAdapter;
import com.example.movieapp.models.NowPlayingMovie;
import com.example.movieapp.models.Result;
import com.example.movieapp.object.CastResponse;
import com.example.movieapp.object.ListNowPlayingResponse;
import com.example.movieapp.object.MovieSearchResponse;
import com.example.movieapp.resporistories.CastResporistory;
import com.example.movieapp.resporistories.HomeResporistory;
import com.example.movieapp.resporistories.SearchResporistory;
import com.example.movieapp.ui.CastFragmentViewModel;
import com.example.movieapp.ui.CastFragmentViewModelFactory;
import com.example.movieapp.ui.HomeFragmentViewModel;
import com.example.movieapp.ui.HomeFragmentViewModelFactory;
import com.example.movieapp.ui.SearchFragmentViewModel;
import com.example.movieapp.ui.SearchFragmentViewModelFactory;
import com.example.movieapp.utils.Constants;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;


public class WatchListFragment extends Fragment {
    private static final String TAG = "WatchListFragment";

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference reference;

    private List<NowPlayingMovie> listDataNow;

    private HomeFragmentViewModel homeFragmentViewModelNow;


   private RecyclerView rcv_wishList;

   private MovieAdapter2 movieAdapter2;

    public WatchListFragment() {

    }




    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Log.d(TAG, "onCreateView: ");
        return inflater.inflate(R.layout.fragment_watch_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initsearch(view);





    }



    private void initsearch(View view) {
        rcv_wishList = view.findViewById(R.id.rcv_wishList);
        rcv_wishList.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager =new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL,false);
        rcv_wishList.setLayoutManager(linearLayoutManager);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String currentUserid = user.getUid();

        reference = database.getReference("favouritesList").child(currentUserid);


        movieAdapter2 = new MovieAdapter2(getActivity().getBaseContext(), new OnMovieListener() {
            @Override
            public void onMovieClick(int positon) {

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
        rcv_wishList.setAdapter(movieAdapter2);

    }
}