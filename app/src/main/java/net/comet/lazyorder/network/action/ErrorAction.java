package net.comet.lazyorder.network.action;

import net.comet.lazyorder.model.bean.ResponseError;
import rx.functions.Action1;

/**
 * author：cheikh on 16/5/14 00:33
 * email：wanghonghi@126.com
 */
public abstract class ErrorAction implements Action1<Throwable> {

    @Override
    public void call(Throwable throwable) {
        if (throwable instanceof ResponseError) {
            call((ResponseError) throwable);
        } else {
            call(ResponseError.handle(throwable));
        }
    }

    public abstract void call(ResponseError error);
}
