package net.comet.lazyorder.ui.activity;

import android.content.Intent;
import android.widget.TextView;
import net.comet.lazyorder.R;
import net.comet.lazyorder.ui.Display;
import net.comet.lazyorder.util.RegisterStep;
import net.comet.lazyorder.util.UpdateStepCallback;
import butterknife.Bind;

public class RegisterActivity extends BaseActivity implements UpdateStepCallback {

    @Bind(R.id.txt_first_step)
    TextView mFirstStepTxt;

    @Bind(R.id.txt_second_step)
    TextView mSecondStepTxt;

    @Bind(R.id.txt_third_step)
    TextView mThirdStepTxt;

    @Override
    protected int getContentViewLayoutId() {
        return R.layout.activity_register;
    }

    @Override
    protected void handleIntent(Intent intent, Display display) {
        if (!display.hasMainFragment()) {
            display.showRegisterStep(RegisterStep.STEP_FIRST, null);
        }
    }

    @Override
    public void updateRegisterStep(RegisterStep step) {
        switch (step) {
            case STEP_FIRST:
                mFirstStepTxt.setTextColor(getResources().getColor(R.color.accent));
                break;
            case STEP_SECOND:
                mFirstStepTxt.setTextColor(getResources().getColor(R.color.accent));
                mSecondStepTxt.setTextColor(getResources().getColor(R.color.accent));
                break;
            case STEP_THIRD:
                mFirstStepTxt.setTextColor(getResources().getColor(R.color.accent));
                mSecondStepTxt.setTextColor(getResources().getColor(R.color.accent));
                mThirdStepTxt.setTextColor(getResources().getColor(R.color.accent));
                break;
        }
    }
}
