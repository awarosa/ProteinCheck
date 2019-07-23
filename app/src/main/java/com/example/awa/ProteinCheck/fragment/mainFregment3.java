package com.example.awa.ProteinCheck.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.example.awa.ProteinCheck.BluetoothService;
import com.example.awa.ProteinCheck.R;
import com.example.awa.ProteinCheck.MainAllActivity.Timeutil2;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class mainFregment3 extends Fragment {
    private View view;
    private TextView textView_swiming_min;
    private TextView textView_swiming_sec;
    private TextView textView_swim_process;
    private ToggleButton tbtn_start_pause_swim;
    private RadioButton rb_set;
    private RadioButton rb_collect;
    private String s1 = "03";
    private String s2 = "00";
    private String sec_splited2;
    private String min_splited2;
    private int set_swim_tm;
    private static int count_flag = 0;
    private static boolean is_swiming = false;
    private final static String TAG = "my_info";
    private  static String get_mess2 = "NONE0";
    private boolean is_reset2 = false;
    Timeutil2 timeutil2;
    IntentFilter intentFilter2;
    ResetSwimBroad mResetSwimBroad;


    @Override
    public void onStart() {
        super.onStart();
        intentFilter2 = new IntentFilter();
        intentFilter2.addAction("com.example.awa.ProteinCheck.MY_ACTION_SET");
        intentFilter2.addAction(BluetoothService.ACTION_DATA_AVAILABLE);
        mResetSwimBroad = new ResetSwimBroad();
        getActivity().registerReceiver(mResetSwimBroad, intentFilter2);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getActivity().unregisterReceiver(mResetSwimBroad);
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume: f3 start");
    }



    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fregment3_main, container, false);
        init_view2();


        tbtn_start_pause_swim.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {



                if (b)
                {
                    is_swiming = true;
                    rb_set.setEnabled(false);
                    rb_collect.setEnabled(false);
                    if (count_flag == 0)
                    {
                        textView_swim_process.setText("电泳中");
                        timeutil2.startTimer();
                        count_flag++;
                    }
                    else
                    {
                        textView_swim_process.setText("电泳中");
                        timeutil2.puseTimer();
                        timeutil2.changeCount();
                    }

                }

                else
                {
                    rb_set.setEnabled(true);
                    rb_collect.setEnabled(true);
                    is_swiming = false;
                    textView_swim_process.setText("暂停中");
                    timeutil2.puseTimer();
                }


            }

        });


        return view;
    }

    private void init_view2()
    {
        textView_swiming_min = view.findViewById(R.id.tv_swiming_min);
        textView_swiming_sec = view.findViewById(R.id.tv_swiming_sec);
        textView_swim_process = view.findViewById(R.id.tv_swim_process);
        tbtn_start_pause_swim = view.findViewById(R.id.tbtn_start_or_pause_swim);
        rb_set = (RadioButton)getActivity().findViewById(R.id.rbset);
        rb_collect = getActivity().findViewById(R.id.rbcollect);

        EventBus.getDefault().register(this);
        if ("NONE0".equals(get_mess2)) {
            sec_splited2 = "00";
            min_splited2 = "03";
            set_swim_tm = 0;
        } else {
            min_splited2 = get_mess2.substring(4, 6);
            sec_splited2 = get_mess2.substring(6, 8);
            set_swim_tm = get_mess2.charAt(4);
        }
        Log.d(TAG, "get_mess in onCreate:" + String.valueOf(get_mess2));

        textView_swiming_min.setText(min_splited2);
        textView_swiming_sec.setText(sec_splited2);
    }

    private void renew_timer2()
    {
//        String get_info_swim_sec = textView_swiming_sec.getText().toString();
//        String  get_info_swim_min = textView_swiming_min.getText().toString();
//        timeutil2 = new Timeutil2(textView_swiming_sec, textView_swiming_min,
//                get_info_swim_min, get_info_swim_sec);
//        Log.d(TAG, "onCheckedChanged: "+get_info_swim_min+":"+get_info_swim_sec);

        Log.d(TAG, "renew_timeutil: come into renew  timeutil");
        String get_info_collect_sec = textView_swiming_sec.getText().toString();
        String  get_info_collect_min = textView_swiming_min.getText().toString();

        if (is_reset2)
        {
            if (timeutil2!=null)
            {
                timeutil2.stopTimer();
                count_flag = 0;
                timeutil2 = null;
                Log.d(TAG, "onCheckedChanged: timutil = null");
            }

            Log.d(TAG, "onCheckedChanged: is_reset tm = null");
        }
        Log.d(TAG, "renew_timeutil: "+get_info_collect_min+":"+get_info_collect_sec);
        timeutil2 = new Timeutil2(textView_swiming_sec, textView_swiming_min,
                get_info_collect_min, get_info_collect_sec);
        Log.d(TAG, "onCheckedChanged: "+get_info_collect_min+":"+get_info_collect_sec);
        is_reset2 = !is_reset2;
    }


    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void onEvent(MyEventbus myEventbusf2)
    {
        Log.d(TAG, "onEvent: in f3");
        if ("eventbus_send_string".equals(myEventbusf2.name))
        {
            get_mess2 = myEventbusf2.content;
            Log.d(TAG, "onEvent:get_mess in f3 "+String.valueOf(get_mess2));
        }
        min_splited2 = get_mess2.substring(4, 6);
        sec_splited2 = get_mess2.substring(6, 8);
        set_swim_tm = get_mess2.charAt(4);
        Log.d(TAG, "onEvent: f3"+min_splited2);

        textView_swiming_min.setText(min_splited2);
        textView_swiming_sec.setText(sec_splited2);
        is_reset2 = !is_reset2;
        renew_timer2();

    }

    public String bytesToString(byte[] bytes) {
        final char[] hexArray = "0123456789ABCDEF".toCharArray();
        char[] hexChars = new char[bytes.length * 2];
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < bytes.length; i++) {
            int v = bytes[i] & 0xFF;
            hexChars[i * 2] = hexArray[v >>> 4];
            hexChars[i * 2 + 1] = hexArray[v & 0x0F];

            sb.append(hexChars[i * 2]);
            sb.append(hexChars[i * 2 + 1]);
            sb.append(' ');
        }
        return sb.toString();
    }

    private class ResetSwimBroad extends BroadcastReceiver
    {

        @Override
        public void onReceive(Context context, Intent intent) {
            String received_intent2 = intent.getAction();
            if ("com.example.awa.ProteinCheck.MY_ACTION_SET".equals(received_intent2))
            {
                Log.d(TAG, "onReceive: is_reset"+String.valueOf(is_reset2));
            }
            else if (BluetoothService.ACTION_DATA_AVAILABLE.equals(received_intent2)) {
                byte[] buf = intent.getByteArrayExtra(BluetoothService.EXTRA_DATA);
                String s = bytesToString(buf);
                StringBuilder mData = new StringBuilder();
                mData.append(s);
                String received_string = mData.toString();

                //进入开始界面,f0
                if ("b0001#".equals(received_string)) {

                }
                //进入设置界面，f1
                else if ("b0100#".equals(received_string)) {

                }
                //照明
                else if ("b0400#".equals(received_string)) {

                }
                //背光
                else if ("b0400#".equals(received_string)) {

                }
            }
        }
    }






}
