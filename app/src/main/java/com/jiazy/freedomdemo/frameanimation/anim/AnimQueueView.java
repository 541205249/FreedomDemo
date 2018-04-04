package com.jiazy.freedomdemo.frameanimation.anim;

import android.content.Context;
import android.util.AttributeSet;

public class AnimQueueView extends IrregularImageView {

    protected AnimQueueDrawable mDrawable;
    private Context mContext;

    public AnimQueueView(Context context) {
        super(context);
        mContext = context;
    }

    public AnimQueueView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
    }

    public AnimQueueView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
    }

    public void setParams(String resName, int frameCount, int repeatMode, int fps) {
        if (mDrawable == null) {
            mDrawable = new AnimQueueDrawable(this);
        }
        mDrawable.setUpParams(resName, frameCount, repeatMode, fps);
        mDrawable.start();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (mDrawable != null) {
            mDrawable.stop();
        }
    }

    public boolean isRunning() {
        if (mDrawable != null) {
            return mDrawable.isRunning();
        }
        return false;
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