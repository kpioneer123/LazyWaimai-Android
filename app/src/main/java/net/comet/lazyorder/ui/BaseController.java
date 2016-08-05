package net.comet.lazyorder.ui;

import android.content.Intent;

import com.google.common.base.Preconditions;

public abstract class BaseController {

    private Display mDisplay;
    private boolean mInited;

    public final void init() {
        Preconditions.checkState(!mInited, "Already inited");
        onInited();
        mInited = true;
    }

    public final void suspend() {
        Preconditions.checkState(mInited, "Not inited");
        onSuspended();
        mInited = false;
    }

    public boolean handleIntent(Intent intent) {
        return false;
    }

    protected void onInited() {}

    protected void onSuspended() {}

    public final boolean isInited() {
        return mInited;
    }

    protected void setDisplay(Display display) {
        mDisplay = display;
    }

    protected final Display getDisplay() {
        return mDisplay;
    }
}
