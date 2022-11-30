package com.example.fridge.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapRegionDecoder;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.fridge.LockscreenActivity;
import com.example.fridge.R;

import java.util.List;

public class GalleryAdapter extends RecyclerView.Adapter<GalleryAdapter.ViewHolder> {

    private Context context;
    private List<String> images;
    protected PhotoListner photoListner;

    public GalleryAdapter(Context context, List<String> images, PhotoListner photoListner) {
        this.context = context;
        this.images = images;
        this.photoListner = photoListner;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(
                LayoutInflater.from(context).inflate(R.layout.gallery_item, parent, false)
        );
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final String image = images.get(position);



        ViewGroup.LayoutParams params = holder.layout.getLayoutParams();
        Glide.with(context).load(image).into(holder.image);

       /*  int min = 480;
         int max = 500;
         int random = new Random().nextInt((max - min) + 1) + min;

        for (int i = 0;i<= images.size();i++){
            params.height = random;
            holder.layout.setLayoutParams(params);
        }
*/
        ConstraintLayout.LayoutParams nparams = new ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.MATCH_CONSTRAINT, ConstraintLayout.LayoutParams.MATCH_CONSTRAINT);

       if (position == 0) {
           holder.layout.setPadding(0,610,0,0);

        }
//          holder.layout.setPadding(0,40,0,0);
     /*   if (position == 0) {
//            nparams.setMargins(0, -200, 0, 0);
//            holder.card.setLayoutParams(nparams);
        holder.layout.setPadding(0,2000,0,0);
        }

        if (position == 3) {
            holder.layout.setPadding(0,210,0,0);
        }
*/
//        holder.layout.setPadding(0,100,0,0);
//        nparams.setMargins(0, 200, 0, 0);

//            holder.layout.setLayoutParams(nparams);


        holder.image.setOnTouchListener(new View.OnTouchListener() {
            private GestureDetector gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener(){
                @Override
                public boolean onDoubleTap(MotionEvent e) {
                    Toast.makeText(context, "unlocked", Toast.LENGTH_SHORT).show();
                    return super.onDoubleTap(e);
                }
            });
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                return false;
            }
        });
/*

        if (position == 1 || position == 3 || position == 6) {
            params.height = 500;
            holder.layout.setLayoutParams(params);
        }
        if (position == 2 || position == 4 || position == 7) {
            params.height = 450;
            holder.layout.setLayoutParams(params);
        }
        if (position == 0 || position == 5 || position == 8) {
            params.height = 400;
            holder.layout.setLayoutParams(params);
        }
*/

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                photoListner.onPhotoClick(image);
            }
        });

    }

    @Override
    public int getItemCount() {
        return images.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView image;
        CardView card;

        ConstraintLayout layout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.image);
            card = itemView.findViewById(R.id.card_view);

            layout = itemView.findViewById(R.id.constraint);

        }


    }

    public interface PhotoListner {
        void onPhotoClick(String path);
    }
}
