package com.example.dogecoinminer.fragment;

import static com.example.dogecoinminer.Config.userWithdrawal;
import static com.example.dogecoinminer.Config.withdrawalSettings;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.dogecoinminer.R;
import com.example.dogecoinminer.activities.AboutActivity;
import com.example.dogecoinminer.activities.FeedbackActivity;

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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.net.ssl.HttpsURLConnection;

import es.dmoral.toasty.Toasty;

public class FragmentWithdrawal extends Fragment {

    private Button withdrawal_Btn, aboutBtn, feedbackBtn;
    private SharedPreferences prefs;
    private String email ,password;
    private ProgressDialog pDialog;
    private TextView minWithdrawalTxt;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_withdrawal, container, false);

        prefs = getContext().getSharedPreferences("User", Context.MODE_PRIVATE);
        email = prefs.getString("userEmail", "");
        password = prefs.getString("userPassword", "");

        withdrawal_Btn = rootView.findViewById(R.id.withdrawal_Btn);
        minWithdrawalTxt = rootView.findViewById(R.id.MinWithdrawalTxt);
        aboutBtn = rootView.findViewById(R.id.about_Btn);
        feedbackBtn = rootView.findViewById(R.id.feedback_Btn);

        withdrawal_Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openProgressBar();
                new SendRequest().execute();
            }
        });

        aboutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), AboutActivity.class);
                startActivity(intent);
            }
        });

        feedbackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), FeedbackActivity.class);
                startActivity(intent);
            }
        });

        openProgressBar();
        new SendRequest1().execute();

        return rootView;
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


    public class SendRequest extends AsyncTask<String, Void, String> {

        protected void onPreExecute(){}

        protected String doInBackground(String... arg0) {

            try{
                URL url = new URL(userWithdrawal);

                JSONObject postDataParams = new JSONObject();
                postDataParams.put("email", email);
                postDataParams.put("method", "CoinBase");

                Log.e("params",postDataParams.toString());

                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(15000 /* milliseconds */);
                conn.setConnectTimeout(15000 /* milliseconds */);
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
                    BufferedReader in= new BufferedReader(new InputStreamReader(conn.getInputStream()));
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

            Pattern pattern2 = Pattern.compile("M1(.*?)M2");
            Matcher matcher2 = pattern2.matcher(result);

            if (matcher2.find())
            {
                int dogecoin = Integer.parseInt(matcher2.group(1));

            }

            if (result.equals("You need more Dogcoin"))
            {
                Toasty.error(getContext(), "You do not have a minimum withdrawal amount!", Toast.LENGTH_LONG, true).show();

            }
            if (result.equals("Request Send"))
            {
                Toasty.success(getContext(), "Withdrawal request has been sent successfully!" , Toast.LENGTH_LONG, true).show();
            }

            //Toast.makeText(getContext(), "r: " + result, Toast.LENGTH_LONG).show();
            closeProgressBar();
        }
    }

    public class SendRequest1 extends AsyncTask<String, Void, String> {

        protected void onPreExecute(){}

        protected String doInBackground(String... arg0) {

            try{
                URL url = new URL(withdrawalSettings);

                JSONObject postDataParams = new JSONObject();
                postDataParams.put("email", email);

                Log.e("params",postDataParams.toString());

                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(15000 /* milliseconds */);
                conn.setConnectTimeout(15000 /* milliseconds */);
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

            Pattern pattern = Pattern.compile("N1(.*?)N2");
            Pattern pattern2 = Pattern.compile("M1(.*?)M2");
            Pattern pattern3 = Pattern.compile("E1(.*?)E2");
            Pattern pattern4 = Pattern.compile("I1(.*?)I2");
            Matcher matcher = pattern.matcher(result);
            Matcher matcher2 = pattern2.matcher(result);
            Matcher matcher3 = pattern3.matcher(result);
            Matcher matcher4 = pattern4.matcher(result);

            if (matcher.find() && matcher2.find() && matcher3.find() && matcher4.find())
            {
                minWithdrawalTxt.setText("" + matcher.group(1));
            }

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
}
