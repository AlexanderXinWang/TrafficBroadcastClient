/*
 * Copyright (C) 2019 xuexiangjys(xuexiangjys@163.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.iflytek.vivian.traffic.android.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.text.TextUtils;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

import com.iflytek.vivian.traffic.android.R;
import com.iflytek.vivian.traffic.android.client.SearchClient;
import com.iflytek.vivian.traffic.android.core.BaseFragment;
import com.iflytek.vivian.traffic.android.dto.Search;
import com.iflytek.vivian.traffic.android.event.SearchEvent;
import com.iflytek.vivian.traffic.android.utils.Utils;
import com.xuexiang.xaop.annotation.SingleClick;
import com.xuexiang.xpage.annotation.Page;
import com.xuexiang.xpage.enums.CoreAnim;
import com.xuexiang.xui.utils.SnackbarUtils;
import com.xuexiang.xui.widget.actionbar.TitleBar;
import com.xuexiang.xui.widget.guidview.GuideCaseView;
import com.xuexiang.xui.widget.searchview.DefaultSearchFilter;
import com.xuexiang.xui.widget.searchview.MaterialSearchView;
import com.xuexiang.xutil.data.DateUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.sql.SQLException;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * @author xuexiang
 * @since 2019/1/2 下午4:49
 */
@Page(name = "搜索", anim = CoreAnim.zoom ,extra = R.drawable.ic_widget_search)
public class SearchViewFragment extends BaseFragment {
    @BindView(R.id.search_view)
    MaterialSearchView mSearchView;
    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;

    private View mAction;

//    private SearchRecordTagAdapter mAdapter;

    private Search searchResult = new Search();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_search_component;
    }

    @Override
    protected TitleBar initTitle() {
        TitleBar titleBar = super.initTitle().setLeftClickListener(new View.OnClickListener() {
            @SingleClick
            @Override
            public void onClick(View v) {
                popToBack();
                Utils.syncAdminMainPageStatus();
            }
        });
        mAction = titleBar.addAction(new TitleBar.ImageAction(R.drawable.icon_action_query) {

            @SingleClick
            @Override
            public void performAction(View view) {
                mSearchView.showSearch();
            }
        });
        return titleBar;
    }

    @Override
    protected void initViews() {
        new GuideCaseView.Builder(getActivity())
                .title("点击按钮开始搜索")
                .focusOn(mAction)
                .showOnce("key_start_search")
                .show();

        mSearchView.setVoiceSearch(false);
        mSearchView.setEllipsize(true);
//        mSearchView.setSuggestions(getPageSuggestions());
        mSearchView.setSearchFilter(new DefaultSearchFilter() {
            @Override
            protected boolean filter(String suggestion, String input) {
                return suggestion.toLowerCase().contains(input.toLowerCase());
            }
        });
        mSearchView.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
//                onQueryResult(query);

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                //Do some magic
                SearchClient.search(getString(R.string.server_url), newText);
                return false;
            }
        });
        mSearchView.setSubmitOnClick(true);

//        recyclerView.setLayoutManager(Utils.getFlexboxLayoutManager(getContext()));
//        recyclerView.setAdapter(mAdapter = new SearchRecordTagAdapter());
//        refreshRecord();
    }

    @Override
    public void onDestroyView() {
        if (mSearchView.isSearchOpen()) {
            mSearchView.closeSearch();
        }
        super.onDestroyView();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == MaterialSearchView.REQUEST_VOICE && resultCode == Activity.RESULT_OK) {
            ArrayList<String> matches = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            if (matches != null && matches.size() > 0) {
                String searchWrd = matches.get(0);
                if (!TextUtils.isEmpty(searchWrd)) {
                    mSearchView.setQuery(searchWrd, false);
                }
            }

            return;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onSearch(SearchEvent event) {
        if (event.isSuccess()) {
            searchResult = event.getData();
        }
    }

}
