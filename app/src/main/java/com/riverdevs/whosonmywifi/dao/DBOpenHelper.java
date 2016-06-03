package com.riverdevs.whosonmywifi.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBOpenHelper extends SQLiteOpenHelper {
    
	private Context context;
	
	public static final String DATABASE_NAME = "whosOnMyWifi";
	public static final int DATABASE_VERSION = 3;
	
	// Hosts table fields
	public static final String KEY_MAC = "mac";
	public static final String KEY_NAME = "name";
	public static final String KEY_NETWORK_ID = "network_id";
	public static final String KEY_HOSTNAME = "hostname";

	// Mac table fields
	public static final String KEY_MAC_INDEX = "mac_index";
	public static final String KEY_MANUFACTURER = "manufacturer";
	
	// Detected table fields
	public static final String KEY_DETECTED_INDEX = "detected_index";
	public static final String KEY_TIMESTAMP = "timestamp";
	public static final String KEY_IP = "ip";
	
	// Table names
	public static final String HOSTS_TABLE_NAME = "hosts";
	public static final String MANUFACTURER_TABLE_NAME = "manufacturers";
	public static final String DETECTED_TABLE_NAME = "detected_history";
	
	public static final String HOSTS_TABLE_CREATE_SQL =
                "CREATE TABLE " + HOSTS_TABLE_NAME + " (" +
                		KEY_MAC + " TEXT PRIMARY KEY, " +
                		KEY_NAME + " TEXT, " +
                		KEY_NETWORK_ID + " INTEGER, " +
                		KEY_HOSTNAME + " TEXT );";
	
	public static final String MANUFACTURER_TABLE_CREATE_SQL =
            "CREATE TABLE " + MANUFACTURER_TABLE_NAME + " (" +
            		KEY_MAC_INDEX + " TEXT PRIMARY KEY, " +
            		KEY_MANUFACTURER + " TEXT );";
	
	public static final String DETECTED_TABLE_CREATE_SQL =
            "CREATE TABLE " + DETECTED_TABLE_NAME + " (" +
            		KEY_DETECTED_INDEX + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            		KEY_TIMESTAMP + " INTEGER, " +
            		KEY_IP + " TEXT, " +
            		KEY_MAC + " TEXT, " +
            		KEY_MANUFACTURER + " TEXT );";
	
	public DBOpenHelper(Context context) {
		 super(context, DATABASE_NAME, null, DATABASE_VERSION);
		 this.context = context;
	}

	public Context getContext(){
		return this.context;
	}
	
	@Override
	public void onCreate(SQLiteDatabase db) {
		// Version 1
		db.execSQL(HOSTS_TABLE_CREATE_SQL);
		// Version 2
		db.execSQL(MANUFACTURER_TABLE_CREATE_SQL);
		// Version 3
		db.execSQL(DETECTED_TABLE_CREATE_SQL);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		   switch(oldVersion) {
			   case 1:
					db.execSQL(MANUFACTURER_TABLE_CREATE_SQL);
			   case 2:
				    db.execSQL(DETECTED_TABLE_CREATE_SQL);
		   }
	}
}
