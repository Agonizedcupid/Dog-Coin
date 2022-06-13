package com.example.dogecoinminer.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.dogecoinminer.BuildConfig;
import com.example.dogecoinminer.MainActivity;
import com.example.dogecoinminer.R;
import com.mikhaellopez.circularimageview.CircularImageView;

public class SplashActivity extends AppCompatActivity {

    private CircularImageView logo;
    private TextView textApp, appVersionTxt;
    private ConstraintLayout no_internet;
    private LinearLayout lyt_no_connection;
    private String userEmail, userPassword;
    private ProgressBar progressBar;
    private SharedPreferences prefs;
    private String agreePrivacy;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);

        logo = findViewById(R.id.logo);
        textApp = findViewById(R.id.textApp);
        progressBar = findViewById(R.id.progress_bar);
        lyt_no_connection = findViewById(R.id.lyt_no_connection);
        no_internet = findViewById(R.id.no_internet);
        appVersionTxt = findViewById(R.id.appVersionTxt);

        appVersionTxt.setText("v" + BuildConfig.VERSION_NAME);

        prefs = getSharedPreferences("App", Context.MODE_PRIVATE);
        agreePrivacy = prefs.getString("agree", "");

        prefs = this.getSharedPreferences("User", Context.MODE_PRIVATE);
        userEmail = prefs.getString("userEmail", "");
        userPassword = prefs.getString("userPassword", "");


        int splashTimeOut = 2000;
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                if (isConnectingToInternet(SplashActivity.this)) {
                    Intent intent;
                    if(agreePrivacy.equals("yes"))
                    {
                        if (userEmail.isEmpty() || userPassword.isEmpty()) {
                            intent = new Intent(SplashActivity.this, SignUpActivity.class);
                        }else {
                            intent = new Intent(SplashActivity.this, MainActivity.class);
                        }

                    }else {
                        intent = new Intent(SplashActivity.this, WelcomeActivity.class);
                    }
                    startActivity(intent);
                    finish();
                } else {
                    showNoInterNet();
                }

            }
        }, splashTimeOut);

        lyt_no_connection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                progressBar.setVisibility(View.VISIBLE);
                no_internet.setVisibility(View.GONE);

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (isConnectingToInternet(SplashActivity.this)) {
                            Intent intent;
                            if(agreePrivacy.equals("yes"))
                            {
                                intent = new Intent(SplashActivity.this, MainActivity.class);
                            }else {
                                intent = new Intent(SplashActivity.this, WelcomeActivity.class);
                            }
                            startActivity(intent);
                            finish();
                        } else {
                            progressBar.setVisibility(View.GONE);
                            no_internet.setVisibility(View.VISIBLE);
                        }
                    }
                }, 1000);
            }
        });
    }

    private void showNoInterNet() {
        logo.setVisibility(View.GONE);
        textApp.setVisibility(View.GONE);

        progressBar.setVisibility(View.GONE);
        no_internet.setVisibility(View.VISIBLE);
    }

    public static boolean isConnectingToInternet(Context context) {
        ConnectivityManager connectivity =
                (ConnectivityManager) context.getSystemService(
                        Context.CONNECTIVITY_SERVICE);
        if (connectivity != null) {
            NetworkInfo[] info = connectivity.getAllNetworkInfo();
            if (info != null)
                for (int i = 0; i < info.length; i++)
                    if (info[i].getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }
        }
        return false;
    }
}