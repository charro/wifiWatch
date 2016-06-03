package com.riverdevs.whosonmywifi.dao;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.riverdevs.whosonmywifi.beans.DetectedConnection;
import com.riverdevs.whosonmywifi.beans.Host;

public class DataManager {

	public static DBOpenHelper dbHelper;
	
	public static void initialize(Context context){
		dbHelper = new DBOpenHelper(context);
	}
	
	public static void closeConnection(){
		dbHelper.getWritableDatabase().close();
	}
	
	public static List<Host> getHosts(String select, String[] selectArgs){
		ArrayList<Host> hosts = new ArrayList<Host>();
		
		String[] columnNames = {DBOpenHelper.KEY_MAC, 
				DBOpenHelper.KEY_NAME, DBOpenHelper.KEY_NETWORK_ID,
				DBOpenHelper.KEY_HOSTNAME};
		
		Cursor cursor = dbHelper.getReadableDatabase().
			query(false, DBOpenHelper.HOSTS_TABLE_NAME,
					columnNames, select, 
					selectArgs, null, 
					null, null, 
					null);
		
		while(cursor.moveToNext()){
			Host host = new Host(cursor.getString(0), 
					cursor.getString(1),
					cursor.getInt(2),
					cursor.getString(3));
			hosts.add(host);
		}
		
		return hosts;
	}

	public static Host getHost(String mac){
		if(mac != null){
			String[] args = {mac};
			
			List<Host> hosts = getHosts(DBOpenHelper.KEY_MAC+"=?", args);
			if(hosts != null && !hosts.isEmpty()){
				return hosts.get(0);
			}
		}
		
		return null;
	}
	
	public static long insertUpdateHost(Host host){
		ContentValues values = new ContentValues();

		values.put(DBOpenHelper.KEY_NAME, host.getName());
		values.put(DBOpenHelper.KEY_NETWORK_ID, host.getNetworkId());
		values.put(DBOpenHelper.KEY_HOSTNAME, host.getHostname());
		
		Host existingHost = getHost(host.getMac());
		// New category
		if(existingHost == null){
			values.put(DBOpenHelper.KEY_MAC, host.getMac());
			return dbHelper.getWritableDatabase().
					insert(DBOpenHelper.HOSTS_TABLE_NAME, null, values);			
		}
		else{
			String[] args = {host.getMac()};
			
			return dbHelper.getWritableDatabase().
					update(DBOpenHelper.HOSTS_TABLE_NAME, values, DBOpenHelper.KEY_MAC+"=?", args);
		}

	}

	public static String getManufacturerName(String macIndex){
		if(macIndex != null){
			String[] args = {macIndex};
			
			String[] columnNames = {DBOpenHelper.KEY_MAC_INDEX, 
					DBOpenHelper.KEY_MANUFACTURER};
			
			Cursor cursor = dbHelper.getReadableDatabase().
				query(false, DBOpenHelper.MANUFACTURER_TABLE_NAME,
						columnNames, DBOpenHelper.KEY_MAC_INDEX+"=?", 
						args, null, 
						null, null, 
						null);
			
			if(cursor.moveToNext()){
				return cursor.getString(1);
			}
			else{
				// Not found in DB
				return null;
			}
		}
		else{
			return null;
		}
	}
	
	public static long insertUpdateManufacturer(String macIndex, String name){
		ContentValues values = new ContentValues();
		values.put(DBOpenHelper.KEY_MAC_INDEX, macIndex);
		values.put(DBOpenHelper.KEY_MANUFACTURER, name);
		
		String manufacturerName = getManufacturerName(macIndex);
		// New manufacturer
		if(manufacturerName == null){
			return dbHelper.getWritableDatabase().
					insert(DBOpenHelper.MANUFACTURER_TABLE_NAME, null, values);			
		}
		else{
			String[] args = {macIndex};
			
			return dbHelper.getWritableDatabase().
					update(DBOpenHelper.MANUFACTURER_TABLE_NAME, values, 
							DBOpenHelper.KEY_MAC_INDEX+"=?", args);
		}
	}
	
	public static long deleteHost(Host host){
		String[] args = {String.valueOf(host.getMac())};

		// Delete the category itself
		return dbHelper.getWritableDatabase().
			delete(DBOpenHelper.HOSTS_TABLE_NAME, DBOpenHelper.KEY_MAC+"=?", args);
	}

	public static String getGivenNameFromMAC(String mac){
		Host host = DataManager.getHost(mac);
		if(host != null && host.getName()!=null){
			return host.getName();
		}
		else{
			return null;
		}
	}
	
	/*************************  Detected connections history *****************************/
	public static List<DetectedConnection> getAllDetectedConnections(){
		return getDetectedConnections(null, null);
	}
	
	public static List<DetectedConnection> getDetectedConnections(String select, 
			String[] selectArgs){
		ArrayList<DetectedConnection> detectedList = new ArrayList<DetectedConnection>();
		
		String[] columnNames = {DBOpenHelper.KEY_TIMESTAMP, DBOpenHelper.KEY_IP,
				DBOpenHelper.KEY_MAC, DBOpenHelper.KEY_MANUFACTURER};
		
		Cursor cursor = dbHelper.getReadableDatabase().
			query(false, DBOpenHelper.DETECTED_TABLE_NAME,
					columnNames, select, 
					selectArgs, null, 
					null,
					DBOpenHelper.KEY_TIMESTAMP + " DESC", 
					null);
		
		while(cursor.moveToNext()){
			DetectedConnection detected = new DetectedConnection(cursor.getLong(0), 
					cursor.getString(1),
					cursor.getString(2),
					cursor.getString(3));
			detectedList.add(detected);
		}
		
		return detectedList;
	}
	
	public static void insertDetectedConnection(DetectedConnection connection){
		ContentValues values = new ContentValues();

		values.put(DBOpenHelper.KEY_TIMESTAMP, connection.getTimestamp());
		values.put(DBOpenHelper.KEY_IP, connection.getIp());
		values.put(DBOpenHelper.KEY_MAC, connection.getMac());
		values.put(DBOpenHelper.KEY_MANUFACTURER, connection.getManufacturer());
		
		dbHelper.getWritableDatabase().
				insert(DBOpenHelper.DETECTED_TABLE_NAME, null, values);			
	}
	
	public static void deleteAllDetectedConnection(){
		dbHelper.getWritableDatabase().delete(DBOpenHelper.DETECTED_TABLE_NAME, null, null);
	}
	
//	public static boolean fillManufacturerTable(){
//		AssetManager assetManager = dbHelper.getContext().getAssets();
//		try {
//			InputStream ims = assetManager.open("manufacturers.txt");
//			BufferedReader br = new BufferedReader(new InputStreamReader(ims));
//			while(br.ready()){
//				String line = br.readLine();
//				String[] chunks = line.split(";");
//				if(chunks!=null && chunks.length==2){
//					insertUpdateManufacturer(chunks[0], chunks[1]);
//				}
//			}
//			
//			ims.close();
//			return true;
//		} catch (IOException e) {
//			e.printStackTrace();
//			return false;
//		}
//	}
}
