package com.iflytek.vivian.traffic.android.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.xuexiang.xaop.annotation.MemoryCache;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;
import java.util.Collection;

/**
 * 一般数据
 */
public class DataProvider {

    @MemoryCache
    public static Collection<String> getDemoData() {
        return Arrays.asList("根据时间升序排列", "根据时间降序排列", "根据上报人升序排列", "根据上报人降序排列", "根据地点升序排列", "根据地点降序排列");
    }

    public static String[] eventFilterItems = new String[]{
            "时间升序",
            "时间降序",
            "上报人升序",
            "上报人降序",
            "地点升序",
            "地点降序",
    };

    public static String[] userFilterItems = new String[]{
            "姓名升序",
            "姓名降序",
            "编号升序",
            "编号降序",
            "年龄升序",
            "年龄降序",
    };

    public static Bitmap getBitmap(String path) throws IOException {

        URL url = new URL(path);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setConnectTimeout(5000);
        conn.setRequestMethod("GET");
        if (conn.getResponseCode() == 200){
            InputStream inputStream = conn.getInputStream();
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
            return bitmap;
        }
        return null;
    }
}
