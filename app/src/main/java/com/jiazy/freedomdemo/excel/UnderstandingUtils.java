package com.jiazy.freedomdemo.excel;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import org.greenrobot.eventbus.EventBus;

import java.io.File;

import jxl.Cell;
import jxl.Sheet;

import static com.jiazy.freedomdemo.excel.SharedPreferenceUtils.getCurrentIndex;

/**
 * 作者： jiazy
 * 日期： 2018/3/22.
 * 公司： 步步高教育电子有限公司
 * 描述：
 */
public class UnderstandingUtils {
    //skiiId和intentName相同就行

    public static void getUnderstandingData(Context context, String excelName) throws Exception {
        File dir = Environment.getExternalStorageDirectory();
        File file = new File(dir, excelName);
        ExcelUtils.loadExcel(file, sheet -> {
            int currentIndex = getCurrentIndex(context);
            getOneUnderstandingData(context, sheet, currentIndex);
        });
    }

    private static void getOneUnderstandingData(Context context, Sheet sheet, int currentIndex) {
        if(sheet == null) {
            return;
        }

        Cell[] cells = sheet.getRow(currentIndex);
        UnderstandingInfo info = new UnderstandingInfo(
                cells[0].getContents().trim(),
                cells[1].getContents().trim(),
                cells[2].getContents().trim());

        EventBus.getDefault().post(info);
        SharedPreferenceUtils.changeCurrentIndex(context);
        Log.i("jzy", info.toString());
    }

    public static void recordOneResult() {

    }
}
