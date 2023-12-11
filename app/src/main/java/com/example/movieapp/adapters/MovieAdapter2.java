package com.example.movieapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.movieapp.R;
import com.example.movieapp.models.NowPlayingMovie;
import com.example.movieapp.models.Result;
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

public class MovieAdapter2 extends RecyclerView.Adapter<MovieAdapter2.ViewHolder> {
    private List<NowPlayingMovie> mMovieNow2;

    private OnMovieListener onMovieListener;
    private Context context;

    private Boolean fvrtChecker = false;



    private DatabaseReference databaseReference,fvrtref,fvrt_listRef,favouriteref;

       FirebaseDatabase database = FirebaseDatabase.getInstance();

    public MovieAdapter2(Context context, OnMovieListener listener) {
        this.context = context;
        this.onMovieListener = listener;
    }

    public void setData2(List<NowPlayingMovie> mMovieNow2){
        this.mMovieNow2 = mMovieNow2;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public MovieAdapter2.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {


            View view = LayoutInflater.from(context).inflate(R.layout.movie_list_item2,parent,false);
            return new ViewHolder(view);




    }

    @Override
    public void onBindViewHolder(@NonNull MovieAdapter2.ViewHolder holder, int position) {



        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String currentUserid = user.getUid();
        fvrtref = database.getReference("favourites");
        fvrt_listRef = database.getReference("favouritesList").child(currentUserid);

        databaseReference = database.getReference("AllWatchList");


       final String postkey = databaseReference.getKey();

        NowPlayingMovie nowPlayingMovie = mMovieNow2.get(position);

       String name = nowPlayingMovie.getTitle();
       String releaseDate = nowPlayingMovie.getReleaseDate() ;

       int movieId = nowPlayingMovie.getId();
            Glide.with(holder.itemView.getContext())
                    .load("https://image.tmdb.org/t/p/w500/" + mMovieNow2.get(position).getPosterPath())
                    .into((holder).imageView);

            holder.imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onMovieListener.onMovieClick(holder.getAdapterPosition());
                }
            });

        //  <!-- TODO: img_wishlist cant get position -->

        holder.img_wishListHome.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    fvrtChecker = true;
                    fvrtref.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if(fvrtChecker.equals(true)){
                                if(snapshot.child(postkey).hasChild(currentUserid)){
                                    fvrtref.child(postkey).child(currentUserid).removeValue();

                                    delete();
                                    fvrtChecker = false;
                                }else{
                                    fvrtref.child(postkey).child(currentUserid).setValue(true);
                                    nowPlayingMovie.setTitle(name);
                                    nowPlayingMovie.setReleaseDate(releaseDate);
                                    nowPlayingMovie.setId(movieId);

                                    String id  =  fvrt_listRef.push().getKey();
                                    fvrt_listRef.child(id).setValue(nowPlayingMovie);
                                    fvrtChecker = false;
                                }
                            }else{


                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
            });

        holder.favouriteChecker(postkey);


    }

    private void delete() {
        Query query = fvrt_listRef.orderByChild("time");
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot1 : snapshot.getChildren()){
                    dataSnapshot1.getRef().removeValue();

                    Toast.makeText(context, "Deleted", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return (mMovieNow2 != null && mMovieNow2.size() > 0) ? mMovieNow2.size() : 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        RecyclerView recyclerView;
        ImageView imageView,img_wishListHome;


        FirebaseDatabase database = FirebaseDatabase.getInstance();


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            recyclerView = itemView.findViewById(R.id.recyclerViewNowPlaying);
            imageView = itemView.findViewById(R.id.movie_img2);
            img_wishListHome = itemView.findViewById(R.id.img_WishlistHome);


        }


        public void favouriteChecker(String postkey) {
            favouriteref = database.getReference("favourites");
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            String uid = user.getUid();

            favouriteref.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(snapshot.child(postkey).hasChild(uid)){
                        Glide.with(context).load(R.drawable.ic_wish_selected).into(img_wishListHome);;
                    }else{
                        Glide.with(context).load(R.drawable.wish).into(img_wishListHome);;

                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }


}
