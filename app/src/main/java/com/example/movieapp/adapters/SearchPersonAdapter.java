package com.example.movieapp.adapters;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.movieapp.R;
import com.example.movieapp.api.MovieApi;
import com.example.movieapp.interfaces.OnMovieListener;
import com.example.movieapp.models.Genre;
import com.example.movieapp.models.Result;
import com.example.movieapp.models.ResultPersonSearch;
import com.example.movieapp.ui.SearchFragmentViewModel;

import java.util.ArrayList;
import java.util.List;

public class SearchPersonAdapter extends RecyclerView.Adapter<SearchPersonAdapter.ViewHolder> {
    private List<ResultPersonSearch> mResultPerson;



    private OnMovieListener onMovieListener;

    private Context context;




    public SearchPersonAdapter(Context context, OnMovieListener listener) {
        this.context = context;
        this.onMovieListener = listener;


    }


    public void setDataPersonSearch(List<ResultPersonSearch> mResultPerson) {

        if (mResultPerson != null) {
            this.mResultPerson = mResultPerson;
            Log.d("TAG", "setDataPersonSearch: " + mResultPerson);
            notifyDataSetChanged();
        }
    }


    @NonNull
    @Override
    public SearchPersonAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_search_person, parent, false);
        return new SearchPersonAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SearchPersonAdapter.ViewHolder holder, int position) {

        ResultPersonSearch mResultMoviePerson = mResultPerson.get(position);

        if (mResultMoviePerson != null) {
            // Sử dụng các trường của mResultMoviePerson mà không lo lắng về NullPointerException
            Glide.with(holder.itemView.getContext())
                    .load("https://image.tmdb.org/t/p/w500/" + mResultMoviePerson.getProfilePath())
                    .into(holder.img_search_person);

            holder.tv_Search_personName.setText(mResultMoviePerson.getName());
            holder.textView_known_for_department.setText(mResultMoviePerson.getKnownForDepartment());
            holder.textView_popularity.setText(String.valueOf(mResultMoviePerson.getPopularity()));
        } else {
            // Xử lý trường hợp mResultMoviePerson là null (nếu cần thiết)
        }


        holder.img_search_person.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onMovieListener.onCastClick(holder.getAdapterPosition());
            }
        });


    }


    @Override
    public int getItemCount() {
        return (mResultPerson != null && mResultPerson.size() > 0) ? mResultPerson.size() : 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView textView_known_for_department, textView_popularity,tv_Search_personName;

        ImageView img_search_person;

        RecyclerView recyclerView;

        EditText editText;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            recyclerView = itemView.findViewById(R.id.rcv_search);
            img_search_person = itemView.findViewById(R.id.img_search_person);

            editText = itemView.findViewById(R.id.edtSearch);

            textView_known_for_department = itemView.findViewById(R.id.textView_known_for_department);
            textView_popularity = itemView.findViewById(R.id.textView_popularity);
            tv_Search_personName = itemView.findViewById(R.id.tv_Search_personName);

        }
    }
}
