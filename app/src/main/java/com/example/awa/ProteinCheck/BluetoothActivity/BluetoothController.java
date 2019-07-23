package com.example.awa.ProteinCheck.BluetoothActivity;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothProfile;
import android.content.Intent;

import java.util.ArrayList;
import java.util.List;

public class BluetoothController {
    private BluetoothAdapter mAdapter;

    public BluetoothController(){
        mAdapter =BluetoothAdapter.getDefaultAdapter();
    }

    public void turn_on_bluetooth(Activity activity, int requestCode){
        Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        activity.startActivityForResult(intent, requestCode);

        //mAdapter.enable();
    }

    public boolean get_blue_status()
    {
        assert mAdapter != null;
        return mAdapter.isEnabled();
    }

    public void find_device()
    {
        if (mAdapter.isDiscovering() != true)
        {
            mAdapter.startDiscovery();
        }
    }

    public void stop_scan()
    {
        mAdapter.cancelDiscovery();
    }

    public List<BluetoothDevice> getBondedDeviceList()
    {
        return new ArrayList<>(mAdapter.getBondedDevices());
    }



}
