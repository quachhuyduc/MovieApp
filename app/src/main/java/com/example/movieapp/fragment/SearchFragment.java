package com.example.movieapp.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.movieapp.activity.DetailActivity;
import com.example.movieapp.R;
import com.example.movieapp.adapters.OnMovieListener;
import com.example.movieapp.adapters.SearchMovieAdapter;
import com.example.movieapp.models.NowPlayingMovie;
import com.example.movieapp.models.Result;
import com.example.movieapp.object.MovieSearchResponse;
import com.example.movieapp.resporistories.SearchResporistory;
import com.example.movieapp.ui.SearchFragmentViewModel;
import com.example.movieapp.ui.SearchFragmentViewModelFactory;
import com.example.movieapp.utils.Constants;
import com.example.movieapp.utils.MessageEvent;
import com.example.movieapp.utils.Service;
import com.google.firebase.auth.FirebaseAuth;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.HashMap;
import java.util.List;


public class SearchFragment extends Fragment {

    private List<Result> listData2;

    private SearchMovieAdapter searchMovieAdapter;

    private SearchFragmentViewModel searchFragmentViewModel;

    private MessageEvent event;






    public SearchFragment() {


    }




    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);




    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Log.d("TAG", "onCreateView: search");
        return inflater.inflate(R.layout.fragment_search, container, false);
    }




    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initsearch(view);
        initViewModel();
    //    onEvent(event);

    }

    private void initViewModel() {
        SearchResporistory searchResporistory = new SearchResporistory();
        SearchFragmentViewModelFactory factory1 = new SearchFragmentViewModelFactory(getActivity().getApplication(), searchResporistory);
        searchFragmentViewModel = new ViewModelProvider(this, factory1).get(SearchFragmentViewModel.class);


        searchFragmentViewModel.movieSearch.observe(getViewLifecycleOwner(), new Observer<MovieSearchResponse>() {
            @Override
            public void onChanged(MovieSearchResponse movieSearchResponse) {
                if (movieSearchResponse != null) {
                    listData2 = movieSearchResponse.getResults();
                    searchMovieAdapter.setDataSearch(listData2);
                }

            }


        });
    }

    private void initsearch(View view) {
        EditText editText = view.findViewById(R.id.edtSearch);

        editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH
                        || actionId == EditorInfo.IME_ACTION_DONE
                        || event.getAction() == KeyEvent.ACTION_DOWN
                        && event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                    if(editText.getText() != null) {
                        String query = editText.getText().toString();
                        if (TextUtils.isEmpty(query)) {
                            Toast.makeText(getActivity(), "Please enter any text...", Toast.LENGTH_SHORT).show();
                        } else {
                            editText.setText("");
                            //get the category to search the query
                            String finalQuery = query.trim();
                            onSearchAction(finalQuery);

                            Service.hideKeyboardFrom(getContext(),view);
                        }

                    }
                    return true;
                }
                return false;
            }
        });


        Button button = view.findViewById(R.id.btnSearch1);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(editText.getText() != null) {
                    String query = editText.getText().toString();
                    if (TextUtils.isEmpty(query)) {
                        Toast.makeText(getActivity(), "Please enter any text...", Toast.LENGTH_SHORT).show();
                    } else {
                        editText.setText("");
                        //get the category to search the query
                        String finalQuery = query.trim();
                        onSearchAction(finalQuery);
                        Service.hideKeyboardFrom(getContext(),view);
                    }

                }
            }
        });



        RecyclerView recyclerView = view.findViewById(R.id.rcv_search);
        //   LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setHasFixedSize(true);
        //    recyclerView.setLayoutManager(layoutManager);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));

        searchMovieAdapter = new SearchMovieAdapter(getActivity().getBaseContext(), new OnMovieListener() {
            @Override
            public void onMovieClick(int positon) {
                onClickGotoDetail(positon);
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
            recyclerView.setAdapter(searchMovieAdapter);
    }
    private void onSearchAction(String movieName) {
   searchFragmentViewModel.getListMovieSearch(movieName);

    }

    @Subscribe(sticky = true)
    public void onEvent(MessageEvent event) {


        searchFragmentViewModel.getListMovieSearch(event.getMessage());
    }



    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }
    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    private void onClickGotoDetail(int positon) {
        int movieId = listData2.get(positon).getId();
        Intent intent = new Intent(getActivity(), DetailActivity.class);
        Bundle bundle = new Bundle();
        bundle.putInt(Constants.MOVIE_ID_KEY, movieId);
        intent.putExtras(bundle);
        startActivity(intent);
    }




}