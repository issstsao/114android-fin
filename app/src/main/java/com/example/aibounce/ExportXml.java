package com.example.aibounce;

import android.content.Context;
import android.database.Cursor;
import android.os.Environment;
import android.widget.Toast;
import java.io.File;
import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ExportXml {

    public static void export(Context context, Cursor cursor) {
        try {
            File downloads = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
            File file = new File(downloads, "AI彈跳實驗報告_" +
                    new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date()) + ".xml");

            FileWriter writer = new FileWriter(file);

            writer.write("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n");
            writer.write("<bounce_experiments>\n");

            if (cursor.moveToFirst()) {
                do {
                    writer.write("  <record>\n");
                    writer.write("    <height>" + cursor.getDouble(1) + "</height>\n");
                    writer.write("    <time>" + cursor.getDouble(2) + "</time>\n");
                    writer.write("    <gravity>" + String.format("%.4f", cursor.getDouble(3)) + "</gravity>\n");
                    writer.write("    <timestamp>" + cursor.getString(4) + "</timestamp>\n");
                    writer.write("  </record>\n");
                } while (cursor.moveToNext());
            }

            writer.write("</bounce_experiments>");
            writer.close();

            Toast.makeText(context, "已匯出！\n位置：Downloads/" + file.getName(), Toast.LENGTH_LONG).show();

        } catch (Exception e) {
            Toast.makeText(context, "匯出失敗：" + e.getMessage(), Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }
}