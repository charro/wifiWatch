package com.riverdevs.whosonmywifi.utils;

import java.io.File;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Build;

public class Utils {

	public static AlertDialog createErrorDialog(final Activity activity, String title, 
			String message, final boolean finishActivity){
		AlertDialog.Builder builder = new AlertDialog.Builder(activity);
		builder.setIcon(android.R.drawable.ic_dialog_alert);
		builder.setTitle(title);
		builder.setMessage(message);
		builder.setCancelable(false);
		builder.setPositiveButton("OK",new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog,int id) {
				if(finishActivity){
					activity.finish();
				}
			}
		  });
		
		return builder.create();
	}

	public static String getApplicationFolder(final Context context, final String subfolder) {
		File appDir = null; 
		
		try {
			appDir = new File (getDataDirectory(context) + "/" + subfolder);
			
			// if it doesn't exist, create it
			if (!appDir.exists())
			{
				if (!appDir.mkdirs())
				{
					return null;
				}
			}
		}
		catch (Exception e)
		{
			appDir = new File ("/tmp/");
		}
		
		return appDir.getAbsolutePath() + "/";
	}
	
	private static String getDataDirectory(final Context context)
	{
		String dataDirectory = "";
		try
		{
			dataDirectory = context.getPackageManager().getApplicationInfo("com.riverdevs.whosinmywifi", 0).dataDir;
		}
		catch (NameNotFoundException e) {
			return null;
		}
		return dataDirectory;
	}

	public static boolean isEmulator() {
		return Build.FINGERPRINT.startsWith("generic")
				|| Build.FINGERPRINT.startsWith("unknown")
				|| Build.MODEL.contains("google_sdk")
				|| Build.MODEL.contains("Emulator")
				|| Build.MODEL.contains("Android SDK built for x86")
				|| Build.MANUFACTURER.contains("Genymotion")
				|| (Build.BRAND.startsWith("generic") && Build.DEVICE.startsWith("generic"))
				|| "google_sdk".equals(Build.PRODUCT);
	}
}
