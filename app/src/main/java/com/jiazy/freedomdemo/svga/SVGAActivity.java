package com.jiazy.freedomdemo.svga;

import android.app.Activity;
import android.os.Bundle;

import com.jiazy.freedomdemo.R;
import com.opensource.svgaplayer.SVGAImageView;

public class SVGAActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_svga);

        SVGAImageView svgaImageView = findViewById(R.id.svga_view);

        findViewById(R.id.btn).setOnClickListener(v -> {
            svgaImageView.startAnimation();
        });
    }

}
