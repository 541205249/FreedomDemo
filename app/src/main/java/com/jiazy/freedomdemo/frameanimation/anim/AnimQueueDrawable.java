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
import android.support.annotation.NonNull;
import android.view.View;

import com.jiazy.freedomdemo.R;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AnimQueueDrawable extends Drawable implements Runnable {

    public static final int STATUS_INFINITE = -1;
    public static final int STATUS_ONESHOT = 0;
    private static final int STATUS_REVERSE = 1;
    private static final int STATUS_LOOP_TIME = 2;

    private Object mTag = null;

    private int mCurFrame = 0;
    private final Paint mPaint = new Paint(Paint.FILTER_BITMAP_FLAG);
    private boolean mRunning;
    private boolean mInflating = false;
    private boolean mAnimating;

    private Context mContext;
    private FastBitmapDrawable mCurrDrawable;
    private FastBitmapDrawable mSwapDrawable;
    //用于mCurrDrawable和mSwapDrawable交替使用的判断标志
    private boolean isUseCurrOption = false;
    private int mWidth, mHeight;
    private boolean mReverse = false;
    private BitmapFactory.Options mCurrOptions = new BitmapFactory.Options();
    private BitmapFactory.Options mSwapOptions = new BitmapFactory.Options();


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

    AnimQueueDrawable(View v) {
        mAttachView = v;
        mContext = v.getContext();
        mResDir = mContext.getResources().getInteger(R.integer.resource_dir);
    }

    void setUpParams(String resName, int frameCount, int repeatMode, int fps) {
        setUpParams(resName, frameCount, repeatMode, 0, fps, -1, mCapacity);
    }

    private void setUpParams(String resName, int frameCount, int repeatMode, int interval, int fps, int repeatCount, int capacity) {
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
        mThreadPool.execute(() -> {
            try {
                Bitmap b = BitmapFactory.decodeStream(res.getAssets().open(mResName  + "/" + 0 + ".png"), null, mCurrOptions);
                isUseCurrOption = true;
                if(mCurrOptions.inBitmap == null){
                    mCurrOptions.inBitmap = b;
                }
                mCurrDrawable = new FastBitmapDrawable(b);
                mWidth = Math.max(mWidth, b.getWidth());
                mHeight = Math.max(mHeight, b.getHeight());
                if(mAttachView != null){
                    mAttachView.post(() -> {
                        if(mAttachView != null){
                            mAttachView.setBackground(AnimQueueDrawable.this);
                        }
                    });
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    public void setAnimationListener(AnimationListener animationListener) {
        mAnimationListener = animationListener;
    }

    void resetLoop(int repeatCount) {
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

        /* 使用inBitmap的初始化设置条件 */
        mCurrOptions.inSampleSize = 1;
        mSwapOptions.inSampleSize = 1;
        mCurrOptions.inMutable = true;
        mSwapOptions.inMutable = true;

        if (!isRunning()) {
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

    void pause() {
        mRunning = false;
    }

    private void inflate(){
        mInflating = true;
    }

    void resume() {
        if (mAnimating && !mRunning) {
            mRunning = true;
            nextFrame();
        }
    }

    boolean isRunning() {
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
        if (repeatMode == STATUS_LOOP_TIME &&
                mCurFrame == (mFrameCount - 1)) {
            mRepeatIndex++;
        }
    }

    private void playNextDrawable(int index) {
        if (mSwapDrawable == null || mCurrDrawable.getBitmap() == mSwapDrawable.getBitmap()) {
            int offset = (index) % mFrameCount;
            inflate();
            inflateDrawable(offset, false);
            return;
        }
        mCurrDrawable.setBitmap(mSwapDrawable.getBitmap());
        invalidateSelf();
    }

    private void inflateDrawable(final int index, final boolean sync) {
        Runnable r = () -> {
            Bitmap bitmap = null;
            try {
                Resources res = mContext.getResources();
                //使用inBitmap（以前不再使用的内存）来复用加载下一帧的内存
                if(isUseCurrOption){
                    bitmap = BitmapFactory.decodeStream(res.getAssets().open(mResName + "/" + index + ".png"), null,mSwapOptions);
                    if(mSwapOptions.inBitmap == null){
                        mSwapOptions.inBitmap = bitmap;
                    }
                    isUseCurrOption = false;
                }else {
                    bitmap = BitmapFactory.decodeStream(res.getAssets().open(mResName + "/" + index + ".png"), null,mCurrOptions);
                    if(mCurrOptions.inBitmap == null){
                        mCurrOptions.inBitmap = bitmap;
                    }
                    isUseCurrOption = true;
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
            if (bitmap != null) {
                if(mSwapDrawable == null){
                    mSwapDrawable = new FastBitmapDrawable(bitmap);
                }else {
                    mSwapDrawable.setBitmap(bitmap);
                }
                if (!sync) {
                    //添加判断，防止mAttachView在使用clear方法后仍然使用mAttachView
                    if(mAttachView != null){
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

    private Runnable mAfterInflateRunnable = () -> {
        if(mInflating){
            mInflating = false;
            nextFrame();
        }
    };

    @Override
    public void draw(@NonNull Canvas canvas) {
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

    private boolean isLoopStatus() {
        return mRepeatMode == STATUS_LOOP_TIME && mRepeatIndex < mRepeatCount;
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
                    mCurrDrawable.setBitmap(mSwapDrawable.getBitmap());
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
            case STATUS_LOOP_TIME:
                nextFrame = (mCurFrame + 1) % mFrameCount;
                if (nextFrame == (mFrameCount - 1)) {
                    interval = mInterval;
                }
                if (mRepeatIndex >= mRepeatCount) {
                    if (mAnimationListener != null) {
                        mAnimationListener.animationEnd(this);
                    }
                    inflateDrawable(nextFrame, true);
                    mCurrDrawable.setBitmap(mSwapDrawable.getBitmap());
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
        return STATUS_INFINITE == mRepeatMode || STATUS_LOOP_TIME == mRepeatMode;
    }

    public void setTag(Object tag) {
        mTag = tag;
    }

    public Object getTag() {
        return mTag;
    }

    public void clear(){
        stop();
        if(mAttachView != null && mAfterInflateRunnable != null){
            mAttachView.removeCallbacks(mAfterInflateRunnable);
        }
        if(mAnimationListener != null) {
            mAnimationListener = null;
        }
        mAttachView = null;
        mCurrDrawable = null;

    }
}