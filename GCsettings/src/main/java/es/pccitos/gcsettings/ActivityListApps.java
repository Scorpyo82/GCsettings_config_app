package es.pccitos.gcsettings;


import android.content.SharedPreferences;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.Toast;


public class ActivityListApps extends ActionBarActivity {

    public static String PLAYSTORE = "UPDATES2SYSTEM_APP_com.android.vending";
    public static String ANDROIDSERVICES = "UPDATES2SYSTEM_APP_com.google.android.gms";
    public static String LINK2SD = "UPDATES2SYSTEM_APP_com.buak.Link2SD";
    public static String FIREWALL = "UPDATES2SYSTEM_APP_dev.ukanth.ufirewall";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_apps);

        //Obtiene el objeto de ajustes de la aplicación llamado ajustesGC.
        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("ajustesGC", 0);

        //Declaración de los CheckBox
        final CheckBox cbPlaystore = (CheckBox) findViewById(R.id.cbCom_android_vending);
        final CheckBox cbAndroidServices = (CheckBox) findViewById(R.id.cbCom_google_android_gms);
        final CheckBox cbLink2SD = (CheckBox) findViewById(R.id.cbCom_buak_Link2SD);
        final CheckBox cbFirewall = (CheckBox) findViewById(R.id.cbDev_ukanth_ufirewall);


        //El prefijo VG_ es para identificar el Valor Guardado inicialmente
        final boolean VG_PLAYSTORE = sharedPreferences.getBoolean(PLAYSTORE,false);
        final boolean VG_ANDROIDSERVICES = sharedPreferences.getBoolean(ANDROIDSERVICES,false);
        final boolean VG_LINK2SD = sharedPreferences.getBoolean(LINK2SD,false);
        final boolean VG_FIREWALL = sharedPreferences.getBoolean(FIREWALL,false);



        //Se le aplica el valor booleano correspondiente a cada clave leida
        cbPlaystore.setChecked(VG_PLAYSTORE);
        cbAndroidServices.setChecked(VG_ANDROIDSERVICES);
        cbLink2SD.setChecked(VG_LINK2SD);
        cbFirewall.setChecked(VG_FIREWALL);

    }


    /**
     * A placeholder fragment containing a simple view.
     */

    @Override
    protected void onDestroy() {
        super.onDestroy();

        //Declaración de los CheckBox
        final CheckBox cbPlaystore = (CheckBox) findViewById(R.id.cbCom_android_vending);
        final CheckBox cbAndroidServices = (CheckBox) findViewById(R.id.cbCom_google_android_gms);
        final CheckBox cbLink2SD = (CheckBox) findViewById(R.id.cbCom_buak_Link2SD);
        final CheckBox cbFirewall = (CheckBox) findViewById(R.id.cbDev_ukanth_ufirewall);

        //Se crean las variables con los nuevos valores, por eso el prefijo VN_, (Valor Nuevo)
        boolean VN_PLAYSTORE;
        boolean VN_ANDROIDSERVICES;
        boolean VN_LINK2SD;
        boolean VN_FIREWALL;


        //Se asigna a cada variable el valor booleano de cada checbox correspondiente
        VN_PLAYSTORE = cbPlaystore.isChecked();
        VN_ANDROIDSERVICES = cbAndroidServices.isChecked();
        VN_LINK2SD = cbLink2SD.isChecked();
        VN_FIREWALL = cbFirewall.isChecked();



        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("ajustesGC", 0);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        //Se graban los nuevos valores en sus claves correspondientes
        editor.putBoolean(PLAYSTORE, VN_PLAYSTORE);
        editor.putBoolean(ANDROIDSERVICES, VN_ANDROIDSERVICES);
        editor.putBoolean(LINK2SD, VN_LINK2SD);
        editor.putBoolean(FIREWALL, VN_FIREWALL);

        editor.commit();

        //Se muestra el mensaje de que se han guardado los cambios
        Toast.makeText(getBaseContext(), "Lista de paquetes guardada", Toast.LENGTH_SHORT).show();


    }
}
