package com.sneva.heywalls;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.sneva.easyprefs.EasyPrefs;
import com.sneva.heywalls.adapters.FeaturedAdapter;
import com.sneva.heywalls.adapters.TabsAdapter;
import com.sneva.heywalls.adapters.WallsAdapter;
import com.sneva.heywalls.databinding.ActivityMainBinding;
import com.sneva.heywalls.databinding.BottomsheetMenuBinding;
import com.sneva.heywalls.listener.TabsSelector;
import com.sneva.heywalls.models.Featured;
import com.sneva.heywalls.models.Tabs;
import com.sneva.heywalls.models.Wallpapers;
import com.sneva.heywalls.utlis.AdRequestBuilder;
import com.sneva.heywalls.utlis.Constants;
import com.sneva.heywalls.utlis.NetworkConfig;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements TabsSelector {

    ActivityMainBinding binding;
    Activity activity = MainActivity.this;
    private List<Featured> featuredList;
    private List<Tabs> tabsList;
    WallsAdapter featuredAdapter;
    private List<Wallpapers> wallpapersList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        
        AdRequestBuilder.initialize(activity);

        featuredList = new ArrayList<>();
        tabsList = new ArrayList<>();
        wallpapersList = new ArrayList<>();

        if (!EasyPrefs.use().getString("profile", "").equals("no")) {
            if( !EasyPrefs.use().getString("profile", "").equalsIgnoreCase("") ){
                byte[] b = Base64.decode(EasyPrefs.use().getString("profile", ""), Base64.DEFAULT);
                Bitmap bitmap = BitmapFactory.decodeByteArray(b, 0, b.length);
                binding.profile.setImageBitmap(bitmap);
            }
        }

        binding.name.setText(EasyPrefs.use().getString("name", ""));

        binding.swipeRefresh.setRefreshing(true);
        binding.swipeRefresh.setOnRefreshListener(() -> {
            readFeatured();
            readTabs();
            readWallpapers();
        });

        binding.profile.setOnClickListener(v -> {
            final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(activity, R.style.StyleBottomSheetDialog);
            View root = LayoutInflater.from(activity).inflate(R.layout.bottomsheet_menu, null);
            BottomsheetMenuBinding binding = BottomsheetMenuBinding.bind(root);

            if (bottomSheetDialog.getWindow() != null) {
                bottomSheetDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
            }

            AdRequestBuilder.loadBanner(activity, binding.adView);

            if (!EasyPrefs.use().getString("profile", "").equals("no")) {
                if( !EasyPrefs.use().getString("profile", "").equalsIgnoreCase("") ){
                    byte[] b = Base64.decode(EasyPrefs.use().getString("profile", ""), Base64.DEFAULT);
                    Bitmap bitmap = BitmapFactory.decodeByteArray(b, 0, b.length);
                    binding.image.setImageBitmap(bitmap);
                }
            }

            binding.name.setText(EasyPrefs.use().getString("name", ""));

            binding.edit.setOnClickListener(v1 -> startActivity(new Intent(activity, FirstTimeActivity.class)));

            binding.about.setOnClickListener(v15 -> {
                Intent intent = new Intent(activity, WebViewActivity.class);
                intent.putExtra("url", "https://snevatechnologies.com/about/");
                startActivity(intent);
            });

            binding.more.setOnClickListener(v14 -> {
                Uri uri = Uri.parse("market://details?id=" + getApplicationContext().getPackageName());
                Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
                goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY |
                        Intent.FLAG_ACTIVITY_NEW_DOCUMENT |
                        Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
                try {
                    startActivity(goToMarket);
                } catch (ActivityNotFoundException e) {
                    startActivity(new Intent(Intent.ACTION_VIEW,
                            Uri.parse("https://play.google.com/store/apps/details?id=" + getApplicationContext().getPackageName())));
                }
            });

            binding.bug.setOnClickListener(v13 -> {
                Intent intent = new Intent(Intent.ACTION_SEND);
                String[] recipients = {"help@snevatechnologies.com"};
                intent.putExtra(Intent.EXTRA_EMAIL, recipients);
                intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.app_name)+", feedback");
                intent.putExtra(Intent.EXTRA_CC, "help@snevatechnologies.com");
                intent.putExtra(Intent.EXTRA_TEXT, "Namaste Sneva, i found a bug in your "+getString(R.string.app_name)+" application.\n");
                intent.setType("text/html");
                intent.setPackage("com.google.android.gm");
                startActivity(Intent.createChooser(intent, "Send mail"));
            });

            binding.share.setOnClickListener(v12 -> {
                try {
                    Intent shareIntent = new Intent(Intent.ACTION_SEND);
                    shareIntent.setType("text/plain");
                    shareIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.app_name)+",");
                    String shareMessage= "\nHello friend,\nI recommend to use this Application "+getString(R.string.app_name)+".\n";
                    shareMessage = shareMessage + "https://play.google.com/store/apps/details?id=" + getApplicationContext().getPackageName() +"\nDownload the App Now.";
                    shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage);
                    startActivity(Intent.createChooser(shareIntent,"Share " + getString(R.string.app_name)));
                } catch(Exception e) {
                    Toast.makeText(activity, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });

            bottomSheetDialog.setContentView(binding.getRoot());
            bottomSheetDialog.show();
        });

        readFeatured();
        readTabs();
        readWallpapers();
    }

    private void readFeatured() {
        if (new NetworkConfig(activity).isConnectedToInternet()) {
            RequestQueue mQueue = Volley.newRequestQueue(activity);
            JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, Constants.FEATURED, null,
                    response -> {
                        try {
                            JSONArray jsonArray = response.getJSONArray("featured");
                            featuredList.clear();
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject data = jsonArray.getJSONObject(i);

                                Featured featured = new Featured(data.getString("image"), data.getString("name"), data.getString("tags"));
                                featuredList.add(featured);
                                binding.stories.setHasFixedSize(true);
                                FeaturedAdapter featuredAdapter = new FeaturedAdapter(activity, featuredList);
                                binding.stories.setLayoutManager(new LinearLayoutManager(activity, RecyclerView.HORIZONTAL, false));
                                binding.stories.setAdapter(featuredAdapter);

                                EasyPrefs.use().setObjectsList("featured", featuredList);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }, Throwable::printStackTrace);

            mQueue.add(request);
        } else {
            featuredList = EasyPrefs.use().getObjectsList("featured", Featured.class);
            binding.stories.setHasFixedSize(true);
            FeaturedAdapter featuredAdapter = new FeaturedAdapter(activity, featuredList);
            binding.stories.setLayoutManager(new LinearLayoutManager(activity, RecyclerView.HORIZONTAL, false));
            binding.stories.setAdapter(featuredAdapter);
        }
    }

    private void readTabs() {
        if (new NetworkConfig(activity).isConnectedToInternet()) {
            RequestQueue mQueue = Volley.newRequestQueue(activity);
            JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, Constants.TABS, null,
                    response -> {
                        try {
                            JSONArray jsonArray = response.getJSONArray("tabs");
                            tabsList.clear();
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject data = jsonArray.getJSONObject(i);

                                Tabs tabs = new Tabs(data.getString("id"), data.getInt("sortID"));
                                tabsList.add(tabs);
                                binding.tabs.setHasFixedSize(false);
                                TabsAdapter featuredAdapter = new TabsAdapter(activity, tabsList, this);
                                binding.tabs.setLayoutManager(new LinearLayoutManager(activity, RecyclerView.HORIZONTAL, false));
                                binding.tabs.setAdapter(featuredAdapter);

                                EasyPrefs.use().setObjectsList("tabs", tabsList);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }, Throwable::printStackTrace);

            mQueue.add(request);
        } else {
            tabsList = EasyPrefs.use().getObjectsList("tabs", Tabs.class);
            binding.tabs.setHasFixedSize(true);
            TabsAdapter featuredAdapter = new TabsAdapter(activity, tabsList, this);
            binding.tabs.setLayoutManager(new LinearLayoutManager(activity, RecyclerView.HORIZONTAL, false));
            binding.tabs.setAdapter(featuredAdapter);
        }
    }

    private void readWallpapers() {
        if (new NetworkConfig(activity).isConnectedToInternet()) {
            RequestQueue mQueue = Volley.newRequestQueue(activity);
            JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, Constants.WALLPAPERS, null,
                    response -> {
                        try {
                            JSONArray jsonArray = response.getJSONArray("All Wallpapers");
                            wallpapersList.clear();
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject data = jsonArray.getJSONObject(i);

                                Wallpapers wallpapers = new Wallpapers(data.getString("image"), data.getString("name"), data.getString("tags"));

                                wallpapersList.add(wallpapers);
                                binding.walls.setHasFixedSize(true);
                                featuredAdapter = new WallsAdapter(activity, wallpapersList);
                                binding.walls.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
                                binding.walls.setAdapter(featuredAdapter);
                                binding.swipeRefresh.setRefreshing(false);

                                EasyPrefs.use().setObjectsList("walls", wallpapersList);
                            }

                            featuredAdapter.notifyDataSetChanged();
                            binding.swipeRefresh.setRefreshing(false);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }, Throwable::printStackTrace);

            mQueue.add(request);
        } else {
            binding.swipeRefresh.setRefreshing(false);
            wallpapersList = EasyPrefs.use().getObjectsList("walls", Wallpapers.class);
            binding.walls.setHasFixedSize(true);
            WallsAdapter featuredAdapter = new WallsAdapter(activity, wallpapersList);
            binding.walls.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
            binding.walls.setAdapter(featuredAdapter);
        }
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setCancelable(true);
        builder.setTitle("Quit App");
        builder.setMessage("Do you want to close HeyWalls! application?");

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

        builder.setPositiveButton("Yes, Close", (dialog, which) -> {
            dialog.dismiss();
            finish();
        });

        builder.create().show();
    }

    @Override
    public void clicked(String child, int position) {
        if (new NetworkConfig(activity).isConnectedToInternet()) {
            RequestQueue mQueue = Volley.newRequestQueue(activity);
            JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, Constants.WALLPAPERS, null,
                    response -> {
                        try {
                            JSONArray jsonArray = response.getJSONArray(child);
                            wallpapersList.clear();
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject data = jsonArray.getJSONObject(i);

                                Wallpapers wallpapers = new Wallpapers(data.getString("image"), data.getString("name"), data.getString("tags"));

                                wallpapersList.add(wallpapers);
                                binding.walls.setHasFixedSize(true);
                                featuredAdapter = new WallsAdapter(activity, wallpapersList);
                                binding.walls.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
                                binding.walls.setAdapter(featuredAdapter);
                                binding.swipeRefresh.setRefreshing(false);

                                EasyPrefs.use().setObjectsList("walls", wallpapersList);
                            }

                            featuredAdapter.notifyDataSetChanged();
                            binding.swipeRefresh.setRefreshing(false);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }, Throwable::printStackTrace);

            mQueue.add(request);
        } else {
            binding.swipeRefresh.setRefreshing(false);
            wallpapersList = EasyPrefs.use().getObjectsList("walls", Wallpapers.class);
            binding.walls.setHasFixedSize(true);
            WallsAdapter featuredAdapter = new WallsAdapter(activity, wallpapersList);
            binding.walls.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
            binding.walls.setAdapter(featuredAdapter);
        }
    }
}