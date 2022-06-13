package com.example.dogecoinminer.activities;


import static com.example.dogecoinminer.Config.privacyPolicyUrl;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;

import com.example.dogecoinminer.R;

public class WelcomeActivity extends AppCompatActivity {

    private CheckBox agreeChekBox;
    private Button nextBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        agreeChekBox = findViewById(R.id.agreeChekBox);
        nextBtn = findViewById(R.id.nextBtn);

        String checkBoxText = getString(R.string.welcome_agree) + " " +"<a href='" + privacyPolicyUrl +"'>" + getString(R.string.privacy_policy) + "</a>";
        agreeChekBox.setText(Html.fromHtml(checkBoxText));
        agreeChekBox.setMovementMethod(LinkMovementMethod.getInstance());

        agreeChekBox.setOnClickListener(v -> {
            if(agreeChekBox.isChecked()){
                nextBtn.setVisibility(View.VISIBLE);
            }
            else {
                nextBtn.setVisibility(View.INVISIBLE);
            }
        });

        nextBtn.setOnClickListener(v -> {
            Intent intent = new Intent(WelcomeActivity.this, SignUpActivity.class);
            startActivity(intent);
            finish();

            SharedPreferences prefs = getSharedPreferences("App", Context.MODE_PRIVATE);
            prefs.edit().putString("agree", "yes").apply();
        });

    }
}