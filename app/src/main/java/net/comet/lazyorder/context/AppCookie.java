package net.comet.lazyorder.context;

import net.comet.lazyorder.model.bean.User;
import net.comet.lazyorder.util.Constants.Persistence;
import net.comet.lazyorder.util.PreferenceUtil;

/**
 * author：cheikh.wang on 16/8/3 19:50
 * email：wanghonghi@126.com
 */
public class AppCookie {

    private static boolean isLoggin;

    public static boolean isLoggin() {
        return isLoggin;
    }

    /**
     * 保存用户信息
     * @param user
     */
    public static void saveUserInfo(User user) {
        isLoggin = (user != null);
        PreferenceUtil.set(Persistence.USER_INFO, user);
    }

    /**
     * 获取用户信息
     * @return
     */
    public static User getUserInfo() {
        return PreferenceUtil.getObject(Persistence.USER_INFO, User.class);
    }

    /**
     * 保存最后一次登录的手机号
     * @param phone
     */
    public static void saveLastPhone(String phone) {
        PreferenceUtil.set(Persistence.LAST_LOGIN_PHONE, phone);
    }

    /**
     * 获取最后一次登录的手机号
     * @return
     */
    public static String getLastPhone() {
        return PreferenceUtil.getString(Persistence.LAST_LOGIN_PHONE, null);
    }

    /**
     * 保存AccessToken
     * @param token
     */
    public static void saveAccessToken(String token) {
        PreferenceUtil.set(Persistence.ACCESS_TOKEN, token);
    }

    /**
     * 获取AccessToken
     * @return
     */
    public static String getAccessToken() {
        return PreferenceUtil.getString(Persistence.ACCESS_TOKEN, null);
    }
}
