package com.iflytek.vivian.traffic.android.fragment;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.nfc.Tag;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TableLayout;

import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.iflytek.vivian.traffic.android.R;
import com.iflytek.vivian.traffic.android.adapter.ImageSelectAdapter;
import com.iflytek.vivian.traffic.android.client.UserClient;
import com.iflytek.vivian.traffic.android.core.BaseFragment;
import com.iflytek.vivian.traffic.android.dto.User;
import com.iflytek.vivian.traffic.android.event.user.UploadImageEvent;
import com.iflytek.vivian.traffic.android.event.user.UserSaveEvent;
import com.iflytek.vivian.traffic.android.utils.StringUtil;
import com.iflytek.vivian.traffic.android.utils.Utils;
import com.iflytek.vivian.traffic.android.utils.XToastUtils;
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.entity.LocalMedia;
import com.xuexiang.xaop.annotation.SingleClick;
import com.xuexiang.xpage.annotation.Page;
import com.xuexiang.xpage.enums.CoreAnim;
import com.xuexiang.xui.widget.dialog.materialdialog.MaterialDialog;
import com.xuexiang.xui.widget.imageview.RadiusImageView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

@Page(anim = CoreAnim.slide, name = "新增警员用户")
public class UserManagerAddFragment extends BaseFragment {

    private static final String TAG = "UserManagerAdd";

    @BindView(R.id.user_add_image)
    RadiusImageView image;
    @BindView(R.id.user_add_name)
    EditText name;
    @BindView(R.id.user_add_id)
    EditText id;
    @BindView(R.id.user_add_age)
    EditText age;
    @BindView(R.id.user_add_role)
    EditText role;
    @BindView(R.id.user_add_place)
    EditText place;
    @BindView(R.id.user_add_department)
    EditText depart;

    private User user;

    private List<LocalMedia> mSelectList = new ArrayList<>();

    private String imageUrl = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        EventBus.getDefault().register(this);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_user_manager_add;
    }

    @Override
    protected void initViews() {

    }

    @Override
    protected void initListeners() {
        super.initListeners();
//        image.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Utils.getPictureSelector(getActivity())
//                        .selectionMedia(mSelectList)
//                        .maxSelectNum(1)
//                        .selectionMode(PictureConfig.SINGLE)
//                        .forResult(PictureConfig.CHOOSE_REQUEST);
//            }
//        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        EventBus.getDefault().unregister(this);
    }

    @SingleClick
    @OnClick({R.id.user_add_save, R.id.user_add_image})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.user_add_save:
                // TODO 入参验证
                user = new User();
                user.setImageUrl(imageUrl);
                user.setId(id.getText().toString());
                user.setName(name.getText().toString());
                user.setAge(age.getText().toString());
                user.setRole(role.getText().toString());
                user.setPlace(place.getText().toString());
                user.setDepartment(depart.getText().toString());
                user.setPassword(id.getText().toString());
                // TODO 上传头像

                new MaterialDialog.Builder(getContext()).title("确认保存？").positiveText("确认").negativeText("取消")
                        .onPositive(((dialog, which) -> UserClient.saveUser(getString(R.string.server_url), user))).show();

                break;
            case R.id.user_add_image:
//                Utils.getPictureSelector(this)
//                        .selectionMedia(mSelectList)
//                        .maxSelectNum(1)
//                        .selectionMode(PictureConfig.SINGLE)
//                        .forResult(PictureConfig.CHOOSE_REQUEST);
                PictureSelector.create(this)
                        .openGallery(PictureMimeType.ofImage())
                        .maxSelectNum(1)
                        .selectionMode(PictureConfig.SINGLE)
                        .forResult(PictureConfig.CHOOSE_REQUEST);
            default:
                break;
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
//        String path = getContext().getCacheDir() + "/tmp.jpeg";
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case PictureConfig.CHOOSE_REQUEST:
//                    Bundle bundle = data.getExtras();
//                    Bitmap bitmap = (Bitmap) bundle.get("data");
//                    try {
//                        FileOutputStream fos = new FileOutputStream(path);
//                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
//                        image.setImageBitmap(bitmap);
//                    } catch (FileNotFoundException e) {
//                        e.printStackTrace();
//                    }
//                    mSelectList = PictureSelector.obtainMultipleResult(data);
//
//                    LocalMedia media = mSelectList.get(0);
//                    String path;
//                    path = media.getPath();
//                    RequestOptions options = new RequestOptions()
//                            .centerCrop()
//                            .placeholder(R.drawable.ic_default_head)
//                            .diskCacheStrategy(DiskCacheStrategy.ALL);
//                    Glide.with(getContext())
//                            .load(path)
//                            .apply(options)
//                            .into(image);
                    mSelectList = PictureSelector.obtainMultipleResult(data);
                    LocalMedia media = mSelectList.get(0);
                    String path = media.getPath();
                    RequestOptions options = new RequestOptions()
                            .centerCrop()
                            .placeholder(R.drawable.ic_default_head)
                            .diskCacheStrategy(DiskCacheStrategy.ALL);
                    Glide.with(getContext())
                            .load(path)
                            .apply(options)
                            .into(image);
                    // TODO
                    if (StringUtil.isNotEmpty(id.getText().toString())) {
                        File file = new File(path);
                        UserClient.uploadImage(getString(R.string.server_url), file, id.getText().toString());
                    } else {
                        XToastUtils.error("请先输入用户编号！");
                    }

                    break;
                default:
                    break;
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onSaveUser(UserSaveEvent event) {
        if (event.isSuccess()) {
            XToastUtils.success("新增警员用户成功，请刷新");
            popToBack();
        } else {
            XToastUtils.error("新增警员用户失败！");
            Log.e(TAG, "新增警员用户失败" + event.getErrorMessage());
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onUploadImage(UploadImageEvent event) {
        if (event.isSuccess()) {
            imageUrl = event.getData();
            if (StringUtil.isNotEmpty(imageUrl)) {
                XToastUtils.success("上传头像成功");
            } else {
                XToastUtils.error("上传头像失败！");
            }
        } else {
            XToastUtils.error("上传头像失败！");
            Log.e(TAG, "上传头像失败" + event.getErrorMessage());
        }
    }
}
