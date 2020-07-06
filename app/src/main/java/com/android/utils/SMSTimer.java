package com.android.utils;

import android.os.CountDownTimer;
import android.widget.Button;
import android.widget.TextView;

public class SMSTimer extends CountDownTimer {

    public static String TIME_HIT_BEFORE = "重新获取(";
    public static String TIME_HIT_AFTER = "s)";
    public static String TIME_HIT_FINISH = "重新获取";

    private TextView tvHint;
    private Button btnHint;

    public SMSTimer(TextView tvHint) {
        super(1000 * 60, 1000);
        this.tvHint = tvHint;
    }

    public SMSTimer(Button btnHint) {
        super(1000 * 60, 1000);
        this.btnHint = btnHint;
    }

    public SMSTimer(TextView tvHint, long millisInFuture, long countDownInterval) {
        super(millisInFuture, countDownInterval);
        this.tvHint = tvHint;
    }

    public SMSTimer(Button btnHint, long millisInFuture, long countDownInterval) {
        super(millisInFuture, countDownInterval);
        this.btnHint = btnHint;
    }

    @Override
    public void onTick(long l) {
        if (tvHint != null) {
            tvHint.setEnabled(false);
            tvHint.setText(TIME_HIT_BEFORE + (l / 1000) + TIME_HIT_AFTER);
        }
        if (btnHint != null) {
            btnHint.setEnabled(false);
            btnHint.setText(TIME_HIT_BEFORE + (l / 1000) + TIME_HIT_AFTER);
        }
    }

    @Override
    public void onFinish() {
        if (tvHint != null) {
            tvHint.setEnabled(true);
            tvHint.setText(TIME_HIT_FINISH);
        }
        if (btnHint != null) {
            btnHint.setEnabled(true);
            btnHint.setText(TIME_HIT_FINISH);
        }
    }
}
