package com.lightingcontour.toucher;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.provider.Settings;
import android.text.TextUtils;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

/**
 * created by peanut
 * on 2019/10/11
 */
public class LauncherManager {
    PackageManager mPackageManager;
    ActivityManager mActivityManager;

    LauncherManager(PackageManager packageManager, ActivityManager activityManager) {
        mPackageManager = packageManager;
        mActivityManager = activityManager;
    }

    public List<LauncherInfo> getLauncherInfos() {
        List<ResolveInfo> rList = mPackageManager.queryIntentActivities(getHomeIntent(), 0);
        List<LauncherInfo> launcherInfoList = new ArrayList<>();

        for (ResolveInfo r : rList) {
            LauncherInfo launcherInfo = new LauncherInfo();
            launcherInfo.name = r.activityInfo.name;
            launcherInfo.packageName = r.activityInfo.packageName;
            launcherInfo.label = r.loadLabel(mPackageManager).toString();
            launcherInfo.icon = r.loadIcon(mPackageManager);
            launcherInfoList.add(launcherInfo);
        }
        return launcherInfoList;
    }



    public Intent getHomeIntent() {
        Intent homeIntent = new Intent(Intent.ACTION_MAIN);
        homeIntent.addCategory(Intent.CATEGORY_HOME);
        homeIntent.addCategory(Intent.CATEGORY_DEFAULT);
        homeIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        return homeIntent;
    }

    public Intent getHomeIntent(String pkgName) {
        return getHomeIntent().setPackage(pkgName);
    }

//    public void switchHome(Context context, String pkgName) {
//        context.startActivity(getHomeIntent(pkgName));
//    }

    public void switchHome(Context context, String pkgName, String activityName) {
        Intent intent = new Intent();
        intent.setClassName(pkgName, activityName);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    public void setDefaultLauncher(Context context, String defPackageName, String defClassName) {
        try {
            if (!TextUtils.isEmpty(defPackageName) && !TextUtils.isEmpty(defClassName)) {
                IntentFilter filter = new IntentFilter();
                filter.addAction("android.intent.action.MAIN");
                filter.addCategory("android.intent.category.HOME");
                filter.addCategory("android.intent.category.DEFAULT");
                Intent intent = new Intent(Intent.ACTION_MAIN);
                intent.addCategory(Intent.CATEGORY_HOME);
                List<ResolveInfo> list = context.getPackageManager().queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
                int bestMatch = 0;
                final int size = list.size();
                TLog.e("size", size);
                ComponentName[] set = new ComponentName[size];
                for (int i = 0; i < size; i++) {
                    ResolveInfo ri = list.get(i);
                    set[i] = new ComponentName(ri.activityInfo.packageName, ri.activityInfo.name);
                    if (ri.match > bestMatch) {
                        bestMatch = ri.match;
                    }
                }

                ComponentName preActivity = new ComponentName(defPackageName, defClassName);
                context.getPackageManager().addPreferredActivity(filter, bestMatch, set, preActivity);
            }
        } catch (java.lang.SecurityException e) {
            e.printStackTrace();
        }
    }

    public void clearDefaultLauncher(Context context) {
        try {
            ArrayList<IntentFilter> intentList = new ArrayList<IntentFilter>();
            ArrayList<ComponentName> cnList = new ArrayList<ComponentName>();
            context.getPackageManager().getPreferredActivities(intentList, cnList, null);
            TLog.e("cnlist size", cnList.size());
            for (int i = 0; i < cnList.size(); i++) {
                IntentFilter dhIF = intentList.get(i);
                if (dhIF.hasAction(Intent.ACTION_MAIN) && dhIF.hasCategory(Intent.CATEGORY_HOME)) {
                    context.getPackageManager().clearPackagePreferredActivities(cnList.get(i).getPackageName());
                }
            }
        } catch (java.lang.SecurityException e) {
            e.printStackTrace();
        }
    }

    public boolean isDefaultLauncher(Context context, String activityName) {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory("android.intent.category.HOME");
        intent.addCategory("android.intent.category.DEFAULT");
        PackageManager pm = context.getPackageManager();
        ResolveInfo info = pm.resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY);
        boolean isDefault = activityName.equals(info.activityInfo.name);
        TLog.e("isDefault", isDefault);
        return isDefault;
    }

    public boolean intentFilterInstall(String packageName) {
        Intent newIntent = new Intent();
        newIntent.setAction(Intent.ACTION_MAIN);
        newIntent.addCategory(Intent.CATEGORY_HOME);
        newIntent.setPackage(packageName);
        ResolveInfo resolveInfo = mPackageManager.resolveActivity(newIntent, PackageManager.MATCH_ALL);
        String s = resolveInfo != null ? "resolve is exit" : "resolve is null";
        TLog.e("resloveInfo", s);
        return (resolveInfo != null);
    }

    public boolean intentFilterUnInstall(String packageName) {
        List<LauncherInfo> infoList = getLauncherInfos();
        for (LauncherInfo launcherInfo : infoList) {
            if (launcherInfo.packageName.equals(packageName)) return true;
        }
        return false;
    }

    public void saveDefaultLauncher(Context context, String packageName, String className) {
        SharedPreferences preferences = context.getSharedPreferences("launcherData", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("packageName", packageName);
        editor.putString("className", className);
        editor.commit();
    }

    public static final int INSTALL = 0;
    public static final int UNINSTALL = 1;

    public void keepDefaultLauncher(Context context, int flag, String cPackageName) {//todo
        SharedPreferences preferences = context.getSharedPreferences("launcherData", Context.MODE_PRIVATE);
        String packageName = preferences.getString("packageName", null);
        String className = preferences.getString("className", null);
        if (packageName == null || className == null) {
            Toast.makeText(context, "not use service to save launcherInfo", Toast.LENGTH_SHORT).show();
            return;
        }
        if (flag == INSTALL || !packageName.equals(cPackageName)) {
            if (!isDefaultLauncher(context, packageName)) clearDefaultLauncher(context);
            setDefaultLauncher(context, packageName, className);
            saveDefaultLauncher(context, packageName, className);
        } else {
            List<LauncherInfo> list = getLauncherInfos();
            clearDefaultLauncher(context);
            setDefaultLauncher(context, list.get(0).packageName, list.get(0).name);
            saveDefaultLauncher(context, list.get(0).packageName, list.get(0).name);
            TLog.e("ListPackageName:", list.get(0).packageName, "  ListclassName:", list.get(0).name);
        }
    }

    public void getDefaultLauncherTest(Context context) {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory("android.intent.category.HOME");
        intent.addCategory("android.intent.category.DEFAULT");
        PackageManager pm = context.getPackageManager();
        ResolveInfo info = pm.resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY);
        Toast.makeText(context, "DefaultLauncher pkg:" + info.activityInfo.packageName, Toast.LENGTH_SHORT).show();
    }
}
