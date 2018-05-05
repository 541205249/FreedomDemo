package com.jiazy.freedomdemo.frameanim;

import android.content.Context;
import android.content.res.XmlResourceParser;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.ImageView;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

public class FrameAnim {
    private static final String TAG = FrameAnim.class.getClass().getSimpleName();

    class FrameData {
        byte[] bytes;
        int duration;
        Drawable drawable;
        boolean isReady = false;
    }

    public interface OnDrawableLoadedListener {
        void onDrawableLoaded(List<FrameData> frameData);
    }

    public interface OnAnimListener {
        void onStart();
        void onEnd();
        void onStop();
    }

    private int mDrawableResId;
    private ImageView mImageView;
    private OnAnimListener mOnAnimListener;

    private boolean isLoop;
    private boolean isRunning = true;

    FrameAnim(@DrawableRes final int drawableResId, @NonNull final ImageView imageView) {
        setDrawableRes(drawableResId);
        mImageView = imageView;
    }

    void setDrawableRes(@DrawableRes final int drawableResId) {
        mDrawableResId = drawableResId;
    }

    void setOnAnimListener(final OnAnimListener onAnimListener) {
        mOnAnimListener = onAnimListener;
    }

    boolean isLoop() {
        return isLoop;
    }

    void setIsLoop(boolean isLoop) {
        this.isLoop = isLoop;
    }

    public void start() {
        isRunning = true;
        loadFromXml(mDrawableResId, mImageView.getContext(), frameData -> {
            if (mOnAnimListener != null) {
                mOnAnimListener.onStart();
            }

            animateRawManually(frameData, mImageView);
        });
    }

    void stop() {
        isRunning = false;
    }

    private void loadFromXml(final int resourceId, final Context context,
                             final OnDrawableLoadedListener onDrawableLoadedListener) {
        new Thread(() -> {
            final List<FrameData> frames = geFrames(context, resourceId);

            new Handler(context.getMainLooper()).post(() -> {
                if (onDrawableLoadedListener != null) {
                    onDrawableLoadedListener.onDrawableLoaded(frames);
                }
            });
        }).run();
    }

    private void animateRawManually(List<FrameData> frameData, ImageView imageView) {
        animateRawManually(frameData, imageView, 0);
    }

    private void animateRawManually(final List<FrameData> frameList, final ImageView imageView, final int currentFrameIndex) {
        if (!isRunning) {
            if (mOnAnimListener != null) {
                mOnAnimListener.onStop();
            }

            return;
        }

        final FrameData thisFrame = frameList.get(currentFrameIndex);
        if (currentFrameIndex == 0) {
            getDrawable(imageView, thisFrame);
        } else {
            recycleBitmap(frameList, currentFrameIndex);
        }

        imageView.setImageDrawable(thisFrame.drawable);

        final int frameListSize = frameList.size();
        new Handler().postDelayed(() -> {
            // Make sure ImageView hasn't been changed to a different Image
            // in this time
            loadCurrentFrame(imageView, thisFrame, frameListSize, currentFrameIndex, frameList);
        }, thisFrame.duration);

        // Load next frame
        if (hasNextFrame(frameListSize, currentFrameIndex)) {
            new Thread(() -> loadNextFrame(frameList, currentFrameIndex, imageView)).run();
        }
    }

    private void loadNextFrame(List<FrameData> frameList, int currentFrameIndex, ImageView imageView) {
        FrameData nextFrame = frameList.get(currentFrameIndex + 1);
        getDrawable(imageView, nextFrame);

        if (nextFrame.isReady) {
            // Animate next frame
            animateRawManually(frameList, imageView, currentFrameIndex + 1);
        } else {
            nextFrame.isReady = true;
        }
    }

    private void loadCurrentFrame(ImageView imageView, FrameData thisFrame, int frameListSize, int currentFrameIndex, List<FrameData> frameList) {
        if (imageView.getDrawable() == thisFrame.drawable) {
            if (hasNextFrame(frameListSize, currentFrameIndex)) {
                FrameData nextFrame = frameList.get(currentFrameIndex + 1);
                if (nextFrame.isReady) {
                    // Animate next frame
                    animateRawManually(frameList, imageView, currentFrameIndex + 1);
                } else {
                    nextFrame.isReady = true;
                }
            } else {
                if (isLoop && isRunning) {
                    animateRawManually(frameList, imageView, 0);
                } else {
                    if (mOnAnimListener != null) {
                        mOnAnimListener.onEnd();
                    }
                }
            }
        }
    }

    private boolean hasNextFrame(int frameListSize, int currentFrameIndex) {
        return currentFrameIndex < frameListSize - 1;
    }

    @NonNull
    private List<FrameData> geFrames(Context context, int resourceId) {
        final List<FrameData> frames = new ArrayList<>();
        XmlResourceParser parser = context.getResources().getXml(resourceId);
        try {
            int eventType = parser.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT) {
                if (eventType == XmlPullParser.START_DOCUMENT) {
                    Log.d(TAG, "START_DOCUMENT");
                } else if (eventType == XmlPullParser.START_TAG) {
                    getDrawableItem(context, frames, parser);
                } else if (eventType == XmlPullParser.END_TAG) {
                    Log.d(TAG, "END_TAG");
                } else if (eventType == XmlPullParser.TEXT) {
                    Log.d(TAG, "TEXT");
                }
                eventType = parser.next();
            }
        } catch (IOException | XmlPullParserException e) {
            e.printStackTrace();
        }
        return frames;
    }

    private void getDrawableItem(Context context, List<FrameData> frames, XmlResourceParser parser) throws IOException {
        if (!parser.getName().equals("item")) {
            return;
        }
        byte[] bytes = null;
        int duration = 1000;
        for (int i = 0; i < parser.getAttributeCount(); i++) {
            if (parser.getAttributeName(i).equals("drawable")) {
                int resId = Integer.parseInt(parser.getAttributeValue(i).substring(1));
                bytes = toByteArray(context.getResources().openRawResource(resId));
            } else if (parser.getAttributeName(i)
                    .equals("duration")) {
                duration = parser.getAttributeIntValue(
                        i, 1000);
            }
        }
        FrameData frameData = new FrameData();
        frameData.bytes = bytes;
        frameData.duration = duration;
        frames.add(frameData);
    }

    private void recycleBitmap(List<FrameData> frameList, int currentFrameIndex) {
        FrameData previousFrame = frameList.get(currentFrameIndex - 1);
        ((BitmapDrawable) previousFrame.drawable).getBitmap().recycle();
        previousFrame.drawable = null;
        previousFrame.isReady = false;
    }

    private void getDrawable(ImageView imageView, FrameData thisFrame) {
        thisFrame.drawable = new BitmapDrawable(imageView.getContext()
                .getResources(), BitmapFactory.decodeByteArray(
                thisFrame.bytes, 0, thisFrame.bytes.length));
    }

    private byte[] toByteArray(InputStream input) throws IOException {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        copy(input, output);
        return output.toByteArray();
    }

    private int copy(InputStream input, OutputStream output) throws IOException {
        long count = copyLarge(input, output);
        return count > 2147483647L ? -1 : (int) count;
    }

    private long copyLarge(InputStream input, OutputStream output) throws IOException {
        byte[] buffer = new byte[4096];
        long count = 0L;
        int length;
        for (; -1 != (length = input.read(buffer)); count += (long) length) {
            output.write(buffer, 0, length);
        }
        return count;
    }

}
