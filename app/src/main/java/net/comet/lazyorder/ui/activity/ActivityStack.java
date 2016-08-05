/*
 * Copyright (c) 2014,KJFrameForAndroid Open Source Project,张涛.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.comet.lazyorder.ui.activity;

import android.app.Activity;

import java.util.Stack;


/**
 * 应用程序Activity管理类：用于Activity管理和应用程序退出<br>
 *
 * <b>创建时间</b> 2014-2-28
 *
 * @author kymjs (https://github.com/kymjs)
 * @version 1.1
 */
final public class ActivityStack {

    private static Stack<Activity> mStack;
    private static ActivityStack instance;

    private ActivityStack() {}

    public static ActivityStack create() {
        if (instance == null) {
            instance = new ActivityStack();
        }
        return instance;
    }

    /**
     * 获取当前Activity栈中元素个数
     */
    public int count() {
        return mStack.size();
    }

    /**
     * 添加Activity到栈
     */
    public void add(Activity activity) {
        if (mStack == null) {
            mStack = new Stack<>();
        }
        mStack.push(activity);
    }

    /**
     * 获取当前Activity（栈顶Activity）
     */
    public Activity top() {
        if (mStack == null) {
            throw new NullPointerException(
                    "Activity stack is Null,your Activity must extend KJActivity");
        }
        if (mStack.isEmpty()) {
            return null;
        }

        return mStack.peek();
    }

    /**
     * 获取指定class的Activity，没有找到则返回null
     */
    public Activity find(Class<?> clazz) {
        Activity activity = null;
        for (Activity aty : mStack) {
            if (aty.getClass().equals(clazz)) {
                activity = aty;
                break;
            }
        }

        return activity;
    }

    /**
     * 结束当前Activity（栈顶Activity）
     */
    public void finish() {
        Activity activity = mStack.peek();
        finish(activity);
    }

    /**
     * 结束指定的Activity(重载)
     */
    public void finish(Activity activity) {
        if (activity != null) {
            mStack.remove(activity);
            activity.finish();
            activity = null;
        }
    }

    /**
     * 结束指定的Activity(重载)
     */
    public void finish(Class<?> clazz) {
        for (Activity aty : mStack) {
            if (aty.getClass().equals(clazz)) {
                finish(aty);
                break;
            }
        }
    }

    /**
     * 关闭除了指定activity以外的全部activity 如果cls不存在于栈中，则栈全部清空
     */
    public void finishOthers(Class<?> clazz) {
        for (Activity aty : mStack) {
            if (!(aty.getClass().equals(clazz))) {
                finish(aty);
            }
        }
    }

    /**
     * 结束所有Activity
     */
    public void finishAll() {
        for (int i = 0, size = mStack.size(); i < size; i++) {
            if (null != mStack.get(i)) {
                mStack.get(i).finish();
            }
        }
        mStack.clear();
    }

    /**
     * 应用程序退出
     */
    public void appExit() {
        try {
            finishAll();
            Runtime.getRuntime().exit(0);
        } catch (Exception e) {
            Runtime.getRuntime().exit(-1);
        }
    }
}
