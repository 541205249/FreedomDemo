package com.jiazy.freedomdemo;

import android.app.Application;
import android.content.Context;

import com.squareup.leakcanary.LeakCanary;
import com.squareup.leakcanary.RefWatcher;

/**
 * 作者： jiazy
 * 日期： 2018/3/30.
 * 公司： 步步高教育电子有限公司
 * 描述：
 */
public class ExampleApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        initLeakCanary();
    }

    private void initLeakCanary(){
        // leakcanary默认只监控Activity的内存泄露
        if (LeakCanary.isInAnalyzerProcess(this)) {
            return;
        }
        refWatcher = LeakCanary.install(this);
    }

    // 如果要监控APP中某个对象的内存泄露情况，可以通过RefWatcher类实现，需要在Application总对RefWatcher类做初始化操作
    public static RefWatcher getRefWatcher(Context context) {
        ExampleApplication application = (ExampleApplication) context.getApplicationContext();
        return application.refWatcher;
    }

    private RefWatcher refWatcher;
}
