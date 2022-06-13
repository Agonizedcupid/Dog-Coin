package com.example.dogecoinminer.activities;

import static com.example.dogecoinminer.Config.emailAddress;
import static com.example.dogecoinminer.Config.privacyPolicyUrl;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.example.dogecoinminer.BuildConfig;
import com.example.dogecoinminer.R;

public class AboutActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        setToolbar();

        TextView appVersion = findViewById(R.id.app_verion);
        appVersion.setText("" + BuildConfig.VERSION_NAME);

    }

    private void setToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        TextView toolbarTitle = (TextView) findViewById(R.id.toolbar_title);
        toolbarTitle.setText(R.string.toolbar_about);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(false);
        actionBar.setDisplayShowTitleEnabled(false);
    }

    public void rateClick(View view)
    {
        try {
            Uri uri = Uri.parse("market://details?id=" + getPackageName());
            Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(goToMarket);
        } catch (ActivityNotFoundException e) {
            startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse("http://play.google.com/store/apps/details?id=" + getPackageName())));
        }
    }

    public void tandClick(View view)
    {
        Intent pp = new Intent(Intent.ACTION_VIEW);
        pp.setData(Uri.parse(privacyPolicyUrl));
        startActivity(pp);
    }

    public void contactClick(View view)
    {
        Intent email = new Intent(Intent.ACTION_SEND);
        email.putExtra(Intent.EXTRA_EMAIL, new String[]{emailAddress});
        email.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.app_name));
        email.putExtra(Intent.EXTRA_TEXT, "Text");
        email.setType("message/rfc822");
        startActivity(Intent.createChooser(email, "Choose an Email client :"));
    }

    public void myWebsiteClick(View view)
    {
        Intent pp = new Intent(Intent.ACTION_VIEW);
        pp.setData(Uri.parse("https://ibrahimodeh.com"));
        startActivity(pp);
    }
}