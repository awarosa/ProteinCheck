package com.example.awa.ProteinCheck.fragment;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.awa.ProteinCheck.BluetoothActivity.BluetoothController;
import com.example.awa.ProteinCheck.BluetoothMain;

import com.example.awa.ProteinCheck.R;


public class mainFregment0 extends Fragment implements View.OnClickListener {
    private static final int REQUEST_ENABLE_BT = 1;
    private View view;
    private Button button_ble_connect;

    private BluetoothController fBlueToothController = new BluetoothController();


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fregment0_main, container, false);
        button_ble_connect = view.findViewById(R.id.btn_bt_connect);

        Drawable drawable1 = getResources().getDrawable(R.drawable.blue_icon);
        drawable1.setBounds(0,0,40,40);
        button_ble_connect.setCompoundDrawables(drawable1,null,null,null);

        Drawable drawable2 = getResources().getDrawable(R.drawable.next5);
        drawable2.setBounds(20,20,0,0);
        button_ble_connect.setCompoundDrawables(null,null,drawable2,null);

        button_ble_connect.setOnClickListener(this);
        fBlueToothController.turn_on_bluetooth(getActivity(),0);
        return view;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId())
        {
            case R.id.btn_bt_connect:
                Intent intent = new Intent();
                intent.setClass(getActivity(), BluetoothMain.class);
                getActivity().startActivity(intent);
                break;

            default:
                 break;

        }

    }

}
