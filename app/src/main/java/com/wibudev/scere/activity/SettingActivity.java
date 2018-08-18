package com.wibudev.scere.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.wibudev.scere.R;

public class SettingActivity extends AppCompatActivity {

    private TextView tvAkanDatang;
    private TextView tvLalu;
    private TextView tvJudul;
    private EditText etAkanDatang;
    private EditText etLalu;
    private Button btSimpan;

    private String lalu = "30";
    private String akanDatang = "60";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        loadSetting();

        tvAkanDatang = findViewById(R.id.tvAkanDatang);
        tvLalu = findViewById(R.id.tvLalu);
        tvJudul = findViewById(R.id.tvJudul);
        etAkanDatang = findViewById(R.id.etAkanDatang);
        etLalu = findViewById(R.id.etLalu);
        btSimpan = findViewById(R.id.btSimpan);

        Typeface custom_font = Typeface.createFromAsset(getAssets(),  "fonts/font.ttf");
        Typeface custom_font_bold = Typeface.createFromAsset(getAssets(),  "fonts/fontBold.ttf");

        tvAkanDatang.setTypeface(custom_font);
        tvLalu.setTypeface(custom_font);
        tvJudul.setTypeface(custom_font_bold);
        etAkanDatang.setTypeface(custom_font);
        etLalu.setTypeface(custom_font);
        btSimpan.setTypeface(custom_font);
        etLalu.setText(lalu);
        etAkanDatang.setText(akanDatang);
    }

    private void saveSetting(){
        lalu = etLalu.getText().toString();
        akanDatang = etAkanDatang.getText().toString();
        if (TextUtils.isEmpty(lalu)) {
            Snackbar.make(findViewById(android.R.id.content), "Rentang hari harus diisi", Snackbar.LENGTH_LONG).show();
        }else if (TextUtils.isEmpty(akanDatang)) {
            Snackbar.make(findViewById(android.R.id.content), "Rentang hari harus diisi", Snackbar.LENGTH_LONG).show();
        }else {
            SharedPreferences sharedPref = getSharedPreferences("setting", 0);
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putString("lalu", lalu);
            editor.putString("akanDatang", akanDatang);
            editor.apply();
            Intent intent = new Intent(SettingActivity.this, TaskActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
    }

    private void loadSetting(){
        SharedPreferences sharedPref = getSharedPreferences("setting", 0);
        lalu = sharedPref.getString("lalu","30");
        akanDatang = sharedPref.getString("akanDatang","60");
    }

    public void simpan(View view) {
        saveSetting();
    }
}