package com.jiazy.freedomdemo.frameanimation.anim;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;

public class FastBitmapDrawable extends Drawable {
    private Bitmap mBitmap;
    private int mAlpha;
    private int mWidth;
    private int mHeight;
    private float mScale = 1.0f;
    private final Paint mPaint = new Paint(Paint.FILTER_BITMAP_FLAG);

    private boolean mIsDrawShadow = false;

    FastBitmapDrawable(Bitmap b) {
        mAlpha = 255;
        mBitmap = b;
        if (b != null) {
            mWidth = (int) (mBitmap.getWidth() * mScale);
            mHeight = (int) (mBitmap.getHeight() * mScale);
        } else {
            mWidth = mHeight = 0;
        }
    }

    @Override
    public void draw(@NonNull Canvas canvas) {
        final Rect r = getBounds();
        canvas.save();
        canvas.scale(mScale, mScale);
        canvas.drawBitmap(mBitmap, r.left, r.top, mPaint);
        canvas.restore();
    }

    @Override
    public void setColorFilter(ColorFilter cf) {
        mPaint.setColorFilter(cf);
    }

    @Override
    public int getOpacity() {
        return PixelFormat.TRANSLUCENT;
    }

    @Override
    public void setAlpha(int alpha) {
        mAlpha = alpha;
        mPaint.setAlpha(alpha);
    }

    public void setFilterBitmap(boolean filterBitmap) {
        mPaint.setFilterBitmap(filterBitmap);
    }

    public int getAlpha() {
        return mAlpha;
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

    public void setBitmap(Bitmap b) {
        mBitmap = b;
        if (b != null) {
            mWidth = mBitmap.getWidth();
            mHeight = mBitmap.getHeight();
        } else {
            mWidth = mHeight = 0;
        }
    }

    public Bitmap getBitmap() {
        return mBitmap;
    }

    public float getScale() {
        return mScale;
    }

    @Override
    protected boolean onStateChange(int[] stateSet) {
        boolean isPressed = false;
        for (int aStateSet : stateSet) {
            if (aStateSet == android.R.attr.state_pressed) {
                isPressed = true;
                break;
            }
        }
        if (mIsDrawShadow != isPressed) {
            mIsDrawShadow = isPressed;

            invalidateSelf();
            return true;
        }
        return false;
    }

    @Override
    public boolean isStateful() {
        return true;
    }
}