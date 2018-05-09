package com.jiazy.freedomdemo.frameanim.anim;

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
    private int mSpecificFrame = 0;
    private boolean isSetSpecificFrame = false;

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
    private long mStartInterval;
    private long mEndInterval;
    private String mResName;
    private int mFrameCount = mCapacity;

    private AnimationListener mAnimationListener;
    private View mAttachView;
    private ExecutorService mThreadPool = Executors.newSingleThreadExecutor();

    public interface AnimationListener {
        void animationEnd(AnimQueueDrawable drawable);
        void afterPlaySpecificFrame();
    }

    private int mRepeatMode = STATUS_ONESHOT;

    private int mRepeatCount = -1;
    private int mRepeatIndex = 0;

    AnimQueueDrawable(View v) {

        /* 使用inBitmap的初始化设置条件 */
        mCurrOptions.inSampleSize = 1;
        mSwapOptions.inSampleSize = 1;
        mCurrOptions.inMutable = true;
        mSwapOptions.inMutable = true;

        mAttachView = v;
        mContext = getContext(v);
    }

    private Context getContext(View v) {
        Context context = v.getContext().getApplicationContext();
        //防止桌面异常
        if(context == null){
            context = v.getContext();
        }

        return context;
    }

    void setUpParams(String resName, int frameCount, int repeatMode, int startInterval, int endInterval, int fps, int repeatCount) {
        mResName = resName;
        mFrameCount = frameCount;
        mRepeatMode = repeatMode;
        mStartInterval = startInterval;
        mEndInterval = endInterval;
        mFpsDuration = 1000/fps;
        mRepeatCount = repeatCount;
        mRepeatIndex = 0;
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
            } catch (Exception e) {
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

    void execute() {
        if (!isRunning()) {
            mRunning = true;
            mCurFrame = mReverse ? (mFrameCount - 1) : 0;
            nextFrame();
        }
    }

    public void stop() {
        mCurFrame = 0;
        mRunning = false;
    }

    void pause() {
        mRunning = false;
    }

    void resume() {
        if (!isRunning()) {
            mRunning = true;
            nextFrame();
        }
    }

    boolean isRunning() {
        return mRunning;
    }

    private void setFrame(int frame, long interval, int repeatMode) {
        playNextDrawable(frame);
        if (isRunning() && !mInflating) {
            setCurFrame(frame, repeatMode);
            mRunning = true;
            scheduleSelf(this, SystemClock.uptimeMillis() + mFpsDuration + interval);
        }
    }

    private void setCurFrame(int frame, int repeatMode) {
        mCurFrame = frame;
        if (repeatMode == STATUS_LOOP_TIME && mCurFrame == (mFrameCount - 1)) {
            mRepeatIndex++;
        }
    }

    private void playNextDrawable(int index) {
        if (mSwapDrawable == null || mCurrDrawable == null || mCurrDrawable.getBitmap() == mSwapDrawable.getBitmap()) {
            int offset = (index) % mFrameCount;
            mInflating = true;
            inflateDrawable(offset, false);
            return;
        }
        mCurrDrawable.setBitmap(mSwapDrawable.getBitmap());
        invalidateSelf();
        // 若有播放特定帧回调
        if(mAnimationListener != null &&
                isSetSpecificFrame &&
                mCurFrame == mSpecificFrame){
            mAnimationListener.afterPlaySpecificFrame();
        }
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

            } catch (Exception e) {
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
            if(!mThreadPool.isShutdown()){
                mThreadPool.execute(r);
            }
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
        if ((isInfiniteStatus() || isLoopStatus()) && isRunning()) {
            execute();
        }
    }

    public void setPlaySpecificFrame(int specificFrame){
        mSpecificFrame = specificFrame;
        isSetSpecificFrame = true;
    }

    public void cancelPlaySpecificFrame(){
        isSetSpecificFrame = false;
    }

    @Override
    public void setAlpha(int i) {
        mPaint.setAlpha(i);
    }

    @Override
    public void run() {
        if (isRunning()) {
            nextFrame();
        }
    }

    private boolean isInfiniteStatus() {
        return mRepeatMode == STATUS_INFINITE;
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
                if (nextFrame == 1){
                    interval = mStartInterval;
                }else if (nextFrame == (mFrameCount - 1)) {
                    interval = mEndInterval;
                }
                break;
            case STATUS_LOOP_TIME:
                nextFrame = (mCurFrame + 1) % mFrameCount;
                if (nextFrame == (mFrameCount - 1)) {
                    interval = mEndInterval;
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
        setFrame(nextFrame, interval, mRepeatMode);
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
        mThreadPool.shutdownNow();
        mAttachView = null;
    }
}