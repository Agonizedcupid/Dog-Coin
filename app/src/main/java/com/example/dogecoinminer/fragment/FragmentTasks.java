package com.example.dogecoinminer.fragment;

import static com.example.dogecoinminer.Config.tasksBonus;
import static com.example.dogecoinminer.Config.userPoints;

import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.dogecoinminer.R;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;

import javax.net.ssl.HttpsURLConnection;

public class FragmentTasks extends Fragment {

    private ProgressDialog pDialog;
    private String email ,password;
    private SharedPreferences prefs;
    private LinearLayout Task1Btn, Task2Btn, Task3Btn;
    private int taskSave1, taskSave2, taskSave3;
    private double bonusCoin;

    @Override
    public void onResume() {
        super.onResume();

        if (taskSave1 >= 1) {
            Task1Btn.setVisibility(View.GONE);
        }
        if (taskSave2 >= 1) {
            Task2Btn.setVisibility(View.GONE);
        }
        if (taskSave3 >= 1) {
            Task3Btn.setVisibility(View.GONE);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_tasks, container, false);

        Task1Btn = (LinearLayout) rootView.findViewById(R.id.task_1);
        Task2Btn = (LinearLayout) rootView.findViewById(R.id.task_2);
        Task3Btn = (LinearLayout) rootView.findViewById(R.id.task_3);

        prefs = getContext().getSharedPreferences("User", Context.MODE_PRIVATE);
        email = prefs.getString("userEmail", "");
        password = prefs.getString("userPassword", "");
        taskSave1 = prefs.getInt("task1", 0);
        taskSave2 = prefs.getInt("task2", 0);
        taskSave3 = prefs.getInt("task3", 0);

        if (taskSave1 >= 1) {
            Task1Btn.setVisibility(View.GONE);
        }
        if (taskSave2 >= 1) {
            Task2Btn.setVisibility(View.GONE);
        }
        if (taskSave3 >= 1) {
            Task3Btn.setVisibility(View.GONE);
        }

        Task1Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                taskSave1 = taskSave1 + 1;
                SharedPreferences prefs = getContext().getSharedPreferences("User", Context.MODE_PRIVATE);
                prefs.edit().putInt("task1", taskSave1).apply();

                Toast.makeText(getContext(), "If You Cheat You will not get paid", Toast.LENGTH_LONG).show();
                try {
                    Uri uri = Uri.parse("market://details?id=" + getContext().getPackageName());
                    Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
                    startActivity(goToMarket);
                } catch (ActivityNotFoundException e) {
                    startActivity(new Intent(Intent.ACTION_VIEW,
                            Uri.parse("http://play.google.com/store/apps/details?id=" + getContext().getPackageName())));
                }

                openProgressBar();
                bonusCoin = tasksBonus;
                new SendRequest().execute();
            }
        });

        Task2Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                taskSave2 = taskSave2 + 1;
                SharedPreferences prefs = getContext().getSharedPreferences("User", Context.MODE_PRIVATE);
                prefs.edit().putInt("task2", taskSave2).apply();

                Intent pp = new Intent(Intent.ACTION_VIEW);
                pp.setData(Uri.parse("https://www.youtube.com/channel/UC1sx0hsj_ynkA9fauDLRAzg"));
                startActivity(pp);

                openProgressBar();
                bonusCoin = tasksBonus;
                new SendRequest().execute();
            }
        });

        Task3Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                taskSave3 = taskSave3 + 1;
                SharedPreferences prefs = getContext().getSharedPreferences("User", Context.MODE_PRIVATE);
                prefs.edit().putInt("task3", taskSave3).apply();

                Intent pp = new Intent(Intent.ACTION_VIEW);
                pp.setData(Uri.parse("https://www.youtube.com/watch?v=0XdLRJjXRho"));
                startActivity(pp);

                openProgressBar();
                bonusCoin = tasksBonus;
                new SendRequest().execute();
            }
        });

        return rootView;
    }

    public class SendRequest extends AsyncTask<String, Void, String> {

        protected void onPreExecute(){}

        protected String doInBackground(String... arg0) {

            try{
                URL url = new URL(userPoints);

                JSONObject postDataParams = new JSONObject();
                postDataParams.put("email", email);
                postDataParams.put("password", password);
                postDataParams.put("mocions", bonusCoin);
                Log.e("params",postDataParams.toString());

                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(20000 /* milliseconds */);
                conn.setConnectTimeout(20000 /* milliseconds */);
                conn.setRequestMethod("POST");
                conn.setDoInput(true);
                conn.setDoOutput(true);

                OutputStream os = conn.getOutputStream();
                BufferedWriter writer = new BufferedWriter(
                        new OutputStreamWriter(os, StandardCharsets.UTF_8));
                writer.write(getPostDataString(postDataParams));

                writer.flush();
                writer.close();
                os.close();

                int responseCode=conn.getResponseCode();

                if (responseCode == HttpsURLConnection.HTTP_OK)
                {
                    BufferedReader in=new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    StringBuffer sb = new StringBuffer("");
                    String line = "";

                    while((line = in.readLine()) != null)
                    {
                        sb.append(line);
                        break;
                    }
                    in.close();
                    return sb.toString();
                }
                else {
                    return new String("false : "+responseCode);
                }
            }
            catch(Exception e){
                return new String("Exception: " + e.getMessage());
            }
        }

        @Override
        protected void onPostExecute(String result) {


            closeProgressBar();
        }
    }

    public String getPostDataString(JSONObject params) throws Exception {

        StringBuilder result = new StringBuilder();
        boolean first = true;

        Iterator<String> itr = params.keys();

        while(itr.hasNext()){

            String key= itr.next();
            Object value = params.get(key);

            if (first)
                first = false;
            else
                result.append("&");

            result.append(URLEncoder.encode(key, "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(value.toString(), "UTF-8"));

        }
        return result.toString();
    }


    protected void openProgressBar(){
        pDialog = new ProgressDialog(getContext());
        pDialog.setMessage("Loading..");
        pDialog.show();
        pDialog.setCancelable(false);
        pDialog.setCanceledOnTouchOutside(false);
    }

    protected void closeProgressBar(){
        pDialog.dismiss();
    }
}
