package com.example.fridge;

import static android.os.Build.VERSION_CODES.S;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.Manifest;
import android.app.ActivityManager;
import android.app.WallpaperManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.fridge.activity.HomeKeyLocker;
import com.example.fridge.adapter.GalleryAdapter;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import io.alterac.blurkit.BlurLayout;
import no.danielzeller.blurbehindlib.BlurBehindLayout;

public class LockscreenActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    GalleryAdapter galleryAdapter;
    List<String> images;
    TextView time, date;
    ImageView lock;
    RelativeLayout layout,background;
    private static final int MY_READ_PERMISSION_CODE = 101;
    boolean isDoubleClicked=false;


    @RequiresApi(api = S)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_lockscreen);

//        gallery_number = findViewById(R.id.gallerynumber);
        recyclerView = findViewById(R.id.recyclerview_gallery_images);
        layout = findViewById(R.id.rr1);

        time = findViewById(R.id.time);
        lock = findViewById(R.id.lock);
        //        blurtimeset();
        date = findViewById(R.id.date);
        background = findViewById(R.id.background);

        lock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                finish();

            }
        });

        WallpaperManager wallpaperManager = WallpaperManager.getInstance(this);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        Drawable wallpaperDrawable = wallpaperManager.getDrawable();


        background.setBackground(wallpaperDrawable);

        Typeface typeface = ResourcesCompat.getFont(this, R.font.poppins_semibold);
        time.setTypeface(typeface);
        Typeface typedate = ResourcesCompat.getFont(this, R.font.poppins);
        date.setTypeface(typedate);
        if (ContextCompat.checkSelfPermission(LockscreenActivity.this,
                Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(LockscreenActivity.this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, MY_READ_PERMISSION_CODE
            );
        } else {
            loadImages();
        }
        setdatetime();

    }

    private void blurtimeset() {

    }

    private void setdatetime() {
        String currentTime = new SimpleDateFormat("HH:mm", Locale.getDefault()).format(new Date());
        String currentDate = new SimpleDateFormat("EEEE, MMMM yy", Locale.getDefault()).format(new Date());
        date.setText(currentDate);
        time.setText(currentTime);
    }

    private void loadImages() {
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL));
        images = ImagesGallery.listOfImages(this);
        galleryAdapter = new GalleryAdapter(this, images, new GalleryAdapter.PhotoListner() {
            @Override
            public void onPhotoClick(String path) {


                Handler handler=new Handler();
                Runnable r=new Runnable(){
                    @Override
                    public void run(){
                        //Actions when Single Clicked
                        isDoubleClicked=false;
                    }
                };

                if(isDoubleClicked){
                    //Actions when double Clicked
                    finish();
                    isDoubleClicked=false;
                    //remove callbacks for Handlers
                    handler.removeCallbacks(r);
                }else{
                    isDoubleClicked=true;
                    handler.postDelayed(r,500);
                }
            }
        });
        recyclerView.setAdapter(galleryAdapter);
//        gallery_number.setText();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_READ_PERMISSION_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show();
                loadImages();
            } else {
                Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_HOME) {

            Log.i("TAG", "Press Home");
            System.exit(0);
            return true;
        } else {
            return super.onKeyDown(keyCode, event);
        }
    }

    @Override
    public void onAttachedToWindow() {
//        this.getWindow().setType(WindowManager.LayoutParams.TYPE_KEYGUARD_DIALOG);
        super.onAttachedToWindow();
    }

    @Override
    public void onBackPressed() {

    }

    @Override
    protected void onStart() {
//        lock(this);
        super.onStart();
    }

}