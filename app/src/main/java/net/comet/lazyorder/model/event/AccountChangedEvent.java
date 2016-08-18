package net.comet.lazyorder.model.event;

import net.comet.lazyorder.model.bean.User;

/**
 * author：cheikh.wang on 16/7/18 14:21
 * email：wanghonghi@126.com
 */
public class AccountChangedEvent {

    private User user;

    public AccountChangedEvent(User user) {
        this.user = user;
    }

    public User getUser() {
        return user;
    }
}