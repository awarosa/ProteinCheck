package com.example.awa.ProteinCheck;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.example.awa.ProteinCheck.BluetoothActivity.BluetoothController;
import com.example.awa.ProteinCheck.BluetoothService;
import com.example.awa.ProteinCheck.R;
import com.example.awa.ProteinCheck.fragment.MyEventbus;
import com.example.awa.ProteinCheck.fragment.mainFregment0;
import com.example.awa.ProteinCheck.fragment.mainFregment1;
import com.example.awa.ProteinCheck.fragment.mainFregment2;
import com.example.awa.ProteinCheck.fragment.mainFregment3;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends FragmentActivity implements View.OnClickListener {

    public static final String EXTRAS_DEVICE_NAME = "DEVICE_NAME";
    public static final String EXTRAS_DEVICE_ADDRESS = "DEVICE_ADDRESS";
    public static final String EXTRAS_BOLN_CONNECTED = "BOLN_CONNECTED";

    private static final String F0_KEY = "f0";
    private static final String F1_KEY = "f1";
    private static final String F2_KEY = "f2";
    private static final String F3_KEY = "f3";

    private mainFregment0 f0;
    private mainFregment1 f1;
    private mainFregment2 f2;
    private mainFregment3 f3;
    private List<Fragment> fragmentList = new ArrayList<>();
    private Fragment[] mFragments;
    private int mIndex;
    private RadioGroup rg_group;
    private RadioButton rb_set;
    private TextView tv_ces;
    private String linked_device_name;
    private static final String TAG = "my_info";
    private boolean mainConnected;
    private ToggleButton toggleButton_sunlit;
    private ToggleButton toggleButton_lit;
    private boolean lit_flag = false;

    private static String get_mes_main;
    private static String mainDevicename;

//    private ImageView imageView_home;
//    private ImageView imageView_set;
//    private ImageView imageView_collect;
//    private ImageView imageView_swim;

    //发送信息到单片机部分

    private BluetoothService mainBluetoothService;
    //private BluetoothController mainBluetoothController = new BluetoothController();

    ServiceConnection mainServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            mainBluetoothService = ((BluetoothService.LocalBinder) iBinder).getService();
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mainBluetoothService = null;

        }
    };


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        initView();
        //initFragment();
        Intent mainServiceIntent = new Intent(this, BluetoothService.class);
        bindService(mainServiceIntent, mainServiceConnection, BIND_AUTO_CREATE);
        initFragment();
        EventBus.getDefault().register(this);

    }

    @Override
    public void onAttachFragment(Fragment fragment) {
        if (f0 == null && fragment instanceof mainFregment0)
            f0 = (mainFregment0) fragment;

        if (f1 == null && fragment instanceof mainFregment1)
            f1 = (mainFregment1) fragment;
        if (f2 == null && fragment instanceof mainFregment2)
            f2 = (mainFregment2) fragment;
        if (f3 == null && fragment instanceof mainFregment3)
            f3 = (mainFregment3) fragment;
    }


    @Override
    protected void onResume() {
        super.onResume();
        IntentFilter temp_intentFilter = new IntentFilter();
        temp_intentFilter.addAction(BluetoothService.ACTION_DATA_AVAILABLE);
        registerReceiver(mainReceiver,temp_intentFilter);


    }

    @Override
    protected void onPause() {
        super.onPause();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(mainServiceConnection);
        mainBluetoothService = null;
        unregisterReceiver(mainReceiver);
        EventBus.getDefault().unregister(this);
    }


    private void initView() {
        tv_ces = (TextView) findViewById(R.id.tv_rbsen);
        toggleButton_sunlit = findViewById(R.id.tbtn_sunlit);
        toggleButton_lit = findViewById(R.id.tbtn_lit);


        toggleButton_sunlit.setOnClickListener(this);
        toggleButton_lit.setOnClickListener(this);
        rg_group = (RadioGroup) findViewById(R.id.radioGroup);
        rg_group.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int i) {
                for (int index = 0; index < group.getChildCount(); index++) {
                    RadioButton rb = (RadioButton) group.getChildAt(index);
                    if (rb.isChecked()) {
                        if (index == 1) {

                            setIndexSelected(index);
                            //广播设置事件
                            Intent intent_to_set = new Intent();
                            intent_to_set.setAction("com.example.awa.ProteinCheck.MY_ACTION_SET");
                            sendBroadcast(intent_to_set);
                            Log.d(TAG, "has send in set");
                            break;
                        } else if (index == 2) {
                            setIndexSelected(index);
                            Intent intent_to_collect = new Intent();
                            intent_to_collect.setAction("com.example.awa.ProteinCheck.MY_ACTION_COLLECT");
                            sendBroadcast(intent_to_collect);
                            break;
                        } else if (index == 3) {
                            setIndexSelected(index);
                            Intent intent_to_swim = new Intent();
                            intent_to_swim.setAction("com.example.awa.ProteinCheck.MY_ACTION_SWIM");
                            sendBroadcast(intent_to_swim);
                            break;

                        } else {
                            setIndexSelected(index);
                            Intent intent_to_welcome = new Intent();
                            intent_to_welcome.setAction("com.example.awa.ProteinCheck.MainAllActivity.MY_ACTION_WELCOME");
                            sendBroadcast(intent_to_welcome);
                            break;
                        }

                    }
                }
            }
        });

    }

    //方法一，默认第一fragment
    private void initFragment() {
        f0 = new mainFregment0();
        f1 = new mainFregment1();
        f2 = new mainFregment2();
        f3 = new mainFregment3();
        //添加到数组
        mFragments = new Fragment[]{f0, f1, f2, f3};
        //开启事务
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction ft = fragmentManager.beginTransaction();

        //添加首页

        ft.add(R.id.content, f0);

        ft.commit();
        //默认设置为第0个
        setIndexSelected(0);
    }

    //方法一，选中显示与隐藏
    private void setIndexSelected(int index) {

        if (mIndex == index) {
            return;
        }

        //隐藏
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction ft = fragmentManager.beginTransaction();
        ft.hide(mFragments[mIndex]);
        //判断是否添加
        if (!mFragments[index].isAdded()) {
            ft.add(R.id.content, mFragments[index]).show(mFragments[index]);
        } else {
            ft.show(mFragments[index]);
        }

        ft.commit();
        //再次赋值
        mIndex = index;

    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tbtn_sunlit:
                Intent intent_tbtn_sunlit = new Intent();
                intent_tbtn_sunlit.setAction("com.example.awa.ProteinCheck.MY_ACTION_SUNLIT");
                sendBroadcast(intent_tbtn_sunlit);
                Log.d(TAG, "has send sunlit");

                break;
            case R.id.tbtn_lit:
                Intent intent_tbtn_lit = new Intent();
                intent_tbtn_lit.setAction("com.example.awa.ProteinCheck.MY_ACTION_LIT");
                sendBroadcast(intent_tbtn_lit);
                Log.d(TAG, "has send lit");
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

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void onEvent(MyEventbus myEventbusf3)
    {
        if ("eventbus_send_string_main".equals(myEventbusf3.name))
        {
            get_mes_main = myEventbusf3.content;
        }
        if (get_mes_main!=null)
        {
            if ("tr".equals(get_mes_main.substring(0,2)))
            {
                mainConnected = true;
                int lengths = get_mes_main.length();
                mainDevicename = get_mes_main.substring(4,lengths);
                tv_ces.setText(mainDevicename);

            }
            else
            {
                mainConnected = false;
                tv_ces.setText("NOT CONNECTED");
            }

        }

    }


    private final BroadcastReceiver mainReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String string_action = intent.getAction();
            if (BluetoothService.ACTION_DATA_AVAILABLE.equals(string_action)) {

                byte[] buf = intent.getByteArrayExtra(BluetoothService.EXTRA_DATA);
                String s = bytesToString(buf);
                StringBuilder mData = new StringBuilder();
                mData.append(s);
                String received_string = mData.toString();
                Log.d(TAG, "onReceive: here is the string received:"+received_string);

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

                    Log.d(TAG, "onReceive: lit flag changed");
                    toggleButton_lit.setChecked(!lit_flag);

                } else {

                }

            }
        }
    };
}






