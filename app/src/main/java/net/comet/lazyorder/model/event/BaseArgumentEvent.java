package net.comet.lazyorder.model.event;

import com.google.common.base.Preconditions;

class BaseArgumentEvent<T> extends UiCausedEvent {

    protected final T arg;

    protected BaseArgumentEvent(int callingId, T arg) {
        super(callingId);
        this.arg = Preconditions.checkNotNull(arg, "arg cannot be null");
    }
}