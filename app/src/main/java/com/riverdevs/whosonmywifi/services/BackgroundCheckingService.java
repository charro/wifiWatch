package com.riverdevs.whosonmywifi.services;

import android.app.IntentService;
import android.app.NotificationManager;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationCompat.Builder;

// FIXME: DELETE THIS CLASS, IS NOT USED !!

public class BackgroundCheckingService extends IntentService {

	public BackgroundCheckingService(String name) {
		super(name);
	}

	@Override
	protected void onHandleIntent(Intent intent) {
//		String dataString = intent.getDataString();
		NotificationCompat.Builder builder = new Builder(this.getApplication().getApplicationContext());
		builder.setContentTitle("Background Checking").setContentText("Device connected !!");
		NotificationManager mNotifyMgr = 
		        (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		// Builds the notification and issues it.
		mNotifyMgr.notify(1, builder.build());
		
		while(true){
			// Check for new connected devices
			System.out.println("New DEVICE FOUND");
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
