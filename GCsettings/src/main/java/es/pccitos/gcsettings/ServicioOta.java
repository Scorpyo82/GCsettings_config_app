package es.pccitos.gcsettings;

/**
 * Created by scorpyomint on 6/12/14.
 */

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;


public class ServicioOta extends Service {

    //Opciones presentes en OtaInfo
    public static String FALLO = "fallo";
    public static String MOTIVO_FALLO = "motivo_fallo";
    public static String REINICIAR = "reiniciar";
    public static String PREPARADO = "preparado";
    public static String VERSION_ROM_ACTUAL = "version_rom_actual";
    public static String UPDATE_OTA = "update_ota";
    public static String OTA_CHANGES = "ota_changes";
    public static String UPDATE_PACKAGE = "update_package";


    public boolean fallo;
    public String motivo_fallo;
    public boolean reiniciar;
    public boolean preparado;
    public String version_rom_actual;
    public String update_ota;
    public String ota_changes;
    public String update_package;

    //Opciones presentes en OTA_Settings
    public static String AUTO_CHECK_OTA = "auto_check_ota";

    private static ServicioOta instance = null;

    int notificationID = 1;


    public static boolean isRunning() {
        return instance !=null;
    }

    @Override

    public void onCreate() {

        //Toast.makeText(this, "Servicio Iniciando" Toast.LENGTH_SHORT).show();
        //Obtiene el objeto de ajustes de la aplicación llamado ajustesGC.
        SharedPreferences sharedPreferences = ServicioOta.this.getSharedPreferences("ajustesGC", 0);
        //SharedPreferences sharedPreferences = getSharedPreferences("ajustesGC", 0);

        //Obtenemos el booleano almacenado en las preferencias de nombre "inicializado".
        //El segundo parametro indica el valor a devolver si no lo encuentra, en este caso, falso.
        final boolean VG_AUTO_CHECK_OTA = sharedPreferences.getBoolean(AUTO_CHECK_OTA,false);

        if(VG_AUTO_CHECK_OTA){
            Toast.makeText(this,"Servicio OTA Activado ",
                    Toast.LENGTH_SHORT).show();
            metodoComprobarUpdates();
            metodoUpdatesDisponibles();


        }else{
            stopService(new Intent(ServicioOta.this,
                    ServicioOta.class));

        }


    }



    @Override

    public int onStartCommand(Intent intent, int flags, int startID) {


        return START_STICKY;


    }



    @Override

    public void onDestroy() {

        Toast.makeText(this,"Servicio OTA detenido",
                Toast.LENGTH_SHORT).show();

    }



    @Override

    public IBinder onBind(Intent intent) {
        // TODO Auto-generated method stub
        return null;
    }

    public void metodoSearch(){


        // Ejecutamos un comando en modo root. Esto es lo que hace que nos aparezca
        // el superuser pidiendo confirmación.
        try {
            String [] cmd = {"su","-c","/system/xbin/gc-ota","--app","discreet"};
            Runtime.getRuntime().exec(cmd);
        } catch (IOException e) {
            e.printStackTrace();
        }

        Toast.makeText(getBaseContext(), "¡Se están realizando las tareas de segundo plano...!", Toast.LENGTH_SHORT).show();


    }


    // ****************ESTA ES LA ZONA DE EXPERIMENTACIÓN CON EL MÓDULO DE TIMERTASK ********************

    int contadorScan = 0;
    int contadorSearchUpdates = 0;
    static final int UPDATE_INTERVAL = 3600000;
    private Timer timer = new Timer();

    private void metodoComprobarUpdates() {
        timer.scheduleAtFixedRate(new TimerTask() {

            @Override
            public void run() {
                // TODO Auto-generated method stub
                Log.d("Servicio OTA Escanéo número: ", String.valueOf(++contadorScan));

                // Ejecutamos un comando en modo root. Esto es lo que hace que nos aparezca
                // el superuser pidiendo confirmación.
                try {
                    String [] cmd = {"su","-c","/system/xbin/gc-ota","--app","discreet"};
                    Runtime.getRuntime().exec(cmd);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }, 0, UPDATE_INTERVAL);
    }



    private void metodoUpdatesDisponibles() {
        timer.scheduleAtFixedRate(new TimerTask() {

            @Override
            public void run() {
                // TODO Auto-generated method stub
                //Log.d("Servicio OTA: ", String.valueOf(++contadorSearchUpdates));

                //Obtiene el objeto de ajustes de la aplicación llamado OtaInfo.
                SharedPreferences sharedPreferences = ServicioOta.this.getSharedPreferences("OtaInfo", 0);

                //Recargamos las variables con sus respectivos valores:
                preparado = sharedPreferences.getBoolean(PREPARADO, false);
                fallo = sharedPreferences.getBoolean(FALLO,true);

                if (contadorSearchUpdates != contadorScan){
                    if (preparado && !fallo)

                    //Abrimos OtaInfo mediante notificación
                    metodoNotificacion();

                    contadorSearchUpdates = contadorScan;

                }
            }

        }, 60000, 3600000);

    }

    protected void metodoNotificacion(){

        //Obtiene el objeto de ajustes de la aplicación llamado OtaInfo.
        SharedPreferences sharedPreferences = ServicioOta.this.getSharedPreferences("OtaInfo", 0);

        //Recargamos las variables con sus respectivos valores:
        update_ota = sharedPreferences.getString(UPDATE_OTA, "" );


        Intent i = new Intent(this, OtaInfo.class);
        i.putExtra("notificationID", notificationID);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, i, 0);
        NotificationManager nm = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);

        CharSequence ticker ="Actualización encontrada";
        CharSequence contentTitle = "Servicio OTA";
        CharSequence contentText = "Disponible ("+update_ota+")";
        Notification noti = new NotificationCompat.Builder(this)
                .setContentIntent(pendingIntent)
                .setTicker(ticker)
                .setContentTitle(contentTitle)
                .setContentText(contentText)
                .setSmallIcon(R.drawable.ic_launcher)
                .addAction(R.drawable.ic_launcher, ticker, pendingIntent)
                .setVibrate(new long[]{100, 250, 100, 500})
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .setAutoCancel(true)
                .build();
        nm.notify(notificationID, noti);
    }


}
