package main.taskSync;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class SynchronizerService extends Service {
    private static SynchronizerAdapter synchronizerAdapter = null;
    private static final Object sSyncAdapterLock = new Object();

    @Override
    public void onCreate() {
        Log.e("SynchronizerService", "Service created");
        synchronized (sSyncAdapterLock) {
            if (synchronizerAdapter == null) {
                synchronizerAdapter = new SynchronizerAdapter(getApplicationContext(), true);
            }
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return synchronizerAdapter.getSyncAdapterBinder();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
