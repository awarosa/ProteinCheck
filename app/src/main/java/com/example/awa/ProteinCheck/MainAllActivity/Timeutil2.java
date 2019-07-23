package com.example.awa.ProteinCheck.MainAllActivity;

import android.os.Handler;
import android.os.Message;
import android.text.format.DateFormat;
import android.util.Log;
import android.widget.TextView;

import java.util.Timer;
import java.util.TimerTask;

public class Timeutil2 {
    private Timer mTimer = null;
    private TimerTask mTimerTask = null;
    private Handler mHandler = null;
    private static long count = 0;
    private static boolean  isPause = false;
    private static int delay = 1000;  //1s
    private static int period = 1000;  //1s
    private static final int UPDATE_TEXTVIEW = 0;
    TextView mTextv_min;
    TextView mTextv_sec;
    String mCollect_min;
    String mCollect_sec;


    public Timeutil2(TextView mTextv_sec, TextView mTextv_min, String class_collect_min, String class_collect_sec)
    {
        this.mTextv_sec = mTextv_sec;
        this.mTextv_min = mTextv_min;
        this.mCollect_min = class_collect_min;
        this.mCollect_sec = class_collect_sec;
        mHandler = new Handler()
        {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what)
                {
                    case UPDATE_TEXTVIEW:
                        updateTextView();
                        break;
                    default:
                        break;
                }
                super.handleMessage(msg);
            }
        };
    }

    public void puseTimer(){
        isPause = !isPause;
        //count -= 1;
    }

    private void updateTextView(){
        int sum_time_sec = Integer.valueOf(mCollect_min) * 60 + Integer.valueOf(mCollect_sec);
        int i = 1000;
        long time= (sum_time_sec - count) * i;
        CharSequence sysTimeStr = DateFormat.format("mm:ss", time);

        String TextView_min = String.valueOf(sysTimeStr).substring(0,2);
        String TextView_sec = String.valueOf(sysTimeStr).substring(3, 5);
        mTextv_min.setText(TextView_min);
        mTextv_sec.setText(TextView_sec);
    }

    public void startTimer(){
        isPause = false;
        final int msum_time_sec2 = Integer.valueOf(mCollect_min) * 60 + Integer.valueOf(mCollect_sec);
        if (mTimer == null) {
            mTimer = new Timer();
        }

        if (mTimerTask == null) {
            mTimerTask = new TimerTask() {
                @Override
                public void run() {

                    sendMessage(UPDATE_TEXTVIEW);

                    while (isPause) {
                        try {

                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                        }

                    }
                    count ++;
                    if (count == msum_time_sec2)
                    {
                        stopTimer();
                    }
                    Log.i("count1", "count: "+String.valueOf(count));


                }
            };
        }

        if(mTimer != null && mTimerTask != null )
            mTimer.schedule(mTimerTask, delay, period);

    }


    public void sendMessage(int id){
        if (mHandler != null) {
            Message message = Message.obtain(mHandler, id);
            mHandler.sendMessage(message);
        }
    }

    public void changeCount()
    {
        count -= 1;
    }

    public void stopTimer(){

        if (mTimer != null) {
            mTimer.cancel();
            mTimer = null;
        }

        if (mTimerTask != null) {
            mTimerTask.cancel();
            mTimerTask = null;
        }
        isPause = true;
        count = -1;

    }

}