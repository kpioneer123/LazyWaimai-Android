package net.comet.lazyorder.network.action;

import net.comet.lazyorder.model.bean.ResponseError;
import net.comet.lazyorder.model.event.AccountChangedEvent;
import net.comet.lazyorder.util.EventUtil;
import rx.functions.Action1;
import static net.comet.lazyorder.util.Constants.Code.HTTP_UNAUTHORIZED;

/**
 * author：cheikh on 16/5/14 00:33
 * email：wanghonghi@126.com
 */
public abstract class ErrorAction implements Action1<Throwable> {

    @Override
    public void call(Throwable throwable) {
        ResponseError error = ResponseError.handle(throwable);
        checkError(error);
        call(error);
    }

    private void checkError(ResponseError error) {
        // 登录身份失效
        if (error.getStatus() == HTTP_UNAUTHORIZED) {
            EventUtil.sendEvent(new AccountChangedEvent(null));
        }
    }

    public abstract void call(ResponseError error);
}
