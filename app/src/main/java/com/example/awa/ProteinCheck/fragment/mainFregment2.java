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
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.example.awa.ProteinCheck.BluetoothService;
import com.example.awa.ProteinCheck.R;
import com.example.awa.ProteinCheck.MainAllActivity.Timeutil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class mainFregment2 extends Fragment implements View.OnClickListener {
    private View view;
    private TextView textView_collecting_min;
    private TextView textView_collecting_sec;
    private TextView textView_collect_process;
    private ToggleButton tbtn_start_pause;


    private String s1 = "10";
    private String s2 = "00";
    private String sec_splited;
    private String min_splited;
    private int set_taged_tm;
    private RadioButton rb_set;
    private RadioButton rb_swim;
    private static int count_flag = 0;
    private int collect_time = 0;
    private static boolean is_collecting = false;
    private boolean hasReceiveEvent = false;
    private static boolean is_reset = false;
    private IntentFilter intentFilter;
    ResetCollectBroadcast mResetCollectBroadcast;



    private final static String TAG = "my_info";
    private  static String get_mess = "NONE0";
    Timeutil timeutil;

    @Override
    public void onStart() {
        super.onStart();
        intentFilter = new IntentFilter();
        intentFilter.addAction("com.example.awa.ProteinCheck.MY_ACTION_SET");
        intentFilter.addAction(BluetoothService.ACTION_DATA_AVAILABLE);
        mResetCollectBroadcast = new ResetCollectBroadcast();
        getActivity().registerReceiver(mResetCollectBroadcast, intentFilter);
        Log.d(TAG, "onStart: in f2");
    }



    @Override
    public void onResume() {
        super.onResume();
        //renew_timeutil();
        Log.d(TAG, "onResume: f2 start");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        getActivity().unregisterReceiver(mResetCollectBroadcast);
    }



    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fregment2_main, container, false);
        init_view();
        //renew_timeutil();


        tbtn_start_pause.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                //renew_timeutil();
                if (b)
                {
                    is_collecting = true;
                    rb_set.setEnabled(false);
                    rb_swim.setEnabled(false);
                    Log.d(TAG, "BTN PRESEED"+String.valueOf(count_flag));
                    if (count_flag == 0)
                    {
                        textView_collect_process.setText("凝胶中");
                        timeutil.startTimer();
                        count_flag++;
                        Log.d(TAG, "onCheckedChanged: startimer");
                    }
                    else
                    {
                        textView_collect_process.setText("凝胶中");
                        timeutil.puseTimer();
                        timeutil.changeCount();
                        Log.d(TAG, "onCheckedChanged: pausetimer");
                    }

                }

                else
                {
                    rb_set.setEnabled(true);
                    rb_swim.setEnabled(true);
                    textView_collect_process.setText("暂停中");
                    is_collecting = false;
                    timeutil.puseTimer();
                    Log.d(TAG, "onCheckedChanged: else:pause timer");
                }
            }

        });

        return view;
    }

    public void init_view()
    {
        textView_collecting_min = view.findViewById(R.id.tv_collecting_min);
        textView_collecting_sec = view.findViewById(R.id.tv_collecting_sec);
        textView_collect_process = view.findViewById(R.id.tv_swim_process);
        tbtn_start_pause = view.findViewById(R.id.tbtn_start_or_pause);


        rb_set = getActivity().findViewById(R.id.rbset);
        rb_swim = getActivity().findViewById(R.id.rbswim);

        EventBus.getDefault().register(this);
        if ("NONE0".equals(get_mess)) {
            sec_splited = "00";
            min_splited = "10";
            set_taged_tm = 0;
        } else {
            min_splited = get_mess.substring(0, 2);
            sec_splited = get_mess.substring(2, 4);
            set_taged_tm = get_mess.charAt(4);
        }
        Log.d(TAG, "get_mess in onCreate:" + String.valueOf(get_mess));

        textView_collecting_min.setText(min_splited);
        textView_collecting_sec.setText(sec_splited);

    }

    public void renew_timeutil()
    {
        Log.d(TAG, "renew_timeutil: come into renew  timeutil");
        String get_info_collect_sec = textView_collecting_sec.getText().toString();
        String  get_info_collect_min = textView_collecting_min.getText().toString();

        if (is_reset)
        {
            if (timeutil!=null)
            {
                timeutil.stopTimer();
                count_flag = 0;//used to control the 'pause' button
                timeutil = null;
                Log.d(TAG, "onCheckedChanged: timutil = null");
            }

            Log.d(TAG, "onCheckedChanged: is_reset tm = null");
        }
        Log.d(TAG, "renew_timeutil: "+get_info_collect_min+":"+get_info_collect_sec);
        timeutil = new Timeutil(textView_collecting_sec, textView_collecting_min,
                get_info_collect_min, get_info_collect_sec);
        Log.d(TAG, "onCheckedChanged: "+get_info_collect_min+":"+get_info_collect_sec);
        is_reset = !is_reset;
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void onEvent(MyEventbus myEventbusf2)
    {
        Log.d(TAG, "onEvent: in f2");
        if ("eventbus_send_string".equals(myEventbusf2.name))
        {
            get_mess = myEventbusf2.content;
            Log.d(TAG, "onEvent f2: get_mess"+String.valueOf(get_mess));

        }
        min_splited = get_mess.substring(0, 2);
        sec_splited = get_mess.substring(2, 4);
        set_taged_tm = get_mess.charAt(0);

        textView_collecting_min.setText(min_splited);
        textView_collecting_sec.setText(sec_splited);

        collect_time = Integer.valueOf(min_splited)*60+Integer.valueOf(sec_splited);
        hasReceiveEvent = !hasReceiveEvent;
        is_reset = !is_reset;
        renew_timeutil();
        Log.d(TAG, "onEvent: has renewed timeutil");

    }

    @Override
    public void onClick(View view) {
        switch (view.getId())
        {

            default:
                break;
        }
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


    private class ResetCollectBroadcast extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            final String received_intent = intent.getAction();

            if ("com.example.awa.ProteinCheck.MY_ACTION_SET".equals(received_intent)) {
                Log.d(TAG, "onReceive: is_reset" + String.valueOf(is_reset));
            }
            else if (BluetoothService.ACTION_DATA_AVAILABLE.equals(received_intent)) {
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
