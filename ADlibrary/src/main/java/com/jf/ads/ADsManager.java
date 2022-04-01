package com.jf.ads;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;

import com.bytedance.sdk.openadsdk.TTAdConfig;
import com.bytedance.sdk.openadsdk.TTAdConstant;
import com.bytedance.sdk.openadsdk.TTAdNative;
import com.bytedance.sdk.openadsdk.TTAdSdk;
import com.jf.ads.ADTypeActivity.RewardVideoActivity;
import com.jf.ads.adlibrary.CallBackKeys;
import com.jf.ads.adlibrary.Tools;

public class ADsManager extends Activity {

    public static Context mContext;

    //广告相关
    public static boolean debugModel = false;
    public static String appID = "";
    public static String rewardCode = "";


    private static ADsManager aDsManager = new ADsManager();
    public static Object getInstance() {
        Tools.LogInfo("getInstance");
        return aDsManager;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext=this;
        try {
            appID = getData("appID");
            rewardCode = getData("rewardCode");
        } catch (PackageManager.NameNotFoundException e) {
            Tools.LogError("请先在AndroidManifest.xml中的Activity中增加appID/rewardCode对应的meta-data元素");
        }
    }

    public void initSDK() {
        TTAdSdk.init(mContext, buildConfig(), new TTAdSdk.InitCallback() {
            @Override
            public void success() {
                Tools.SendToUnityMsg(CallBackKeys.INIT_SUCCEED);
                Tools.LogInfo("success: " + TTAdSdk.isInitSuccess());
                //初始化激励广告
                RewardVideoActivity.getInstance().initRewardAD(mContext, rewardCode);
            }

            @Override
            public void fail(int code, String msg) {
                Tools.SendToUnityMsg(CallBackKeys.INIT_FAIL);
                Tools.LogInfo("fail:  code = " + code + " msg = " + msg);
            }
        });
    }

    public void initSDK(boolean mDebugModel) {
        debugModel = mDebugModel;
        initSDK();
    }

    private TTAdConfig buildConfig() {

        return new TTAdConfig.Builder()
                .appId(appID)
                .useTextureView(true) //使用TextureView控件播放视频,默认为SurfaceView,当有SurfaceView冲突的场景，可以使用TextureView
                .allowShowNotify(true) //是否允许sdk展示通知栏提示
                .debug(debugModel) //测试阶段打开，可以通过日志排查问题，上线时去除该调用
                .directDownloadNetworkType(TTAdConstant.NETWORK_STATE_WIFI, TTAdConstant.NETWORK_STATE_3G) //允许直接下载的网络状态集合
                .supportMultiProcess(false)//是否支持多进程
                .needClearTaskReset()
                .build();
    }

    public boolean hasRewardAd() {
        return RewardVideoActivity.getInstance().mttRewardVideoAd != null;
    }

    public void showRewardAd(Activity _mActivity) {
        Tools.LogInfo("Reward:  展示reward广告");
        RewardVideoActivity.getInstance().showRewardVideo(_mActivity);
    }


    public String getData(String data_name) throws PackageManager.NameNotFoundException {
        ApplicationInfo appInfo = this.getPackageManager().getApplicationInfo(getPackageName(), PackageManager.GET_META_DATA);
        String msg = appInfo.metaData.getString("data_name");
        return msg;
    }


}
