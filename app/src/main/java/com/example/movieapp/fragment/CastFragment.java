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

import com.example.movieapp.activity.DetailCastActivity;
import com.example.movieapp.R;
import com.example.movieapp.adapters.CastAdapter;
import com.example.movieapp.interfaces.OnMovieListener;
import com.example.movieapp.models.Cast;
import com.example.movieapp.models.NowPlayingMovie;
import com.example.movieapp.object.CastResponse;

import com.example.movieapp.resporistories.CastResporistory;
import com.example.movieapp.ui.CastFragmentViewModel;
import com.example.movieapp.ui.CastFragmentViewModelFactory;
import com.example.movieapp.utils.Constants;

import java.util.List;


public class CastFragment extends Fragment {

    private static final String TAG = "CastFragment";

    private List<Cast> castData;

    private CastFragmentViewModel castFragmentViewModel;

    private CastAdapter castAdapter;

    public CastFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_cast, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView(view);
        initViewModel();
    }

    private void initViewModel() {
        CastResporistory castResporistory = new CastResporistory();
        CastFragmentViewModelFactory factory = new CastFragmentViewModelFactory(getActivity().getApplication(),castResporistory);
       castFragmentViewModel = new ViewModelProvider(this,factory).get(CastFragmentViewModel.class);


        Intent intent = getActivity().getIntent();
        int movieID = intent.getExtras().getInt(Constants.MOVIE_ID_KEY,-1);
        if(movieID != -1){
            castFragmentViewModel.getListCast(movieID);
        }
        else {
            getActivity().finish();
        }

       castFragmentViewModel.castResponse.observe(getViewLifecycleOwner(), new Observer<CastResponse>() {
           @Override
           public void onChanged(CastResponse castResponse) {
               if(castResponse != null){
                   castData = castResponse.getCast();
                   castAdapter.setDataCast(castData);
               }
           }
       });
    }


    private void initView(View view) {

        RecyclerView recyclerView = view.findViewById(R.id.rcv_cast);
        recyclerView.setHasFixedSize(true);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 2);
        recyclerView.setLayoutManager(gridLayoutManager);

         castAdapter = new CastAdapter(getActivity().getBaseContext(), new OnMovieListener() {
             @Override
             public void onMovieClick(int positon) {
                 onClickGoToDetailCast(positon);

             }

             @Override
             public void onCastClick(int positon) {
                 onClickGoToDetailCast(positon);
             }

             @Override
             public void onSaveClick(NowPlayingMovie nowPlayingMovie) {

             }

             @Override
             public void onChangeWishList(int position) {
             }
         });

        recyclerView.setAdapter(castAdapter);


    }

    private void onClickGoToDetailCast(int positon) {
        int personId = castData.get(positon).getId();
        Intent intent = new Intent(getActivity(), DetailCastActivity.class);
        Bundle bundle = new Bundle();
        bundle.putInt(Constants.PERSON_ID_KEY,personId);
        intent.putExtras(bundle);
        startActivity(intent);
    }

}


