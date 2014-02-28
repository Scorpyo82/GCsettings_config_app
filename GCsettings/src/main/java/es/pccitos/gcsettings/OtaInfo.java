package es.pccitos.gcsettings;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;


public class OtaInfo extends ActionBarActivity {

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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ota_info);

        //Declaración de los botones
        Button btActualizar = (Button) findViewById(R.id.btActualizar);
        Button btDescartar = (Button) findViewById(R.id.btSalir);



        metodoLoadPreferences();
        if(fallo)
        {

            metodoFallo();

        }


        if(reiniciar)
        {
            //Se muestra un mensaje avisando de que se debe realizar un reboot recovery

            metodoInstalar();

        }


        if(!preparado)
        {

            metodoSinDatos();

        }


        //Eventos de los botones Aplicar y salir

        btActualizar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                metodoActualizar();

            }
        });

        btDescartar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Toast.makeText(getBaseContext(), "Cerrando sin cambios", Toast.LENGTH_SHORT).show();

                finish();
                //SystemClock.sleep(8000);
                //System.exit(0);

            }
        });


    }


    /**
     * A placeholder fragment containing a simple view.
     */

    public void metodoActualizar(){


        //Se muestra un mensaje avisando de que se está descargando la actualización

        AlertDialog.Builder msgConfirmarDescarga = new AlertDialog.Builder(this);
        msgConfirmarDescarga.setTitle("Listo para descarga...");
        msgConfirmarDescarga.setMessage("Querido usuario, a continuación se descargará la actualización. " +
                "Por el momento no está implementada ninguna barra de progreso y se descargará en segundo plano." +
                "Se le notificará con la finalización de la descarga.\n\n ¿Descargar ahora?");

        msgConfirmarDescarga.setPositiveButton("Si", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                //Se ejecuta el comando para actualizar
                try {
                    String [] cmd = {"su","-c","/system/xbin/gc-ota","--app","upgrade"};
                    Runtime.getRuntime().exec(cmd);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                //Se muestra un mensaje y diciendo el paquete que se está descargando y se sale de la app
                Toast.makeText(getBaseContext(), "Descargando " + update_package, Toast.LENGTH_SHORT).show();

                finish();
                //android.os.Process.killProcess(android.os.Process.myPid());
            }});

        msgConfirmarDescarga.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                //Se muestra un mensaje y diciendo que no se hacen cambios.
                Toast.makeText(getBaseContext(), "Sin cambios", Toast.LENGTH_SHORT).show();

            }});
        msgConfirmarDescarga.show();

    }



    public void metodoInstalar(){


        //Se muestra un mensaje avisando de que se está descargando la actualización

        AlertDialog.Builder msgInstalarUpdate = new AlertDialog.Builder(this);
        msgInstalarUpdate.setCancelable(false);
        msgInstalarUpdate.setTitle("OTA Descarga terminada");
        msgInstalarUpdate.setMessage("Ya se ha compleado la descarga de la actualización.\n\n" +
                "Puede reiniciar e instalar la actualización ahora o más tarde reiniciando " +
                "en modo recovery. Si reinicia en modo normal se anulará la instalación.\n\n" +
                "¿Quiere usted reiniciar e instalarlo ahora?");


        msgInstalarUpdate.setPositiveButton("Reiniciar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                //Se ejecuta el comando para actualizar
                try {
                    String[] cmd = {"su","-c","reboot","recovery"};
                    Runtime.getRuntime().exec(cmd);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                finish();

            }
        });

        msgInstalarUpdate.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                //Se muestra un mensaje y diciendo que no se hacen cambios.
                Toast.makeText(getBaseContext(), "Recuerde reiniciar en modo recovery", Toast.LENGTH_LONG).show();

            }});
        msgInstalarUpdate.show();

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

    /*
    public void reload() {

        if (Build.VERSION.SDK_INT >= 11) {
            recreate();
        } else {
            Intent intent = getIntent();
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            finish();
            overridePendingTransition(0, 0);

            startActivity(intent);
            overridePendingTransition(0, 0);
        }
    }
    */

    @Override
    protected void onResume(){
        super.onResume();
        //Se llama a este método para recargar las variables
        metodoLoadPreferences();

    }



    public void metodoLoadPreferences(){

        //Obtiene el objeto de ajustes de la aplicación llamado OtaInfo.
        SharedPreferences sharedPreferences = OtaInfo.this.getSharedPreferences("OtaInfo", 0);

        //Recargamos las variables con sus respectivos valores:

        preparado = sharedPreferences.getBoolean(PREPARADO, false);

        //Obtenemos el booleano almacenado en las preferencias de nombre "fallo"
        //Si existe y es verdadero significa que ha habido un problema
        fallo = sharedPreferences.getBoolean(FALLO, false);
        motivo_fallo = sharedPreferences.getString(MOTIVO_FALLO, "Fallo desconocido");

        //Obtenemos el booleano almacenado en las preferencias de nombre "reiniciar"
        //Si existe y es verdadero significa que la descarga terminó y hay que reiniciar en modo recovery
        reiniciar = sharedPreferences.getBoolean(REINICIAR, false);

        version_rom_actual = sharedPreferences.getString(VERSION_ROM_ACTUAL,"Sin datos");
        update_ota = sharedPreferences.getString(UPDATE_OTA, "Sin datos");
        ota_changes = sharedPreferences.getString(OTA_CHANGES,"Sin datos");
        update_package = sharedPreferences.getString(UPDATE_PACKAGE,"Sin datos");

        //Declaración de los textview
        TextView tvMostrarVersion = (TextView) findViewById(R.id.tvMostrarVersion);
        TextView tvMostrarUpdateOtaInfo = (TextView) findViewById(R.id.tvMostrarUpdateOtaInfo);
        TextView tvMostrarCambios = (TextView) findViewById(R.id.tvMostrarCambios);

        //Se le aplica el valor string correspondiente a cada clave leida a cada textview
        tvMostrarVersion.setText(version_rom_actual);
        tvMostrarUpdateOtaInfo.setText(update_ota);
        tvMostrarCambios.setText(ota_changes);

        /*
        //Creamos el editor
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putBoolean(FALLO, fallo);
        editor.putString(MOTIVO_FALLO, motivo_fallo);
        editor.putBoolean(REINICIAR, reiniciar);
        editor.putBoolean(PREPARADO, preparado);
        editor.putString(VERSION_ROM_ACTUAL, version_rom_actual);
        editor.putString(UPDATE_OTA, update_ota);
        editor.putString(OTA_CHANGES,ota_changes);
        editor.putString(UPDATE_PACKAGE, update_package);

        editor.apply();
        */

    }

    public void metodoSinDatos(){

        //Declaración de los botones
        Button btActualizar = (Button) findViewById(R.id.btActualizar);
        //Button btDescartar = (Button) findViewById(R.id.btSalir);

        /*
        //Obtiene el objeto de ajustes de la aplicación llamado OtaInfo.
        SharedPreferences sharedPreferences = OtaInfo.this.getSharedPreferences("OtaInfo", 0);

        //Creamos el editor
        SharedPreferences.Editor editor = sharedPreferences.edit();

        //Le indicamos que queremos que almacene un booleano de nombre "preparado" con valor true
        //Además, introducimos el resto de valores para los textview por defecto, en este caso ajustados
        //para la CustomRom GingerCerecilla v0.8

        editor.putBoolean(FALLO, false);
        editor.putBoolean(REINICIAR, false);
        editor.putBoolean(PREPARADO, false);
        editor.putString(VERSION_ROM_ACTUAL, "No es posible determinar...");
        editor.putString(UPDATE_OTA, "No se han encontrado datos...");
        editor.putString(OTA_CHANGES,"No se han encontrado datos...");


        //Tras haber indicado todos los cambios a realizar (en este caso una configuración por defecto) le
        //indicamos al editor que los almacene en las preferencias.
        editor.apply();
        */

        //Además, desactivamos el botón de actualizar para que no se pueda continuar.
        btActualizar.setEnabled(false);

        //Se muestra un mensaje avisando de que no hay rastro de que el binario "gc-ota" haya
        //dejado información en el archivo que sea de utilidad.

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("La busqueda de la actualización fue infructuosa por falta de datos.\n\n" +
                "NO SE PERMITIRÁ LA ACTUALIZACIÓN")
                .setTitle("Hubo un problema...")
                .setCancelable(false)
                .setNeutralButton("Continuar",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.cancel();

                                Toast.makeText(getBaseContext(), "¡Botón desactivado!", Toast.LENGTH_SHORT).show();

                            }
                        });
        AlertDialog alert = builder.create();
        alert.show();

    }

    public void metodoFallo(){

        //Se muestra un mensaje avisando del fallo y el motivo

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Ha habido un problema. El motivo es el siguiente:\n\n" + motivo_fallo)
                .setTitle("OTA ¡UPS!")
                .setCancelable(false)
                .setNeutralButton("Aceptar",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.cancel();

                                finish();
                            }
                        });
        AlertDialog alert = builder.create();
        alert.show();

    }



    }
