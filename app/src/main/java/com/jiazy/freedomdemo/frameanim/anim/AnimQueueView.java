package com.jiazy.freedomdemo.frameanim.anim;

import android.content.Context;
import android.util.AttributeSet;

public class AnimQueueView extends IrregularImageView {

    public AnimQueueDrawable mDrawable;

    private String mResName;
    private int mFrameCount;
    private int mRepeatMode;
    private int mRepeatCount = -1;
    private int mStartInterval = 0;
    private int mEndInterval = 0;
    private int mFPS;
    private boolean isAttached = false;
    private boolean isStartedButFailed = false;

    public AnimQueueView(Context context) {
        super(context);
    }

    public AnimQueueView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public AnimQueueView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setParams(String resName, int frameCount, int repeatMode, int fps) {
        mResName = resName;
        mFrameCount = frameCount;
        mRepeatMode = repeatMode;
        mFPS = fps;

        setDrawable();
    }

    public void setParams(String resName, int frameCount, int repeatMode, int repeatCount, int startInterval, int endInterval, int fps) {
        mStartInterval = startInterval;
        mEndInterval = endInterval;
        mRepeatCount = repeatCount;
        setParams(resName, frameCount, repeatMode, fps);
    }

    public void setParams(String resName, int frameCount, int repeatMode,int startInterval, int endInterval, int fps) {
        mStartInterval = startInterval;
        mEndInterval = endInterval;
        setParams(resName, frameCount, repeatMode, fps);
    }

    public void setParams(String resName, int frameCount, int repeatMode, int startInterval, int fps) {
        mStartInterval = startInterval;
        mEndInterval = 0;
        setParams(resName, frameCount, repeatMode, fps);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (mDrawable != null) {
            //解除与activity的绑定，防止内存泄漏
            mDrawable.clear();
            mDrawable = null;
        }
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        isAttached = true;
        if (isStartedButFailed) {
            setDrawable();
            start();
        }
    }

    public boolean isRunning() {
        return mDrawable != null && mDrawable.isRunning();
    }

    public void start() {
        //防止在没有attachedView时进行view的操作
        if(isAttached && mDrawable != null){
            mDrawable.execute();
        } else {
            isStartedButFailed = true;
        }
    }

    private void setDrawable() {
        if (mDrawable == null) {
            mDrawable = new AnimQueueDrawable(this);
        }
        mDrawable.setUpParams(mResName, mFrameCount, mRepeatMode,  mStartInterval, mEndInterval, mFPS, mRepeatCount);
    }

    public void stop() {
        if (mDrawable != null) {
            mDrawable.stop();
        }
    }

    public void pause() {
        if (mDrawable != null) {
            mDrawable.pause();
        }
    }

    public void resume() {
        if (mDrawable != null) {
            mDrawable.resume();
        }
    }

    public void setRepeatCount(int repeatCount) {
        if (mDrawable != null) {
            mDrawable.resetLoop(repeatCount);
        }
    }

    public void setAnimationListener(AnimQueueDrawable.AnimationListener animationListener) {
        if (mDrawable != null) {
            mDrawable.setAnimationListener(animationListener);
        }
    }

    public AnimQueueDrawable getDrawable() {
        if (mDrawable != null) {
            return mDrawable;
        }
        return null;
    }
}