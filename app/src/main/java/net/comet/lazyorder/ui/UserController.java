package net.comet.lazyorder.ui;

import com.google.common.base.Preconditions;
import com.squareup.otto.Subscribe;
import net.comet.lazyorder.R;
import net.comet.lazyorder.context.AppConfig;
import net.comet.lazyorder.context.AppCookie;
import net.comet.lazyorder.model.bean.ResponseError;
import net.comet.lazyorder.model.bean.Token;
import net.comet.lazyorder.model.bean.User;
import net.comet.lazyorder.model.event.AccountChangedEvent;
import net.comet.lazyorder.network.RestApiClient;
import net.comet.lazyorder.network.action.ErrorAction;
import net.comet.lazyorder.util.EventUtil;
import net.comet.lazyorder.util.StringFetcher;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import javax.inject.Inject;
import javax.inject.Singleton;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;
import static net.comet.lazyorder.util.Constants.Key.*;

@Singleton
public class UserController extends BaseUiController<UserController.UserUi, UserController.UserUiCallbacks> {

    private static final String LOG_TAG = UserController.class.getSimpleName();

    private final RestApiClient mRestApiClient;

    @Inject
    public UserController(RestApiClient restApiClient) {
        super();
        mRestApiClient = Preconditions.checkNotNull(restApiClient, "restApiClient cannot be null");
    }

    @Subscribe
    public void onAccountChanged(AccountChangedEvent event) {
        User user = event.getUser();
        if (user != null) {
            AppCookie.saveUserInfo(user);
            AppCookie.saveLastPhone(user.getMobile());
        } else {
            AppCookie.saveUserInfo(null);
            AppCookie.saveAccessToken(null);
            AppCookie.saveRefreshToken(null);
            mRestApiClient.setToken(null);
        }

        populateUis();
    }

    @Override
    protected void onInited() {
        super.onInited();
        EventUtil.register(this);
    }

    @Override
    protected void onSuspended() {
        EventUtil.unregister(this);
        super.onSuspended();
    }

    @Override
    protected String getUiTitle(UserUi ui) {
        if (ui instanceof UserRegisterUi) {
            return StringFetcher.getString(R.string.title_register);
        } else if (ui instanceof UserLoginUi) {
            return StringFetcher.getString(R.string.title_login);
        } else if (ui instanceof UserCenterUi) {
            return StringFetcher.getString(R.string.title_user_center);
        } else if (ui instanceof UserProfileUi) {
            return StringFetcher.getString(R.string.title_profile);
        }

        return null;
    }

    @Override
    protected void populateUi(UserUi ui) {
        if (ui instanceof UserCenterUi) {
            populateUserCenterUi((UserCenterUi)ui);
        }
    }

    private void populateUserCenterUi(UserCenterUi ui) {
        ui.showUserInfo(AppCookie.isLoggin() ? AppCookie.getUserInfo() : null);
    }

    /**
     * 注册的第1个步骤:发送短信验证码
     * @param callingId
     * @param mobile
     */
    private void doSendCode(final int callingId, String mobile) {
        mRestApiClient.accountService()
                .sendCode("send_code", mobile)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Object>() {
                    @Override
                    public void call(Object o) {
                        UserUi ui = findUi(callingId);
                        if (ui instanceof RegisterFirstStepUi) {
                            ((RegisterFirstStepUi) ui).sendCodeFinish();
                        }
                    }
                }, new ErrorAction() {
                    @Override
                    public void call(ResponseError error) {
                        UserUi ui = findUi(callingId);
                        if (ui instanceof RegisterFirstStepUi) {
                            ((RegisterFirstStepUi) ui).onNetworkError(error);
                        }
                    }
                });

    }

    /**
     * 注册的第2个步骤:检查短信验证码
     * @param callingId
     * @param mobile
     * @param code
     */
    private void doCheckCode(final int callingId, String mobile, String code) {
        mRestApiClient.accountService()
                .verifyCode("verify_code", mobile, code)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Object>() {
                    @Override
                    public void call(Object o) {
                        UserUi ui = findUi(callingId);
                        if (ui instanceof RegisterFirstStepUi) {
                            ((RegisterSecondStepUi) ui).verifyMobileFinish();
                        }
                    }
                }, new ErrorAction() {
                    @Override
                    public void call(ResponseError error) {
                        UserUi ui = findUi(callingId);
                        if (ui instanceof RegisterSecondStepUi) {
                            ((RegisterSecondStepUi) ui).onNetworkError(error);
                        }
                    }
                });

    }

    /**
     * 注册的第3个步骤:创建账户
     * @param callingId
     * @param mobile
     * @param password
     */
    private void doCreateUser(final int callingId, String mobile, String password) {
        mRestApiClient.accountService()
                .register("create_user", mobile, password)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Object>() {
                    @Override
                    public void call(Object o) {
                        UserUi ui = findUi(callingId);
                        if (ui instanceof RegisterThirdStepUi) {
                            ((RegisterThirdStepUi) ui).userCreateFinish();
                        }
                    }
                }, new ErrorAction() {
                    @Override
                    public void call(ResponseError error) {
                        UserUi ui = findUi(callingId);
                        if (ui instanceof RegisterThirdStepUi) {
                            ((RegisterThirdStepUi) ui).onNetworkError(error);
                        }
                    }
                });
    }

    /**
     * 用户登录,获取 Access Token 以及用户资料
     * @param callingId
     * @param username
     * @param password
     */
    private void doLogin(final int callingId, String username, String password) {
        Map<String, String> params = new HashMap<>();
        params.put(PARAM_CLIENT_ID, AppConfig.CLIENT_ID);
        params.put(PARAM_CLIENT_SECRET, AppConfig.CLIENT_SECRET);
        params.put(PARAM_GRANT_TYPE, "password");
        params.put(PARAM_USER_NAME, username);
        params.put(PARAM_PASSWORD, password);

        mRestApiClient.tokenService()
                .accessToken(params)
                .flatMap(new Func1<Token, Observable<User>>() {
                    @Override
                    public Observable<User> call(final Token token) {
                        AppCookie.saveAccessToken(token.getAccessToken());
                        AppCookie.saveRefreshToken(token.getRefreshToken());

                        return mRestApiClient.setToken(token.getAccessToken())
                                .accountService()
                                .profile(token.getUserId());
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<User>() {
                    @Override
                    public void call(User user) {
                        UserUi ui = findUi(callingId);
                        if (ui instanceof UserLoginUi) {
                            ((UserLoginUi) ui).userLoginFinish();
                            // 发送用户账户改变的事件
                            EventUtil.sendEvent(new AccountChangedEvent(user));
                        }
                    }
                }, new ErrorAction() {
                    @Override
                    public void call(ResponseError error) {
                        UserUi ui = findUi(callingId);
                        if (ui instanceof UserLoginUi) {
                            ((UserLoginUi) ui).onNetworkError(error);
                        }
                    }
                });
    }

    /**
     * 用户上传头像
     * @param callingId
     */
    private void doUploadAvatar(final int callingId, File file) {
        RequestBody requestBody = RequestBody.create(MediaType.parse("image/*"), file);
        MultipartBody.Part part = MultipartBody.Part.createFormData("avatar", file.getName(), requestBody);

        mRestApiClient.commonService()
            .singleFileUpload(part)
            .flatMap(new Func1<String, Observable<User>>() {
                @Override
                public Observable<User> call(final String url) {
                    return mRestApiClient
                            .accountService()
                            .updateAvatar(AppCookie.getUserInfo().getId(), url);
                }
            })
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(new Action1<User>() {
                @Override
                public void call(User user) {
                    UserUi ui = findUi(callingId);
                    if (ui instanceof UserProfileUi) {
                        ((UserProfileUi) ui).uploadAvatarFinish(user.getAvatarUrl());
                    }
                }
            }, new ErrorAction() {
                @Override
                public void call(ResponseError error) {
                    UserUi ui = findUi(callingId);
                    if (ui instanceof UserProfileUi) {
                        ((UserProfileUi) ui).onNetworkError(error);
                    }
                }
            });
    }

    /**
     * 创建留给UI界面使用的回调
     * @param ui
     * @return
     */
    @Override
    protected UserUiCallbacks createUiCallbacks(final UserUi ui) {
        return new UserUiCallbacks() {

            @Override
            public void showRegister() {
                Display display = getDisplay();
                if (display != null) {
                    display.startRegisterActivity();
                }
            }

            @Override
            public void showLogin() {
                Display display = getDisplay();
                if (display != null) {
                    display.startLoginActivity();
                }
            }

            @Override
            public void showUserProfile() {
                Display display = getDisplay();
                if (display != null) {
                    display.startUserActivity();
                }
            }

            @Override
            public void showAddressList() {
                Display display = getDisplay();
                if (display != null) {
                    if (AppCookie.isLoggin()) {
                        display.startAddressListActivity();
                    } else {
                        display.startLoginActivity();
                    }
                }
            }

            @Override
            public void checkUpdate() {

            }

            @Override
            public void sendCode(String mobile) {
                doSendCode(getId(ui), mobile);
            }

            @Override
            public void checkCode(String mobile, String code) {
                doCheckCode(getId(ui), mobile, code);
            }

            @Override
            public void createUser(String mobile, String password) {
                doCreateUser(getId(ui), mobile, password);
            }

            @Override
            public void uploadAvatar(File file) {
                doUploadAvatar(getId(ui), file);
            }

            @Override
            public void login(String account, String password) {
                doLogin(getId(ui), account, password);
            }

            @Override
            public void logout() {
                // 发送用户账户改变的事件
                EventUtil.sendEvent(new AccountChangedEvent(null));
            }
        };
    }

    public interface UserUiCallbacks {

        void showLogin();

        void showUserProfile();

        void showRegister();

        void showAddressList();

        void checkUpdate();

        void sendCode(String mobile);

        void checkCode(String mobile, String code);

        void createUser(String mobile, String password);

        void uploadAvatar(File file);

        void login(String account, String password);

        void logout();
    }

    public interface UserUi extends BaseUiController.Ui<UserUiCallbacks> {
    }

    public interface UserRegisterUi extends UserUi {
        void onNetworkError(ResponseError error);
    }

    public interface RegisterFirstStepUi extends UserRegisterUi {
        void sendCodeFinish();
    }

    public interface RegisterSecondStepUi extends RegisterFirstStepUi {
        void verifyMobileFinish();
    }

    public interface RegisterThirdStepUi extends UserRegisterUi {
        void userCreateFinish();
    }

    public interface UserLoginUi extends UserUi {
        void userLoginFinish();

        void onNetworkError(ResponseError error);
    }

    public interface UserCenterUi extends UserUi {
        void showUserInfo(User userInfo);

        void onNetworkError(ResponseError error);
    }

    public interface UserProfileUi extends UserUi {
        void uploadAvatarFinish(String url);

        void onNetworkError(ResponseError error);
    }
}
