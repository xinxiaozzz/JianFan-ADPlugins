package com.jf.ads.ADTypeActivity;

import android.app.Activity;
import android.content.Context;

import androidx.annotation.NonNull;

import com.bytedance.sdk.openadsdk.AdSlot;
import com.bytedance.sdk.openadsdk.TTAdConstant;
import com.bytedance.sdk.openadsdk.TTAdLoadType;
import com.bytedance.sdk.openadsdk.TTAdManager;
import com.bytedance.sdk.openadsdk.TTAdNative;
import com.bytedance.sdk.openadsdk.TTAdSdk;
import com.bytedance.sdk.openadsdk.TTRewardVideoAd;
import com.jf.ads.adlibrary.CallBackKeys;
import com.jf.ads.adlibrary.Tools;

import java.util.Map;

public class RewardVideoActivity {

    public String codeID = "";
    private TTAdNative mTTAdNative;
    public TTRewardVideoAd mttRewardVideoAd;
    private boolean mIsLoaded = false; //视频是否加载完成

    private Context mContext;
    private static RewardVideoActivity rewardVideoActivity = new RewardVideoActivity();

    private RewardVideoActivity() {

    }

    public static RewardVideoActivity getInstance() {
        return rewardVideoActivity;
    }

    public void initRewardAD(@NonNull Context context, @NonNull String _codeID) {
        mContext = context;
        codeID = _codeID;

        TTAdManager ttAdManager = TTAdSdk.getAdManager();
        TTAdSdk.getAdManager().requestPermissionIfNecessary(context);
        mTTAdNative = ttAdManager.createAdNative(context);

        loadAD();
    }

    public void loadAD() {
        AdSlot adSlot;
        adSlot = new AdSlot.Builder()
                .setAdLoadType(TTAdLoadType.LOAD)
                .setCodeId(codeID)
                .build();

        mTTAdNative.loadRewardVideoAd(adSlot, new TTAdNative.RewardVideoAdListener() {
            @Override
            public void onError(int code, String message) {
                Tools.SendToUnityMsg(CallBackKeys.REWARD_FAILED);
                Tools.LogInfo("Reward load fail:  code = " + code + " msg = " + message);
            }

            @Override
            public void onRewardVideoCached() {
                Tools.SendToUnityMsg(CallBackKeys.REWARD_CACHED);
                Tools.LogInfo("Reward load:  onRewardVideoCached");
                mIsLoaded = true;
            }

            @Override
            public void onRewardVideoCached(TTRewardVideoAd ad) {
                Tools.SendToUnityMsg(CallBackKeys.REWARD_CACHED);
                Tools.LogInfo("Reward load:  onRewardVideoCached");
                mIsLoaded = true;
            }

            @Override
            public void onRewardVideoAdLoad(TTRewardVideoAd ad) {
                Tools.SendToUnityMsg(CallBackKeys.REWARD_LOADED);
                Tools.LogInfo("Reward load:  onRewardVideoAdLoad");
                Tools.LogInfo("rewardVideoAd loaded 广告类型：" + getAdType(ad.getRewardVideoAdType()));

                mIsLoaded = false;
                mttRewardVideoAd = ad;

                Map<String, Object> adMap = mttRewardVideoAd.getMediaExtraInfo();
                Tools.LogError("遍历广告价值： ");
                for (Map.Entry<String, Object> entry : adMap.entrySet()) {
                    String mapKey = entry.getKey();
                    Object mapValue = entry.getValue();
                    Tools.LogError(mapKey + "：" + mapValue);
                }

                mttRewardVideoAd.setRewardAdInteractionListener(new TTRewardVideoAd.RewardAdInteractionListener() {
                    @Override
                    public void onAdShow() {
                        Tools.SendToUnityMsg(CallBackKeys.REWARD_OPEN);
                        Tools.LogInfo("Reward:  rewardVideoAd show");
                    }

                    @Override
                    public void onAdVideoBarClick() {
                        Tools.SendToUnityMsg(CallBackKeys.REWARD_CLICK);
                        Tools.LogInfo("Reward:  rewardVideoAd bar click");
                    }

                    @Override
                    public void onAdClose() {
                        Tools.SendToUnityMsg(CallBackKeys.REWARD_CLOSE);
                        Tools.LogInfo("Reward:  rewardVideoAd close");
                    }

                    @Override
                    public void onVideoComplete() {
                        Tools.SendToUnityMsg(CallBackKeys.REWARD_COMPLETE);
                        Tools.LogInfo("Reward:  rewardVideoAd complete");
                    }

                    @Override
                    public void onVideoError() {
                        Tools.SendToUnityMsg(CallBackKeys.REWARD_PLAY_ERROR);
                        Tools.LogInfo("Reward:  rewardVideoAd error");
                    }

                    @Override
                    public void onRewardVerify(boolean rewardVerify, int rewardAmount, String rewardName, int errorCode, String errorMsg) {
                        //视频播放完成后，奖励验证回调，rewardVerify：是否有效，rewardAmount：奖励梳理，rewardName：奖励名称
                        String logString = "verify:" + rewardVerify + " amount:" + rewardAmount +
                                " name:" + rewardName + " errorCode:" + errorCode + " errorMsg:" + errorMsg;
                        Tools.SendToUnityMsg(CallBackKeys.REWARD_VERIFY);
                        Tools.LogInfo("Reward:  rewardVideoAd Verify: " + logString);
                    }

                    @Override
                    public void onSkippedVideo() {
                        Tools.SendToUnityMsg(CallBackKeys.REWARD_SKIP);
                        Tools.LogInfo("Reward:  rewardVideoAd skip");
                    }
                });
            }
        });

    }

    public void showRewardVideo(Activity mActivity) {

        if (mttRewardVideoAd != null && mIsLoaded) {
            mttRewardVideoAd.showRewardVideoAd(mActivity, TTAdConstant.RitScenes.CUSTOMIZE_SCENES, "scenes_test");
            mttRewardVideoAd = null;
        } else {
            Tools.LogInfo("Reward:  请先加载广告");
        }
    }


    private String getAdType(int type) {
        switch (type) {

            case TTAdConstant.AD_TYPE_COMMON_VIDEO:
                return "普通激励视频，type=" + type;

            case TTAdConstant.AD_TYPE_PLAYABLE_VIDEO:
                return "Playable激励视频，type=" + type;

            case TTAdConstant.AD_TYPE_PLAYABLE:
                return "纯Playable，type=" + type;

            case TTAdConstant.AD_TYPE_LIVE:
                return "直播流，type=" + type;
        }

        return "未知类型+type=" + type;
    }

//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        if (mttRewardVideoAd != null) {
//            mttRewardVideoAd = null;
//        }
//    }

//    @Override
//    public void onPointerCaptureChanged(boolean hasCapture) {
//        super.onPointerCaptureChanged(hasCapture);
//    }
}
