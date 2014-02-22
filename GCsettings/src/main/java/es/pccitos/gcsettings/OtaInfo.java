package es.pccitos.gcsettings;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;


public class OtaInfo extends ActionBarActivity {

    //Opciones presentes en OtaInfo
    public static String INICIALIZADO = "inicializado";
    public static String AUTO_CHECK_OTA = "auto_check_ota";
    public static String VERSION_ACTUAL = "version_actual";
    public static String UPDATE_OTA = "update_ota";
    public static String OTA_CHANGES = "ota_changes";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ota_info);

    }


    /**
     * A placeholder fragment containing a simple view.
     */

}
