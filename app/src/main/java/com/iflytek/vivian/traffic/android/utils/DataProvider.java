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
        return Arrays.asList("根据时间升序排列", "根据时间降序排列", "根据事件名称升序排列", "根据事件名称降序排列");
    }
}
