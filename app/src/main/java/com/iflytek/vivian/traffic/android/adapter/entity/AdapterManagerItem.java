package com.iflytek.vivian.traffic.android.adapter.entity;

import android.content.Context;
import android.graphics.drawable.Drawable;

import com.xuexiang.xui.adapter.simple.AdapterItem;
import com.xuexiang.xui.utils.ResUtils;

public class AdapterManagerItem {
    private Drawable mIcon;

    public AdapterManagerItem(Drawable icon) {
        mIcon = icon;
    }

    public static AdapterManagerItem of(Drawable mIcon) {
        return new AdapterManagerItem(mIcon);
    }

    public static AdapterManagerItem[] arrayof(Drawable[] icon) {
        AdapterManagerItem[] array = new AdapterManagerItem[icon.length];
        for (int i = 0; i < array.length; i++) {
            array[i] = new AdapterManagerItem(icon[i]);
        }
        return array;
    }

    public AdapterManagerItem(Context context, int drawableId) {
        this(ResUtils.getDrawable(context, drawableId));
    }

    public Drawable getIcon() {
        return mIcon;
    }

    public void setIcon(Drawable mIcon) {
        this.mIcon = mIcon;
    }
}
