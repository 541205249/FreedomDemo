package com.jiazy.freedomdemo.retrofit.bean;

/**
 * 作者： jiazy
 * 日期： 2018/3/1.
 * 公司： 步步高教育电子有限公司
 * 描述：
 */
public class Movie {
    private String title;
    private String original_title;
    private String alt;
    private Image images;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getOriginal_title() {
        return original_title;
    }

    public void setOriginal_title(String original_title) {
        this.original_title = original_title;
    }

    public String getAlt() {
        return alt;
    }

    public void setAlt(String alt) {
        this.alt = alt;
    }

    public Image getImages() {
        return images;
    }

    public void setImages(Image images) {
        this.images = images;
    }
}
