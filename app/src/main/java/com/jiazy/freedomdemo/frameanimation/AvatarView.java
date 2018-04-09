package com.jiazy.freedomdemo.frameanimation;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;

import com.jiazy.freedomdemo.frameanimation.anim.AnimQueueView;

import static com.jiazy.freedomdemo.frameanimation.anim.AnimQueueDrawable.STATUS_INFINITE;
import static com.jiazy.freedomdemo.frameanimation.anim.AnimQueueDrawable.STATUS_ONESHOT;

public class AvatarView extends AnimQueueView {

    public static final int STATE_SILENCE = 0x00;
    public static final int STATE_RECOGNIZE = 0x01;
    public static final int STATE_RECOGNIZE2 = 0x02;
    public static final int STATE_RESULT = 0x03;
    public static final int STATE_ERROR = 0x04;
    public static final int STATE_AWAKEN = 0x05;
    private int mCurrentSate = -1;

    private final MyHandler myHandler = new MyHandler();

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

        myHandler.post(() -> {
            setParams("xiaobu_sleep", 90, STATUS_INFINITE, 60);
            mDrawable.setAnimationListener(drawable -> {
                if (mCurrentSate == STATE_RESULT || mCurrentSate == STATE_ERROR) {
                    switchState(STATE_SILENCE);
                }else if (mCurrentSate == STATE_RECOGNIZE) {
                    switchState(STATE_RECOGNIZE2);
                }
            });
        });
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
                setParams("xiaobu_sleep", 90, STATUS_INFINITE, 60);
                break;
            case STATE_RECOGNIZE:
                setParams("xiaobu_awaken", 23, STATUS_ONESHOT, 60);
                break;
            case STATE_RECOGNIZE2:
                setParams("xiaobu_input_search", 54, STATUS_INFINITE, 60);
                break;
            case STATE_RESULT:
            case STATE_ERROR:
                setParams("xiaobu_result", 52, STATUS_ONESHOT, 60);
                break;
            case STATE_AWAKEN:
                setParams("xiaobu_awaken", 23, STATUS_ONESHOT, 60);
                break;
        }
    }

    static class MyHandler extends Handler{

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
        }
    }

}


