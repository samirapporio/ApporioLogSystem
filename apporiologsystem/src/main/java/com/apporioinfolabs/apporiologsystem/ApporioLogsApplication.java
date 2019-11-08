package com.apporioinfolabs.apporiologsystem;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.hypertrack.hyperlog.DeviceLogModel;
import com.hypertrack.hyperlog.HyperLog;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.TimeZone;

public class ApporioLogsApplication extends Application implements Application.ActivityLifecycleCallbacks  {



    private static SharedPreferences.Editor editor;
    private static SharedPreferences sharedPref ;
    public static final String SHARED_PREFRENCE = "com.apporio.apporiologs";
    public static Context mContext  = null;
    public static String UNIQUE_NO  = "";
    private int activityReferences = 0;
    private boolean isActivityChangingConfigurations = false;
    private Gson gson ;
    private final String TAG  = "ApporioLogsApplication";
    private static final String EndPoint = "http://13.233.98.63:3108/api/v1/logs/add_log";

    @Override
    public void onCreate() {
        mContext = this;
        HyperLog.initialize(this);
        HyperLog.setLogLevel(Log.VERBOSE);
        HyperLog.setLogFormat(new CustomLogMessageFormat(this));
        UNIQUE_NO = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
        gson = new GsonBuilder().create();
        super.onCreate();
        registerActivityLifecycleCallbacks(this);
    }


    public static void setPlayerId(String data){
        editor.putString("player_id",""+data);
        editor.commit();
    }

    public static void setExtraData (String data){
        editor.putString("extra_data",""+data);
        editor.commit();
    }

    public static SharedPreferences getAtsPrefrences (){
        if(sharedPref == null){
            sharedPref = mContext.getSharedPreferences(SHARED_PREFRENCE, Context.MODE_PRIVATE);
            return sharedPref ;
        }else{
            return sharedPref;
        }
    }

    @Override
    public void onActivityStarted(Activity activity) {
        if (++activityReferences == 1 && !isActivityChangingConfigurations) {
            Toast.makeText(activity, "Enters in foreground | Pending logs:"+HyperLog.hasPendingDeviceLogs()+" | Log count:"+HyperLog.getDeviceLogsCount(), Toast.LENGTH_LONG).show();
            Log.d(TAG , "Enters in foreground");
        }
    }

    @Override
    public void onActivityStopped(Activity activity) {

        isActivityChangingConfigurations = activity.isChangingConfigurations();
        if (--activityReferences == 0 && !isActivityChangingConfigurations) {
//            Toast.makeText(activity, "Enters in background | Pending logs"+HyperLog.hasPendingDeviceLogs()+" | Log count:"+HyperLog.getDeviceLogsCount(), Toast.LENGTH_LONG).show();
            APPORIOLOGS.debugLog(TAG , "Enters in Background");
            try{syncLogsAccordingly();}catch (Exception e){}
        }
    }


    @Override
    public void onActivityCreated(Activity activity, Bundle bundle) {

    }

    @Override
    public void onActivityResumed(Activity activity) {

    }

    @Override
    public void onActivityPaused(Activity activity) {

    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle bundle) {

    }

    @Override
    public void onActivityDestroyed(Activity activity) {

    }


    private void syncLogsAccordingly() throws Exception{

//        Toast.makeText(mContext, ""+HyperLog.getDeviceLogsInFile(this), Toast.LENGTH_SHORT).show();

        //Extra header to post request
        HashMap<String, String> params = new HashMap<>();
        params.put("timezone", TimeZone.getDefault().getID());
        List<DeviceLogModel> deviceLogModels = HyperLog.getDeviceLogs(false) ;


        JSONObject jsonObject  = new JSONObject();

        try{
            jsonObject.put("key",gson.toJson(deviceLogModels));
        }catch (Exception e){
            Toast.makeText(mContext, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
        }


        AndroidNetworking.post("" + EndPoint)
                .addJSONObjectBody(jsonObject)
                .setTag(this)
                .setPriority(Priority.HIGH)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(final JSONObject jsonObject) {
                        HyperLog.deleteLogs();
                    }

                    @Override
                    public void onError(ANError anError) {
//                        Toast.makeText(ATSApplication.this, "ERROR :  "+anError.getErrorBody(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
