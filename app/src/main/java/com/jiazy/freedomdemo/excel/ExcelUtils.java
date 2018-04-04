package com.jiazy.freedomdemo.excel;

import android.os.Environment;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import jxl.Sheet;
import jxl.Workbook;
import jxl.format.Colour;
import jxl.read.biff.BiffException;
import jxl.write.Label;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;

/**
 * Excel不能超过3万8千行，否则读取报错
 */
public class ExcelUtils {
	private static final String EXCEL_FILE_NAME = "语义理解测试结果.xls";
	private static final String[] EXCEL_TITLE = { "data", "skillId", "intentName", "isCorrect" };

	public interface OnExcelLoadListener {
		void onLoaded(Sheet sheet);
	}

	public interface OnWriteExcelListener {
		void onWriteEnd();
	}

	public static void loadExcel(File file, OnExcelLoadListener onExcelLoadListener) throws Exception {
		if (!file.exists()) {
			onExcelLoadListener.onLoaded(null);
		}

		new Thread(() -> {
            try {
				Workbook wb = Workbook.getWorkbook(file);
				onExcelLoadListener.onLoaded(wb.getSheet(0));
				wb.close();
            } catch (IOException | BiffException e) {
                e.printStackTrace();
                onExcelLoadListener.onLoaded(null);
            }
        }).start();
	}

	public static synchronized void writeExcel(UnderstandingResultInfo resultInfo, OnWriteExcelListener onWriteExcelListener) throws Exception {
		WritableWorkbook wwb = getWritableWorkbook();
		WritableSheet sheet = getWritableSheet(wwb);
		writeRow(resultInfo, wwb, sheet);
		wwb.close();

		onWriteExcelListener.onWriteEnd();
	}

	private static void writeRow(UnderstandingResultInfo resultInfo, WritableWorkbook wwb, WritableSheet sheet)
			throws WriteException, IOException {
		int sheetRows = sheet.getRows();

		sheet.addCell(new Label(0, sheetRows, resultInfo.getData()));
		sheet.addCell(new Label(1, sheetRows, resultInfo.getSkillId()));
		sheet.addCell(new Label(2, sheetRows, resultInfo.getIntentName()));
		sheet.addCell(new Label(3, sheetRows, resultInfo.getResult() + ""));

		wwb.write();
	}

	private static WritableSheet getWritableSheet(WritableWorkbook wwb) throws WriteException {
		if (wwb.getNumberOfSheets() == 0) {
			return createWritableSheet(wwb);
		} else {
			return wwb.getSheet(0);
		}
	}

    private static WritableSheet createWritableSheet(WritableWorkbook wwb) throws WriteException {
		WritableSheet sheet = wwb.createSheet("Sheet1", 0);

		Label label;
		for (int i = 0; i < EXCEL_TITLE.length; i++) {
			// Label(x,y,z) 代表单元格的第x+1列，第y+1行, 内容z
			// 在Label对象的子对象中指明单元格的位置和内容
			label = new Label(i, 0, EXCEL_TITLE[i], getHeader());
			sheet.addCell(label);
		}

		return sheet;
	}

	private static WritableWorkbook getWritableWorkbook() throws IOException, BiffException {
		File dir = Environment.getExternalStorageDirectory();
		File file = new File(dir, EXCEL_FILE_NAME);

		WritableWorkbook wwb;
		if (file.exists()) {
			Workbook wb = Workbook.getWorkbook(file);
			wwb = Workbook.createWorkbook(file, wb);
		} else {
            OutputStream os = new FileOutputStream(file);
            wwb = Workbook.createWorkbook(os);
        }

		return wwb;
	}

	private static WritableCellFormat getHeader() {
		WritableFont font = new WritableFont(WritableFont.TIMES, 12,
				WritableFont.BOLD);// 定义字体
		try {
			font.setColour(Colour.BLUE);// 蓝色字体
		} catch (WriteException e1) {
			e1.printStackTrace();
		}

		WritableCellFormat format = new WritableCellFormat(font);
		try {
			format.setAlignment(jxl.format.Alignment.CENTRE);// 左右居中
			format.setVerticalAlignment(jxl.format.VerticalAlignment.CENTRE);// 上下居中
			// format.setBorder(Border.ALL, BorderLineStyle.THIN,
			// Colour.BLACK);// 黑色边框
			// format.setBackground(Colour.YELLOW);// 黄色背景
		} catch (WriteException e) {
			e.printStackTrace();
		}
		return format;
	}

}
