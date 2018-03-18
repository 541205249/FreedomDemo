package com.jiazy.freedomdemo.frameanimation;

import android.content.Context;
import android.content.res.XmlResourceParser;
import android.graphics.BitmapFactory;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.widget.ImageView;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * GayHub上面找到的
 * 此工具类用于解决帧动画OutOfMemory问题
 * update by X 补充了一些缺少的方法
 **/
public class MyAnimationDrawable {

    public static class MyFrame {
        byte[] bytes;
        int duration;
        Drawable drawable;
        boolean isReady = false;
    }

    public interface OnDrawableLoadedListener {
        void onDrawableLoaded(List<MyFrame> myFrames);
    }

    // 1

    /**
     * 在animation-list中设置时间性能更优
     **/
    public static void animateRawManuallyFromXML(int resourceId, final ImageView imageView, final Runnable onStart, final Runnable onComplete) {
        loadRaw(resourceId, imageView.getContext(), new OnDrawableLoadedListener() {
            @Override
            public void onDrawableLoaded(List<MyFrame> myFrames) {
                if (onStart != null) {
                    onStart.run();
                }
                animateRawManually(myFrames, imageView, onComplete);
            }
        });
    }

    // 2
    private static void loadRaw(final int resourceId, final Context context, final OnDrawableLoadedListener onDrawableLoadedListener) {
        loadFromXml(resourceId, context, onDrawableLoadedListener);
    }

    // 3
    private static void loadFromXml(final int resourceId, final Context context, final OnDrawableLoadedListener onDrawableLoadedListener) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                final ArrayList<MyFrame> myFrames = new ArrayList<>();
                XmlResourceParser parser = context.getResources().getXml(resourceId);
                try {
                    int eventType = parser.getEventType();
                    while (eventType != XmlPullParser.END_DOCUMENT) {
                        if (eventType == XmlPullParser.START_DOCUMENT) {

                        } else if (eventType == XmlPullParser.START_TAG) {
                            if (parser.getName().equals("item")) {
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
                                MyFrame myFrame = new MyFrame();
                                myFrame.bytes = bytes;
                                myFrame.duration = duration;
                                myFrames.add(myFrame);
                            }
                        } else if (eventType == XmlPullParser.END_TAG) {

                        } else if (eventType == XmlPullParser.TEXT) {

                        }
                        eventType = parser.next();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (XmlPullParserException e2) {
                    // TODO: handle exception
                    e2.printStackTrace();
                }

                // Run on UI Thread
                new Handler(context.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        if (onDrawableLoadedListener != null) {
                            onDrawableLoadedListener.onDrawableLoaded(myFrames);
                        }
                    }
                });
            }
        }).run();
    }

    // 4
    private static void animateRawManually(List<MyFrame> myFrames, ImageView imageView, Runnable onComplete) {
        animateRawManually(myFrames, imageView, onComplete, 0);
    }

    // 5
    private static void animateRawManually(final List<MyFrame> myFrames, final ImageView imageView, final Runnable onComplete,
                                           final int frameNumber) {
        final MyFrame thisFrame = myFrames.get(frameNumber);
        if (frameNumber == 0) {
            thisFrame.drawable = new BitmapDrawable(imageView.getContext()
                    .getResources(), BitmapFactory.decodeByteArray(
                    thisFrame.bytes, 0, thisFrame.bytes.length));
        } else {
            MyFrame previousFrame = myFrames.get(frameNumber - 1);
            ((BitmapDrawable) previousFrame.drawable).getBitmap().recycle();
            previousFrame.drawable = null;
            previousFrame.isReady = false;
        }

        imageView.setImageDrawable(thisFrame.drawable);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // Make sure ImageView hasn't been changed to a different Image
                // in this time
                if (imageView.getDrawable() == thisFrame.drawable) {
                    if (frameNumber + 1 < myFrames.size()) {
                        MyFrame nextFrame = myFrames.get(frameNumber + 1);
                        if (nextFrame.isReady) {
                            // Animate next frame
                            animateRawManually(myFrames, imageView, onComplete, frameNumber + 1);
                        } else {
                            nextFrame.isReady = true;
                        }
                    } else {
                        if (onComplete != null) {
                            onComplete.run();
                        }
                    }
                }
            }
        }, thisFrame.duration);

        // Load next frame
//        if (frameNumber < myFrames.size()) {
//        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                int index = frameNumber + 1;
                if(index == myFrames.size()) {
                    index = 0;
                }

                MyFrame nextFrame = myFrames.get(index);
                nextFrame.drawable = new BitmapDrawable(imageView.getContext().getResources(),
                        BitmapFactory.decodeByteArray(nextFrame.bytes, 0, nextFrame.bytes.length));
                if (nextFrame.isReady) {
                    // Animate next frame
                    animateRawManually(myFrames, imageView, onComplete, index);
                } else {
                    nextFrame.isReady = true;
                }

            }
        }).run();

    }

    //第二种方法

    /***
     * 代码中控制时间,但不精确
     * duration = 1000;
     * ****/
    public static void animateManuallyFromRawResource(
            int animationDrawableResourceId, ImageView imageView,
            Runnable onStart, Runnable onComplete, int duration) throws IOException,
            XmlPullParserException {
        AnimationDrawable animationDrawable = new AnimationDrawable();

        XmlResourceParser parser = imageView.getContext().getResources()
                .getXml(animationDrawableResourceId);
        int eventType = parser.getEventType();
        while (eventType != XmlPullParser.END_DOCUMENT) {
            if (eventType == XmlPullParser.START_DOCUMENT) {

            } else if (eventType == XmlPullParser.START_TAG) {

                if (parser.getName().equals("item")) {
                    Drawable drawable = null;

                    for (int i = 0; i < parser.getAttributeCount(); i++) {
                        if (parser.getAttributeName(i).equals("drawable")) {
                            int resId = Integer.parseInt(parser
                                    .getAttributeValue(i).substring(1));
                            byte[] bytes = toByteArray(imageView
                                    .getContext().getResources()
                                    .openRawResource(resId));//IOUtils.readBytes
                            drawable = new BitmapDrawable(imageView
                                    .getContext().getResources(),
                                    BitmapFactory.decodeByteArray(bytes, 0,
                                            bytes.length));
                        } else if (parser.getAttributeName(i)
                                .equals("duration")) {
                            duration = parser.getAttributeIntValue(i, 66);
                        }
                    }
                    animationDrawable.addFrame(drawable, duration);
                }

            } else if (eventType == XmlPullParser.END_TAG) {

            } else if (eventType == XmlPullParser.TEXT) {

            }
            eventType = parser.next();
        }

        if (onStart != null) {
            onStart.run();
        }
        animateDrawableManually(animationDrawable, imageView, onComplete, 0);
    }

    private static void animateDrawableManually(
            final AnimationDrawable animationDrawable,
            final ImageView imageView, final Runnable onComplete,
            final int frameNumber) {
        final Drawable frame = animationDrawable.getFrame(frameNumber);
        imageView.setImageDrawable(frame);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // Make sure ImageView hasn't been changed to a different Image
                // in this time
                if (imageView.getDrawable() == frame) {
                    if (frameNumber + 1 < animationDrawable.getNumberOfFrames()) {
                        // Animate next frame
                        animateDrawableManually(animationDrawable, imageView,
                                onComplete, frameNumber + 1);
                    } else {
                        // Animation complete
                        if (onComplete != null) {
                            onComplete.run();
                        }
                    }
                }
            }
        }, animationDrawable.getDuration(frameNumber));
    }

    private static byte[] toByteArray(InputStream input) throws IOException {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        copy(input, output);
        return output.toByteArray();
    }

    private static int copy(InputStream input, OutputStream output) throws IOException {
        long count = copyLarge(input, output);
        return count > 2147483647L ? -1 : (int) count;
    }

    private static long copyLarge(InputStream input, OutputStream output) throws IOException {
        byte[] buffer = new byte[4096];
        long count = 0L;
        int length;
        for (; -1 != (length = input.read(buffer)); count += (long) length) {
            output.write(buffer, 0, length);
        }
        return count;
    }

}
