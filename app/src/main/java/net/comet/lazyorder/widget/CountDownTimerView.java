package net.comet.lazyorder.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.Button;

import net.comet.lazyorder.R;
import net.comet.lazyorder.util.CountDownTimer;

public class CountDownTimerView extends Button {

    private CountDownTimer countDownTimer;
    private OnCountDownListener onCountDownListener;

    public CountDownTimerView(Context context) {
        super(context);
    }

    public CountDownTimerView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CountDownTimerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void countDown(long millisInFuture) {
        setEnabled(false);
        if(countDownTimer == null) {
            countDownTimer = new CountDownTimer() {

                @Override
                protected void onTick(long millisUntilFinished) {
                    int secondsLeft = (int) Math.ceil((double) millisUntilFinished / 1000.0);
                    setText(getResources().getString(R.string.btn_send_code_again, secondsLeft));
                }

                @Override
                protected void onFinish() {
                    setText(R.string.btn_again_send_code);
                    if (onCountDownListener != null) {
                        setEnabled(onCountDownListener.onCountDownFinishState());
                    } else {
                        setEnabled(true);
                    }
                }
            };
        }
        countDownTimer.start(millisInFuture, 0x3e8);
    }

    @Override
    public void setEnabled(boolean enabled) {
        if(enabled && countDownTimer != null && countDownTimer.countingDown()) {
            return;
        }
        super.setEnabled(enabled);
    }

    public void setOnCountDownListener(OnCountDownListener listener) {
        onCountDownListener = listener;
    }

    public interface OnCountDownListener {
        boolean onCountDownFinishState();
    }
}