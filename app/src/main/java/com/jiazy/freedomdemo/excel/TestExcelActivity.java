package com.jiazy.freedomdemo.excel;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.jiazy.freedomdemo.R;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class TestExcelActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_testexcel);
        setTitle("Excel");

        EventBus.getDefault().register(this);

        findViewById(R.id.btn).setOnClickListener(v -> {
            executeTest();
        });

    }

    private void executeTest() {
        try {
            UnderstandingUtils.getUnderstandingData(getApplicationContext(), "语义理解1.xls");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(UnderstandingInfo understandingInfo){
        Log.i("jzy",understandingInfo.toString());
        Toast.makeText(getApplicationContext(), "info=" + understandingInfo.toString(), Toast.LENGTH_SHORT).show();

        try {
            ExcelUtils.writeExcel(new UnderstandingResultInfo(understandingInfo, 1), this::executeTest);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
