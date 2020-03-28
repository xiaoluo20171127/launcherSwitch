package com.lightingcontour.toucher;


import android.graphics.drawable.Drawable;

public class LauncherInfo {
	public String name;
	public String packageName;
	public String label;
	public Drawable icon;

	LauncherInfo(){

	}

	LauncherInfo(String packageName){
		this.packageName = packageName;
	}
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPackageName() {
		return packageName;
	}

	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public Drawable getIcon() {
		return icon;
	}

	public void setIcon(Drawable icon) {
		this.icon = icon;
	}

}
