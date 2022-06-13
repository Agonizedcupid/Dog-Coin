package com.example.dogecoinminer.fragment;

import static android.content.Context.MODE_PRIVATE;

import static com.example.dogecoinminer.Config.adUnitId;
import static com.example.dogecoinminer.Config.minerCoin;
import static com.example.dogecoinminer.Config.testMode;
import static com.example.dogecoinminer.Config.unityGameID;
import static com.example.dogecoinminer.Config.userPoints;
import static com.example.dogecoinminer.Config.userProfile;
import static com.unity3d.services.core.properties.ClientProperties.getApplicationContext;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dogecoinminer.MainActivity;
import com.example.dogecoinminer.R;
import com.example.dogecoinminer.activities.LoginActivity;
import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.unity3d.ads.IUnityAdsListener;
import com.unity3d.ads.UnityAds;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.math.RoundingMode;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
import java.util.Iterator;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.net.ssl.HttpsURLConnection;

public class FragmentHome extends Fragment implements IUnityAdsListener {

    private Button miningBtn, multiMiningBtn;
    private Context context;
    private TextView txtProgress, dogecoinBalance;
    private Animation animation;
    private double coin;
    private SharedPreferences prefs;
    private Boolean multiMining = false;
    private ProgressDialog pDialog;
    private String email, password;
    private InterstitialAd mInterstitialAd;
    private double theCoin;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);

        miningBtn = rootView.findViewById(R.id.miningBtn);
        multiMiningBtn = rootView.findViewById(R.id.multiMiningBtn);
        dogecoinBalance = rootView.findViewById(R.id.dogecoinBalance);
        txtProgress = rootView.findViewById(R.id.txt);

        // Init Admob Ads
        MobileAds.initialize(getActivity(), new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });
        AdView mAdView = rootView.findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        setInterAds();

        // LOGS
        Log.e("MINER", "1" + multiMining.toString());

        prefs = rootView.getContext().getSharedPreferences("User", MODE_PRIVATE);
        email = prefs.getString("userEmail", "");
        password = prefs.getString("userPassword", "");

        miningBtn.setOnClickListener(view ->
        {
            showDialogConfirm();
        });

        multiMiningBtn.setOnClickListener(v ->
        {
            DisplayRewardedAd();
            Toast.makeText(getContext(), "Loading the video..", Toast.LENGTH_SHORT).show();
        });

        openProgressBar();
        new SendRequest().execute();

        //Unity Ads
        // Declare a new listener:
        final UnityAdsListener myAdsListener = new UnityAdsListener();
        UnityAds.addListener(myAdsListener);
        UnityAds.initialize(getActivity(), unityGameID, myAdsListener, testMode);
        UnityAds.getDebugMode();

        return rootView;
    }
    private void setCounter()
    {
        long totalSeconds = 10;
        long intervalSeconds = 1;

        new CountDownTimer(totalSeconds * 1000, intervalSeconds * 1000) {

            public void onTick(long millisUntilFinished) {
                txtProgress.setText("%" + (totalSeconds * 1000 - millisUntilFinished) / 1000);
            }

            public void onFinish() {
                setOnCompletedMining();
            }
        }.start();
    }

    private void setOnCompletedMining()
    {
        MainActivity.navigation.setVisibility(View.VISIBLE);
        miningBtn.setClickable(true);
        miningBtn.setBackground(getContext().getDrawable(R.drawable.cpu));
        stopAnimation();
        setCoins();
    }

    private void setAnimation()
    {
        animation = AnimationUtils.loadAnimation(getContext(), R.anim.cpu_anim);
        miningBtn.startAnimation(animation);
    }

    private void stopAnimation()
    {
        animation.cancel();
    }

    private void setCoins()
    {
        if (multiMining == true)
        {
            theCoin = minerCoin * 2;
            multiMining = false;
            multiMiningBtn.setVisibility(View.VISIBLE);
        }else{
            theCoin = minerCoin;
        }
        openProgressBar();
        new SendRequest2().execute();
        txtProgress.setVisibility(View.INVISIBLE);
    }


    // Implement the IUnityAdsListener interface methods:
    private class UnityAdsListener implements IUnityAdsListener {

        @Override
        public void onUnityAdsReady (String adUnitId) {
            // Implement functionality for an ad being ready to show.
            //Toast.makeText(getApplicationContext(), "Ready", Toast.LENGTH_LONG).show();
        }

        @Override
        public void onUnityAdsStart (String adUnitId) {
            // Implement functionality for a user starting to watch an ad.
            // Toast.makeText(getApplicationContext(), "AdsStart", Toast.LENGTH_LONG).show();
        }

        @Override
        public void onUnityAdsFinish (String adUnitId, UnityAds.FinishState finishState) {
            // Implement functionality for a user finishing an ad.
            multiMining = true;
            multiMiningBtn.setVisibility(View.INVISIBLE);
            // LOGS
            Log.e("MINER", "2" + multiMining.toString());
        }

        @Override
        public void onUnityAdsError (UnityAds.UnityAdsError error, String message) {
            // Implement functionality for a Unity Ads service error occurring.
            Toast.makeText(getContext(), "No Ads, Try Again Later",  Toast.LENGTH_LONG).show();
        }
    }

    // Implement a function to display an ad if the Ad Unit is ready:
    public void DisplayRewardedAd () {
        if (UnityAds.isReady (adUnitId)) {
            UnityAds.show (getActivity(), adUnitId);
        }
    }

    @Override
    public void onUnityAdsReady(String s) {

    }

    @Override
    public void onUnityAdsStart(String s) {

    }

    @Override
    public void onUnityAdsFinish(String s, UnityAds.FinishState finishState) {

    }

    @Override
    public void onUnityAdsError(UnityAds.UnityAdsError unityAdsError, String s) {
        Toast.makeText(getContext(), "No Video Ads, Try Again Later",  Toast.LENGTH_LONG).show();
    }


    private void showDialogConfirm()
    {
        final Dialog dialog = new Dialog(getContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_confirm);
        dialog.setCancelable(true);

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;

        final EditText editTextConfirm = dialog.findViewById(R.id.Confirm_EditTxt);
        final TextView randomNumberTxt = dialog.findViewById(R.id.randomNumberTxt);

        final int random = new Random().nextInt(999) + 10;

        randomNumberTxt.setText(String.valueOf(random));

        ((AppCompatButton) dialog.findViewById(R.id.Done_Dialog)).setOnClickListener(v ->
        {
            String theNumber = editTextConfirm.getText().toString().trim();
            String NumberTxt = randomNumberTxt.getText().toString();

            if (TextUtils.isEmpty(theNumber))
            {
                editTextConfirm.setError(getString(R.string.Empty));
            }
            if(theNumber.equals(NumberTxt))
            {
                dialog.cancel();

                if (isConnectingToInternet(getActivity()))
                {
                    // Show Ads
                    showInterstitial();

                    setCounter();
                    MainActivity.navigation.setVisibility(View.INVISIBLE);
                    miningBtn.setClickable(false);
                    miningBtn.setBackground(getContext().getDrawable(R.drawable.cpu_bolt));
                    setAnimation();
                    txtProgress.setVisibility(View.VISIBLE);
                }else {
                    Toast.makeText(getContext(), "There is no internet connection ! Try again", Toast.LENGTH_LONG).show();
                }

            }
            else {
                editTextConfirm.setError(getString(R.string.Wrong));
            }

        });

        dialog.show();
        dialog.getWindow().setAttributes(lp);
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


    public class SendRequest extends AsyncTask<String, Void, String> {

        protected void onPreExecute(){}

        protected String doInBackground(String... arg0) {

            try{

                URL url = new URL(userProfile);

                JSONObject postDataParams = new JSONObject();

                postDataParams.put("email", email);
                postDataParams.put("password", password);

                Log.e("params",postDataParams.toString());

                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(15000 /* milliseconds */);
                conn.setConnectTimeout(15000 /* milliseconds */);
                conn.setRequestMethod("POST");
                conn.setDoInput(true);
                conn.setDoOutput(true);

                OutputStream os = conn.getOutputStream();
                BufferedWriter writer = new BufferedWriter(
                        new OutputStreamWriter(os, "UTF-8"));
                writer.write(getPostDataString(postDataParams));

                writer.flush();
                writer.close();
                os.close();

                int responseCode=conn.getResponseCode();

                if (responseCode == HttpsURLConnection.HTTP_OK) {

                    BufferedReader in=new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    StringBuffer sb = new StringBuffer("");
                    String line="";

                    while((line = in.readLine()) != null) {

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

            Pattern pattern = Pattern.compile("M1(.*?)M2");
            Matcher matcher = pattern.matcher(result);

            if (matcher.find())
            {
                DecimalFormat df = new DecimalFormat("#.#######");
                df.setRoundingMode(RoundingMode.CEILING);

                coin = Double.parseDouble(matcher.group(1));

                dogecoinBalance.setText(df.format(coin));
            }

            if (result.contains("Sorry, your email or password is incorrect"))
            {
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
                getActivity().finish();
            }

            if (result.equals("0"))
            {
                unconfirmedAccountDialog();
            }

            closeProgressBar();
        }
    }

    public class SendRequest2 extends AsyncTask<String, Void, String> {

        protected void onPreExecute(){}

        protected String doInBackground(String... arg0) {

            try{
                URL url = new URL(userPoints);

                JSONObject postDataParams = new JSONObject();
                postDataParams.put("email", email);
                postDataParams.put("password", password);
                postDataParams.put("mocions", theCoin);
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

            Pattern pattern2 = Pattern.compile("M1(.*?)M2");
            Matcher matcher2 = pattern2.matcher(result);

            if (matcher2.find())
            {
                DecimalFormat df = new DecimalFormat("#.#######");
                df.setRoundingMode(RoundingMode.CEILING);

                dogecoinBalance.setText(df.format(coin));
                Log.e("MINER-Coin", df.format(coin));
                new SendRequest().execute();
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

    private void unconfirmedAccountDialog()
    {
        AlertDialog.Builder build = new AlertDialog.Builder(getApplicationContext());
        build.setTitle("Sorry !");
        build.setMessage("You Have Problem in your Account, please contact us on our email.");
        build.setCancelable(false);

        build.setPositiveButton("Ok", new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int which){
                dialog.cancel();
                getActivity().finish();
            }
        });

        AlertDialog olustur = build.create();
        olustur.show();
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


    private void setInterAds() {
        AdRequest adRequest = new AdRequest.Builder().build();

        InterstitialAd.load(getActivity(), getString(R.string.admob_interstitial_id), adRequest,
                new InterstitialAdLoadCallback() {
                    @Override
                    public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                        // The mInterstitialAd reference will be null until
                        // an ad is loaded.
                        mInterstitialAd = interstitialAd;
                        Log.i("TAG", "onAdLoaded");

                        mInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                            @Override
                            public void onAdDismissedFullScreenContent() {
                                // Called when fullscreen content is dismissed.
                                Log.d("TAG", "The ad was dismissed.");
                            }

                            @Override
                            public void onAdFailedToShowFullScreenContent(AdError adError) {
                                // Called when fullscreen content failed to show.
                                Log.d("TAG", "The ad failed to show.");
                            }

                            @Override
                            public void onAdShowedFullScreenContent() {
                                mInterstitialAd = null;
                                Log.d("TAG", "The ad was shown.");
                            }
                        });
                    }

                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        // Handle the error
                        Log.i("TAG", loadAdError.getMessage());
                        mInterstitialAd = null;
                    }
                });
    }

    private void showInterstitial() {

        if (mInterstitialAd != null) {
            mInterstitialAd.show(getActivity());
        } else {
            Toast.makeText(getActivity(), "Ad did not load, try again later", Toast.LENGTH_LONG).show();
        }
    }


}