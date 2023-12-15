package com.example.movieapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.movieapp.R;
import com.example.movieapp.interfaces.OnMovieListener;
import com.example.movieapp.models.Video;

import java.util.List;

public class VideoAdapter extends RecyclerView.Adapter<VideoAdapter.ViewHolder> {
    private List<Video> mVideos;
    private Context context;

    private OnMovieListener onMovieListener;

    public VideoAdapter(Context context,OnMovieListener onMovieListener) {
        this.context = context;
        this.onMovieListener = onMovieListener;
    }
    public void setDataVideo(List<Video> mVideos){
        this.mVideos = mVideos;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public VideoAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.movie_list_video,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VideoAdapter.ViewHolder holder, int position) {

        holder.videoName.setText(mVideos.get(position).getName());
        holder.videoSize.setText(mVideos.get(position).getSize() +"");
        holder.videoPublish.setText(mVideos.get(position).getPublishedAt());

        holder.videoImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onMovieListener.onMovieClick(holder.getAdapterPosition());
            }
        });

    }

    @Override
    public int getItemCount() {
        return (mVideos != null && mVideos.size() > 0) ? mVideos.size() : 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        RecyclerView rcvVideos;
     ImageView videoImage;
        TextView videoName,videoSize,videoPublish;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            rcvVideos = itemView.findViewById(R.id.rcvVideos);
          videoImage = itemView.findViewById(R.id.video_image);
            videoName = itemView.findViewById(R.id.videoName);
            videoSize = itemView.findViewById(R.id.videoSize);
            videoPublish = itemView.findViewById(R.id.videoPublish);
        }
    }
}
