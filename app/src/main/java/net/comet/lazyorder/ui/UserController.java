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
import java.util.HashMap;
import java.util.Map;
import javax.inject.Inject;
import javax.inject.Singleton;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

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
            AppCookie.saveAccessToken(user.getAccessToken());
            AppCookie.saveLastPhone(user.getMobile());
            mRestApiClient.setToken(user.getAccessToken());
        } else {
            AppCookie.saveUserInfo(null);
            AppCookie.saveAccessToken(null);
            mRestApiClient.setToken(null);
        }

        populateUis();
    }

    @Override
    protected void onInited() {
        super.onInited();
        EventUtil.register(this);

        String accessToken = AppCookie.getAccessToken();
        if (accessToken != null) {
            mRestApiClient.setToken(accessToken);
        }
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
        ui.showUserInfo(AppCookie.getUserInfo());
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
        params.put("username", username);
        params.put("password", password);
        params.put("grant_type", "password");
        params.put("client_id", AppConfig.CLIENT_ID);
        params.put("client_secret", AppConfig.CLIENT_SECRET);

        mRestApiClient.accountService()
                .login(params)
                .flatMap(new Func1<Token, Observable<User>>() {
                    @Override
                    public Observable<User> call(final Token token) {
                        return mRestApiClient.setToken(token.getAccessToken())
                                .accountService()
                                .profile(token.getUserId())
                                .doOnNext(new Action1<User>() {
                                    @Override
                                    public void call(User user) {
                                        user.setAccessToken(token.getAccessToken());
                                    }
                                });
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

        void showRegister();

        void showAddressList();

        void checkUpdate();

        void sendCode(String mobile);

        void checkCode(String mobile, String code);

        void createUser(String mobile, String password);

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
}
