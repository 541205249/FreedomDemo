package com.jiazy.freedomdemo.retrofit.bean;

import java.util.List;

/**
 * 作者： jiazy
 * 日期： 2018/2/28.
 * 公司： 步步高教育电子有限公司
 * 描述：
 */
public class MovieSubject {
    private int count;
    private int start;
    private int total;
    private String title;
    private List<Movie> subjects;

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public int getStart() {
        return start;
    }

    public void setStart(int start) {
        this.start = start;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<Movie> getSubjects() {
        return subjects;
    }

    public void setSubjects(List<Movie> subjects) {
        this.subjects = subjects;
    }
}
