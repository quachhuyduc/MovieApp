package com.example.movieapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.movieapp.R;
import com.example.movieapp.interfaces.OnMovieListener;
import com.example.movieapp.models.AuthorDetails;
import com.example.movieapp.models.Cast;
import com.example.movieapp.models.Review;

import java.util.List;

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ViewHolder>{
    private List<Review> mReview;

    private Context context;



    public ReviewAdapter(Context context) {
        this.context = context;

    }
    public void setDataReview(List<Review> mReview){
        this.mReview = mReview;
        notifyDataSetChanged();
    }


    @NonNull
    @Override
    public ReviewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.review_item,parent,false);

        return new ReviewAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReviewAdapter.ViewHolder holder, int position) {

      Review currentReview = mReview.get(position);

        AuthorDetails authorDetails = currentReview.getAuthorDetails();

        if (authorDetails != null) {
            // Truy cập hình ảnh và tên của người bình luận
            String profilePath = authorDetails.getAvatarPath();
            String authorName = authorDetails.getUsername();
            String authorUserName = authorDetails.getName();
            if (context != null && profilePath != null) {
                // Sử dụng Glide để tải hình ảnh
                Glide.with(context)
                        .load("https://image.tmdb.org/t/p/w500/" + profilePath)
                        .placeholder(R.drawable.resource_default)
                        .error(R.drawable.ic_launcher_background)
                        .into(holder.img_avatar_author);

                // Cập nhật tên người bình luận trong TextView
                if(authorName != null){
                    holder.tv_author_name.setText(authorName);
                }else{
                    holder.tv_author_name.setText(authorUserName);
                }

            }
            // Sử dụng Glide để hiển thị hình ảnh người bình luận
            holder.tv_author_content.setText(currentReview.getContent());



        }





      //  Glide.with(holder.itemView.getContext()).load("https://image.tmdb.org/t/p/w500/" + mReview.get(position).getAuthorDetails().getAvatarPath()).into((holder).img_avatar_author);
     //   holder.tv_author_name.setText(mReview.get(position).getAuthorDetails().getUsername());


    }

    @Override
    public int getItemCount() {
        return (mReview != null && mReview.size() > 0) ? mReview.size() : 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        RecyclerView recyclerView;
        ImageView img_avatar_author;
        TextView tv_author_name,tv_author_content;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            recyclerView = itemView.findViewById(R.id.rcv_cast);
            img_avatar_author = itemView.findViewById(R.id.img_avatar_author);
            tv_author_name = itemView.findViewById(R.id.tv_author_name);
            tv_author_content = itemView.findViewById(R.id.tv_author_content);
        }
    }
}
