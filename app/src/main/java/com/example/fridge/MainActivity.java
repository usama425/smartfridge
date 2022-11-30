package com.example.fridge;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.WallpaperManager;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.nfc.Tag;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Lock;

public class MainActivity extends AppCompatActivity {
    ImageView close;
    RelativeLayout drawer;
    Animation animFadeIn, animFadeOut, bounceIn, bounceOut;
    RelativeLayout swipe;
    LinearLayout ll1;
    private PackageManager manager;
    private List<Item> apps;
    private GridView list;
    GridView docklist;
    Handler handler;
    Runnable r;

    @SuppressLint({"ResourceType", "ClickableViewAccessibility"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent intent = new Intent(MainActivity.this, LockscreenActivity.class);
        startActivity(intent);

        handler = new Handler();
        startHandler();

        r = new Runnable() {
            @Override
            public void run() {
                // TODO Auto-generated method stub
                Intent intent = new Intent(MainActivity.this, LockscreenActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                stopHandler();
            }
        };

        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
//        WallpaperManager wallpaperManager = WallpaperManager.getInstance(getApplicationContext());
//        try {
//            wallpaperManager.setResource(R.drawable.backgroung_image);
//
//        } catch (IOException e) {
//            e.printStackTrace();
//        }


        WallpaperManager wallpaperManager = WallpaperManager.getInstance(this);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        Drawable wallpaperDrawable = wallpaperManager.getDrawable();


        close = findViewById(R.id.close);
        drawer = findViewById(R.id.drawer);
        ll1 = findViewById(R.id.linearLayout);
        drawer.setVisibility(View.GONE);
        list = findViewById(R.id.list);
        docklist = (GridView) findViewById(R.id.listdock);
        swipe = findViewById(R.id.background);
        loadapps();
        loadListView();
        addClickListner();
        dockloadListView();
        dockaddClickListner();

        swipe.setBackground(wallpaperDrawable);
        animFadeIn = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.fade_in);

        animFadeOut = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.fade_out);

        bounceIn = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.bounce_in);

        bounceOut = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.bounce_out);

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawer.setVisibility(View.GONE);
                drawer.startAnimation(animFadeOut);
//                drawer.startAnimation(bounceIn);
//                drawer.startAnimation(bounceOut);

            }
        });
        swipe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawer.setVisibility(View.GONE);
                drawer.startAnimation(animFadeOut);
            }
        });

        swipe.setOnTouchListener(new OnSwipeTouchListener(MainActivity.this) {
            public void onSwipeTop() {
                drawer.setVisibility(View.VISIBLE);
                drawer.startAnimation(animFadeIn);
            }

            public void onSwipeRight() {
            }

            public void onSwipeLeft() {
            }

            public void onSwipeBottom() {
                drawer.setVisibility(View.GONE);
                drawer.startAnimation(animFadeOut);
            }

        });
    }
    private void dockaddClickListner() {
        docklist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
             /*   Intent i = manager.getLaunchIntentForPackage(apps.get(position).label.toString());
                startActivity(i);*/
                drawer.setVisibility(View.VISIBLE);
                drawer.startAnimation(animFadeIn);
            }
        });
    }

    private void dockloadListView() {

        ArrayAdapter<Item> adapter = new ArrayAdapter<Item>(this, R.layout.dock_item, apps) {
            @NonNull
            @Override
            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                if (convertView == null) {
                    convertView = getLayoutInflater().inflate(R.layout.dock_item, null);
                }
                ImageView appIcon = (ImageView) convertView.findViewById(R.id.icon);
                appIcon.setImageDrawable(apps.get(position).icon);
                TextView appName = (TextView) convertView.findViewById(R.id.name);
                appName.setText(apps.get(position).name);
                return convertView;
            }
        };
//        Toast.makeText(this, ""+apps.size(), Toast.LENGTH_SHORT).show();
//        customizedock(apps.size());
        docklist.setAdapter(adapter);
    }

    private void customizedock(int size) {
        switch (size) {
            case 1:
                ll1.setMinimumWidth(200);

//                docklist.setNumColumns(1);
                break;
            case 2:
                ll1.setMinimumWidth(400);

//                docklist.setNumColumns(2);
                break;
            case 3:
                ll1.setMinimumWidth(600);

//                docklist.setNumColumns(3);
                break;
            case 4:
                ll1.setMinimumWidth(800);

//                docklist.setNumColumns(4);
                break;
            case 5:
                ll1.setMinimumWidth(1000);

//                docklist.setNumColumns(5);
                break;
            case 6:
//                ll1.setMinimumWidth();

                docklist.setNumColumns(6);
                break;
            default:
                docklist.setNumColumns(7);
        }
    }

    private void loadapps() {
        manager = getPackageManager();
        apps = new ArrayList<>();

        Intent i = new Intent(Intent.ACTION_MAIN, null);
        i.addCategory(Intent.CATEGORY_LAUNCHER);

        List<ResolveInfo> availableActivities = manager.queryIntentActivities(i, 0);
        for (ResolveInfo ri : availableActivities) {
            Item app = new Item();
            app.label = ri.activityInfo.packageName;
            app.name = ri.loadLabel(manager);
            app.icon = ri.loadIcon(manager);
            apps.add(app);
        }

      /*  final PackageManager pm = getPackageManager();
//get a list of installed apps.
        List<ApplicationInfo> packages = pm.getInstalledApplications(PackageManager.GET_META_DATA);

        for (ApplicationInfo packageInfo : packages) {
            Item app = new Item();
            app.name = packageInfo.loadLabel(manager);
            app.label = packageInfo.packageName;
            app.icon = packageInfo.loadIcon(manager);
            apps.add(app);
        }*/

    }

    private void loadListView() {

        ArrayAdapter<Item> adapter = new ArrayAdapter<Item>(this, R.layout.item, apps) {
            @NonNull
            @Override
            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                if (convertView == null) {
                    convertView = getLayoutInflater().inflate(R.layout.item, null);
                }
                ImageView appIcon = (ImageView) convertView.findViewById(R.id.icon);
                appIcon.setImageDrawable(apps.get(position).icon);
//                apps.get(position).icon.applyTheme(getTheme().getResources().newTheme());
                TextView appName = (TextView) convertView.findViewById(R.id.name);
                appName.setText(apps.get(position).name);
                return convertView;
            }
        };
        list.setAdapter(adapter);

    }

    private void addClickListner() {
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent i = manager.getLaunchIntentForPackage(apps.get(position).label.toString());
                startActivity(i);

//                Toast.makeText(MainActivity.this, ""+apps.get(position).name.toString(), Toast.LENGTH_SHORT).show();
//                Intent intent = new Intent(MainActivity.this, LockscreenActivity.class);
//                startActivity(intent);
            }
        });


    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // Do nothing or catch the keys you want to block
        return false;
    }

    @Override
    public void onBackPressed() {

    }

    @Override
    public void onUserInteraction() {
        // TODO Auto-generated method stub
        super.onUserInteraction();
        stopHandler();
        startHandler();
    }

    public void stopHandler() {
        handler.removeCallbacks(r);
    }

    public void startHandler() {
        handler.postDelayed(r, 30000);
    }

    @Override
    protected void onDestroy() {
        stopHandler();
        super.onDestroy();
    }

    @Override
    protected void onStop() {
        stopHandler();
        super.onStop();
    }

    @Override
    protected void onPause() {
        stopHandler();
        super.onPause();
    }
}