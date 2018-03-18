package com.jiazy.freedomdemo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageView;

import com.jiazy.freedomdemo.frameanimation.MyAnimationDrawable;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ImageView imageView = findViewById(R.id.img);

        MyAnimationDrawable.animateRawManuallyFromXML(R.drawable.xiaobu_begin_anim, imageView, new Runnable() {
            @Override
            public void run() {
                Log.i("jzy", "start");
            }
        }, null);

    }
}
