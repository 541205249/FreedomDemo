package com.jiazy.freedomdemo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.jiazy.freedomdemo.excel.TestExcelActivity;
import com.jiazy.freedomdemo.frameanim.FastBitmapAnimActivity;
import com.jiazy.freedomdemo.lottie.LottieActivity;
import com.jiazy.freedomdemo.retrofit.RetrofitActivity;
import com.jiazy.freedomdemo.svga.SVGAActivity;

public class MainActivity extends Activity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle("主页面");
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent();
        switch (v.getId()) {
            case R.id.btn_excel:
                intent.setClass(this, TestExcelActivity.class);
                break;
            case R.id.btn_frame_anim:
                intent.setClass(this, FastBitmapAnimActivity.class);
                break;
            case R.id.btn_lottie:
                intent.setClass(this, LottieActivity.class);
                break;
            case R.id.btn_retrofit:
                intent.setClass(this, RetrofitActivity.class);
                break;
            case R.id.btn_svga:
                intent.setClass(this, SVGAActivity.class);
                break;
            default:
                intent.setClass(this, TestExcelActivity.class);
                break;
        }

        startActivity(intent);
    }
}
