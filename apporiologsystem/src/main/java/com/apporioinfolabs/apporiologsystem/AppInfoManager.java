package com.apporioinfolabs.apporiologsystem;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;

import org.json.JSONArray;
import org.json.JSONObject;

public class AppInfoManager {
    public final static String TAG = "AppInfoManager";



    public static JSONObject getApplicafionInfo (){
        JSONObject jsonObject = new JSONObject();
        PackageManager pm = ApporioLogsApplication.mContext.getPackageManager() ;
        String packagename = ApporioLogsApplication.mContext.getApplicationContext().getPackageName();
        try{

            jsonObject.put("package_name", ""+packagename);
            jsonObject.put("app_name", ""+(String) pm.getApplicationLabel(pm.getApplicationInfo(packagename, PackageManager.GET_META_DATA)));
            jsonObject.put("permissions", getPermissionWithStatus());

        }catch (Exception e){
//            APPORIOLOGS.errorLog(TAG , ""+e.getMessage());
        }


        return jsonObject ;

    }

    public static String getPackageName (){
        return ""+ApporioLogsApplication.mContext.getApplicationContext().getPackageName();
    }

    public static String getAppName() {
        try{
            PackageManager pm = ApporioLogsApplication.mContext.getPackageManager() ;
            String packagename = ApporioLogsApplication.mContext.getApplicationContext().getPackageName();
            return ""+(String) pm.getApplicationLabel(pm.getApplicationInfo(packagename, PackageManager.GET_META_DATA));
        }catch (Exception e ){
            // dont want to upload this as log
            return "NA";
        }

    }




    public static Drawable getApplicationLogo() throws Exception{
        PackageManager pm = ApporioLogsApplication.mContext.getPackageManager() ;
        String packagename = ApporioLogsApplication.mContext.getApplicationContext().getPackageName();
        return  pm.getApplicationLogo(pm.getApplicationInfo(packagename, PackageManager.GET_META_DATA));
    }

    public static JSONArray getPermissionWithStatus(){

        JSONArray permissionsarray = new JSONArray();
        try{
            String packagename = ApporioLogsApplication.mContext.getApplicationContext().getPackageName();
            PackageInfo packageInfo = ApporioLogsApplication.mContext.getPackageManager().getPackageInfo(packagename,PackageManager.GET_PERMISSIONS);
            for(int i = 0 ; i < packageInfo.requestedPermissions.length ; i++){
                permissionsarray.put(new JSONObject().put("name",packageInfo.requestedPermissions[i]).put("status",hasPermission(packageInfo.requestedPermissions[i])));

            }
        }catch (Exception e){
//            APPORIOLOGS.errorLog(TAG , ""+e.getMessage());
        }
        return permissionsarray ;
    }


    private static boolean hasPermission(String permission) {
        int res = ApporioLogsApplication.mContext.checkCallingOrSelfPermission(permission);
        return (res == PackageManager.PERMISSION_GRANTED);
    }


}