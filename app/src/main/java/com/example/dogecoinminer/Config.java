package com.example.dogecoinminer;

public class Config {

    // Change this --------------------------------------------------------
    // Type your website url without "/" in the end:
    public static String websiteUrl = "https://ibrahimodeh.com/codecanyon/DogecoinMinerApp";
    // Your Email Address:
    public static final String emailAddress = "example@yourdomain.com";
    // User Earning each mining:
    public static double minerCoin = 0.0001;
    // Tasks Bonus:
    public static final double tasksBonus = 0.0005;

    // Unity Ads
    public static final String unityGameID = "4595629";
    public static final String adUnitId = "Rewarded_Android";
    public static final Boolean testMode = true;


    // Do not change -----------------------------------------------------
    public static final String websiteHomeUrl = websiteUrl + "/index.php";
    public static final String privacyPolicyUrl = websiteUrl + "/privacy_policy.php";
    public static final String userProfile = websiteUrl + "/api/profile.php?app=" + BuildConfig.APPLICATION_ID;
    public static final String userLogin = websiteUrl + "/api/login.php";
    public static final String userWithdrawal = websiteUrl + "/api/withdrawal.php";
    public static final String userRegister = websiteUrl + "/api/register.php";
    public static final String userPoints = websiteUrl + "/api/points.php" ;
    public static final String withdrawalSettings = websiteUrl + "/api/withdrawalSettings.php";
    public static final String userWallet = websiteUrl + "/api/wallet.php";
    public static final String userFeedback = websiteUrl + "/api/feedback.php";
    public static final String appUpdate = websiteUrl + "/api/appUpdate.php";

}
