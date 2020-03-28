package com.lightingcontour.toucher;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.widget.Toast;

public class PackageChangeReceiver extends BroadcastReceiver {
    PackageChangeListener mListener;
    String packageName;
    String[] ActionString = new String[]{"android.intent.action.PACKAGE_ADDED", "android.intent.action.PACKAGE_REMOVED"};

    public PackageChangeReceiver(PackageChangeListener listener) {
        mListener = listener;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(ActionString[0]) || intent.getAction().equals(ActionString[1])) {
            String dataString = intent.getDataString();
            if (dataString.contains("package:")) {
                packageName = dataString.replace("package:", "");
            }else{
                packageName = dataString;
            }
            TLog.e("packageName", packageName);


            if (intent.getAction().equals(ActionString[0])) {
                mListener.packagechange(packageName,LauncherManager.INSTALL);
                mListener.install(packageName);
            } else {
                mListener.packagechange(packageName,LauncherManager.UNINSTALL);
                mListener.uninstall(packageName);
            }
        }
    }



}
