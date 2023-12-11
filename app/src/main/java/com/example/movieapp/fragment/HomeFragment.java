package com.example.movieapp.fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;


import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import com.example.movieapp.activity.DetailActivity;
import com.example.movieapp.R;

import com.example.movieapp.adapters.OnMovieListener;
import com.example.movieapp.adapters.PagerAdapter;
import com.example.movieapp.adapters.SearchMovieAdapter;
import com.example.movieapp.models.NowPlayingMovie;
import com.example.movieapp.models.Result;
import com.example.movieapp.models.UserInfo;
import com.example.movieapp.object.ListNowPlayingResponse;
import com.example.movieapp.adapters.MovieAdapter;
import com.example.movieapp.resporistories.HomeResporistory;
import com.example.movieapp.ui.HomeFragmentViewModel;
import com.example.movieapp.ui.HomeFragmentViewModelFactory;

import com.example.movieapp.ui.SearchFragmentViewModel;
import com.example.movieapp.utils.Constants;
import com.example.movieapp.utils.MessageEvent;
import com.google.android.material.tabs.TabLayout;


import org.greenrobot.eventbus.EventBus;

import java.util.List;


public class HomeFragment extends Fragment {

    private List<NowPlayingMovie> listData;

    private List<Result> listData2;

    private UserInfo userInfo;


    private HomeFragmentViewModel homeFragmentViewModel;
    private MovieAdapter mMovieAdapter;

    private SearchMovieAdapter searchMovieAdapter;

    private SearchFragmentViewModel searchFragmentViewModel;


    public HomeFragment() {

    }

    public UserInfo getUserInfo() {

        return userInfo;
    }

    public void setUserInfo(UserInfo userInfo) {
        this.userInfo = userInfo;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        Log.d("TAG", "onCreateView: home");
        return inflater.inflate(R.layout.fragment_home, container, false);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView(view);
        initsearch(view);
        initViewModel();
        addControl(view);

    }


    private void addControl(View view) {
        ViewPager pager = view.findViewById(R.id.view_pager);
        TabLayout tabLayout = view.findViewById(R.id.tab_layout);
        FragmentManager manager = getActivity().getSupportFragmentManager();
        PagerAdapter pagerAdapter = new PagerAdapter(manager);
        pager.setAdapter(pagerAdapter);
        tabLayout.setupWithViewPager(pager);
        pager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.setTabsFromPagerAdapter(pagerAdapter);
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(pager));

    }

    private void initViewModel() {

        HomeResporistory homeResporistory = new HomeResporistory();
        HomeFragmentViewModelFactory factory = new HomeFragmentViewModelFactory(getActivity().getApplication(), homeResporistory);
        homeFragmentViewModel = new ViewModelProvider(this, factory).get(HomeFragmentViewModel.class);

        homeFragmentViewModel.getListNowPlaying(1);

        homeFragmentViewModel.nowPlaying.observe(getViewLifecycleOwner(), new Observer<ListNowPlayingResponse>() {
            @Override
            public void onChanged(ListNowPlayingResponse listNowPlayingResponse) {
                //              Log.d("TAG", "onChanged: " + listNowPlayingResponse.getResults().get(0).getTitle());
                if (listNowPlayingResponse != null) {
                    listData = listNowPlayingResponse.getResults();
                    mMovieAdapter.setData(listData);
                }
            }
        });


    }

    private void initView(View view) {
        RecyclerView recyclerView = view.findViewById(R.id.recycler_nowPlaying);
        //   LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setHasFixedSize(true);
        //    recyclerView.setLayoutManager(layoutManager);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));

        mMovieAdapter = new MovieAdapter(getActivity().getBaseContext(), new OnMovieListener() {
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
        recyclerView.setAdapter(mMovieAdapter);

    }


    private void initsearch(View view) {

        EditText editText = view.findViewById(R.id.edtSearch);

        editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH || actionId == EditorInfo.IME_ACTION_DONE || event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                    if (editText.getText() != null) {

                        String query = editText.getText().toString();
                        if (TextUtils.isEmpty(query)) {
                            Toast.makeText(getActivity(), "Please enter any text...", Toast.LENGTH_SHORT).show();
                        } else {
                            editText.setText("");
                            Navigation.findNavController(view).navigate(R.id.searchFragment);
                            String finalQuery = query.trim();


                            onSearchAction(finalQuery);
                        }

                    }
                    return true;
                }
                return false;
            }
        });

    }




    private void onSearchAction(String movieName) {
        EventBus.getDefault().postSticky(new MessageEvent(movieName));
    }

    private void onClickGotoDetail(int positon) {
        int movieId = listData.get(positon).getId();
        Intent intent = new Intent(getActivity(), DetailActivity.class);
        Bundle bundle = new Bundle();
        bundle.putInt(Constants.MOVIE_ID_KEY, movieId);
        intent.putExtras(bundle);
        startActivity(intent);
    }


}