package es.pccitos.gcsettings;

import android.support.v7.app.ActionBarActivity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Toast;

import java.io.IOException;

public class OTA_Settings extends ActionBarActivity {

    public static String AUTO_CHECK_OTA = "auto_check_ota";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ota_settings);

        //Obtiene el objeto de ajustes de la aplicación llamado ajustesGC.
        SharedPreferences sharedPreferences = OTA_Settings.this.getSharedPreferences("ajustesGC", 0);

        //Declaración de los CheckBox
        final CheckBox cbActivateOta = (CheckBox) findViewById(R.id.cbActivateOta);

        //El prefijo VG_ es para identificar el Valor Guardado inicialmente
        final boolean VG_AUTO_CHECK_OTA = sharedPreferences.getBoolean(AUTO_CHECK_OTA,false);

        //Se le aplica el valor booleano correspondiente a cada clave leida
        cbActivateOta.setChecked(VG_AUTO_CHECK_OTA);


        //Declaración de los botones

        Button btCheckUpdate = (Button) findViewById(R.id.btCheckUpdates);

        //Eventos de los botones Aplicar y salir

        btCheckUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                metodoSearch();

            }
        });

    }




    /**
     * A placeholder fragment containing a simple view.
     */

    @Override
    protected void onDestroy() {
        super.onDestroy();

        //Declaración de los CheckBox
        final CheckBox cbActivateOta = (CheckBox) findViewById(R.id.cbActivateOta);

        boolean VN_AUTO_CHECK_OTA;


        //Se asigna a cada variable el valor booleano de cada checbox correspondiente
        VN_AUTO_CHECK_OTA = cbActivateOta.isChecked();



        SharedPreferences sharedPreferences = OTA_Settings.this.getSharedPreferences("ajustesGC", 0);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        //Se graban los nuevos valores en sus claves correspondientes
        editor.putBoolean(AUTO_CHECK_OTA, VN_AUTO_CHECK_OTA);

        editor.commit();

        //Se muestra el mensaje de que se han guardado los cambios
        Toast.makeText(getBaseContext(), "Guardando", Toast.LENGTH_SHORT).show();


    }

    public void metodoSearch(){


        // Ejecutamos un comando en modo root. Esto es lo que hace que nos aparezca
        // el superuser pidiendo confirmación.
        try {
            String [] cmd = {"su","-c","/system/xbin/gc-ota","--upgrade"};
            Runtime.getRuntime().exec(cmd);
        } catch (IOException e) {
            e.printStackTrace();
        }

        Toast.makeText(getBaseContext(), "¡Se están realizando las tareas!", Toast.LENGTH_SHORT).show();


    }


}
