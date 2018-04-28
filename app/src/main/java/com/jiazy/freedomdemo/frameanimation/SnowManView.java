package com.jiazy.freedomdemo.frameanimation;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;

import com.jiazy.freedomdemo.frameanimation.anim.AnimQueueDrawable;
import com.jiazy.freedomdemo.frameanimation.anim.AnimQueueView;

public class SnowManView extends AnimQueueView implements AnimQueueDrawable.AnimationListener {

    private static final int XUEREN_FRAME_COUNT = 93;
    private static final String XUEREN_RES_NAME = "xueren";
    private static final int FPS = 20;
    private static final long DELAY_PLAY_ANIM = 15000;

    private final InitHandler initHandler = new InitHandler();

    private boolean mIsAttachedToWindow = false;
    private boolean mIsSetDelayPlayStop = false;

    public SnowManView(Context context) {
        super(context);
        init();
    }

    public SnowManView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public SnowManView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }


    private void init() {
        setParams(XUEREN_RES_NAME, XUEREN_FRAME_COUNT, AnimQueueDrawable.STATUS_ONESHOT, FPS);
        mDrawable.setAnimationListener(this);
        //为了在播放第一帧后延迟播放
        setPlaySpecificFrame(0);
    }

    public void start() {
        mIsSetDelayPlayStop = false;
        if (mDrawable != null) {
            mDrawable.start();
            setPlaySpecificFrame(0);
        }else {
            setParams(XUEREN_RES_NAME, XUEREN_FRAME_COUNT, AnimQueueDrawable.STATUS_ONESHOT, FPS);
            mDrawable.setAnimationListener(this);
            setPlaySpecificFrame(0);
        }
    }

    public void stop() {
        mIsSetDelayPlayStop = true;
        if (mDrawable != null) {
            mDrawable.stop();
        }
    }

    @Override
    public void animationEnd(AnimQueueDrawable drawable) {
        delayPlayAnim(DELAY_PLAY_ANIM);
    }

    /* 在所需要播放的动画播放前设置 */
    public void setPlaySpecificFrame(int specificFrame){
        mDrawable.setPlaySpecificFrame(specificFrame);
    }

    public void cancelPlaySpecificFrame(){
        mDrawable.cancelPlaySpecificFrame();
    }


    @Override
    public void afterPlaySpecificFrame() {
        cancelPlaySpecificFrame();
        mDrawable.stop();
        delayPlayAnim(DELAY_PLAY_ANIM);
    }

    private void delayPlayAnim(long delayMS) {
        if(mIsAttachedToWindow){
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(delayMS);
                        if(mDrawable != null && !mIsSetDelayPlayStop){
                            mDrawable.start();
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        mIsAttachedToWindow = true;
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        mIsAttachedToWindow = false;
    }


    static class InitHandler extends Handler{

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
        }
    }

}


