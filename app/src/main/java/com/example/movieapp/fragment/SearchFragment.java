 package com.example.movieapp.fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.movieapp.activity.DetailActivity;
import com.example.movieapp.R;
import com.example.movieapp.activity.DetailCastActivity;
import com.example.movieapp.adapters.SearchPersonAdapter;
import com.example.movieapp.api.MovieApi;
import com.example.movieapp.api.RetrofitClient;
import com.example.movieapp.interfaces.OnMovieListener;
import com.example.movieapp.adapters.SearchMovieAdapter;
import com.example.movieapp.models.Cast;
import com.example.movieapp.models.Genre;
import com.example.movieapp.models.NowPlayingMovie;
import com.example.movieapp.models.Result;
import com.example.movieapp.models.ResultPersonSearch;
import com.example.movieapp.object.GenresMovie;
import com.example.movieapp.object.MovieSearchResponse;
import com.example.movieapp.object.SearchPersonResponse;
import com.example.movieapp.resporistories.SearchResporistory;
import com.example.movieapp.ui.HomeFragmentViewModel;
import com.example.movieapp.ui.SearchFragmentViewModel;
import com.example.movieapp.ui.SearchFragmentViewModelFactory;
import com.example.movieapp.utils.Constants;
import com.example.movieapp.utils.MessageEvent;
import com.example.movieapp.utils.Service;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class SearchFragment extends Fragment {

    private Spinner sourceSpinner;
    private String selectedSource = "movie"; // Chọn mặc định

    private List<Result> listData2;
    private List<ResultPersonSearch> listDataPerson;

    private SearchMovieAdapter searchMovieAdapter;
    private SearchPersonAdapter searchPersonAdapter;
;
    private SearchFragmentViewModel searchFragmentViewModel;
    private SearchFragmentViewModel personViewModel;

    private MessageEvent event;

    private RecyclerView recyclerView ;

    private Button btn_action;



    private MovieApi movieApi = RetrofitClient.getMovieApi();


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
        SearchResporistory personSearchRepository = new SearchResporistory();
        SearchFragmentViewModelFactory personFactory = new SearchFragmentViewModelFactory(getActivity().getApplication(), personSearchRepository);
        personViewModel = new ViewModelProvider(this, personFactory).get(SearchFragmentViewModel.class);

        personViewModel.personSearch.observe(getViewLifecycleOwner(), new Observer<SearchPersonResponse>() {
            @Override
            public void onChanged(SearchPersonResponse searchPersonResponse) {
                if (searchPersonResponse != null) {
                    listDataPerson = searchPersonResponse.getResults();
                    if (searchPersonAdapter != null) {
                        searchPersonAdapter.setDataPersonSearch(listDataPerson);
                    }
                } else {
                    Log.e("SearchFragment", "searchPersonResponse is null");
                    // Nếu bạn muốn xử lý trường hợp searchPersonResponse là null, bạn có thể thêm xử lý ở đây
                }
            }


        });
    }

    private void initsearch(View view) {
        sourceSpinner = view.findViewById(R.id.sourceSpinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(requireContext(), R.array.search_sources, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sourceSpinner.setAdapter(adapter);

        sourceSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                // Cập nhật nguồn đã chọn khi mục Spinner được chọn
                selectedSource = position == 0 ? "movie" : "actor";
                initRecyclerView(view);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // Không làm gì ở đây
            }
        });
        EditText editText = view.findViewById(R.id.edtSearch);

        editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH
                        || actionId == EditorInfo.IME_ACTION_DONE
                        || event.getAction() == KeyEvent.ACTION_DOWN
                        && event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                    if (editText.getText() != null) {
                        String query = editText.getText().toString();
                        if (TextUtils.isEmpty(query)) {
                            Toast.makeText(getActivity(), "Please enter any text...", Toast.LENGTH_SHORT).show();
                        } else {
                            editText.setText("");
                            //get the category to search the query
                            String finalQuery = query.trim();
                            SearchFragmentViewModel selectedViewModel = "movie".equals(selectedSource) ? searchFragmentViewModel : personViewModel;
                            onSearchAction(selectedViewModel, finalQuery, selectedSource);

                            Service.hideKeyboardFrom(getContext(), view);
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
                if (editText.getText() != null) {
                    String query = editText.getText().toString();
                    if (TextUtils.isEmpty(query)) {
                        Toast.makeText(getActivity(), "Please enter any text...", Toast.LENGTH_SHORT).show();
                    } else {
                        editText.setText("");
                        // Lựa chọn ViewModel tương ứng dựa trên nguồn đã chọn
                        SearchFragmentViewModel selectedViewModel = "movie".equals(selectedSource) ? searchFragmentViewModel : personViewModel;
                        onSearchAction(selectedViewModel, query, selectedSource);
                        Service.hideKeyboardFrom(getContext(), view);
                    }
                }
            }
        });

        btn_action = view.findViewById(R.id.btn_action);
        btn_action.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                filterMoviesByGenre(28);
            }
        });





    }

    // Trong lớp SearchFragment
    private void filterMoviesByGenre(int genreId) {

        if (listData2 != null) {
            List<Result> filteredMovies = new ArrayList<>();

            for (Result movie : listData2) {
                if (movie.getGenreIds() != null && movie.getGenreIds().contains(genreId)) {
                    filteredMovies.add(movie);
                }
            }

            // Cập nhật dữ liệu trong Adapter
            searchMovieAdapter.setDataSearch(filteredMovies);
            recyclerView.setAdapter(searchMovieAdapter);

            // Thông báo rằng dữ liệu đã thay đổi
            searchMovieAdapter.notifyDataSetChanged();
        }
    }


    private void initRecyclerView(View view) {
       recyclerView = view.findViewById(R.id.rcv_search);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));

        // Xóa dữ liệu cũ khỏi Adapter (nếu có)
        if (searchMovieAdapter != null) {
            searchMovieAdapter.setDataSearch(null);
        }
        if (searchPersonAdapter != null) {
            searchPersonAdapter.setDataPersonSearch(null);
        }

        // Khởi tạo Adapter nếu cần thiết
        if ("movie".equals(selectedSource)) {
            if (searchMovieAdapter == null) {
                searchMovieAdapter = new SearchMovieAdapter(getActivity().getBaseContext(), new OnMovieListener() {
                    @Override
                    public void onMovieClick(int position) {
                        onClickGotoDetail(position);
                    }

                    @Override
                    public void onCastClick(int position) {
                        // Không cần thực hiện gì ở đây vì là tìm kiếm theo phim
                    }

                    @Override
                    public void onSaveClick(NowPlayingMovie nowPlayingMovie) {
                        // Có thể thực hiện thêm xử lý khi người dùng bấm vào nút "Save"
                    }

                    @Override
                    public void onChangeWishList(int position) {
                        // Có thể thực hiện thêm xử lý khi danh sách mong muốn thay đổi
                    }
                });
            }
            recyclerView.setAdapter(searchMovieAdapter);
        } else if ("actor".equals(selectedSource)) {
            if (searchPersonAdapter == null) {
                searchPersonAdapter = new SearchPersonAdapter(getActivity().getBaseContext(), new OnMovieListener() {
                    @Override
                    public void onMovieClick(int position) {
                        //   onClickGotoDetailCast(position);// Không cần thực hiện gì ở đây vì là tìm kiếm theo diễn viên
                    }

                    @Override
                    public void onCastClick(int position) {
                        onClickGotoDetailCast(position);
                    }

                    @Override
                    public void onSaveClick(NowPlayingMovie nowPlayingMovie) {
                        // Có thể thực hiện thêm xử lý khi người dùng bấm vào nút "Save"
                    }

                    @Override
                    public void onChangeWishList(int position) {
                        // Có thể thực hiện thêm xử lý khi danh sách mong muốn thay đổi
                    }
                });
            }
            recyclerView.setAdapter(searchPersonAdapter);
        }
    }


    private void onSearchAction(SearchFragmentViewModel viewModel,String query, String source) {
        if ("movie".equals(source)) {
            viewModel.getListMovieSearch(query);
        } else if ("actor".equals(source)) {
            viewModel.getListPersonSearch(query);
        }

        Call<GenresMovie> call = movieApi.getGenresMovie();
        call.enqueue(new Callback<GenresMovie>() {
            @Override
            public void onResponse(Call<GenresMovie> call, Response<GenresMovie> response) {
                if (response.isSuccessful()) {
                    List<Genre> genresList = response.body().getGenres();

                    // Cập nhật danh sách thể loại trong adapter
                    searchMovieAdapter.setGenresList(genresList);

                    updateMovieData(query);

                    // Tiếp theo, cập nhật dữ liệu phim và thông báo thay đổi
                    // searchMovieAdapter.setDataSearch(newResultList);
                } else {
                    Toast.makeText(getActivity(), "Network error", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<GenresMovie> call, Throwable t) {
                // Xử lý lỗi
            }
        });


    }

    private void updateMovieData(String query) {
        searchFragmentViewModel.getListMovieSearch(query);
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
    private void onClickGotoDetailCast(int positon) {
        if (listDataPerson != null && positon >= 0 && positon < listDataPerson.size()) {
            int personId = listDataPerson.get(positon).getId();
            Intent intent = new Intent(getActivity().getBaseContext(), DetailCastActivity.class);
            Bundle bundle = new Bundle();
            bundle.putInt(Constants.PERSON_ID_KEY, personId);
            intent.putExtras(bundle);
            startActivity(intent);
        } else {
            // Xử lý trường hợp listDataPerson không được khởi tạo hoặc vị trí không hợp lệ
            Log.e("SearchFragment", "onClickGotoDetailCast: Invalid position or listDataPerson not initialized");
            // Hiển thị thông báo hoặc thực hiện các xử lý khác tùy thuộc vào yêu cầu của bạn
        }
    }







} 