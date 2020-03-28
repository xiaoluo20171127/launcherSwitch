package com.lightingcontour.toucher;

import android.content.Context;

public interface PackageChangeListener {
    void packagechange(String packageName , int flag);
    void uninstall(String packageName);
    void install(String packageName);
}
