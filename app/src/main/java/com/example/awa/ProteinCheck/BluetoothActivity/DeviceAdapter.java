package com.example.awa.ProteinCheck.BluetoothActivity;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.awa.ProteinCheck.R;

import java.util.List;

public class DeviceAdapter extends BaseAdapter {


        private List<BluetoothDevice> mData;
        private Context mContext;


        public DeviceAdapter(List<BluetoothDevice>data, Context context)
        {
            mData = data;
            mContext = context.getApplicationContext();
        }
        @Override
        public int getCount() {
            return mData.size();
        }

        @Override
        public Object getItem(int i) {
            return mData.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            //View itemView =view;
            if(view == null)
            {
                view = LayoutInflater.from(mContext).inflate(R.layout.adapter_content, viewGroup, false);
            }
            TextView line1 = (TextView) view.findViewById(R.id.tv_name);
            TextView line2 = (TextView) view.findViewById(R.id.tv_address);

            //获取对应的蓝牙设备
            BluetoothDevice device = (BluetoothDevice) getItem(i);
            if (line1 == null)
            {
                Log.d("info", "line1 = null");

            }

            if (device.getName() == null)
            {
                Log.d("info", "device.getName() == null");
            }

            //显示名称
            if (device.getName() == null)
            {
                line1.setText("unknown device");
            }
            else
            {
                line1.setText(device.getName());
            }

            //显示地址
            line2.setText(device.getAddress());

            return view;
        }

        public void refresh(List<BluetoothDevice> data) {
            mData = data;
            notifyDataSetChanged();
        }
}
