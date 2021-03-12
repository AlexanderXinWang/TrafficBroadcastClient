package com.iflytek.vivian.traffic.android.fragment;

import com.iflytek.vivian.traffic.android.R;
import com.iflytek.vivian.traffic.android.core.BaseFragment;
import com.xuexiang.xpage.annotation.Page;
import com.xuexiang.xpage.enums.CoreAnim;
import com.xuexiang.xui.widget.actionbar.TitleBar;

@Page(anim = CoreAnim.none)
public class EventReportFragment extends BaseFragment {

    /**
     * @return 返回为 null意为不需要导航栏
     */
    @Override
    protected TitleBar initTitle() {
        return null;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_report;
    }

    @Override
    protected void initViews() {

    }
}
