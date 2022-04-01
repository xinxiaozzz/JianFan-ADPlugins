package com.jf.ads.adlibrary;

import android.util.Log;

import com.unity3d.player.UnityPlayer;

public class Tools {

    public static final String TAG = "JF-Game";

    public static void SendToUnityMsg(String msg) {
        UnityPlayer.UnitySendMessage("JFADManager","ADCallback","msg");
        LogInfo(msg);
    }



    public static void LogInfo(String msg){
        Log.d(TAG,msg);
    }
    public static void LogError(String msg){
        Log.e(TAG,msg);
    }
}
