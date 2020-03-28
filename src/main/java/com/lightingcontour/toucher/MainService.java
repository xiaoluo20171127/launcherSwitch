package com.lightingcontour.toucher;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.IBinder;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.lightingcontour.toucher.adapter.QuickAdapter;
import com.lightingcontour.toucher.adapter.VHolder;

import java.util.ArrayList;
import java.util.List;

public class MainService extends Service {

    private static final String TAG = "MainService";
    ConstraintLayout mToucherLayout;
    WindowManager.LayoutParams params;
    WindowManager windowManager;
    RecyclerView mRecyclerView;
    QuickAdapter<LauncherInfo> mAdapter;
    List<LauncherInfo> mInfoList = new ArrayList<>();
    LauncherManager launcherManager;
    IntentFilter mIntentFilter;
    PackageChangeReceiver mReveiver;
    PackageChangeListener mListener;
    Button controlBtnList;


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG, "MainService Created");
        createTouch();
    }

    @SuppressLint("ClickableViewAccessibility")
    private void createTouch() {

        initBoradCast();
        initManager();
        initList();
        initFloatView();
        initRecycleView();
        launcherManager.getDefaultLauncherTest(this);
    }

    public void initBoradCast() {
        mListener = new PackageChangeListener() {
            @Override
            public void packagechange(String packageName,int flag) {
                if(flag==LauncherManager.INSTALL &&!launcherManager.intentFilterInstall(packageName))return;
                if(flag==LauncherManager.UNINSTALL&&!launcherManager.intentFilterUnInstall(packageName)) return;
                initList();
                mAdapter.setNewData(mInfoList);
            }
            @Override
            public void uninstall(String packageName) {
                Toast.makeText(getApplicationContext(),"uninstall is apply",Toast.LENGTH_SHORT).show();
                launcherManager.keepDefaultLauncher(getApplicationContext(), LauncherManager.UNINSTALL, packageName);
            }
            @Override
            public void install(String packageName) {
                launcherManager.keepDefaultLauncher(getApplicationContext(), LauncherManager.INSTALL, packageName);
            }
        };
        mIntentFilter = new IntentFilter();
        mIntentFilter.addAction("android.intent.action.PACKAGE_ADDED");
        mIntentFilter.addAction("android.intent.action.PACKAGE_REMOVED");
        mIntentFilter.addDataScheme("package");
        mReveiver = new PackageChangeReceiver(mListener);
        registerReceiver(mReveiver, mIntentFilter);
    }

    public void initManager() {
        PackageManager pm = getPackageManager();
        ActivityManager am = (ActivityManager) this.getSystemService(Context.ACTIVITY_SERVICE);
        launcherManager = new LauncherManager(pm, am);
    }

    public void initList() {
        mInfoList = launcherManager.getLauncherInfos();
    }

    public void initFloatView() {
        params = new WindowManager.LayoutParams();
        windowManager = (WindowManager) getApplication().getSystemService(Context.WINDOW_SERVICE);
        //设置type.系统提示型窗口，一般都在应用程序窗口之上.
        //Android8.0行为变更，对8.0进行适配https://developer.android.google.cn/about/versions/oreo/android-8.0-changes#o-apps
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            params.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        } else {
            params.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
        }
        params.format = PixelFormat.RGBA_8888;
        params.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        params.gravity = Gravity.LEFT | Gravity.TOP;
        params.x = 700;
        params.y = 0;
        params.width = 200;
        params.height = 50;
    }

    public void initRecycleView() {
        LayoutInflater inflater = LayoutInflater.from(getApplication());
        mToucherLayout = (ConstraintLayout) inflater.inflate(R.layout.toucherlayout, null);
        windowManager.addView(mToucherLayout, params);
        mToucherLayout.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        mRecyclerView = mToucherLayout.findViewById(R.id.recyclerView);
        controlBtnList = mToucherLayout.findViewById(R.id.Button);
        controlBtnList.setOnClickListener((v) -> {
            if (mRecyclerView.getVisibility() == View.GONE) {
                mRecyclerView.setVisibility(View.VISIBLE);
                params.height = 800;
            } else {
                mRecyclerView.setVisibility(View.GONE);
                params.height = 60;
            }
            windowManager.updateViewLayout(mToucherLayout, params);
        });
        final LinearLayoutManager layoutManager = new LinearLayoutManager(mRecyclerView.getContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(layoutManager);
        mAdapter = new QuickAdapter<LauncherInfo>(R.layout.launcher_item) {
            @Override
            protected void convert(final VHolder holder, final LauncherInfo launcherInfo) {
                if (launcherInfo.icon != null)
                    ((ImageView) holder.getView(R.id.imageView)).setImageDrawable(launcherInfo.icon);
                ((TextView) holder.getView(R.id.textView)).setText(launcherInfo.packageName);
                holder.itemView.setOnClickListener(v -> {
                    if (!launcherManager.isDefaultLauncher(getApplication(), launcherInfo.name)) {
                        launcherManager.clearDefaultLauncher(getApplication());
                    }
                    launcherManager.setDefaultLauncher(getApplication(), launcherInfo.packageName, launcherInfo.name);
                    Log.e("demo", "package:" + launcherInfo.packageName + "    name:" + launcherInfo.name);
                    launcherManager.switchHome(getApplication(), launcherInfo.packageName,launcherInfo.name);
                  //  launcherManager.saveDefaultLauncher(getApplicationContext(), launcherInfo.packageName, launcherInfo.name);
                });
            }
        };
        mRecyclerView.setAdapter(mAdapter);
        TLog.e("launcherSize", mInfoList.size());
        mAdapter.setNewData(mInfoList);
    }


    @Override
    public void onDestroy() {
        if (mRecyclerView != null) {
            windowManager.removeView(mToucherLayout);
        }
        unregisterReceiver(mReveiver);
        super.onDestroy();
    }
}
