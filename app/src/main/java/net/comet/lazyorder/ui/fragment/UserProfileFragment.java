package net.comet.lazyorder.ui.fragment;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.ImageView;
import com.levelmoney.velodrome.annotations.OnActivityResult;
import com.squareup.picasso.Picasso;
import net.comet.lazyorder.R;
import net.comet.lazyorder.context.AppContext;
import net.comet.lazyorder.context.AppCookie;
import net.comet.lazyorder.model.bean.ResponseError;
import net.comet.lazyorder.model.bean.User;
import net.comet.lazyorder.ui.BaseUiController;
import net.comet.lazyorder.ui.UserController;
import net.comet.lazyorder.util.Constants.RequestCode;
import net.comet.lazyorder.util.FileUtil;
import net.comet.lazyorder.util.SDCardUtil;
import net.comet.lazyorder.util.StringUtil;
import net.comet.lazyorder.util.SystemUtil;
import net.comet.lazyorder.util.ToastUtil;
import net.comet.lazyorder.widget.section.SectionExtensionItemView;
import net.comet.lazyorder.widget.section.SectionTextItemView;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import butterknife.Bind;
import butterknife.OnClick;

public class UserProfileFragment extends BaseFragment<UserController.UserUiCallbacks>
        implements UserController.UserProfileUi {

    private final static int CROP = 300;

    @Bind(R.id.item_avatar)
    SectionExtensionItemView mAvatarItem;

    @Bind(R.id.img_avatar)
    ImageView mAvatarImg;

    @Bind(R.id.item_nickname)
    SectionTextItemView mNicknameItem;

    @Bind(R.id.item_mobile)
    SectionTextItemView mMobileItem;

    @Bind(R.id.item_email)
    SectionTextItemView mEmailItem;

    private User mUserInfo;
    private Uri mOriginUri;
    private File mCroppedFile;

    protected BaseUiController getController() {
        return AppContext.getContext().getMainController().getUserController();
    }

    @Override
    protected int getViewLayoutId() {
        return R.layout.fragment_user_profile;
    }

    @Override
    protected void initialViews(Bundle savedInstanceState) {
        mUserInfo = AppCookie.getUserInfo();
        String avatarUrl = mUserInfo.getAvatarUrl();
        if (StringUtil.isNotEmpty(avatarUrl)) {
            Picasso.with(getContext())
                    .load(avatarUrl)
                    .into(mAvatarImg);
        }
    }

    @Override
    public void uploadAvatarFinish(String url) {
        cancelLoading();
        mUserInfo.setAvatarUrl(url);
        AppCookie.saveUserInfo(mUserInfo);
        ToastUtil.showToast(R.string.toast_success_upload_avatar);
    }

    @Override
    public void onNetworkError(ResponseError error) {
        cancelLoading();
        ToastUtil.showToast(error.getMessage());
    }

    @OnClick({R.id.item_avatar, R.id.item_nickname, R.id.item_mobile, R.id.item_email})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.item_avatar:
                selectUpdateAvatarMethod();
                break;
            case R.id.item_nickname:
                break;
            case R.id.item_mobile:
                break;
            case R.id.item_email:
                break;
        }
    }

    /**
     * 显示选择更换头像的方式
     */
    private void selectUpdateAvatarMethod() {
        new AlertDialog.Builder(getContext())
                .setTitle(R.string.dialog_update_avatar_title)
                .setItems(R.array.update_avatar, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (which == 0) {
                            takePhotoByCamera();
                        } else {
                            pickPhotoByAlbums();
                        }
                    }
                })
                .show();
    }

    /**
     * 使用相机进行拍照
     */
    @SuppressLint("SimpleDateFormat")
    private void takePhotoByCamera() {
        String timeStamp = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
        String fileName = "lazy_" + timeStamp + ".jpg";
        if (SDCardUtil.isAvailable()) {
            File out = FileUtil.createImageFile(getContext(), fileName);
            mOriginUri = Uri.fromFile(out);
            SystemUtil.takePhoto(this, mOriginUri, RequestCode.REQUEST_CODE_TAKE_PHOTO);
        } else {
            ToastUtil.showToast("无法保存照片，请检查SD卡是否挂载");
        }
    }

    /**
     * 从相册选择照片
     */
    private void pickPhotoByAlbums() {
        SystemUtil.openAlbums(this, "选择图片", RequestCode.REQUEST_CODE_PICK_PHOTO);
    }

    /**
     * 裁剪照片
     */
    private void startPhotoCrop(Uri data) {
        String timeStamp = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
        String fileName = "lazy_crop_" + timeStamp + ".jpg";// 照片命名
        if (SDCardUtil.isAvailable()) {
            mCroppedFile = FileUtil.createImageFile(getContext(), fileName);
            Bundle option = new Bundle();
            option.putInt("width", CROP);
            option.putInt("height", CROP);
            SystemUtil.cropPicture(this, data, Uri.fromFile(mCroppedFile), RequestCode.REQUEST_CODE_CUTTING, option);
        } else {
            ToastUtil.showToast("无法保存上传的头像，请检查SD卡是否挂载");
        }
    }

    @OnActivityResult(RequestCode.REQUEST_CODE_TAKE_PHOTO)
    public void onTakePhotoByCamera(Intent data) {
        startPhotoCrop(mOriginUri);
    }

    @OnActivityResult(RequestCode.REQUEST_CODE_PICK_PHOTO)
    public void onPickPhotoByAlbums(Intent data) {
        try {
            startPhotoCrop(data.getData());
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    @OnActivityResult(RequestCode.REQUEST_CODE_CUTTING)
    public void onCuttingPhoto(Intent data) {
        Picasso.with(getContext())
                .load(mCroppedFile)
                .into(mAvatarImg);
        // 开始上传图片
        showLoading(R.string.label_being_something);
        if (hasCallbacks()) {
            getCallbacks().uploadAvatar(mCroppedFile);
        }
    }
}
