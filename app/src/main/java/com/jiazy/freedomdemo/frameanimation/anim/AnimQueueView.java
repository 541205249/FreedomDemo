package com.jiazy.freedomdemo.frameanimation.anim;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;

public class AnimQueueView extends IrregularImageView {

    private static final String TAG = "AnimQueueView";

    public AnimQueueDrawable mDrawable;

    private String mResName;
    private int mFrameCount;
    private int mRepeatMode;
    private int mFPS;
    private boolean isAttached = false;
    private boolean isSetParams = false;


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
        isSetParams = true;

        if (mDrawable == null) {
            mDrawable = new AnimQueueDrawable(this);
        }

        //防止在没有attachedView时进行view的操作
        if(isAttached){
            loadViewParams();
        }
    }

    private void loadViewParams(){
        if(isAttached && isSetParams){
            if (mDrawable == null) {
                mDrawable = new AnimQueueDrawable(this);
            }
            mDrawable.setUpParams(mResName, mFrameCount, mRepeatMode, mFPS);
            mDrawable.start();
        }

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
        loadViewParams();
    }

    public boolean isRunning() {
        return mDrawable != null && mDrawable.isRunning();
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