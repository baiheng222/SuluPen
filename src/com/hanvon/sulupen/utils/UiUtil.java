package com.hanvon.sulupen.utils;

import android.content.Context;
import android.view.Gravity;
import android.widget.Toast;

public class UiUtil {
	public static void showToast(Context context, String message) {
		Toast toast = Toast.makeText(context, message, Toast.LENGTH_SHORT);
		toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
		toast.show();
	}
}
