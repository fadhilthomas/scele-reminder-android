package com.wibudev.scere.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.wibudev.scere.R;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.Calendar;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    private TextView tvNPM;
    private TextView tvPass;
    private TextInputLayout tilNPM;
    private TextInputLayout tilPass;
    private String npm = "null";
    private String pass = "null";
    private String nama = "null";
    private CheckBox mCbShowPwd;
    private Button btMasuk;
    private boolean logged = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Typeface custom_font = Typeface.createFromAsset(getAssets(),  "fonts/font.ttf");
        Typeface custom_font_bold = Typeface.createFromAsset(getAssets(),  "fonts/fontBold.ttf");

        tvNPM = findViewById(R.id.tvNPM);
        tvPass = findViewById(R.id.tvPass);
        btMasuk = findViewById(R.id.btMasuk);
        tilNPM = findViewById(R.id.tilNPM);
        tilPass = findViewById(R.id.tilPass);

        tvNPM.setTypeface(custom_font);
        tvPass.setTypeface(custom_font);
        btMasuk.setTypeface(custom_font);
        tilNPM.setTypeface(custom_font);
        tilPass.setTypeface(custom_font);

        mCbShowPwd = findViewById(R.id.cbShowPwd);
        mCbShowPwd.setTypeface(custom_font);
        mCbShowPwd.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (!isChecked) {
                    tvPass.setTransformationMethod(PasswordTransformationMethod.getInstance());
                } else {
                    tvPass.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                }
            }
        });
    }

    public void login(View view) {
        if (TextUtils.isEmpty(tvNPM.getText())) {
            Snackbar.make(findViewById(android.R.id.content), "NPM tidak boleh kosong", Snackbar.LENGTH_LONG).show();
        } else if (TextUtils.isEmpty(tvPass.getText())) {
            Snackbar.make(findViewById(android.R.id.content), "Kata sandi tidak boleh kosong", Snackbar.LENGTH_LONG).show();
        } else {
            final ProgressDialog progressDialog = new ProgressDialog(this,R.style.AppTheme_Dark_Dialog);
            progressDialog.setIndeterminate(true);
            progressDialog.setCancelable(false);
            progressDialog.setMessage("Proses ...");
            progressDialog.show();
            loginAcc();
        }
    }

    private void saveLogin(){
        npm = tvNPM.getText().toString();
        pass = tvPass.getText().toString();
        SharedPreferences sharedPref= getSharedPreferences("login", 0);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("npm", npm);
        editor.putString("pass", pass);
        editor.putString("nama", nama);
        editor.apply();
    }

    private void loginAcc() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    npm = tvNPM.getText().toString();
                    pass = tvPass.getText().toString();
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
                    logged = !nama.contains("Login");
                    if(logged) {
                        saveLogin();
                        Intent intent = new Intent(LoginActivity.this, TaskActivity.class);
                        intent.putExtra("first", "1");
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    }else{
                        Snackbar.make(findViewById(android.R.id.content), "NPM atau Kata sandi salah", Snackbar.LENGTH_LONG).show();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}