package com.jiazy.freedomdemo.frameanim;

import android.content.Context;
import android.util.AttributeSet;

import com.jiazy.freedomdemo.R;
import com.jiazy.freedomdemo.frameanim.anim.AnimQueueDrawable;
import com.jiazy.freedomdemo.frameanim.anim.AnimQueueView;

import static com.jiazy.freedomdemo.frameanim.anim.AnimQueueDrawable.STATUS_INFINITE;
import static com.jiazy.freedomdemo.frameanim.anim.AnimQueueDrawable.STATUS_ONESHOT;


public class AvatarView extends AnimQueueView implements AnimQueueDrawable.AnimationListener {

    public static final int STATE_SILENCE = 0x00;
    public static final int STATE_RECOGNIZE = 0x01;
    public static final int STATE_RECOGNIZE2 = 0x02;
    public static final int STATE_RESULT = 0x03;
    public static final int STATE_ERROR = 0x04;
    public static final int STATE_AWAKEN = 0x05;

    private static final int XIAOBU_AWAKEN_COUNT = 23;
    private static final int XIAOBU_INPUT_SEARCH_COUNT = 54;
    private static final int XIAOBU_RESULT_COUNT = 36;
    private static final int XIAOBU_SLEEP_COUNT = 89;

    private static int FPS = 45;

    private int mCurrentSate = -1;

    private AvatarListener mAvatarListener;

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

    public void setAvatarViewListener(AvatarListener avatarViewListener) {
        mAvatarListener = avatarViewListener;
    }

    private void init() {
        setFPS();
        setParams("xiaobu_sleep", XIAOBU_SLEEP_COUNT, STATUS_INFINITE, FPS);
        mDrawable.setAnimationListener(this);
    }

    private void setFPS() {
        int resDir = getResources().getInteger(R.integer.resource_dir);
        switch (resDir) {
            case 1024:
            case 1280:
                FPS = 45;
                break;
            case 2048:
                FPS = 60;
                break;
        }
    }

    public void start() {
        if (mDrawable != null) {
            mDrawable.start();
        }
    }

    public void stop() {
        if (mDrawable != null) {
            mDrawable.stop();
        }
    }

    public void switchState(int state) {
        if (mDrawable == null) {
            return;
        }
        if (mCurrentSate == STATE_SILENCE && state == STATE_RESULT) {
            return;
        }

        mCurrentSate = state;
        switch (state) {
            case STATE_SILENCE:
                setClickable(true);
                setParams("xiaobu_sleep", XIAOBU_SLEEP_COUNT, STATUS_INFINITE, FPS);
                break;
            case STATE_RECOGNIZE:
                setClickable(true);
                setParams("xiaobu_awaken", XIAOBU_AWAKEN_COUNT, STATUS_ONESHOT, FPS);
                break;
            case STATE_RECOGNIZE2:
                setClickable(true);
                setParams("xiaobu_input_search", XIAOBU_INPUT_SEARCH_COUNT, STATUS_INFINITE, FPS);
                break;
            case STATE_RESULT:
            case STATE_ERROR:
                setClickable(false);
                setParams("xiaobu_result", XIAOBU_RESULT_COUNT, STATUS_ONESHOT, FPS);
                break;
            case STATE_AWAKEN:
                setClickable(true);
                setParams("xiaobu_awaken", XIAOBU_AWAKEN_COUNT, STATUS_ONESHOT, FPS);
                break;
        }
    }

    @Override
    public void animationEnd(AnimQueueDrawable drawable) {
        if (mCurrentSate == STATE_RESULT || mCurrentSate == STATE_ERROR) {
            switchState(STATE_SILENCE);
            if (mAvatarListener != null) {
                mAvatarListener.afterResult();
            }
        } else if (mCurrentSate == STATE_RECOGNIZE) {
            switchState(STATE_RECOGNIZE2);
        } else if (mCurrentSate == STATE_AWAKEN) {
            if (mAvatarListener != null) {
                mAvatarListener.afterAwaken();
            }
        }
    }

    public void setPlaySpecificFrame(int specificFrame) {
        mDrawable.setPlaySpecificFrame(specificFrame);
    }

    public void cancelPlaySpecificFrame() {
        mDrawable.cancelPlaySpecificFrame();
    }

    @Override
    public void afterPlaySpecificFrame() {
        if (mAvatarListener != null) {
            mAvatarListener.afterPlaySpecificFrame();
        }
        cancelPlaySpecificFrame();
    }

}


