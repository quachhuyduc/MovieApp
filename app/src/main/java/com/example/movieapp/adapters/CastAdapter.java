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
import com.example.movieapp.models.Cast;

import java.util.List;

public class CastAdapter extends RecyclerView.Adapter<CastAdapter.ViewHolder> {

    private List<Cast> mCast;

    private Context context;

    private OnMovieListener onMovieListener;

    public CastAdapter(Context context,OnMovieListener onMovieListener) {
        this.context = context;

        this.onMovieListener = onMovieListener;
    }
    public void setDataCast(List<Cast> mCast){
        this.mCast = mCast;
        notifyDataSetChanged();
    }


    @NonNull
    @Override
    public CastAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.cast_item,parent,false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CastAdapter.ViewHolder holder, int position) {

        Cast cast = mCast.get(position);

        Glide.with(holder.itemView.getContext()).load("https://image.tmdb.org/t/p/w500/" + mCast.get(position).getProfilePath()).into((holder).imageView);
         holder.textView.setText(mCast.get(position).getName());
         holder.imageView.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 onMovieListener.onCastClick(holder.getAdapterPosition());
             }
         });

    }

    @Override
    public int getItemCount() {
        return (mCast != null && mCast.size() > 0) ? mCast.size() : 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        RecyclerView recyclerView;
        ImageView imageView;
        TextView textView;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            recyclerView = itemView.findViewById(R.id.rcv_cast);
            imageView = itemView.findViewById(R.id.img_Cast);
            textView = itemView.findViewById(R.id.textView_Cast);
        }
    }
}
