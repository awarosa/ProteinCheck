package com.example.awa.ProteinCheck.fragment;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import android.widget.TextView;

import com.example.awa.ProteinCheck.BluetoothService;
import com.example.awa.ProteinCheck.R;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.Date;

import static android.content.Context.BIND_AUTO_CREATE;

public class mainFregment1 extends Fragment implements View.OnClickListener {
    private View view;
    private final static String TAG = "my_info";

    private BluetoothService bluetoothServiceF1;

    private TextView textView_collect_min;
    private Button button_add_collect_sec;
    private Button button_dec_collect_sec;
    private TextView textView_collect_sec;

    private TextView textView_swim_min;
    private Button button_add_swim_sec;
    private Button button_dec_swim_sec;
    private TextView textView_swim_sec;

    private long t1 = 0;



    private String collecting_min_plus_collecting_sec;
    private String swiming_min_plus_swiming_sec;

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);


        if (!hidden)
        {

            
        }
        else
        {

            String to_be_event_hidden = collecting_min_plus_collecting_sec + swiming_min_plus_swiming_sec;

            EventBus.getDefault().postSticky(new MyEventbus("eventbus_send_string",
                    to_be_event_hidden));
            Log.d(TAG, "onHiddenChanged: come in hidden");
            
        }

    }

    ServiceConnection serviceConnectionF1 = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            bluetoothServiceF1 = ((BluetoothService.LocalBinder)iBinder).getService();
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            bluetoothServiceF1 = null;
        }
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        Intent F1ServiceIntent = new Intent(getActivity(), BluetoothService.class);
        getActivity().bindService(F1ServiceIntent,serviceConnectionF1,BIND_AUTO_CREATE);

        view = inflater.inflate(R.layout.fregment1_main, container, false);

        textView_collect_min = view.findViewById(R.id.tv_collect_min);
        button_add_collect_sec = view.findViewById(R.id.bt_add_collect_sec);
        button_dec_collect_sec = view.findViewById(R.id.bt_dec_collect_sec);
        button_add_collect_sec.setOnClickListener(this);
        button_dec_collect_sec.setOnClickListener(this);
        textView_collect_sec = view.findViewById(R.id.tv_collect_sec);

        textView_swim_min = view.findViewById(R.id.tv_swim_min);
        button_add_swim_sec = view.findViewById(R.id.bt_add_swim_sec);
        button_dec_swim_sec = view.findViewById(R.id.bt_dec_swim_sec);
        button_add_swim_sec.setOnClickListener(this);
        button_dec_swim_sec.setOnClickListener(this);
        textView_swim_sec = view.findViewById(R.id.tv_swim_sec);


        collecting_min_plus_collecting_sec = textView_collect_min.getText().toString() +
                textView_collect_sec.getText().toString();
        swiming_min_plus_swiming_sec = textView_swim_min.getText().toString() +
                textView_swim_sec.getText().toString();

        String to_be_event = collecting_min_plus_collecting_sec + swiming_min_plus_swiming_sec;

        EventBus.getDefault().postSticky(new MyEventbus("eventbus_send_string",
                to_be_event));
        return view;

    }

    @Override
    public void onResume() {
        super.onResume();
        IntentFilter temp1_intentFilter = new IntentFilter();
        temp1_intentFilter.addAction(BluetoothService.ACTION_DATA_AVAILABLE);
        getActivity().registerReceiver(f1Receiver,temp1_intentFilter);
        Log.d(TAG, "onResume: f1 start");

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getActivity().unbindService(serviceConnectionF1);
        bluetoothServiceF1 = null;
        getActivity().unregisterReceiver(f1Receiver);

    }

    @Override
    public void onPause() {

        Log.d(TAG, "onPause: f1 is paused");
        super.onPause();

    }


    
    

    @Override
    public void onClick(View view) {
        switch (view.getId())
        {


                    //延时环节
//                Button btn = (Button)findViewById(R.id.xxxx);//获取该按钮
//                btn.setOnClickListener(new View.OnClickListener() { //定义按钮的点击事件
//                    @Override
//                    public void onClick(View v) {
//                        if(t1==0){//第一次单击，初始化为本次单击的时间
//                            t1= (new Date()).getTime();
//                        }else{
//                            long curTime = (new Date()).getTime();//本地单击的时间
//                            System.out.println("两次单击间隔时间："+(curTime-t1));//计算本地和上次的时间差
//                            if(curTime-t1>5*1000){
//                                //间隔5秒允许点击，可以根据需要修改间隔时间
//                                t1 = curTime;//当前单击事件变为上次时间
//                            }
//                        }
//                    }
//                }



                case R.id.bt_add_collect_sec:

                    if (t1 == 0)
                    {
                        t1 = (new Date()).getTime();
                        Log.d(TAG, "onClick: t1:"+t1);
                    }else
                    {
                        long curTime = (new Date()).getTime();
                        Log.d(TAG, "onClick: time postpone"+(curTime-t1));
                        if (curTime-t1 < 5 * 1000)
                        {
                            break;
                        }

                        else
                        {
                            t1 = curTime;
                            char collect_sec_decimal = textView_collect_sec.getText().toString().charAt(0);
                            char collect_sec_single = textView_collect_sec.getText().toString().charAt(1);

                            char collect_min_decimal = textView_collect_min.getText().toString().charAt(0);
                            char collect_min_single = textView_collect_min.getText().toString().charAt(1);

                            int int_collect_sec_decimal = Integer.valueOf(String.valueOf(collect_sec_decimal));
                            int int_collect_min_single = Integer.valueOf(String.valueOf(collect_min_single));
                            int int_collect_min_decimal = Integer.valueOf(String.valueOf(collect_min_decimal));
                            Log.d(TAG, "onClick: int_collect_min_single:"+String.valueOf(int_collect_min_single));


                            if (int_collect_sec_decimal < 5)
                            {
                                int_collect_sec_decimal += 1;
                            }
                            else
                            {
                                int_collect_sec_decimal = 0;
                                if (int_collect_min_single  < 9)
                                {
                                    Log.d(TAG, "onClick: come in collect min single");
                                    int_collect_min_single += 1;
                                }
                                else
                                {
                                    int_collect_min_single = 0;
                                    if (int_collect_min_decimal < 5)
                                    {
                                        int_collect_min_decimal += 1;
                                    }
                                    else
                                    {
                                        int_collect_min_decimal = 5;
                                        int_collect_min_single = 9;
                                    }

                                }
                            }
                            String res3 = String.valueOf(int_collect_sec_decimal) + String.valueOf(collect_sec_single)+ "";
                            String res33 = String.valueOf(int_collect_min_decimal) + String.valueOf(int_collect_min_single)+ "";
                            Log.d(TAG, "Click_bt_add_collect_sec");
                            textView_collect_sec.setText(res3);
                            textView_collect_min.setText(res33);
                            collecting_min_plus_collecting_sec = textView_collect_min.getText().toString()
                                    + textView_collect_sec.getText().toString();
                            Intent intent_to_add_collect_sec = new Intent();
                            intent_to_add_collect_sec.setAction("com.example.awa.ProteinCheck.fragment.MY_ACTION_ADD_COLLECT_SEC");
                            getActivity().sendBroadcast(intent_to_add_collect_sec);

                            Log.d(TAG, "has send");
                            String to_be_event3 = collecting_min_plus_collecting_sec + swiming_min_plus_swiming_sec;

                            EventBus.getDefault().postSticky(new MyEventbus("eventbus_send_string",
                                    to_be_event3));

                        }
                    }
                    break;





            case R.id.bt_dec_collect_sec:
                char collect_sec_decimal2 = textView_collect_sec.getText().toString().charAt(0);
                char collect_sec_single2 = textView_collect_sec.getText().toString().charAt(1);

                char collect_min_decimal2 = textView_collect_min.getText().toString().charAt(0);
                char collect_min_single2 = textView_collect_min.getText().toString().charAt(1);

                int int_collect_sec_decimal2 = Integer.valueOf(String.valueOf(collect_sec_decimal2));
                int int_collect_min_single2 = Integer.valueOf(String.valueOf(collect_min_single2));
                int int_collect_min_decimal2 = Integer.valueOf(String.valueOf(collect_min_decimal2));

                if (int_collect_sec_decimal2 > 0)
                {
                    int_collect_sec_decimal2 -= 1;
                }
                else
                {
                    if (int_collect_min_single2 > 0)
                    {
                        int_collect_min_single2 -= 1;
                        int_collect_sec_decimal2 = 5;
                    }
                    else
                    {
                        if (int_collect_min_decimal2 > 0)
                        {
                            int_collect_min_decimal2 -= 1;
                            int_collect_min_single2 = 9;
                            int_collect_sec_decimal2 = 5;
                        }
                        else
                        {
                            int_collect_min_decimal2 = 0;
                            int_collect_min_single2 = 0;
                            int_collect_sec_decimal2 = 0;
                        }
                    }

                }
                String res4 = String.valueOf(int_collect_sec_decimal2) + String.valueOf(collect_sec_single2)+ "";
                String res44 = String.valueOf(int_collect_min_decimal2) + String.valueOf(int_collect_min_single2)+ "";
                textView_collect_sec.setText(res4);
                textView_collect_min.setText(res44);
                collecting_min_plus_collecting_sec = textView_collect_min.getText().toString()
                        + textView_collect_sec.getText().toString();
                Intent intent_to_dec_collect_sec = new Intent();
                intent_to_dec_collect_sec.setAction("com.example.awa.ProteinCheck.fragment.MY_ACTION_DEC_COLLECT_SEC");
                getActivity().sendBroadcast(intent_to_dec_collect_sec);
                Log.d(TAG, "has send");
                String to_be_event4 = collecting_min_plus_collecting_sec + swiming_min_plus_swiming_sec;

                EventBus.getDefault().postSticky(new MyEventbus("eventbus_send_string",
                        to_be_event4));
                break;


            case R.id.bt_add_swim_sec:
                char swim_add_decimal = textView_swim_sec.getText().toString().charAt(0);
                char swim_sec_single = textView_swim_sec.getText().toString().charAt(1);
                char swim_min_decimal = textView_swim_min.getText().toString().charAt(0);
                char swim_min_single = textView_swim_min.getText().toString().charAt(1);

                int int_swim_sec_decimal = Integer.valueOf(String.valueOf(swim_add_decimal));
                int int_swim_min_single = Integer.valueOf(String.valueOf(swim_min_single));
                int int_swim_min_decimal = Integer.valueOf(String.valueOf(swim_min_decimal));

                if (int_swim_sec_decimal < 5)
                {
                    int_swim_sec_decimal += 1;
                }
                else
                {
                    int_swim_sec_decimal = 0;
                    if (int_swim_min_single  < 9)
                    {
                        int_swim_min_single += 1;
                    }
                    else
                    {
                        int_swim_min_single = 0;
                        if (int_swim_min_decimal < 5)
                        {
                            int_swim_min_decimal += 1;
                        }
                        else
                        {
                            int_swim_min_decimal = 5;
                            int_swim_min_single = 9;
                        }

                    }
                }
                String res7 = String.valueOf(int_swim_sec_decimal) + String.valueOf(swim_sec_single)+ "";
                String res77 = String.valueOf(int_swim_min_decimal) + String.valueOf(int_swim_min_single)+ "";
                textView_swim_sec.setText(res7);
                textView_swim_min.setText(res77);
                swiming_min_plus_swiming_sec = textView_swim_min.getText().toString()
                        + textView_swim_sec.getText().toString();

                Intent intent_to_add_swim_sec = new Intent();
                intent_to_add_swim_sec.setAction("com.example.awa.ProteinCheck.fragment.MY_ACTION_ADD_SWIM_SEC");
                getActivity().sendBroadcast(intent_to_add_swim_sec);
                Log.d(TAG, "has send");
                String to_be_event7 = collecting_min_plus_collecting_sec + swiming_min_plus_swiming_sec;

                EventBus.getDefault().postSticky(new MyEventbus("eventbus_send_string",
                        to_be_event7));
                break;

            case R.id.bt_dec_swim_sec:
                char swim_add_decimal2 = textView_swim_sec.getText().toString().charAt(0);
                char swim_sec_single2 = textView_swim_sec.getText().toString().charAt(1);

                char swim_min_decimal2 = textView_swim_min.getText().toString().charAt(0);
                char swim_min_single2 = textView_swim_min.getText().toString().charAt(1);

                int int_swim_sec_decimal2 = Integer.valueOf(String.valueOf(swim_add_decimal2));
                int int_swim_min_single2 = Integer.valueOf(String.valueOf(swim_min_single2));
                int int_swim_min_decimal2 = Integer.valueOf(String.valueOf(swim_min_decimal2));

                if (int_swim_sec_decimal2 > 0)
                {
                    int_swim_sec_decimal2 -= 1;
                }
                else
                {
                    if (int_swim_min_single2 > 0)
                    {
                        int_swim_min_single2 -= 1;
                        int_swim_sec_decimal2 = 5;
                    }
                    else
                    {
                        if (int_swim_min_decimal2 > 0)
                        {
                            int_swim_min_decimal2 -= 1;
                            int_swim_min_single2 = 9;
                            int_swim_sec_decimal2 = 5;
                        }
                        else
                        {
                            int_swim_min_decimal2 = 0;
                            int_swim_min_single2 = 0;
                            int_swim_sec_decimal2 = 0;
                        }
                    }

                }
                String res8 = String.valueOf(int_swim_sec_decimal2) + String.valueOf(swim_sec_single2);
                String res88 = String.valueOf(int_swim_min_decimal2) + String.valueOf(int_swim_min_single2);
                textView_swim_sec.setText(res8);
                textView_swim_min.setText(res88);
                swiming_min_plus_swiming_sec = textView_swim_min.getText().toString()
                        + textView_swim_sec.getText().toString();

                Intent intent_to_dec_swim_sec = new Intent();
                intent_to_dec_swim_sec.setAction("com.example.awa.ProteinCheck.fragment.MY_ACTION_DEC_SWIM_SEC");
                getActivity().sendBroadcast(intent_to_dec_swim_sec);
                Log.d(TAG, "has send");
                String to_be_event8 = collecting_min_plus_collecting_sec + swiming_min_plus_swiming_sec;

                EventBus.getDefault().postSticky(new MyEventbus("eventbus_send_string",
                        to_be_event8));
                break;

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

    private final BroadcastReceiver f1Receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String string_action = intent.getAction();
            if (BluetoothService.ACTION_DATA_AVAILABLE.equals(string_action)) {

                byte[] buf = intent.getByteArrayExtra(BluetoothService.EXTRA_DATA);
                String s = bytesToString(buf);
                StringBuilder mData = new StringBuilder();
                mData.append(s);
                String received_string = mData.toString();

                //电泳时间+
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

                } else {

                }

            }
        }
    };

    




}

