package com.riverdevs.whosonmywifi;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.riverdevs.whosinmywifi.R;
import com.riverdevs.whosonmywifi.beans.DetectedConnection;
import com.riverdevs.whosonmywifi.dao.DataManager;
import com.riverdevs.whosonmywifi.layout.ConnectedDevicesHistoryAdapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class ConnectedHistoryActivity extends Activity {

	private ListView connectedHistoryList;
	private TextView deleteResults;
	private TextView noResultsFound;
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_detected_history);
		
		deleteResults = (TextView) findViewById(R.id.deleteAllEntriesView);
		noResultsFound = (TextView) findViewById(R.id.noDetectedConnectionsView);
		
		connectedHistoryList = (ListView) findViewById(R.id.connectedHistoryList);
		final ConnectedDevicesHistoryAdapter adapter = new ConnectedDevicesHistoryAdapter(this);
		connectedHistoryList.setAdapter(adapter);
		
		for(DetectedConnection connection : DataManager.getAllDetectedConnections()){
			adapter.add(connection);
		}
		
		adapter.notifyDataSetChanged();
		
		deleteResults.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				new AlertDialog.Builder(ConnectedHistoryActivity.this)
		        .setIcon(android.R.drawable.ic_dialog_alert)
		        .setTitle(getString(R.string.delete_all_entries))
		        .setMessage(getString(R.string.remove_all_message))
		        .setPositiveButton("OK", new DialogInterface.OnClickListener() {

		            @Override
		            public void onClick(DialogInterface dialog, int which) {
		            	DataManager.deleteAllDetectedConnection();
		        		adapter.clear();
		        		adapter.notifyDataSetChanged();
		        		
		        		setElementsVisibility();
		            }

		        })
		        .setNegativeButton(getString(R.string.cancel), null)
		        .show();
			}
		});
		
		setElementsVisibility();

		MobileAds.initialize(getApplicationContext(), "ca-app-pub-7348866844562014/5306510085");

		AdView mAdView = (AdView) findViewById(R.id.adView);
		AdRequest adRequest = new AdRequest.Builder().build();
		mAdView.loadAd(adRequest);
	}
	
	private void setElementsVisibility(){
		ListAdapter adapter = connectedHistoryList.getAdapter();
		
		if(!adapter.isEmpty()){
			connectedHistoryList.setVisibility(View.VISIBLE);
			deleteResults.setVisibility(View.VISIBLE);
			noResultsFound.setVisibility(View.GONE);
		}
		else{
			connectedHistoryList.setVisibility(View.GONE);
			deleteResults.setVisibility(View.GONE);
			noResultsFound.setVisibility(View.VISIBLE);
		}
	}
}
