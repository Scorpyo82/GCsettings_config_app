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
    //public static String UPDATE_PACKAGE = "update_package";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ota_info);

        //Declaración de los botones

        final Button btActualizar = (Button) findViewById(R.id.btActualizar);
        Button btDescartar = (Button) findViewById(R.id.btSalir);

        //Obtiene el objeto de ajustes de la aplicación llamado OtaInfo.
        SharedPreferences sharedPreferences = OtaInfo.this.getSharedPreferences("OtaInfo", 0);

        //Obtenemos el booleano almacenado en las preferencias de nombre "fallo"
        //Si existe y es verdadero significa que ha habido un problema
        boolean fallo = sharedPreferences.getBoolean(FALLO,false);
        String motivo_fallo = sharedPreferences.getString(MOTIVO_FALLO,"Fallo desconocido");


        if(fallo)
        {
            //Se muestra un mensaje avisando del fallo y el motivo

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Se vé que ha habido un problema. El motivo es el siguiente:\n" + motivo_fallo)
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


        //Obtenemos el booleano almacenado en las preferencias de nombre "reiniciar"
        //Si existe y es verdadero significa que la descarga terminó y hay que reiniciar en modo recovery
        boolean reiniciar = sharedPreferences.getBoolean(REINICIAR,false);

        if(reiniciar)
        {
            //Se muestra un mensaje avisando de que se debe realizar un reboot recovery

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Ya se ha completado la descarga de la actualización." +
                    "Cuando esté listo puede reiniciar en modo recovery para que comienze la instalación." +
                    "Recuerde, si reinicia de modo normal, se rechazará la acualización.")
                    .setTitle("OTA ¡Todo listo!")
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


        //Obtenemos el booleano almacenado en las preferencias de nombre "preparado".
        //El segundo parametro indica el valor a devolver si no lo encuentra, en este caso, falso.
        boolean preparado = sharedPreferences.getBoolean(PREPARADO,false);

        if(!preparado)
        {

            //Preparamos el editor para ShareprePreferences
            SharedPreferences.Editor editor = sharedPreferences.edit();

            //Le indicamos que queremos que almacene un booleano de nombre inicializado con valor true
            //Además, introducimos el resto de valores para los textview por defecto, en este caso ajustados
            //la CustomRom GingerCerecilla v0.8

            editor.putBoolean(FALLO, false);
            editor.putBoolean(REINICIAR, false);
            editor.putBoolean(PREPARADO, false);
            editor.putString(VERSION_ROM_ACTUAL, "No es posible determinar...");
            editor.putString(UPDATE_OTA, "No se han encontrado datos...");
            editor.putString(OTA_CHANGES,"No se han encontrado datos...");

            //Además, desactivamos el botón de actualizar para que no se pueda continuar.
            btActualizar.setEnabled(false);


            //Tras haber indicado todos los cambios a realizar (en este caso una configuración por defecto) le
            //indicamos al editor que los almacene en las preferencias.
            editor.commit();

            //Se muestra un mensaje avisando de que no hay rastro de que el binario "gc-ota" haya
            //dejado información en el archivo que sea de utilidad.

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("La busqueda de la actualización fué infructuosa por falta de datos." +
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




        //Cargar configuración para los CheckBox
        //Obtiene un booleano almacenado en las preferencias para cada
        //El segundo parametro indica el valor a devolver si no lo encuentra, en este caso, falso.

        //El prefijo VG_ es para identificar el Valor Guardado inicialmente
        final String VG_VERSION_ROM_ACTUAL = sharedPreferences.getString(VERSION_ROM_ACTUAL,"Sin datos");
        final String VG_UPDATE_OTA = sharedPreferences.getString(UPDATE_OTA, "Sin datos");
        final String VG_OTA_CHANGES = sharedPreferences.getString(OTA_CHANGES,"Sin datos");



        //Declaración de los textview
        TextView tvMostrarVersion = (TextView) findViewById(R.id.tvMostrarVersion);
        TextView tvMostrarUpdateOtaInfo = (TextView) findViewById(R.id.tvMostrarUpdateOtaInfo);
        TextView tvMostrarCambios = (TextView) findViewById(R.id.tvMostrarCambios);


        //Se le aplica el valor string correspondiente a cada clave leida a cada textview
        tvMostrarVersion.setText(VG_VERSION_ROM_ACTUAL);
        tvMostrarUpdateOtaInfo.setText(VG_UPDATE_OTA);
        tvMostrarCambios.setText(VG_OTA_CHANGES);


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

            }
        });


    }


    /**
     * A placeholder fragment containing a simple view.
     */

    public void metodoActualizar(){


        //Se muestra un mensaje avisando de que se está descargando la actualización

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Querido usuario, a continuación se descargará la actualización." +
                "Por el momento no está implementada ninguna barra de progreso y se descargará en segundo plano." +
                "Se le notificará con la finalización de la descarga.")
                .setTitle("Listo para descarga...")
                .setCancelable(false)
                .setNeutralButton("Aceptar",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.cancel();
                                //Se ejecuta el comando para actualizar
                                try {
                                    String [] cmd = {"su","-c","/system/xbin/gc-ota","--app","upgrade"};
                                    Runtime.getRuntime().exec(cmd);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                finish();
                            }
                        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

}