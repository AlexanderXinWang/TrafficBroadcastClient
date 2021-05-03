package com.iflytek.vivian.traffic.android.utils;

import com.xuexiang.xaop.annotation.MemoryCache;

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
}
