package com.example.movieapp.fragment;

import android.content.Intent;
import android.os.Bundle;
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

import com.example.movieapp.R;
import com.example.movieapp.activity.DetailActivity;
import com.example.movieapp.adapters.MovieAdapter;
import com.example.movieapp.adapters.PagerAdapter;
import com.example.movieapp.interfaces.OnMovieListener;
import com.example.movieapp.models.NowPlayingMovie;
import com.example.movieapp.resporistories.HomeResporistory;
import com.example.movieapp.ui.HomeFragmentViewModel;
import com.example.movieapp.ui.HomeFragmentViewModelFactory;
import com.example.movieapp.utils.Constants;
import com.example.movieapp.utils.MessageEvent;
import com.google.android.material.tabs.TabLayout;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;


public class HomeFragment extends Fragment {

    private RecyclerView recyclerView;

    private List<NowPlayingMovie> listData;

    private List<NowPlayingMovie> filteredMovies;

    private HomeFragmentViewModel homeFragmentViewModel;
    private MovieAdapter mMovieAdapter;

    private Button btn_action1, btn_animation1, btn_drama1, btn_fantasy1;


    public HomeFragment() {

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

        homeFragmentViewModel.nowListLiveData.observe(getViewLifecycleOwner(), new Observer<ArrayList<NowPlayingMovie>>() {
            @Override
            public void onChanged(ArrayList<NowPlayingMovie> nowPlayingMovies) {
                mMovieAdapter.setData(nowPlayingMovies);
                filteredMovies = new ArrayList<>(nowPlayingMovies);
            }
        });
    }

    private void initView(View view) {
        recyclerView = view.findViewById(R.id.recycler_nowPlaying);
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
            public void onChangeWishList(int position, NowPlayingMovie movie) {

            }
        });
        recyclerView.setAdapter(mMovieAdapter);

        btn_action1 = view.findViewById(R.id.btn_action1);
        btn_action1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                filterMoviesByGenre(28);
            }
        });
        btn_animation1 = view.findViewById(R.id.btn_animation1);
        btn_animation1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                filterMoviesByGenre(16);
            }
        });
        btn_drama1 = view.findViewById(R.id.btn_drama1);
        btn_drama1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                filterMoviesByGenre(18);
            }
        });
        btn_fantasy1 = view.findViewById(R.id.btn_fantasy1);
        btn_fantasy1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                filterMoviesByGenre(14);
            }
        });

    }

    private void filterMoviesByGenre(int genreId) {
        if (listData != null) {
            filteredMovies.clear();


            for (NowPlayingMovie movie : listData) {
                if (movie.getGenreIds() != null && movie.getGenreIds().contains(genreId)) {
                    filteredMovies.add(movie);
                }
            }

            // Cập nhật dữ liệu trong Adapter
            mMovieAdapter.setData(filteredMovies);
            recyclerView.setAdapter(mMovieAdapter);

            // Thông báo rằng dữ liệu đã thay đổi
            mMovieAdapter.notifyDataSetChanged();
        }
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
        if (filteredMovies != null && positon >= 0 && positon < filteredMovies.size()) {
            int movieId = filteredMovies.get(positon).getId();
            Intent intent = new Intent(getActivity(), DetailActivity.class);
            Bundle bundle = new Bundle();
            bundle.putInt(Constants.MOVIE_ID_KEY, movieId);
            intent.putExtras(bundle);
            startActivity(intent);
        } else {
            // Xử lý trường hợp filteredMovies không được khởi tạo hoặc vị trí không hợp lệ
            Log.e("HomeFragment", "onClickGotoDetail: Invalid position or filteredMovies not initialized");
            // Hiển thị thông báo hoặc thực hiện các xử lý khác tùy thuộc vào yêu cầu của bạn
        }
    }


}