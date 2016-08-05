package net.comet.lazyorder.model;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import java.io.Serializable;

/**
 * author：cheikh.wang on 16/8/5 12:22
 * email：wanghonghi@126.com
 */
public class Route implements Serializable {

    private Class<? extends AppCompatActivity> mTargetActivity;
    private boolean mIsNeedLogin;
    private Bundle mArguments;

    public Route() {
    }

    public Route(Class<? extends AppCompatActivity> targetActivity, boolean isNeedLogin, Bundle arguments) {
        mTargetActivity = targetActivity;
        mIsNeedLogin = isNeedLogin;
        mArguments = arguments;
    }

    public Class<? extends AppCompatActivity> getTargetActivity() {
        return mTargetActivity;
    }

    public void setTargetActivity(Class<? extends AppCompatActivity> targetActivity) {
        mTargetActivity = targetActivity;
    }

    public boolean isIsNeedLogin() {
        return mIsNeedLogin;
    }

    public void setIsNeedLogin(boolean isNeedLogin) {
        mIsNeedLogin = isNeedLogin;
    }

    public Bundle getArguments() {
        return mArguments;
    }

    public void setArguments(Bundle arguments) {
        mArguments = arguments;
    }
}