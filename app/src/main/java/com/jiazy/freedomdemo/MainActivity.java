package com.jiazy.freedomdemo;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.jiazy.freedomdemo.frameanimation.FrameAnim;

public class MainActivity extends Activity {
    private static final String TAG = MainActivity.class.getClass().getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ImageView imageView = findViewById(R.id.img);

        final FrameAnim frameAnim = new FrameAnim(R.drawable.anim_xiaobu_begin, imageView);
        frameAnim.setIsLoop(true);
        frameAnim.setOnAnimListener(new FrameAnim.OnAnimListener() {
            @Override
            public void onStart() {
                Log.d(TAG, "onStart");
            }

            @Override
            public void onEnd() {
                Log.d(TAG, "onEnd");
            }

            @Override
            public void onStop() {

            }
        });

        findViewById(R.id.btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                frameAnim.stop();
            }
        });

        findViewById(R.id.btn2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                frameAnim.setDrawableRes(R.drawable.anim_xiaobu_begin);
                frameAnim.setIsLoop(false);
                frameAnim.start();
            }
        });

        findViewById(R.id.btn3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                frameAnim.setDrawableRes(R.drawable.anim_xiaobu_search_start);
                frameAnim.setIsLoop(true);
                frameAnim.start();
            }
        });
    }
}
