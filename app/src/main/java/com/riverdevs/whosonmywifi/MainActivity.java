package com.riverdevs.whosonmywifi;

import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.AsyncTask.Status;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.riverdevs.whosinmywifi.R;
import com.riverdevs.whosonmywifi.beans.Host;
import com.riverdevs.whosonmywifi.beans.InfoToPassBetweenActivities;
import com.riverdevs.whosonmywifi.beans.PingResult;
import com.riverdevs.whosonmywifi.beans.WifiConnectionInfo;
import com.riverdevs.whosonmywifi.dao.DataManager;
import com.riverdevs.whosonmywifi.layout.FoundDevicesAdapter;
import com.riverdevs.whosonmywifi.utils.NetUtils;
import com.riverdevs.whosonmywifi.utils.SimpleEula;
import com.riverdevs.whosonmywifi.utils.Utils;

public class MainActivity extends Activity {

	private SharedPreferences preferences;
	private static FoundDevicesAdapter connectedDevicesAdapter;
	private ListView connectedDeviceList;
	private ProgressBar progressBar;
	private TextView startSearch;
	private CheckBox searchNotConnected;
	private Animation fadeInAnimation;
	private Animation fadeInFadeOutInfiniteLoopAnimation;
	private static MonitorConnectionThread monitorThread;
	
	private TextView networkNameTextView;
	private TextView myHostnameTextView;
	private TextView myIPTextView;
	private TextView gatewayTextView;
	private TextView myMACTextView;
	
	public int addressesLooked = 0;
	
	private WifiConnectionInfo myConnectionInfo;

	private BroadcastReceiver connectionReceiver;
	
//	private ProgressDialog installDialog;
	
	private PingTask pingTask = null;
	
//	private Scan scanner;
	
	private static class ConnectionHandler extends Handler {
		
		private Context context;
		
		public ConnectionHandler(Context context) {
			this.context = context;
		}
		
		@Override
		public void handleMessage(Message msg) {
			if(msg.what == MonitorConnectionThread.CONNECTED_CHANGED){
				PingResult modifiedResult = (PingResult) msg.obj;
			
				String who = 
						(modifiedResult.getGivenName() == null ?
								modifiedResult.getIp() :
								modifiedResult.getGivenName());
				Toast.makeText(context, 
						who + " " + 
						(modifiedResult.isNotCurrentlyConnected() ? 
								context.getString(R.string.disconnected) : 
									context.getString(R.string.connected)), 
						Toast.LENGTH_LONG).show();

				getConnectedDevicesAdapter().notifyDataSetChanged();
			}
			else if(msg.what == MonitorConnectionThread.MONITOR_FINISHED){
				Toast.makeText(context, context.getString(R.string.connection_checking_stopped), 
						Toast.LENGTH_LONG).show();
			}
			else if(msg.what == MonitorConnectionThread.NEW_CONNECTION_DETECTED){
				PingResult newDetected = (PingResult) msg.obj;
				getConnectedDevicesAdapter().add(newDetected);
				getMonitorThread().setNewResultList(getConnectedDevicesAdapter().getItemsList());
				getConnectedDevicesAdapter().notifyDataSetChanged();
				
				String who = 
						(newDetected.getGivenName() == null ?
								newDetected.getIp() :
									newDetected.getGivenName());
				Toast.makeText(context, 
						who + " " + context.getString(R.string.connected), 
						Toast.LENGTH_LONG).show();
			}
		}
	};
	
	private ConnectionHandler handler;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		new SimpleEula(this).show();
		
		// Open DB connection
		DataManager.initialize(this);
		
		PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
		preferences = PreferenceManager
				.getDefaultSharedPreferences(this);
		NetUtils.preferences = getPreferences();
		NetUtils.unknownString = getString(R.string.unknown);
		
		init();

		MobileAds.initialize(getApplicationContext(), "ca-app-pub-7348866844562014/5306510085");

		AdView mAdView = (AdView) findViewById(R.id.adView);
		AdRequest adRequest = new AdRequest.Builder().build();
		mAdView.loadAd(adRequest);
	}
	
	public void init(){
		// Get all views
		progressBar = (ProgressBar) findViewById(R.id.lookUpProgressBar);
		startSearch = (TextView) findViewById(R.id.startSearchButton);
		connectedDeviceList = (ListView) findViewById(R.id.connectedDeviceList);
		searchNotConnected = (CheckBox) findViewById(R.id.searchNotConnectedCheckBox);
		networkNameTextView = (TextView) findViewById(R.id.networkNameTextView);
		myHostnameTextView = (TextView) findViewById(R.id.myHostnameTextView);
		myIPTextView = (TextView) findViewById(R.id.myIPTextView);
		gatewayTextView = (TextView) findViewById(R.id.gatewayIPTextView);
		myMACTextView = (TextView) findViewById(R.id.myMACTextView);
		
		fadeInAnimation = AnimationUtils.loadAnimation(this, R.animator.fade_in);
		
		fadeInFadeOutInfiniteLoopAnimation = AnimationUtils.loadAnimation(this, R.animator.fade_in_fade_out);
		fadeInFadeOutInfiniteLoopAnimation.setRepeatCount(-1);
		fadeInFadeOutInfiniteLoopAnimation.setRepeatMode(2);
		
		connectedDeviceList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				PingResult item = getConnectedDevicesAdapter().getItem(position);
				
				Intent intent = new Intent(MainActivity.this, PopUpActivity.class);
				intent.putExtra("selectedIP", item.getIp());
				intent.putExtra("myIP", myConnectionInfo.getMyIp());
				intent.putExtra("gatewayIP", myConnectionInfo.getGatewayIp());
				intent.putExtra("selectedHostname", item.getHostname());
				intent.putExtra("selectedMac", item.getMacAddress());
				intent.putExtra("selectedGivenName", item.getGivenName());
				
				startActivity(intent);
			}
		});
		
		registerForContextMenu(connectedDeviceList);
		
		findViewById(R.id.refreshResultsView).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				cleanAndShowStartSearchLayout();
			}
		});
		
		startSearch.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if (NetUtils.isWifiConnected(MainActivity.this)) {
					progressBar.setProgress(0);
					findViewById(R.id.searchResultLayout).setVisibility(View.VISIBLE);
					findViewById(R.id.startSearchContainerLayout).setVisibility(View.GONE);
					findViewById(R.id.refreshResultsView).setVisibility(View.GONE);
					findViewById(R.id.searchProgressContainerLayout).setVisibility(View.VISIBLE);
					
					setConnectedDevicesAdapter(new FoundDevicesAdapter(MainActivity.this));
					connectedDeviceList.setAdapter(getConnectedDevicesAdapter());
					
					// Start pinging all
					pingTask = 
							(PingTask) (new PingTask(MainActivity.this)).execute(searchNotConnected.isChecked());
					
					ImageView cancelSearchButton = (ImageView) findViewById(R.id.cancelSearchImage);
					cancelSearchButton.setOnClickListener(new View.OnClickListener() {
						
						@Override
						public void onClick(View v) {
							cancelSearch();
//							endSearch(null);
							endSearch(true);
						}
					});
				}
				else{
					Utils.createErrorDialog(MainActivity.this, "", 
							getString(R.string.error_no_connected_title) +
							getString(R.string.error_no_connected_text), false).show();
				}
			}
		});
		
		handler = new ConnectionHandler(this);
		
		// Register the Wifi connection receiver to check Wifi connections/disconnections
		IntentFilter filter = new IntentFilter();
		filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);

		connectionReceiver = new BroadcastReceiver() {
		    @Override
		    public void onReceive(Context context, Intent intent) {     
		        ConnectivityManager conMan = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE); 
		        NetworkInfo netInfo = conMan.getActiveNetworkInfo();
		        if (netInfo != null && netInfo.getType() == ConnectivityManager.TYPE_WIFI){ 
		            // Wifi connected again
					(new GetMyWifiInfoTask()).execute();
		        }
		        else{
		            // Wifi disconnected
		            cleanMyData();
		        }    
		    }
		};
		
		registerReceiver(connectionReceiver, filter);

		// Check if installation done. Proceed with installation otherwise
//		if(getPreferences().getBoolean(SettingsActivity.KEY_PREF_INSTALLATION_DONE, false) == false){
//			(new InstallTask()).execute();
//		}
//		else{
			checkInfoBetweenActivities();
//		}
	}
	
	public void checkInfoBetweenActivities(){
		InfoToPassBetweenActivities info = (InfoToPassBetweenActivities)getLastNonConfigurationInstance();
		
		if(info!=null){

			// Set saved Wifi Data
			if(info.getWifiConnectionInfo() != null){
				updateMyData(info.getWifiConnectionInfo());
			}
			else{
				if(NetUtils.isWifiConnected(MainActivity.this)){
					(new GetMyWifiInfoTask()).execute();
				}
			}
			
			// Set saved search data from last Activity
			if(info.getListAdapter() != null){
				findViewById(R.id.searchResultLayout).setVisibility(View.VISIBLE);
				findViewById(R.id.startSearchContainerLayout).setVisibility(View.GONE);
				
				setConnectedDevicesAdapter(info.getListAdapter());
				connectedDeviceList.setAdapter(getConnectedDevicesAdapter());
				getConnectedDevicesAdapter().notifyDataSetChanged();
				
				addressesLooked = info.getAddressesLooked();
				progressBar.incrementProgressBy((addressesLooked/8)*3);
				
				if(info.getExecutingPingTask() != null){
					pingTask = info.getExecutingPingTask(); 
					pingTask.attach(this);
					
					if(info.getExecutingPingTask().getStatus() == Status.RUNNING){
						findViewById(R.id.refreshResultsView).setVisibility(View.GONE);
						findViewById(R.id.searchProgressContainerLayout).setVisibility(View.VISIBLE);	
					}
					else{
						findViewById(R.id.refreshResultsView).setVisibility(View.VISIBLE);
						findViewById(R.id.searchProgressContainerLayout).setVisibility(View.GONE);
					}			
				}
				
			}
			
			if(info.getMonitorThread() != null){
				monitorThread = info.getMonitorThread();
				if(monitorThread==null || monitorThread.stopFlag){
					hideBusyBar();
				}
				else{
					showBusyBar();
				}
			}
		}

	}
	
//	public void installStarted() {
//		installDialog = new ProgressDialog(this);
//		installDialog.setTitle(getString(R.string.configuring_app_title));
//		installDialog.setMessage(getString(R.string.configuring_app_text));
//		installDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
//				
//		installDialog.show();
//	}
//	
//	public void installFinished(Boolean result) {
//		SharedPreferences.Editor editor = getPreferences().edit();
//		editor.putBoolean(SettingsActivity.KEY_PREF_INSTALLATION_DONE, true);
//        editor.putBoolean(SettingsActivity.KEY_PREF_INSTALLATION_SUCCESSFUL, result);
//        editor.commit();
//        
//        if(installDialog != null && installDialog.isShowing()){
//        	installDialog.dismiss();
//        }
//        
//        checkInfoBetweenActivities();
//	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}
	
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
//	    MenuInflater inflater = getMenuInflater();
//	    inflater.inflate(R.menu.shortcut_context_menu, menu);
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) menuInfo;
		PingResult item = (PingResult) getConnectedDevicesAdapter().getItem(info.position);
		if(item.getMacAddress() == null || item.getMacAddress().length() < 1){
		    	Toast.makeText(this, R.string.error_no_mac_address_edit_name, 
		    			Toast.LENGTH_LONG).show();
		}
		else{
			showEditPopUp(item);
		}
	}
	
	@Override
	public Object onRetainNonConfigurationInstance() {
		weAreChangingOfScreenOrientation = true;
		
		if(pingTask != null){
			pingTask.detach();	
		}
	    
		InfoToPassBetweenActivities info = 
				new InfoToPassBetweenActivities(pingTask, myConnectionInfo, 
						addressesLooked, getConnectedDevicesAdapter(), monitorThread);
		
		return(info);
	}
	
	private void showEditPopUp(final PingResult pingResult){
		AlertDialog.Builder alert = new AlertDialog.Builder(this);

		alert.setTitle(getString(R.string.edit_device_name));

		// Set an EditText view to get user input 
		final EditText input = new EditText(this);
		if(pingResult.getGivenName() != null){
			input.setText(pingResult.getGivenName());	
		}
		else{
			input.setText(pingResult.getIp());
		}
		
		alert.setView(input);

		alert.setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
		public void onClick(DialogInterface dialog, int whichButton) {
			  String value = input.getText().toString();
			  
			  Host host = new Host(pingResult.getMacAddress(), 
					  value, myConnectionInfo.getNetworkId(),
					  pingResult.getHostname());
			  DataManager.insertUpdateHost(host);
			  
			  pingResult.setGivenName(value);
			  getConnectedDevicesAdapter().notifyDataSetChanged();
			}
		});

		alert.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
		  public void onClick(DialogInterface dialog, int whichButton) {
		    // Canceled.
		  }
		});

		alert.show();
	}
	
	private boolean weAreChangingOfScreenOrientation = false;
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		
		// Check if the app is really closing or we are just doing a change in its config
		if(!weAreChangingOfScreenOrientation){
			stopMonitorService();	
		}
		unregisterReceiver(connectionReceiver);
		DataManager.closeConnection();
	}
	
	private void stopMonitorService(){
		hideBusyBar();
		
		if(monitorThread != null){
			monitorThread.stopWork();
		}
	}
	
	private void cancelSearch(){
		if(pingTask != null && 
				pingTask.getStatus() == Status.RUNNING){
			pingTask.cancel(true);	
		}
	}
	
	private void cancelSearchAndCleanSearch(){
		pingTask = null;
		connectedDevicesAdapter = null;
		addressesLooked = 0;
		findViewById(R.id.startSearchContainerLayout).setVisibility(View.VISIBLE);		
		findViewById(R.id.searchResultLayout).setVisibility(View.GONE);
		findViewById(R.id.refreshResultsView).setVisibility(View.GONE);
	}
	
	private void startMonitorService(){
		stopMonitorService();
		
		if(getPreferences().getBoolean(SettingsActivity.KEY_PREF_AUTO_CONNECTION_CHECKING, false)){
			showBusyBar();
			
			final List<PingResult> results = getConnectedDevicesAdapter().getItemsList();
			
			String timeString = getPreferences().getString(SettingsActivity.KEY_PREF_TIME_TO_NEXT_CHECKING, "1000");
			
//			monitorThread = 
//					new MonitorConnectionThread(results, handler, Integer.valueOf(timeString), scanner);
			monitorThread = 
					new MonitorConnectionThread(this, results, handler, Integer.valueOf(timeString));
			monitorThread.start();
		}
	}
	
	private void showBusyBar(){
		findViewById(R.id.busyBar).setVisibility(View.VISIBLE);
		findViewById(R.id.busyBar).startAnimation(fadeInFadeOutInfiniteLoopAnimation);
	}
	
	private void hideBusyBar(){
		findViewById(R.id.busyBar).clearAnimation();
		findViewById(R.id.busyBar).setVisibility(View.GONE);		
	}
	
	private void updateMyData(WifiConnectionInfo connectionInfo){
		this.myConnectionInfo = connectionInfo;
		
		networkNameTextView.setText(myConnectionInfo.getWifiName());
		myHostnameTextView.setText(getString(R.string.my_name) + " " + 
				(myConnectionInfo.getHostname()==null ? 
						getString(R.string.unknown) :
						myConnectionInfo.getHostname()) );
		myIPTextView.setText(getString(R.string.my_ip) + " " + myConnectionInfo.getMyIp());
		gatewayTextView.setText(getString(R.string.gateway) + " " + myConnectionInfo.getGatewayIp());
		myMACTextView.setText(getString(R.string.my_mac) + " " + myConnectionInfo.getMac());
		
		findViewById(R.id.loadingWifiData).setVisibility(View.GONE);
		findViewById(R.id.noConnectedContainer).setVisibility(View.GONE);
		findViewById(R.id.searchContainerLayout).setVisibility(View.VISIBLE);
		View myAddressesContainer = findViewById(R.id.myAddressesDataContainer);
		myAddressesContainer.setVisibility(View.VISIBLE);
		myAddressesContainer.startAnimation(fadeInAnimation);		
	}
	
	// Usually called when disconnected from network
	private void cleanMyData(){
		myConnectionInfo = null;
		stopMonitorService();
		cancelSearchAndCleanSearch();
		findViewById(R.id.searchContainerLayout).setVisibility(View.GONE);
		
		findViewById(R.id.loadingWifiData).setVisibility(View.GONE);
		findViewById(R.id.myAddressesDataContainer).setVisibility(View.GONE);
		findViewById(R.id.noConnectedContainer).setVisibility(View.VISIBLE);
		
		networkNameTextView.setCompoundDrawablesWithIntrinsicBounds(
				0, R.drawable.wifi_router_disconnected, 0, 0);
		networkNameTextView.setText("");
	}
	
	private void showStartLookingMyData(){
		findViewById(R.id.loadingWifiData).setVisibility(View.VISIBLE);
		findViewById(R.id.myAddressesDataContainer).setVisibility(View.GONE);
		findViewById(R.id.noConnectedContainer).setVisibility(View.GONE);
		
		networkNameTextView.setCompoundDrawablesWithIntrinsicBounds(
				0, R.drawable.wifi_router, 0, 0);
	}
	
	private void cleanAndShowStartSearchLayout(){
		pingTask = null;
		addressesLooked = 0;
		connectedDevicesAdapter = null;
		
		stopMonitorService();
		
		View searchContainer = 
				findViewById(R.id.startSearchContainerLayout);
		searchContainer.setVisibility(View.VISIBLE);
		searchContainer.startAnimation(fadeInAnimation);
		findViewById(R.id.searchProgressContainerLayout).setVisibility(View.VISIBLE);
		
		findViewById(R.id.searchResultLayout).setVisibility(View.GONE);
		findViewById(R.id.refreshResultsView).setVisibility(View.GONE);
	}
	
	void updatePingTaskProgress(){
		addressesLooked++;
		if(addressesLooked % 8 == 0){
			progressBar.incrementProgressBy(3);
		}
	}
	
	void updateList(PingResult result){
		
		if(result != null){
			
			// Add Data for my own IP
//			if(result.getIp().equals(myConnectionInfo.getMyIp())){
//				result.setMacAddress(myConnectionInfo.getMac());
//			}
			
			// Get the given name in case it was saved in DB
			Host host = DataManager.getHost(result.getMacAddress());
			if(host != null){
				result.setGivenName(host.getName());
			}
			
			// Not available, but maybe recently connected
			if(!result.isSuccess()){
				if(searchNotConnected.isChecked() &&
					 !TextUtils.isEmpty(result.getHostname()) &&
					  !result.getHostname().equals(getString(R.string.unknown))){
					
					result.setNotCurrentlyConnected(true);
					getConnectedDevicesAdapter().add(result);
					getConnectedDevicesAdapter().notifyDataSetChanged();	
				}			
			}
			else{
				getConnectedDevicesAdapter().add(result);
				getConnectedDevicesAdapter().notifyDataSetChanged();					
			}
			
		}
		
	}
	
//	void endSearch(Scan scanner){
//		this.scanner = scanner;
	void endSearch(boolean cancelled){
		Toast.makeText(this, getString(R.string.search_finished), Toast.LENGTH_LONG).show();
		progressBar.setProgress(100);
		findViewById(R.id.searchProgressContainerLayout).setVisibility(View.GONE);
		findViewById(R.id.refreshResultsView).setVisibility(View.VISIBLE);
		
		if(!cancelled){
			// Start automatic monitoring of devices
			startMonitorService();	
		}
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    // Handle item selection
	    switch (item.getItemId()) {
	        case R.id.menu_settings:
	            startActivity(new Intent(this, SettingsActivity.class));
	            return true;
	        case R.id.menu_history:
	            startActivity(new Intent(this, ConnectedHistoryActivity.class));
	            return true;
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	}
	
	private class GetMyWifiInfoTask extends AsyncTask<Void, Void, WifiConnectionInfo> {
		
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			showStartLookingMyData();
		}
		
		@Override
		protected WifiConnectionInfo doInBackground(Void... params) {
			return NetUtils.getMyWifiInfo(MainActivity.this);
		}
		
		@Override
		protected void onPostExecute(WifiConnectionInfo result) {
			updateMyData(result);
		}
	}
	
//	private class InstallTask extends AsyncTask<Void, Boolean, Boolean> {
//		
//		@Override
//		protected void onPreExecute() {
//			installStarted();
//		}
//		
//		@Override
//		protected Boolean doInBackground(Void... arg0) {
//			// Install nmap binaries
//			Install install = new Install(MainActivity.this,
//					Utils.getApplicationFolder(MainActivity.this,"bin"));
//			return install.installNmapFiles();
//		};
//		
//		protected void onPostExecute(Boolean result) {
//			installFinished(result);
//		};
//	}
	
	public static FoundDevicesAdapter getConnectedDevicesAdapter() {
		return connectedDevicesAdapter;
	}
	
	public static MonitorConnectionThread getMonitorThread() {
		 return monitorThread; 
	}

	public static void setConnectedDevicesAdapter(
			FoundDevicesAdapter connectedDevicesAdapter) {
		MainActivity.connectedDevicesAdapter = connectedDevicesAdapter;
	}

	public SharedPreferences getPreferences() {
		return preferences;
	}
	
	public WifiConnectionInfo getMyConnectionInfo() {
		return myConnectionInfo;
	}

}
