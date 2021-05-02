package com.iflytek.vivian.traffic.android.adapter;

import com.iflytek.vivian.traffic.android.R;
import com.iflytek.vivian.traffic.android.client.EventClient;
import com.iflytek.vivian.traffic.android.utils.XToastUtils;
import com.scwang.smartrefresh.layout.adapter.SmartRecyclerAdapter;
import com.scwang.smartrefresh.layout.adapter.SmartViewHolder;
import com.xuexiang.xui.adapter.recyclerview.RecyclerViewHolder;
import com.xuexiang.xui.utils.ResUtils;

import java.util.Collection;

/**
 * 基于simple_list_item_2简单的适配器
 *
 * @author XUE
 * @since 2019/4/1 11:04
 */
public class SimpleRecyclerAdapter extends SmartRecyclerAdapter<String> {

    public SimpleRecyclerAdapter() {
        super(android.R.layout.simple_list_item_2);
    }

    public SimpleRecyclerAdapter(Collection<String> data) {
        super(data, android.R.layout.simple_list_item_2);
    }

    /**
     * 绑定布局控件
     *
     * @param holder
     * @param model
     * @param position
     */
    @Override
    protected void onBindViewHolder(SmartViewHolder holder, String model, int position) {
//        holder.text(android.R.id.text1, ResUtils.getResources().getString(R.string.item_example_number_title, position));
//        holder.text(android.R.id.text2, ResUtils.getResources().getString(R.string.item_example_number_abstract, position));


    }

}
