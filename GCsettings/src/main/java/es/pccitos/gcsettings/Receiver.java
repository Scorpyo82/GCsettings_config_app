package es.pccitos.gcsettings;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by scorpyomint on 8/12/14.
 */
public class Receiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Intent serviceIntent = new Intent(context,ServicioOta.class);
        if(!ServicioOta.isRunning())context.startService(serviceIntent);
    }
}
