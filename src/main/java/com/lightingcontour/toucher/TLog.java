package com.lightingcontour.toucher;

import android.util.Log;


public class TLog {
	public static final String LOG_TAG = "tag";
	public static final boolean Debug = BuildConfig.DEBUG;

	public static final void v(Object... args) {
		println(Log.VERBOSE, args);
	}

	public static final void d(Object... args) {
		println(Log.DEBUG, args);
	}

	public static final void i(Object... args) {
		println(Log.INFO, args);
	}

	public static final void w(Object... args) {
		println(Log.WARN, args);
	}

	public static final void e(Object... args) {
		println(Log.ERROR, args);
	}

	public static String getLineNumber() {
		StackTraceElement[] trace = new Throwable().fillInStackTrace().getStackTrace();
		String caller = "<unknown>";
		for (int i = 3; i < trace.length; i++) {
			return trace[i].getClassName() + "  lineNumber:" + trace[i].getLineNumber() + " "
					+ trace[i].getMethodName();
		}
		return "";
	}

	private static int println(int priority, Object... msgs) {

		if (msgs.length == 0) {
			return Log.println(priority, LOG_TAG,getLineNumber());
		} else if (msgs.length == 1) {
			return Log.println(priority, LOG_TAG, msgs[0].toString());
		}
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < msgs.length; i++) {
			sb.append(msgs[i]);
			sb.append(" ");
		}
		return Log.println(priority,LOG_TAG, sb.toString());
	}
}
