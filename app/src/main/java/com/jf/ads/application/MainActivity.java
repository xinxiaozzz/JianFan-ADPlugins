package com.jf.ads.application;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.jf.ads.adlibrary.Tools;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void TestCode(){
        Tools.Test();
    }
}