package com.example.dogecoinminer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.SpannableString;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.example.dogecoinminer.fragment.FragmentHome;
import com.example.dogecoinminer.fragment.FragmentTasks;
import com.example.dogecoinminer.fragment.FragmentWallet;
import com.example.dogecoinminer.fragment.FragmentWithdrawal;
import com.example.dogecoinminer.unit.CustomTypefaceSpan;
import com.example.dogecoinminer.unit.Tools;

public class MainActivity extends AppCompatActivity {

    private TextView ToolbarTitle;
    public static BottomNavigationView navigation;


    private final BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @SuppressLint("NonConstantResourceId")
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    ToolbarTitle.setText(R.string.app_name);
                    MainActivity.this.loadFragment(new FragmentHome());
                    return true;
                case R.id.navigation_wallet:
                    MainActivity.this.ToolbarTitle.setText(R.string.bottom_nav_wallet);
                    MainActivity.this.loadFragment(new FragmentWallet());
                    return true;
                case R.id.navigation_task:
                    MainActivity.this.ToolbarTitle.setText(R.string.bottom_nav_task);
                    MainActivity.this.loadFragment(new FragmentTasks());
                    return true;
                case R.id.navigation_withdrawal:
                    MainActivity.this.ToolbarTitle.setText(R.string.bottom_nav_withdrawal);
                    MainActivity.this.loadFragment(new FragmentWithdrawal());
                    return true;
                default:
                    return false;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setToolbar();
        loadTheFragment();


    }

    public void setToolbar()
    {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        this.ToolbarTitle = findViewById(R.id.toolbar_title);
        this.ToolbarTitle.setText(R.string.bottom_nav_home);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(false);
        actionBar.setHomeButtonEnabled(false);
        actionBar.setDisplayShowTitleEnabled(false);
        Tools.changeOverflowMenuIconColor(toolbar, Color.WHITE);

    }

    private void loadTheFragment()
    {
        navigation = findViewById(R.id.bottom_nav_bar);
        navigation.setOnNavigationItemSelectedListener(this.mOnNavigationItemSelectedListener);
        navigation.setSelectedItemId(R.id.navigation_home);

        Menu m = navigation.getMenu();
        for (int i = 0; i < m.size(); i++) {
            MenuItem mi = m.getItem(i);
            SubMenu subMenu = mi.getSubMenu();
            if (subMenu != null && subMenu.size() > 0) {
                for (int j = 0; j < subMenu.size(); j++) {
                    applyFontToMenuItem(subMenu.getItem(j));
                }
            }
            applyFontToMenuItem(mi);
        }

        loadFragment(new FragmentHome());
    }

    private void loadFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.enter, R.anim.exit, R.anim.pop_enter, R.anim.pop_exit);
        transaction.replace(R.id.frame_container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    private void applyFontToMenuItem(MenuItem mi) {
        Typeface font = ResourcesCompat.getFont(this, R.font.font1);
        SpannableString mNewTitle = new SpannableString(mi.getTitle());
        mNewTitle.setSpan(new CustomTypefaceSpan("", font), 0, mNewTitle.length(), 18);
        mi.setTitle(mNewTitle);
    }
}