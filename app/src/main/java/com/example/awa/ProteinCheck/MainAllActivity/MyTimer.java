package com.example.awa.ProteinCheck.MainAllActivity;

import android.os.CountDownTimer;
import android.text.format.DateFormat;
import android.widget.TextView;

public class MyTimer {

    private CountDownTimer countDownTimer;
    private long sum_secs;
    private long each_secs;
    private long rest_mins;
    private long rest_secs;
    TextView mTextv_min;
    TextView mTextv_sec;

    //millisInFuture是总时间，countDownInterval是倒计时时间间隔
    public MyTimer(long para_sum_sec, long para_each_sec, TextView para_tv_min, TextView para_tv_sec) {
        this.each_secs = para_each_sec;
        this.sum_secs = para_sum_sec;
        this.mTextv_min = para_tv_min;
        this.mTextv_sec = para_tv_sec;
    }

    public void start_timer() {
        if (countDownTimer == null)
        {
            countDownTimer = new CountDownTimer(sum_secs,each_secs) {
                @Override
                public void onTick(long l) {
                    updata_view(l);
                }

                @Override
                public void onFinish() {

                }
            };

        }
        countDownTimer.start();
    }

    public void updata_view(long time)
    {
        CharSequence sysTimeStr = DateFormat.format("mm:ss", time);

        String TextView_min = String.valueOf(sysTimeStr).substring(0,2);
        String TextView_sec = String.valueOf(sysTimeStr).substring(3, 5);
        mTextv_min.setText(TextView_min);
        mTextv_sec.setText(TextView_sec);
    }

    public void pause_timer()
    {

    }

    public void stop_timer()
    {
        if (countDownTimer != null)
        {
            countDownTimer.cancel();
            countDownTimer = null;
        }
    }



    
}
