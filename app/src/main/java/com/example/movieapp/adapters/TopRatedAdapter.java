package com.example.movieapp.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.movieapp.R;
import com.example.movieapp.interfaces.OnMovieListener;
import com.example.movieapp.models.NowPlayingMovie;
import com.example.movieapp.utils.SharedPreferencesUtil;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class TopRatedAdapter extends RecyclerView.Adapter<TopRatedAdapter.ViewHolder> {
    private List<NowPlayingMovie> mMovieTopRated;
    private OnMovieListener onMovieListener;
    private Context context;
    private DatabaseReference databaseReference, fvrtref, fvrt_listRef, favouriteref;
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private List<Boolean> fvrtStatusList;  // Thêm danh sách trạng thái yêu thích


    public TopRatedAdapter(Context context, OnMovieListener listener) {
        this.context = context;
        this.onMovieListener = listener;
        this.fvrtStatusList = new ArrayList<>();

    }

    public void setDataTopRated(List<NowPlayingMovie> mMovieTopRated) {
        this.mMovieTopRated = mMovieTopRated;
        notifyDataSetChanged();
        initFvrtStatusList();  // Khởi tạo danh sách trạng thái yêu thích khi có dữ liệu mới
    }

    private void initFvrtStatusList() {
        fvrtStatusList.clear();
        for (int i = 0; i < getItemCount(); i++) {
            fvrtStatusList.add(false);  // Ban đầu, đặt tất cả các trạng thái là false
        }
    }

    @NonNull
    @Override
    public TopRatedAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.movie_list_item2, parent, false);
        return new TopRatedAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TopRatedAdapter.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String currentUserid = user.getUid();
        fvrtref = database.getReference("favourites");
        fvrt_listRef = database.getReference("favouritesList").child(currentUserid);
        databaseReference = database.getReference("AllWatchList");
        final String postkey = databaseReference.getKey();
        NowPlayingMovie nowPlayingMovie = mMovieTopRated.get(position);

        String name = nowPlayingMovie.getTitle();
        String releaseDate = nowPlayingMovie.getReleaseDate();
        int movieId = nowPlayingMovie.getId();

        holder.tv_movieName.setText(mMovieTopRated.get(position).getTitle());
        holder.tv_originalLanguage.setText(mMovieTopRated.get(position).getReleaseDate());


        holder.tv_voteAverage.setText(mMovieTopRated.get(position).getVoteAverage()+"");

        Glide.with(holder.itemView.getContext())
                .load("https://image.tmdb.org/t/p/w500/" + mMovieTopRated.get(position).getPosterPath())
                .into((holder).imageView);

        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onMovieListener.onMovieClick(holder.getAbsoluteAdapterPosition());
            }
        });

        boolean wishListStatus = SharedPreferencesUtil.getWishListStatus(context, nowPlayingMovie.getId());
        holder.img_wishListHome.setImageResource(wishListStatus ? R.drawable.ic_wish_selected : R.drawable.ic_wish);


        holder.img_wishListHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                boolean newStatus = !fvrtStatusList.get(position);
                fvrtStatusList.set(position, newStatus);

                SharedPreferencesUtil.setWishListStatus(context, nowPlayingMovie.getId(), newStatus);
                holder.img_wishListHome.setImageResource(newStatus ? R.drawable.ic_wish_selected : R.drawable.ic_wish);
                //         SharedPreferencesUtil.removeWishListStatus(context, nowPlayingMovie.getId());

                if (newStatus) {
                    // Nếu là trạng thái mới là yêu thích, thêm vào Firebase và danh sách yêu thích local
                    fvrtref.child(postkey).child(currentUserid).setValue(true);
                    nowPlayingMovie.setTitle(name);
                    nowPlayingMovie.setReleaseDate(releaseDate);
                    nowPlayingMovie.setId(movieId);

                    String id = fvrt_listRef.push().getKey();
                    fvrt_listRef.child(id).setValue(nowPlayingMovie);
                } else {
                    // Nếu là trạng thái mới không phải yêu thích, xóa khỏi Firebase và danh sách yêu thích local
                    delete(postkey, currentUserid, nowPlayingMovie.getId());


                }
                //        onMovieListener.onChangeWishList(position);
                notifyDataSetChanged();
            }
        });

        holder.favouriteChecker(postkey, position);
    }



    private void delete(String postKey, String userId, int movieId) {
        Query query = fvrt_listRef.orderByChild("id").equalTo(movieId);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    dataSnapshot.getRef().removeValue();
                    Toast.makeText(context, "Deleted", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        // Xóa khỏi Firebase
        fvrtref.child(postKey).child(userId).removeValue();
    }

    @Override
    public int getItemCount() {
        return (mMovieTopRated != null) ? mMovieTopRated.size() : 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        RecyclerView recyclerView;

        TextView tv_movieName,tv_originalLanguage,tv_voteAverage;
        ImageView imageView, img_wishListHome;
        FirebaseDatabase database = FirebaseDatabase.getInstance();

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            recyclerView = itemView.findViewById(R.id.recyclerViewNowPlaying);
            imageView = itemView.findViewById(R.id.movie_img2);
            tv_movieName = itemView.findViewById(R.id.tv_movieName);
            tv_originalLanguage = itemView.findViewById(R.id.tv_originalLanguage);
            tv_voteAverage = itemView.findViewById(R.id.tv_voteAverage);
            img_wishListHome = itemView.findViewById(R.id.img_WishlistHome);
        }

        public void favouriteChecker(String postkey, int position) {
            favouriteref = database.getReference("favourites");
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            String uid = user.getUid();

            favouriteref.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.child(postkey).hasChild(uid)) {
                        if (fvrtStatusList.get(position)) {
                            Glide.with(context).load(R.drawable.ic_wish_selected).into(img_wishListHome);
                        }
                    } else {
                        if (!fvrtStatusList.get(position)) {
                            Glide.with(context).load(R.drawable.ic_wish).into(img_wishListHome);
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                }
            });
        }
    }
}

