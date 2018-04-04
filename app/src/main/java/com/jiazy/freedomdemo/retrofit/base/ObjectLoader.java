package com.jiazy.freedomdemo.retrofit.base;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * 作者： jiazy
 * 日期： 2018/3/1.
 * 公司： 步步高教育电子有限公司
 * 描述：将一些重复的操作提出来，放到父类以免Loader 里每个接口都有重复代码
 */
public class ObjectLoader {
    /**
     * @param observable
     * @param <T>
     * @return
     */
    protected  <T> Observable<T> observe(Observable<T> observable){
        return observable
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }
}
