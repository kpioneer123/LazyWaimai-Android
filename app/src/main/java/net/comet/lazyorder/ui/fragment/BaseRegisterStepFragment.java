package net.comet.lazyorder.ui.fragment;

import android.content.Context;
import net.comet.lazyorder.ui.Display;
import net.comet.lazyorder.ui.UserController;
import net.comet.lazyorder.util.RegisterStep;
import net.comet.lazyorder.util.UpdateStepCallback;

/**
 * author：cheikh on 16/5/15 13:26
 * email：wanghonghi@126.com
 */
public abstract class BaseRegisterStepFragment extends BaseFragment<UserController.UserUiCallbacks> {

    public abstract RegisterStep getRegisterStep();

    protected String getRequestParameter() {
        return getArguments().getString(Display.PARAM_OBJ);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof UpdateStepCallback) {
            ((UpdateStepCallback) context).updateRegisterStep(getRegisterStep());
        } else {
            throw new IllegalArgumentException("the parent activity must be implement UpdateStepCallback interface.");
        }
    }
}