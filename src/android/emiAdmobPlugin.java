package emi.indo.cordova.plugin.admob;import org.apache.cordova.CordovaPlugin;import org.apache.cordova.CallbackContext;import org.json.JSONArray;import org.json.JSONException;import android.util.Log;import org.apache.cordova.CordovaInterface;import org.apache.cordova.CordovaWebView;import org.json.JSONObject;import android.view.ViewGroup;import android.widget.RelativeLayout;import androidx.annotation.NonNull;import com.google.android.gms.ads.AdListener;import com.google.android.gms.ads.MobileAds;import com.google.android.gms.ads.OnUserEarnedRewardListener;import com.google.android.gms.ads.initialization.InitializationStatus;import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;import com.google.android.gms.ads.AdError;import com.google.android.gms.ads.AdRequest;import com.google.android.gms.ads.AdSize;import com.google.android.gms.ads.AdView;import com.google.android.gms.ads.FullScreenContentCallback;import com.google.android.gms.ads.LoadAdError;import com.google.android.gms.ads.interstitial.InterstitialAd;import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;import com.google.android.gms.ads.rewarded.RewardItem;import com.google.android.gms.ads.rewarded.RewardedAd;import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback;import com.google.android.gms.ads.rewardedinterstitial.RewardedInterstitialAd;import com.google.android.gms.ads.rewardedinterstitial.RewardedInterstitialAdLoadCallback;import java.util.Objects;public class emiAdmobPlugin extends CordovaPlugin{private static final String TAG="emiAdmobPlugin";private InterstitialAd mInterstitialAd;private RewardedAd rewardedAd;private RewardedInterstitialAd rewardedInterstitialAd;private CordovaWebView cWebView;private RelativeLayout bannerViewLayout;private AdView bannerView;public void initialize(CordovaInterface cordova,CordovaWebView webView){super.initialize(cordova,webView);cWebView=webView;}public boolean execute(String action,JSONArray args,final CallbackContext callbackContext)throws JSONException{switch(action){case "initialize":cordova.getActivity().runOnUiThread(()-> MobileAds.initialize(cordova.getActivity(),initializationStatus -> cWebView.loadUrl("javascript:cordova.fireDocumentEvent('onInitializationComplete');")));return true;case "loadInterstitialAd":cordova.getActivity().runOnUiThread(()->{final String InterstitialAdAdUnitId=args.optString(0);AdRequest adRequest=new AdRequest.Builder().build();InterstitialAd.load(cordova.getActivity(),InterstitialAdAdUnitId,adRequest,new InterstitialAdLoadCallback(){@Override public void onAdLoaded(@NonNull InterstitialAd interstitialAd){cWebView.loadUrl("javascript:cordova.fireDocumentEvent('onAdLoaded.InterstitialAd');");mInterstitialAd=interstitialAd;Log.i(TAG,"onAdLoaded");}@Override public void onAdFailedToLoad(@NonNull LoadAdError loadAdError){Log.d(TAG,loadAdError.toString());mInterstitialAd=null;cWebView.loadUrl("javascript:cordova.fireDocumentEvent('onAdFailedToLoad.InterstitialAd');");}});});return true;case "showInterstitialAd":cordova.getActivity().runOnUiThread(()->{mInterstitialAd.show(cordova.getActivity());});return true;case "loadRewardedAd":cordova.getActivity().runOnUiThread(()->{final String RewardedAdAdUnitId=args.optString(0);AdRequest adRequest=new AdRequest.Builder().build();RewardedAd.load(cordova.getActivity(),RewardedAdAdUnitId,adRequest,new RewardedAdLoadCallback(){@Override public void onAdFailedToLoad(@NonNull LoadAdError loadAdError){Log.d(TAG,loadAdError.toString());rewardedAd=null;Log.d(TAG,"The rewarded ad wasn't ready yet.");cWebView.loadUrl("javascript:cordova.fireDocumentEvent('onAdFailedToLoad.RewardedAd');");}@Override public void onAdLoaded(@NonNull RewardedAd ad){rewardedAd=ad;Log.d(TAG,"Ad was loaded.");cWebView.loadUrl("javascript:cordova.fireDocumentEvent('onAdLoaded.RewardedAd');");}});});return true;case "showRewardedAd":cordova.getActivity().runOnUiThread(()->{if(rewardedAd!=null){rewardedAd.show(cordova.getActivity(),rewardItem ->{Log.d(TAG,"The user earned the reward.");int rewardAmount=rewardItem.getAmount();String rewardType=rewardItem.getType();});}else{Log.d(TAG,"The rewarded ad wasn't ready yet.");}});return true;case "loadRewardedInterstitialAd":cordova.getActivity().runOnUiThread(()->{final String RewardedInterstitialAdUnitId=args.optString(0);RewardedInterstitialAd.load(cordova.getActivity(),RewardedInterstitialAdUnitId,new AdRequest.Builder().build(),new RewardedInterstitialAdLoadCallback(){@Override public void onAdLoaded(RewardedInterstitialAd ad){Log.d(TAG,"Ad was loaded.");rewardedInterstitialAd=ad;cWebView.loadUrl("javascript:cordova.fireDocumentEvent('onAdLoaded.RewardedInterstitial');");}@Override public void onAdFailedToLoad(LoadAdError loadAdError){Log.d(TAG,loadAdError.toString());rewardedInterstitialAd=null;cWebView.loadUrl("javascript:cordova.fireDocumentEvent('onAdFailedToLoad.RewardedInterstitial');");}});});return true;case "showRewardedInterstitialAd":cordova.getActivity().runOnUiThread(()->{if(rewardedInterstitialAd!=null){rewardedInterstitialAd.show(cordova.getActivity(),rewardItem ->{Log.d(TAG,"The user earned the reward.");int rewardAmount=rewardItem.getAmount();String rewardType=rewardItem.getType();});}else{Log.d(TAG,"The rewarded ad wasn't ready yet.");}});return true;case "showBannerAd":cordova.getActivity().runOnUiThread(()->{final String bannerAdUnitId=args.optString(0);final String size=args.optString(1);final String position=args.optString(2);if(bannerViewLayout==null){bannerViewLayout=new RelativeLayout(cordova.getActivity());RelativeLayout.LayoutParams params=new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,RelativeLayout.LayoutParams.MATCH_PARENT);bannerViewLayout.setLayoutParams(params);((ViewGroup)Objects.requireNonNull(cWebView.getView())).addView(bannerViewLayout);}bannerView=new AdView(cordova.getActivity());if(Objects.equals(size,"BANNER")){bannerView.setAdSize(AdSize.BANNER);}else if(Objects.equals(size,"LARGE_BANNER")){bannerView.setAdSize(AdSize.LARGE_BANNER);}else if(Objects.equals(size,"MEDIUM_RECTANGLE")){bannerView.setAdSize(AdSize.MEDIUM_RECTANGLE);}else if(Objects.equals(size,"FULL_BANNER")){bannerView.setAdSize(AdSize.FULL_BANNER);}else if(Objects.equals(size,"LEADERBOARD")){bannerView.setAdSize(AdSize.LEADERBOARD);}else{bannerView.setAdSize(AdSize.BANNER);}bannerView.setAdUnitId(bannerAdUnitId);AdRequest adRequest=new AdRequest.Builder().build();bannerView.loadAd(adRequest);RelativeLayout.LayoutParams bannerParams=new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,RelativeLayout.LayoutParams.WRAP_CONTENT);switch(position){case "top-right":bannerParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);bannerParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);break;case "top-center":bannerParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);bannerParams.addRule(RelativeLayout.CENTER_HORIZONTAL);break;case "left":bannerParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);bannerParams.addRule(RelativeLayout.CENTER_VERTICAL);break;case "center":bannerParams.addRule(RelativeLayout.CENTER_HORIZONTAL);bannerParams.addRule(RelativeLayout.CENTER_VERTICAL);break;case "right":bannerParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);bannerParams.addRule(RelativeLayout.CENTER_VERTICAL);break;case "bottom-center":bannerParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);bannerParams.addRule(RelativeLayout.CENTER_HORIZONTAL);break;case "bottom-right":bannerParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);bannerParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);break;default:bannerParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);bannerParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);break;}bannerView.setLayoutParams(bannerParams);bannerViewLayout.addView(bannerView);bannerView.setAdListener(new AdListener(){@Override public void onAdClicked(){cWebView.loadUrl("javascript:cordova.fireDocumentEvent('onAdClicked.bannerAd');");}@Override public void onAdClosed(){cWebView.loadUrl("javascript:cordova.fireDocumentEvent('onAdClosed.bannerAd');");}@Override public void onAdFailedToLoad(LoadAdError adError){cWebView.loadUrl("javascript:cordova.fireDocumentEvent('onAdFailedToLoad.bannerAd');");}@Override public void onAdImpression(){cWebView.loadUrl("javascript:cordova.fireDocumentEvent('onAdImpression.bannerAd');");}@Override public void onAdLoaded(){cWebView.loadUrl("javascript:cordova.fireDocumentEvent('onAdLoaded.bannerAd');");}@Override public void onAdOpened(){cWebView.loadUrl("javascript:cordova.fireDocumentEvent('onAdOpened.bannerAd');");}});});return true;case "removeBannerAd":cordova.getActivity().runOnUiThread(()->{if(bannerView==null)return;ViewGroup parentView=(ViewGroup)bannerView.getParent();if(parentView!=null){parentView.removeView(bannerView);bannerView.destroy();bannerView=null;}});return true;}mInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback(){@Override public void onAdClicked(){Log.d(TAG,"Ad was clicked.");cWebView.loadUrl("javascript:cordova.fireDocumentEvent('onAdClicked.InterstitialAd');");}@Override public void onAdDismissedFullScreenContent(){Log.d(TAG,"Ad dismissed fullscreen content.");mInterstitialAd=null;cWebView.loadUrl("javascript:cordova.fireDocumentEvent('onAdDismissedFullScreenContent.InterstitialAd');");}@Override public void onAdFailedToShowFullScreenContent(AdError adError){Log.e(TAG,"Ad failed to show fullscreen content.");mInterstitialAd=null;cWebView.loadUrl("javascript:cordova.fireDocumentEvent('onAdFailedToShowFullScreenContent.InterstitialAd');");}@Override public void onAdImpression(){Log.d(TAG,"Ad recorded an impression.");cWebView.loadUrl("javascript:cordova.fireDocumentEvent('onAdImpression.InterstitialAd');");}@Override public void onAdShowedFullScreenContent(){Log.d(TAG,"Ad showed fullscreen content.");cWebView.loadUrl("javascript:cordova.fireDocumentEvent('onAdShowedFullScreenContent.InterstitialAd');");}});rewardedAd.setFullScreenContentCallback(new FullScreenContentCallback(){@Override public void onAdClicked(){Log.d(TAG,"Ad was clicked.");cWebView.loadUrl("javascript:cordova.fireDocumentEvent('onAdClicked.rewardedAd');");}@Override public void onAdDismissedFullScreenContent(){Log.d(TAG,"Ad dismissed fullscreen content.");rewardedAd=null;cWebView.loadUrl("javascript:cordova.fireDocumentEvent('onAdDismissedFullScreenContent.rewardedAd');");}@Override public void onAdFailedToShowFullScreenContent(AdError adError){Log.e(TAG,"Ad failed to show fullscreen content.");rewardedAd=null;cWebView.loadUrl("javascript:cordova.fireDocumentEvent('onAdFailedToShowFullScreenContent.rewardedAd');");}@Override public void onAdImpression(){Log.d(TAG,"Ad recorded an impression.");cWebView.loadUrl("javascript:cordova.fireDocumentEvent('onAdImpression.rewardedAd');");}@Override public void onAdShowedFullScreenContent(){Log.d(TAG,"Ad showed fullscreen content.");cWebView.loadUrl("javascript:cordova.fireDocumentEvent('onAdShowedFullScreenContent.rewardedAd');");}});rewardedInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback(){@Override public void onAdClicked(){Log.d(TAG,"Ad was clicked.");cWebView.loadUrl("javascript:cordova.fireDocumentEvent('onAdClicked.rewardedInterstitialAd');");}@Override public void onAdDismissedFullScreenContent(){Log.d(TAG,"Ad dismissed fullscreen content.");rewardedInterstitialAd=null;cWebView.loadUrl("javascript:cordova.fireDocumentEvent('onAdDismissedFullScreenContent.rewardedInterstitialAd');");}@Override public void onAdFailedToShowFullScreenContent(AdError adError){Log.e(TAG,"Ad failed to show fullscreen content.");rewardedInterstitialAd=null;cWebView.loadUrl("javascript:cordova.fireDocumentEvent('onAdFailedToShowFullScreenContent.rewardedInterstitialAd');");}@Override public void onAdImpression(){Log.d(TAG,"Ad recorded an impression.");cWebView.loadUrl("javascript:cordova.fireDocumentEvent('onAdImpression.rewardedInterstitialAd');");}@Override public void onAdShowedFullScreenContent(){Log.d(TAG,"Ad showed fullscreen content.");cWebView.loadUrl("javascript:cordova.fireDocumentEvent('onAdShowedFullScreenContent.rewardedInterstitialAd');");}});return false;}}