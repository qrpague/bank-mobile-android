package container.bank.tools;

import android.util.Log;

public class LogApp {

    public static void debug(String msg){
        if ( msg != null)
            Log.d("DEBUG",msg);
    }

    public static void info(String msg){
        if ( msg != null)
            Log.i("INFO",msg);
    }

}
