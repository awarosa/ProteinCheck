package com.example.awa.ProteinCheck;


import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;


import com.example.awa.ProteinCheck.BluetoothActivity.BluetoothController;
import com.example.awa.ProteinCheck.BluetoothActivity.DeviceAdapter;
import com.example.awa.ProteinCheck.fragment.MyEventbus;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

public class  BluetoothMain extends AppCompatActivity {

    public static final String EXTRAS_DEVICE_NAME = "DEVICE_NAME";
    public static final String EXTRAS_DEVICE_ADDRESS = "DEVICE_ADDRESS";
    public static final String EXTRAS_BOLN_CONNECTED = "BOLN_CONNECTED";
    public static final String TAG = "my_info";

    public static final int REQUEST_CODE = 0;
    public static final int SCAN_PERIOID = 10000;
    private List<BluetoothDevice> mDeviceList = new ArrayList<>();
    private List<BluetoothDevice> mBondedDeviceList = new ArrayList<>();

    private BluetoothController mController = new BluetoothController();
    private ListView mListView;
    private DeviceAdapter mAdapter;
    private ActionBar actionBar;
    private Toolbar toolbar;
    private Toast mToast;
    private boolean mScanning;//mscanning代表正在搜索设备中
    private Handler myhandler;

    //从sendrecvMESS.class迁移而来
    private BluetoothService mBluetoothService;
    private static String mDeviceName = "NONE";
    private static String mDeviceAddress = "a";
    private static int connect_state = 0;//connect_state==0代表蓝牙未连接，=1代表蓝牙连接中，=2代表已连接

    private boolean mConnected = false;



    private final ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            mBluetoothService = ((BluetoothService.LocalBinder)iBinder).getService();
            if (!mBluetoothService.initialize())
            {
                Log.d(TAG, "unable to initialize");
                finish();
            }
            if (!"a".equals(mDeviceAddress))
            {
                mBluetoothService.connect(mDeviceAddress);
                Log.d(TAG, "onServiceConnected: trying to connecting");
            }

        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mBluetoothService = null;
        }
    };



    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);



        myhandler = new Handler();

        setContentView(R.layout.main_bluetooth);
        initUI();

        toolbar = findViewById(R.id.tb_bluetooth_toolbar);

        toolbar.setLogo(R.mipmap.blue_icon);


        toolbar.setTitle("");
        //toolbar.setTitleTextColor();

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationIcon(R.mipmap.back0);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent_back_to_main = new Intent();
                intent_back_to_main.setClass(BluetoothMain.this, MainActivity.class);
                intent_back_to_main.putExtra(EXTRAS_DEVICE_NAME, mDeviceName);
                intent_back_to_main.putExtra(EXTRAS_DEVICE_ADDRESS, mDeviceAddress);
                intent_back_to_main.putExtra(EXTRAS_BOLN_CONNECTED, mConnected);

                BluetoothMain.this.startActivity(intent_back_to_main);
            }
        });


        if (mController == null) {
            Toast.makeText(this, R.string.error_bluetooth_not_supported, Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        Intent gattServiceIntent = new Intent(BluetoothMain.this, BluetoothService.class);
        bindService(gattServiceIntent, mServiceConnection, BIND_AUTO_CREATE);
        Log.d(TAG, "onCreate: has binded");
        //registerReceiver(mReceiver, makeGattUpdateIntentFilter());

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.bluetooth_main_menu, menu);

        if (connect_state == 0)
        {
            String to_be_event_disconnect = "false";
            EventBus.getDefault().postSticky(new MyEventbus("eventbus_send_string_main",
                    to_be_event_disconnect));

            if (!mScanning) {
                menu.findItem(R.id.menu_stop).setVisible(false);
                menu.findItem(R.id.menu_scan).setVisible(true);

                menu.findItem(R.id.menu_bluetooth_not_connected).setVisible(true);
                menu.findItem(R.id.menu_bluetooth_is_connecting).setVisible(false);
                menu.findItem(R.id.menu_bluetooth_has_connected).setVisible(false);
                menu.findItem(R.id.menu_refresh).setActionView(null);
            }
            else {
                menu.findItem(R.id.menu_stop).setVisible(true);
                menu.findItem(R.id.menu_scan).setVisible(false);

                menu.findItem(R.id.menu_bluetooth_not_connected).setVisible(true);
                menu.findItem(R.id.menu_bluetooth_is_connecting).setVisible(false);
                menu.findItem(R.id.menu_bluetooth_has_connected).setVisible(false);
                menu.findItem(R.id.menu_refresh).setActionView(R.layout.actionbar_indeterminate_progress2);
            }

        }
        else if (connect_state == 1)
        {
            String to_be_event_is_connecting = "false";
            EventBus.getDefault().postSticky(new MyEventbus("eventbus_send_string_main",
                    to_be_event_is_connecting));
            menu.findItem(R.id.menu_bluetooth_not_connected).setVisible(false);
            menu.findItem(R.id.menu_bluetooth_is_connecting).setVisible(true);
            menu.findItem(R.id.menu_bluetooth_has_connected).setVisible(false);
            menu.findItem(R.id.menu_refresh).setActionView(R.layout.actionbar_indeterminate_progress2);
            if (!mScanning) {
                menu.findItem(R.id.menu_stop).setVisible(false);
                menu.findItem(R.id.menu_scan).setVisible(true);

            }
            else {
                menu.findItem(R.id.menu_stop).setVisible(true);
                menu.findItem(R.id.menu_scan).setVisible(false);

            }
        }
        else
        {
            String to_be_event_has_connected = "true"+mDeviceName;
            EventBus.getDefault().postSticky(new MyEventbus("eventbus_send_string_main",
                    to_be_event_has_connected));
            menu.findItem(R.id.menu_bluetooth_not_connected).setVisible(false);
            menu.findItem(R.id.menu_bluetooth_is_connecting).setVisible(false);
            menu.findItem(R.id.menu_bluetooth_has_connected).setVisible(true);
            if (!mScanning) {
                menu.findItem(R.id.menu_stop).setVisible(false);
                menu.findItem(R.id.menu_scan).setVisible(true);

                menu.findItem(R.id.menu_refresh).setActionView(null);
            } else {
                menu.findItem(R.id.menu_stop).setVisible(true);
                menu.findItem(R.id.menu_scan).setVisible(false);
                menu.findItem(R.id.menu_refresh).setActionView(R.layout.actionbar_indeterminate_progress);
            }

        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.menu_scan:
                mAdapter.refresh(mDeviceList);
                mController.find_device();
                scan_device(true);
                break;

            case R.id.menu_stop:
                scan_device(false);
                break;

            default:
                break;
        }
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(mReceiver, makeGattUpdateIntentFilter());
        Log.d(TAG, "onResume: has regeister");
        if (!mController.get_blue_status())
        {
            mController.turn_on_bluetooth(BluetoothMain.this, 0);
        }
        //registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
        if (mBluetoothService != null) {
            if (!"a".equals(mDeviceAddress))
            {
                final boolean result = mBluetoothService.connect(mDeviceAddress);
                if (result) {
                    showToast("BONDED");
                } else {
                    showToast("not bond");
                }
            }

        }

        mAdapter.refresh(mDeviceList);
        mController.find_device();
        mListView.setOnItemClickListener(bindDeviceClick);
        scan_device(true);
        mayRequestLocation();
    }

    @Override
    protected void onPause() {
        scan_device(false);
        mAdapter.refresh(mDeviceList);
        super.onPause();
    }

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if( BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(action) ) {
                setProgressBarIndeterminateVisibility(true);
                //初始化数据列表
                mDeviceList.clear();
                mAdapter.notifyDataSetChanged();
            }
            else if( BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                setProgressBarIndeterminateVisibility(false);
            }
            else if( BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                //找到一个，添加一个

                //滤除重复的设备
                if (!mDeviceList.contains(device))
                {
                    mDeviceList.add(device);
                    mAdapter.notifyDataSetChanged();
                }



            }
            else if(BluetoothAdapter.ACTION_SCAN_MODE_CHANGED.equals(action)) {
                int scanMode = intent.getIntExtra(BluetoothAdapter.EXTRA_SCAN_MODE,0);
                if( scanMode == BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE) {
                    setProgressBarIndeterminateVisibility(true);
                }
                else {
                    setProgressBarIndeterminateVisibility(false);
                }
            }
            else if (BluetoothService.ACTION_GATT_CONNECTED.equals(action)) {

            }
            else if (BluetoothService.ACTION_GATT_DISCONNECTED.equals(action)) {

                mConnected = false;
                mBluetoothService.connect(mDeviceAddress);
            }
            else if (BluetoothService.ACTION_GATT_SERVICES_DISCOVERED.equals(action)) {
                showToast("has connected");
                //特征值找到才代表连接成功
                mConnected = true;
                Log.d(TAG, "onReceive: has connected");

                Log.d(TAG, "onReceive: "+String.valueOf(mConnected));
                connect_state = 2;
                invalidateOptionsMenu();

            }

            else if (BluetoothDevice.ACTION_ACL_DISCONNECTED.equals(action))
            {
                mConnected = false;
                Log.d(TAG, "onReceive: has disconnected");
                connect_state = 0;
                invalidateOptionsMenu();
            }

            else if (BluetoothService.ACTION_GATT_SERVICES_NO_DISCOVERED.equals(action)){
                if (!"a".equals(mDeviceAddress))
                {mBluetoothService.connect(mDeviceAddress);}
            }



        }
    };


    private void scan_device(boolean enable)
    {
        if (enable)
        {
            myhandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mScanning = false;
                    mController.stop_scan();
                    invalidateOptionsMenu();
                }
            }, SCAN_PERIOID);
            mScanning = true;
            mController.find_device();
        }
        else
        {
            mScanning = false;
            mController.stop_scan();
        }
        invalidateOptionsMenu();
    }


    private void initUI() {

        mListView = (ListView)findViewById(R.id.device_list);
        mAdapter = new DeviceAdapter(mDeviceList, this);
        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(bindDeviceClick);

    }




    @Override
    protected void onDestroy() {
        super.onDestroy();
        //unregisterReceiver(mReceiver);
    }




    private void showToast(String text) {

        if( mToast == null) {
            mToast = Toast.makeText(this, text, Toast.LENGTH_SHORT);
        }
        else {
            mToast.setText(text);
        }
        mToast.show();
    }



    private AdapterView.OnItemClickListener bindDeviceClick = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            BluetoothDevice device = mDeviceList.get(i);
            if (device == null) return;
            mDeviceAddress = device.getAddress();
            mDeviceName = device.getName();

            if (mScanning) {
                mController.stop_scan();
                mScanning = false;
            }
            try{
                mBluetoothService.connect(mDeviceAddress);
            }catch (Exception e){
                e.printStackTrace();
            }
            Log.d(TAG, "onItemClick: "+String.valueOf(mDeviceName));
            Log.d(TAG, "onItemClick: "+String.valueOf(mDeviceAddress));
            Log.d(TAG, "onItemClick: "+String.valueOf(mConnected));
            connect_state = 1;
            invalidateOptionsMenu();
            //startActivity(intent2);
            //device.createBond();
        }
    };

    private static IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();

        //开始查找
        intentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        //结束查找
        intentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        //查找设备
        intentFilter.addAction(BluetoothDevice.ACTION_FOUND);
        //设备扫描模式改变
        intentFilter.addAction(BluetoothAdapter.ACTION_SCAN_MODE_CHANGED);
        //绑定状态
        intentFilter.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
        //蓝牙已连接
        intentFilter.addAction(BluetoothDevice.ACTION_ACL_CONNECTED);
        //蓝牙已断开
        intentFilter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED);

        //sendrecvMess
        intentFilter.addAction(BluetoothService.ACTION_GATT_CONNECTED);
        intentFilter.addAction(BluetoothService.ACTION_GATT_DISCONNECTED);
        intentFilter.addAction(BluetoothService.ACTION_GATT_SERVICES_DISCOVERED);
        intentFilter.addAction(BluetoothService.ACTION_DATA_AVAILABLE);
        intentFilter.addAction(BluetoothService.ACTION_WRITE_SUCCESSFUL);
        intentFilter.addAction(BluetoothService.ACTION_GATT_SERVICES_NO_DISCOVERED);
        return intentFilter;
    }

    private void mayRequestLocation() {
        if (Build.VERSION.SDK_INT >= 23) {
            int checkCallPhonePermission = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION);
            if(checkCallPhonePermission != PackageManager.PERMISSION_GRANTED){
                //判断是否需要 向用户解释，为什么要申请该权限
                if(ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_COARSE_LOCATION))
                    Toast.makeText(this,R.string.ble_need_location, Toast.LENGTH_LONG).show();

                ActivityCompat.requestPermissions(this ,new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},1);
                return;
            }else{

            }
        } else {

        }
    }



}
