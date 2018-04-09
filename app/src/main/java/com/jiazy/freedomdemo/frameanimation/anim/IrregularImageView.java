package com.jiazy.freedomdemo.frameanimation.anim;

/*
  作者： jiazy
  日期： 2018/2/28.
  公司： 步步高教育电子有限公司
  描述：主要对图片的透明区域做了点击事件拦截，点击不透明区域才响应事件，透明区域不响应事件
 */
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.view.MotionEvent;

public class IrregularImageView extends android.support.v7.widget.AppCompatImageView {

    public IrregularImageView(Context context) {
        super(context);
    }

    public IrregularImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public IrregularImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // 点击了透明区域，则向下传递给底层的控件，当前控件不响应点击事件
        return !isTransparent(event) && super.onTouchEvent(event);
    }

    /**
     * 该点的颜色值是否为透明
     */
    public boolean isTransparent(MotionEvent event) {
        Bitmap bitmap = getBitmap();

        int pixelColor = getViewPixelColor(event, bitmap);
        String colorStr = Integer.toHexString(pixelColor);

        return "0".equals(colorStr);
    }

    private Bitmap getBitmap() {
        setDrawingCacheEnabled(true);// ImageView对象必须做如下设置后，才能获取其中的图像
        Bitmap bitmap = Bitmap.createBitmap(getDrawingCache());// 获取ImageView中的图像
        setDrawingCacheEnabled(false);// 从ImaggeView对象中获取图像后，调用setDrawingCacheEnabled(false)清空画图缓

        return bitmap;
    }

    /**
     * 获取某点上bitmap的颜色值
     */
    private int getViewPixelColor(MotionEvent event, Bitmap bitmap) {
        int x = (int) event.getX();
        int y = (int) event.getY();

        int pixel;
        try {
            // 位图的坐标和MotionEvent的坐标不一致，需要通过一定比例进行转换
            pixel = bitmap.getPixel(x, y);
        } catch (Exception e) {
            pixel = -1;
        }

        return pixel;
    }

}
