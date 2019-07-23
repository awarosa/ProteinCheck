package com.example.awa.ProteinCheck.ExchangeWithChip;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;

public class ExchangeService extends Service {


    @Override
    public IBinder onBind(Intent intent) {
        return new LocalBinder();
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    public final class LocalBinder extends Binder
    {
        public ExchangeService getService()
        {
            return ExchangeService.this;
        }
    }

    protected void sendExchangeBroadcast(final String action)
    {
        Intent intent = new Intent(action);
        sendBroadcast(intent);
    }
}
