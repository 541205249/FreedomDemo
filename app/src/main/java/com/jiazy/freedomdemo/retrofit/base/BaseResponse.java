package com.jiazy.freedomdemo.retrofit.base;

/**
 * 作者： jiazy
 * 日期： 2018/3/1.
 * 公司： 步步高教育电子有限公司
 * 描述：
 */
public class BaseResponse<T> {

    public int status;
    public String message;
    public T data;

    public boolean isSuccess(){
        return status == 200;
    }

}
