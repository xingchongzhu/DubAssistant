package synthesis.voice.com.voicesynthesis.utils;

import android.util.Log;

public class LogUtils {
    private static final String TAG = "LogUtils";
    private static boolean isDebug = true;

    public static void i(String msg){
        if(isDebug){
            Log.i(TAG,msg);
        }
    }

    public static void d(String msg){
        if(isDebug){
            Log.d(TAG,msg);
        }
    }

    public static void w(String msg){
        Log.w(TAG,msg);
    }
    public static void e(String msg){
        Log.e(TAG,msg);
    }
}
