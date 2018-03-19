package com.jiazy.freedomdemo.frameanimation;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.jiazy.freedomdemo.R;

public class AvatarView extends ImageView {

    public static final int STATE_SILENCE = 0x00; //xiumian
    public static final int STATE_RECOGNIZE = 0x01; //luru
    public static final int STATE_SEARCH = 0x02; //qishi
    public static final int STATE_RESULT = 0x03; //
    public static final int STATE_ERROR = 0x04;
    private int mCurrentSate = -1;

    private FrameAnim mFrameAnim;

    public AvatarView(Context context) {
        super(context);
        init();
    }

    public AvatarView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public AvatarView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        switchState(STATE_SILENCE);
        initAnim();
    }

    public void switchState(int state) {
        if (mCurrentSate != state) {
            mCurrentSate = state;
        } else {
            return;
        }

        switch (mCurrentSate) {
            case STATE_SILENCE:
                mFrameAnim.setDrawableRes(R.drawable.anim_xiaobu_sleep);
                mFrameAnim.setIsLoop(true);
                break;
            case STATE_RECOGNIZE:
                mFrameAnim.setDrawableRes(R.drawable.anim_xiaobu_begin);
                mFrameAnim.setIsLoop(true);
                break;
            case STATE_SEARCH:
                mFrameAnim.setDrawableRes(R.drawable.anim_xiaobu_search_start);
                mFrameAnim.setIsLoop(true);
                break;
            case STATE_RESULT:
                mFrameAnim.setDrawableRes(R.drawable.anim_xiaobu_begin);
                mFrameAnim.setIsLoop(true);
                break;
            case STATE_ERROR:
                mFrameAnim.setDrawableRes(R.drawable.anim_xiaobu_begin);
                mFrameAnim.setIsLoop(false);
                break;
        }

        mFrameAnim.start();
    }

    private void initAnim() {
        mFrameAnim = new FrameAnim(R.drawable.anim_xiaobu_begin, this);
        mFrameAnim.setIsLoop(true);
        mFrameAnim.setOnAnimListener(new FrameAnim.OnAnimListener() {
            @Override
            public void onStart() {
//                Log.d(TAG, "onStart");
            }

            @Override
            public void onEnd() {
//                Log.d(TAG, "onEnd");
            }

            @Override
            public void onStop() {

            }
        });
    }
}
