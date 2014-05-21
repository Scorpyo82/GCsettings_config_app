package es.pccitos.gcsettings;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Toast;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;


public class MainActivity extends ActionBarActivity {

    //Opciones presentes en MainActivity
    public static String INICIALIZADO = "inicializado";
    public static String BOOTANIMATION = "bootanimation";
    public static String SHUTDOWNANIMATION = "shutdownanimation";
    public static String SWAP = "swap";
    public static String OUPDATES2SYSTEM = "updates_to_system";
    public static String DATA2EXT = "data_to_ext";

    //Opciones presentes en ActivityListApps
    public static String PLAYSTORE = "UPDATES2SYSTEM_APP_com.android.vending";
    public static String ANDROIDSERVICES = "UPDATES2SYSTEM_APP_com.google.android.gms";
    public static String LINK2SD = "UPDATES2SYSTEM_APP_com.buak.Link2SD";
    public static String FIREWALL = "UPDATES2SYSTEM_APP_dev.ukanth.ufirewall";

    //Opciones presentes en OTA_Settings
    public static String AUTO_CHECK_OTA = "auto_check_ota";




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Obtiene el objeto de ajustes de la aplicación llamado ajustesGC.
        SharedPreferences sharedPreferences = MainActivity.this.getSharedPreferences("ajustesGC", 0);
        //SharedPreferences sharedPreferences = getSharedPreferences("ajustesGC", 0);

        //Obtenemos el booleano almacenado en las preferencias de nombre "inicializado".
        //El segundo parametro indica el valor a devolver si no lo encuentra, en este caso, falso.
        boolean inicializado = sharedPreferences.getBoolean(INICIALIZADO,false);

        if(!inicializado)
        {

            //Código que queremos que se ejecute tras la primera ejecución de la app
            SharedPreferences.Editor editor = sharedPreferences.edit();

            //Le indicamos que queremos que almacene un booleano de nombre inicializado con valor true
            //Además, introducimos el resto de valores para los checbox por defecto, en este caso ajustados
            //la CustomRom GingerCerecilla v0.8
            editor.putBoolean(INICIALIZADO, true);
            editor.putBoolean(BOOTANIMATION, true);
            editor.putBoolean(SHUTDOWNANIMATION, true);
            editor.putBoolean(SWAP, false);
            editor.putBoolean(OUPDATES2SYSTEM, false);
            editor.putBoolean(DATA2EXT, false);
            editor.putBoolean(PLAYSTORE, true);
            editor.putBoolean(ANDROIDSERVICES, true);
            editor.putBoolean(LINK2SD, true);
            editor.putBoolean(FIREWALL, true);
            editor.putBoolean(AUTO_CHECK_OTA, false);

            //Tras haber indicado todos los cambios a realizar (en este caso una configuración por defecto) le
            //indicamos al editor que los almacene en las preferencias.
            editor.commit();

            //Se muestra un mensaje avisando de que es la primera vez que se ejecuta la app y que
            //debe especificar los valores que quiere ejecutar porque se ha activado una configuración por defecto

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Esta es la primera vez que se usa esta aplicación, se cargará una configuración predeterminada que puede cambiar a su gusto, se necesitarán permisos de ROOT")
                    .setTitle("Primera ejecución...")
                    .setCancelable(false)
                    .setNeutralButton("Aceptar",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialogInterface.cancel();

                                    //Ahora lo que hacemos es llamar al método copySettingsToCacheApp() para copiar el
                                    //Script gc-settings a la cache y de ahí moverlo más tarde a /system/xbin/
                                    //Método realizado por Jaime, < jaime82ad@gmail.com >

                                    copySettingsToCacheApp();
                                    copyGCotaToCacheApp();


                                    //Y ejecutamos el comando gc-settings en modo automático

                                    try {
                                        String [] cmd = {"su","-c","/system/xbin/gc-settings","--auto"};
                                        Runtime.getRuntime().exec(cmd);
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }

                                }
                            });
            AlertDialog alert = builder.create();
            alert.show();


        }



        //Cargar configuración para los CheckBox
        //Obtiene un booleano almacenado en las preferencias para cada
        //El segundo parametro indica el valor a devolver si no lo encuentra, en este caso, falso.

        //El prefijo VG_ es para identificar el Valor Guardado inicialmente
        final boolean VG_BOOTANIMATION = sharedPreferences.getBoolean(BOOTANIMATION,false);
        final boolean VG_SHUTDOWNANIMATION = sharedPreferences.getBoolean(SHUTDOWNANIMATION,false);
        final boolean VG_SWAP = sharedPreferences.getBoolean(SWAP,false);
        final boolean VG_UPDATE2SYSTEM = sharedPreferences.getBoolean(OUPDATES2SYSTEM,false);
        final boolean VG_DATA2EXT = sharedPreferences.getBoolean(DATA2EXT,false);


        //Declaración de los CheckBox
        CheckBox cbBootanimation = (CheckBox) findViewById(R.id.cbActivar_bootanimation);
        CheckBox cbShutdownanimation = (CheckBox) findViewById(R.id.cbActivar_shutdownanimation);
        CheckBox cbSwap = (CheckBox) findViewById(R.id.cbActivar_swap);
        CheckBox cbUpdates2system = (CheckBox) findViewById(R.id.cbUpdatesToSystem);
        CheckBox cbData2ext = (CheckBox) findViewById(R.id.cbData2sd_ext);

        //Se le aplica el valor booleano correspondiente a cada clave leida
        cbBootanimation.setChecked(VG_BOOTANIMATION);
        cbShutdownanimation.setChecked(VG_SHUTDOWNANIMATION);
        cbSwap.setChecked(VG_SWAP);
        cbUpdates2system.setChecked(VG_UPDATE2SYSTEM);
        cbData2ext.setChecked(VG_DATA2EXT);


        //Declaración de los botones

        Button btAplicar = (Button) findViewById(R.id.btAplicar);
        Button btSalir = (Button) findViewById(R.id.btSalir);

        //Eventos de los botones Aplicar y salir

        btAplicar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                metodoAplicar();

            }
        });

        btSalir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Toast.makeText(getBaseContext(), "Los cambios no aplicados serán ignorados", Toast.LENGTH_SHORT).show();

                finish();

            }
        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.menu_list_apps:

                Intent i = new Intent(this, ActivityListApps.class);
                startActivity(i);

                return true;

            case R.id.menu_opciones_ota:

                Intent a = new Intent(this, OTA_Settings.class);
                startActivity(a);
                return true;

            case R.id.menu_aplicar:

                metodoAplicar();
                return true;

            case R.id.menu_acercade:

                Intent b = new Intent(this, Acercade.class);
                startActivity(b);

                return true;

            case R.id.menu_salir:

                Toast.makeText(getBaseContext(), "Los cambios no aplicados serán ignorados", Toast.LENGTH_SHORT).show();
                finish();
                return true;



        }

        return false;
    }



    /**
     * A placeholder fragment containing a simple view.
     */


    //Método realizado por Jaime para pasar datos a la cache y de ahí más tarde a /system/xbin
    public void copySettingsToCacheApp() {
        try {

            InputStream in = getResources().openRawResource(R.raw.gcsettings);
            String destino = getCacheDir().getAbsolutePath().toString()+"/gc-settings";

            OutputStream out = new FileOutputStream(destino);

            byte[] data = new byte[1024];

            int i = in.read(data);

            while (i>0) {
                out.write(data, 0, i);
                i = in.read(data);
            }
            in.close();
            out.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

        // Ejecutamos un comando en modo root. Esto es lo que hace que nos aparezca
        // el superuser pidiendo confirmación.
        // Para mover el archivo desde la cache hasta /system/xbin/ y cambiarle los permisos
        try {
            String [] cmd = {"su","-c","busybox","mount","-o","remount,rw","/system",";","cp","/data/data/es.pccitos.gcsettings/cache/gc-settings","/system/xbin/gc-settings",";","chmod","744","/system/xbin/gc-settings",";","busybox","mount","-o","remount,ro","/system"};
            Runtime.getRuntime().exec(cmd);
        } catch (IOException e) {
            e.printStackTrace();
        }

        Toast.makeText(getBaseContext(), "¡Script gc-settings copiado con éxito!", Toast.LENGTH_SHORT).show();

    }

    //Método realizado por Jaime para pasar datos a la cache y de ahí más tarde a /system/xbin
    public void copyGCotaToCacheApp() {
        try {

            InputStream in = getResources().openRawResource(R.raw.gcota);
            String destino = getCacheDir().getAbsolutePath().toString()+"/gc-ota";

            OutputStream out = new FileOutputStream(destino);

            byte[] data = new byte[1024];

            int i = in.read(data);

            while (i>0) {
                out.write(data, 0, i);
                i = in.read(data);
            }
            in.close();
            out.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

        // Ejecutamos un comando en modo root. Esto es lo que hace que nos aparezca
        // el superuser pidiendo confirmación.
        // Para mover el archivo desde la cache hasta /system/xbin/ y cambiarle los permisos
        try {
            String [] cmd = {"su","-c","busybox","mount","-o","remount,rw","/system",";","cp","/data/data/es.pccitos.gcsettings/cache/gc-ota","/system/xbin/gc-ota",";","chmod","744","/system/xbin/gc-ota",";","busybox","mount","-o","remount,ro","/system"};
            Runtime.getRuntime().exec(cmd);
        } catch (IOException e) {
            e.printStackTrace();
        }

        Toast.makeText(getBaseContext(), "¡Script gc-ota copiado con éxito!", Toast.LENGTH_SHORT).show();

    }

    public void metodoAplicar(){

        //Declaración de los CheckBox
        CheckBox cbBootanimation = (CheckBox) findViewById(R.id.cbActivar_bootanimation);
        CheckBox cbShutdownanimation = (CheckBox) findViewById(R.id.cbActivar_shutdownanimation);
        CheckBox cbSwap = (CheckBox) findViewById(R.id.cbActivar_swap);
        CheckBox cbUpdates2system = (CheckBox) findViewById(R.id.cbUpdatesToSystem);
        CheckBox cbData2ext = (CheckBox) findViewById(R.id.cbData2sd_ext);

        //Se crean las variables con los nuevos valores, por eso el prefijo VN_, (Valor Nuevo)
        boolean VN_BOOTANIMATION;
        boolean VN_SHUDOWNANIMATION;
        boolean VN_SWAP;
        boolean VN_UPDATES2SYSTEM;
        boolean VN_DATA2EXT;

        //Se asigna a cada variable el valor booleano de cada checbox correspondiente
        VN_BOOTANIMATION = cbBootanimation.isChecked();

        VN_SHUDOWNANIMATION = cbShutdownanimation.isChecked();

        VN_SWAP = cbSwap.isChecked();

        VN_UPDATES2SYSTEM = cbUpdates2system.isChecked();

        VN_DATA2EXT = cbData2ext.isChecked();


        //Obtiene el objeto de ajustes de la aplicación llamado ajustesGC.
        SharedPreferences sharedPreferences = MainActivity.this.getSharedPreferences("ajustesGC", 0);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        //Grabando las variables en las claves del fichero en sharedprefenrces
        editor.putBoolean(BOOTANIMATION, VN_BOOTANIMATION);
        editor.putBoolean(SHUTDOWNANIMATION, VN_SHUDOWNANIMATION);
        editor.putBoolean(SWAP, VN_SWAP);
        editor.putBoolean(OUPDATES2SYSTEM, VN_UPDATES2SYSTEM);
        editor.putBoolean(DATA2EXT, VN_DATA2EXT);

        //Tras haber indicado todos los cambios a realizar (en este caso una configuración por defecto) le
        //indicamos al editor que los almacene en las preferencias.
        editor.commit();

        // Ejecutamos un comando en modo root. Esto es lo que hace que nos aparezca
        // el superuser pidiendo confirmación.
        try {
            String [] cmd = {"su","-c","/system/xbin/gc-settings","--auto"};
            Runtime.getRuntime().exec(cmd);
        } catch (IOException e) {
            e.printStackTrace();
        }

        Toast.makeText(getBaseContext(), "¡Los datos han sido aplicados!", Toast.LENGTH_SHORT).show();


    }


}

