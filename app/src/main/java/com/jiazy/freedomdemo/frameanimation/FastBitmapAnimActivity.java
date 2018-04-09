package com.jiazy.freedomdemo.frameanimation;

import android.app.Activity;
import android.os.Bundle;

import com.jiazy.freedomdemo.R;

public class FastBitmapAnimActivity extends Activity {

    private AvatarView mAvatarView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fast_bitmap);
        setTitle("FastBitmapAnim");

        mAvatarView = findViewById(R.id.img);

        findViewById(R.id.btn_start).setOnClickListener(v -> mAvatarView.start());

        findViewById(R.id.btn_stop).setOnClickListener(v -> mAvatarView.stop());

        findViewById(R.id.btn_recon).setOnClickListener(v -> mAvatarView.switchState(AvatarView.STATE_RECOGNIZE));

        findViewById(R.id.btn_result).setOnClickListener(v -> mAvatarView.switchState(AvatarView.STATE_RESULT));

        findViewById(R.id.btn_awaken).setOnClickListener(v -> mAvatarView.switchState(AvatarView.STATE_AWAKEN));

    }

}
