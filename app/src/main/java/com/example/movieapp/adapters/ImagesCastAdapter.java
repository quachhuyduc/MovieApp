package com.example.movieapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.movieapp.R;
import com.example.movieapp.models.NowPlayingMovie;
import com.example.movieapp.models.Profile;

import java.util.List;

public class ImagesCastAdapter extends RecyclerView.Adapter<ImagesCastAdapter.ViewHolder> {

    private List<Profile> mProfiles;

    private Context context;

    public ImagesCastAdapter(Context context) {
        this.context = context;
    }

    public void setDataImages(List<Profile> mProfiles) {

        this.mProfiles = mProfiles;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ImagesCastAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.movie_images_item, parent, false);
        return new ImagesCastAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ImagesCastAdapter.ViewHolder holder, int position) {
        Glide.with(holder.itemView.getContext())
                .load("https://image.tmdb.org/t/p/w500/" + mProfiles.get(position)
                        .getFilePath())
                .into((holder).imageView);
    }

    @Override
    public int getItemCount() {
        return (mProfiles != null && mProfiles.size() > 0) ? mProfiles.size() : 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        RecyclerView recyclerView;
        ImageView imageView;

        public ViewHolder(@NonNull View itemView) {

            super(itemView);

       //     recyclerView = itemView.findViewById(R.id.rcv_cast_backdrop_detail);
            imageView = itemView.findViewById(R.id.img_cast_backdrop_detail);
        }
    }
}
