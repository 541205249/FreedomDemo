package com.jiazy.freedomdemo.frameanimation;

import android.app.Activity;
import android.os.Bundle;

import com.jiazy.freedomdemo.R;

public class FrameAnimActivity extends Activity {

    private AvatarView mAvatarView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_frame_anim);
        setTitle("FrameAnim");

        mAvatarView = findViewById(R.id.img);

        findViewById(R.id.btn).setOnClickListener(v -> mAvatarView.stop());

        findViewById(R.id.btn_start).setOnClickListener(v -> mAvatarView.switchState(AvatarView.STATE_RECOGNIZE));

//        findViewById(R.id.btn_search_start).setOnClickListener(v -> mAvatarView.switchState(AvatarView.STATE_SEARCH_START));
//
//        findViewById(R.id.btn_search_loop).setOnClickListener(v -> mAvatarView.switchState(AvatarView.STATE_SEARCH_LOOP));

        findViewById(R.id.btn_result).setOnClickListener(v -> mAvatarView.switchState(AvatarView.STATE_RESULT));

        findViewById(R.id.btn_sleep).setOnClickListener(v -> mAvatarView.switchState(AvatarView.STATE_SILENCE));

//        findViewById(R.id.btn_awaken).setOnClickListener(v -> mAvatarView.switchState(AvatarView.STATE_AWAKEN));
    }
}
