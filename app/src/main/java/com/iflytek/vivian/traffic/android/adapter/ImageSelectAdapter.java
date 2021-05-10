package com.iflytek.vivian.traffic.android.adapter;

import android.content.Context;
import android.net.Uri;
import android.util.AttributeSet;
import android.view.ViewGroup;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.iflytek.vivian.traffic.android.R;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.entity.LocalMedia;
import com.xuexiang.xui.widget.imageview.RadiusImageView;

import butterknife.BindView;


/**
 * @author XUE
 * @since 2019/3/25 11:29
 */
public class ImageSelectAdapter extends RadiusImageView {

    public static final int TYPE_CAMERA = 1;
    public static final int TYPE_PICTURE = 2;
    private LocalMedia mItem;
    private RadiusImageView image;

    public ImageSelectAdapter(Context context) {
        super(context);

    }

    public ImageSelectAdapter(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ImageSelectAdapter(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


}
