package com.jiazy.freedomdemo.frameanimation.anim;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.os.SystemClock;
import android.view.View;

import com.eebbk.bfc.common.devices.DisplayUtils;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AnimQueueDrawable extends Drawable implements Runnable {

    public static boolean sLock = false;

    public static final int STATUS_INFINITE = -1;
    public static final int STATUS_ONESHOT = 0;
    public static final int STATUS_REVERSE = 1;
    public static final int STATUS_LOOPTIME = 2;

    protected Object mTag = null;

    private int mCurFrame = 0;
    private final Paint mPaint = new Paint(Paint.FILTER_BITMAP_FLAG);
    private boolean mRunning;
    private boolean mInflating = false;
    private boolean mAnimating;

    private Context mContext;
    private Drawable mCurrDrawable;
    private Drawable mSwapDrawable;
    private int mWidth, mHeight;
    private boolean mReverse = false;

    private int mCapacity = 2;
    private long mFpsDuration = 1000 / 60;
    private long mInterval;
    private String mResName;
    private int mResDir = 1280;
    private int mFrameCount = mCapacity;
//    ArrayDeque<Drawable> mQueue = new ArrayDeque<Drawable>(mCapacity);

    private AnimationListener mAnimationListener;
    private View mAttachView;
    private ExecutorService mThreadPool = Executors.newSingleThreadExecutor();

    public interface AnimationListener {
        void animationEnd(AnimQueueDrawable drawable);
    }

    private int mRepeatMode = STATUS_ONESHOT;

    private int mRepeatCount = -1;
    private int mRepeatIndex = 0;

    public AnimQueueDrawable(View v) {
        mContext = v.getContext();
        mAttachView = v;

        int screenWidth = DisplayUtils.getScreenWidth(mContext);
        if (screenWidth == 2048 || screenWidth == 1280 || screenWidth == 1024) {
            mResDir = screenWidth;
        }
    }

    public void setUpParams(String resName, int frameCount, int repeatMode, int fps) {
        setUpParams(resName, frameCount, repeatMode, 0, fps, -1, mCapacity);
    }

    public void setUpParams(String resName, int frameCount, int repeatMode, int interval, int fps, int repeatCount, int capacity) {
        mResName = mResDir + "/" + resName;
        mFrameCount = frameCount;
        mRepeatMode = repeatMode;
        mInterval = interval;
        mFpsDuration = 1000/fps;
        mRepeatCount = repeatCount;
        mCapacity = capacity;

        mRepeatIndex = 0;
        mAnimating = false;
        mCurFrame = 0;
        mRunning = false;
        final Resources res = mContext.getResources();
        mThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    Bitmap b = BitmapFactory.decodeStream(res.getAssets().open(mResName  + "/" + 0 + ".png"));
                    mCurrDrawable = new FastBitmapDrawable(b);
                    mWidth = Math.max(mWidth, b.getWidth());
                    mHeight = Math.max(mHeight, b.getHeight());
                    mAttachView.post(new Runnable() {
                        @Override
                        public void run() {
                            mAttachView.setBackground(AnimQueueDrawable.this);
                        }
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void setAnimationListener(AnimationListener animationListener) {
        mAnimationListener = animationListener;
    }

    public void resetLoop(int repeatCount) {
        mRepeatIndex = 0;
        mRepeatCount = repeatCount;
    }

    @Override
    public int getIntrinsicWidth() {
        return mWidth;
    }

    @Override
    public int getIntrinsicHeight() {
        return mHeight;
    }

    @Override
    public int getMinimumWidth() {
        return mWidth;
    }

    @Override
    public int getMinimumHeight() {
        return mHeight;
    }


    public void start() {
        mAnimating = true;
        if (!sLock &&!isRunning()) {
            mRunning = true;
            mCurFrame = mReverse ? (mFrameCount - 1) : 0;
            nextFrame();
        }
    }

    public void stop() {
        mAnimating = false;
        mCurFrame = 0;
        mRunning = false;
    }

    public void pause() {
        mRunning = false;
    }

    public void inflate(){
        mInflating = true;
    }

    public void resume() {
        if (mAnimating && !mRunning) {
            mRunning = true;
            nextFrame();
        }
    }

    public boolean isRunning() {
        return mRunning;
    }

    private void setFrame(int frame, boolean animate, long interval, int repeatMode) {
        mAnimating = animate;
        playNextDrawable(frame);
        if (animate && mRunning && !mInflating) {
            setCurFrame(frame, repeatMode);
            mRunning = true;
            scheduleSelf(this, SystemClock.uptimeMillis() + mFpsDuration + interval);
        }
    }

    private void setCurFrame(int frame, int repeatMode) {
        mCurFrame = frame;
        if (repeatMode == STATUS_LOOPTIME &&
                mCurFrame == (mFrameCount - 1)) {
            mRepeatIndex++;
        }
    }

    private Drawable playNextDrawable(int index) {
        if (mSwapDrawable == null) {
            int offset = (index) % mFrameCount;
            inflate();
            inflateDrawable(offset, false);
            return null;
        }
        mCurrDrawable = mSwapDrawable;
        mSwapDrawable = null;
        invalidateSelf();
        return null;
    }

    private void inflateDrawable(final int index, final boolean sync) {
        Runnable r = new Runnable() {
            @Override
            public void run() {
                Bitmap bitmap = null;
                try {
                    Resources res = mContext.getResources();
                    bitmap = BitmapFactory.decodeStream(res.getAssets().open(mResName + "/" + index + ".png"));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (bitmap != null) {
                    Drawable drawable = new FastBitmapDrawable(bitmap);
                    mSwapDrawable = drawable;
                    if (!sync) {
                        mAttachView.post(mAfterInflateRunnable);
                    }
                }
            }
        };
        if (sync) {
            r.run();
        } else {
            mThreadPool.execute(r);
        }

    }

    private Runnable mAfterInflateRunnable = new Runnable() {
        @Override
        public void run() {
            if(mInflating){
                mInflating = false;
                nextFrame();
            }
        }
    };

    @Override
    public void draw(Canvas canvas) {
        if (mCurrDrawable != null) {
            mCurrDrawable.draw(canvas);
        }
        if ((isInfiniteStatus() || isLoopStatus()) && !mAnimating) {
            start();
        }
    }

    @Override
    public void setAlpha(int i) {
        mPaint.setAlpha(i);
    }

    @Override
    public void run() {
        if (mRunning) {
            nextFrame();
        }
    }

    private boolean isInfiniteStatus() {
        return STATUS_INFINITE == mRepeatMode;
    }

    public boolean isLoopStatus() {
        return mRepeatMode == STATUS_LOOPTIME && mRepeatIndex < mRepeatCount;
    }

    private void nextFrame() {
        int nextFrame = 0;
        long interval = 0;
        switch (mRepeatMode) {
            case STATUS_REVERSE:
                if (mReverse) {
                    nextFrame = mCurFrame - 1;
                } else {
                    nextFrame = (mCurFrame + 1) % mFrameCount;
                }
                if (nextFrame == 0 || nextFrame == (mFrameCount - 1)) {
                    if (mAnimationListener != null) {
                        mAnimationListener.animationEnd(this);
                    }
                    inflateDrawable(nextFrame, true);
                    mCurrDrawable = mSwapDrawable;
                    mSwapDrawable = null;
                    invalidateSelf();
                    pause();
                    mReverse = (nextFrame != 0);
                    return;
                }
                break;

            case STATUS_ONESHOT:
                nextFrame = (mCurFrame + 1) % mFrameCount;
                if (nextFrame == 0) {
                    stop();
                    if (mAnimationListener != null) {
                        mAnimationListener.animationEnd(this);
                    }
                    return;
                }
                break;
            case STATUS_INFINITE:
                nextFrame = (mCurFrame + 1) % mFrameCount;
                if (nextFrame == (mFrameCount - 1)) {
                    interval = mInterval;
                }
                break;
            case STATUS_LOOPTIME:
                nextFrame = (mCurFrame + 1) % mFrameCount;
                if (nextFrame == (mFrameCount - 1)) {
                    interval = mInterval;
                }
                if (mRepeatIndex >= mRepeatCount) {
                    if (mAnimationListener != null) {
                        mAnimationListener.animationEnd(this);
                    }
                    inflateDrawable(nextFrame, true);
                    mCurrDrawable = mSwapDrawable;
                    mSwapDrawable = null;
                    mCurFrame = nextFrame;
                    invalidateSelf();
                    stop();
                    return;
                }
                break;
        }
        setFrame(nextFrame, true, interval, mRepeatMode);
    }


    @Override
    public void setColorFilter(ColorFilter cf) {
        mPaint.setColorFilter(cf);
    }

    @Override
    public int getOpacity() {
        return PixelFormat.TRANSLUCENT;
    }

    public boolean isStatusInfinite() {
        return STATUS_INFINITE == mRepeatMode || STATUS_LOOPTIME == mRepeatMode;
    }

    public void setTag(Object tag) {
        mTag = tag;
    }

    public Object getTag() {
        return mTag;
    }
}