package com.wibudev.scere.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.preference.PreferenceManager;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.gson.Gson;
import com.wibudev.scere.R;
import com.wibudev.scere.adapter.RecyclerViewAdapter;
import com.wibudev.scere.model.Tasks;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

public class TaskActivity extends AppCompatActivity {

    private RecyclerView recyclerView;

    private RecyclerView.Adapter adapter ;
    private ProgressBar pb;

    private SwipeRefreshLayout mSwipeRefreshLayout;
    private BottomSheetBehavior<View> bottomSheetBehavior;
    private TextView bottomSheetHeading;
    private Button btKeluar;
    private ArrayList<Tasks> list = new ArrayList<Tasks>();
    private String nama = " ";
    private String npm = "null";
    private String pass = "null";
    private String first = "null";
    private String lalu = "30";
    private String akanDatang = "60";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_task);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(TaskActivity.this));

        loadSetting();
        loadLogin();
        if(npm.contains("null")){
            Intent intent = new Intent(TaskActivity.this, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }

        try {
            Intent i = getIntent();
            Bundle bd = i.getExtras();
            first = (String) bd.get("first");
        }catch (Exception e){
            e.printStackTrace();
        }

        if(first.contains("1")){
            getTask();
        }else {
            loadTask();
        }

        Typeface custom_font = Typeface.createFromAsset(getAssets(),  "fonts/font.ttf");
        Typeface custom_font_bold = Typeface.createFromAsset(getAssets(),  "fonts/fontBold.ttf");

        bottomSheetHeading = findViewById(R.id.bottomSheetHeading);
        bottomSheetHeading.setTypeface(custom_font);

        pb = findViewById(R.id.pb);

        mSwipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshItems();
            }
        });
        bottomSheetBehavior = BottomSheetBehavior.from(findViewById(R.id.bottomSheetLayout));
        bottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(View bottomSheet, int newState) {
                switch (newState) {
                    case BottomSheetBehavior.STATE_HIDDEN:
                        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                        break;
                }
            }


            @Override
            public void onSlide(View bottomSheet, float slideOffset) {

            }
        });


        bottomSheetHeading = findViewById(R.id.bottomSheetHeading);

        btKeluar = findViewById(R.id.btKeluar);
        btKeluar.setTypeface(custom_font);
        btKeluar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearLogin();
                Intent intent = new Intent(TaskActivity.this, LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });

    }

    private void refreshItems() {
        try {
            pb = findViewById(R.id.pb);
            pb.setVisibility(View.VISIBLE);
            getTask();
            getName();
            mSwipeRefreshLayout.setRefreshing(false);
        }catch (NullPointerException e){
            e.printStackTrace();
        }
    }

    private void getTask() {
        new Thread(new Runnable() {
            @Override
            public void run() {

                try {
                    list.clear();
                    Calendar c = Calendar.getInstance();
                    c.add(Calendar.DAY_OF_MONTH, Integer.parseInt(lalu) * -1 );

                    //login
                    Connection.Response res = Jsoup.connect("http://scele.teknokrat.ac.id/login/index.php")
                            .data("username", npm, "password", pass)
                            .method(Connection.Method.POST)
                            .execute();

                    //get cookies
                    Map<String, String> loginCookies = res.cookies();

                    //connect w/ cookies
                    for( int i = 0; i < (Integer.parseInt(lalu) + Integer.parseInt(akanDatang)); i++) {
                        c.add(Calendar.DAY_OF_MONTH, 1);
                        String day = String.valueOf(c.get(Calendar.DAY_OF_MONTH));
                        String month = String.valueOf(c.get(Calendar.MONTH));
                        String year = String.valueOf(c.get(Calendar.YEAR));
                        String url = "http://scele.teknokrat.ac.id/calendar/view.php?view=day&cal_d=" + day + "&cal_m=" + month + "&cal_y=" + year;

                        Document doc = Jsoup.connect(url)
                                .cookies(loginCookies)
                                .userAgent("Chrome/64.0.3282")
                                .get();

                        Elements tables = doc.select("table[class=event]");
                        for (Element table : tables) {
                            Elements rows = table.select("tr");
                            for (Element row : rows) {
                                Elements cols = row.select("td[class=topic]");
                                for (Element col : cols) {
                                    String name = col.select("div[class=referer]").text().trim();
                                    String matkul = col.select("div[class=course]").text().trim();
                                    String link = col.select("div[class=referer]").select("a[href]").attr("abs:href");
                                    String tgl = day+"/"+month+"/"+year;
                                    Tasks tugas = new Tasks(link, name, matkul, tgl);
                                    list.add(tugas);
                                }
                            }
                        }
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        saveTask();
                        adapter = new RecyclerViewAdapter(getApplicationContext(), list);
                        recyclerView.setAdapter(adapter);
                        adapter.notifyDataSetChanged();
                        pb = findViewById(R.id.pb);
                        pb.setVisibility(View.GONE);
                    }
                });
            }
        }).start();
    }

    private void getName() {
        new Thread(new Runnable() {
            @Override
            public void run() {

                try {
                    Calendar c = Calendar.getInstance();

                    //login
                    Connection.Response res = Jsoup.connect("http://scele.teknokrat.ac.id/login/index.php")
                            .data("username", npm, "password", pass)
                            .method(Connection.Method.POST)
                            .execute();

                    //get cookies
                    Map<String, String> loginCookies = res.cookies();

                    //connect w/ cookies
                    String day = String.valueOf(c.get(Calendar.DAY_OF_MONTH));
                    String month = String.valueOf(c.get(Calendar.MONTH));
                    String year = String.valueOf(c.get(Calendar.YEAR));
                    String url = "http://scele.teknokrat.ac.id/calendar/view.php?view=day&cal_d=" + day + "&cal_m=" + month + "&cal_y=" + year;

                    Document doc = Jsoup.connect(url)
                            .cookies(loginCookies)
                            .userAgent("Chrome/64.0.3282")
                            .get();
                    nama = doc.select("div[class=headermenu]").select("div[class=logininfo]").select("a").first().text().trim();
                    if(nama.length() > 23) nama = nama.substring(0, 23);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        bottomSheetHeading.setText("Hi, " + nama);
                    }
                });
            }
        }).start();
    }


    private void loadLogin(){
        SharedPreferences sharedPref = getSharedPreferences("login", 0);
        npm = sharedPref.getString("npm","null");
        pass = sharedPref.getString("pass","null");
        nama = sharedPref.getString("nama"," ");
        bottomSheetHeading = findViewById(R.id.bottomSheetHeading);
        bottomSheetHeading.setText("Hi, " + nama);
    }

    private void clearLogin(){
        SharedPreferences sharedPref = getSharedPreferences("login", 0);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("npm", "null");
        editor.putString("pass", "null");
        editor.apply();
    }

    private void loadTask(){
        SharedPreferences appSharedPrefs = PreferenceManager.getDefaultSharedPreferences(this.getBaseContext());
        Gson gson = new Gson();
        String json = appSharedPrefs.getString("user", "");
        Tasks[] listTask = gson.fromJson(json, Tasks[].class);
        List<Tasks> lists = Arrays.asList(listTask);
        list = new ArrayList<Tasks>(lists);
        adapter = new RecyclerViewAdapter(getApplicationContext(), list);
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        pb = findViewById(R.id.pb);
        pb.setVisibility(View.GONE);
    }

    private void saveTask(){
        SharedPreferences appSharedPrefs = PreferenceManager.getDefaultSharedPreferences(this.getBaseContext());
        SharedPreferences.Editor prefsEditor = appSharedPrefs.edit();
        Gson gson = new Gson();
        String json = gson.toJson(list);
        prefsEditor.putString("user", json);
        prefsEditor.apply();
    }

    private void loadSetting(){
        SharedPreferences sharedPref = getSharedPreferences("setting", 0);
        lalu = sharedPref.getString("lalu","30");
        akanDatang = sharedPref.getString("akanDatang","60");
    }

    public void pengaturan(View view) {
        startActivity(new Intent(this, SettingActivity.class));
    }
}