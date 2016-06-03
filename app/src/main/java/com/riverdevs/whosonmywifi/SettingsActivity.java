package com.riverdevs.whosonmywifi;

import com.riverdevs.whosinmywifi.R;

import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;

public class SettingsActivity extends PreferenceActivity  implements
	OnSharedPreferenceChangeListener {

	public static final String KEY_PREF_TIMEOUT_TO_PING = "pref_timeout_to_ping";
	public static final String KEY_PREF_TIME_TO_NEXT_CHECKING = "pref_time_between_checking";
	public static final String KEY_PREF_AUTO_CONNECTION_CHECKING = "pref_auto_connection_check";
	public static final String KEY_PREF_INSTALLATION_DONE = "installation_done";
	public static final String KEY_PREF_INSTALLATION_SUCCESSFUL = "installation_successful";
	public static final String KEY_PREF_MANUFACTURER_DB_DONE = "manufacturer_db_done";
	public static final String KEY_PREF_MANUFACTURER_DB_SUCCESSFUL = "manufacturer_db_successful";
	public static final String KEY_PREF_SHOW_NOTIFICATION = "pref_show_notifications_check";
    
	@SuppressWarnings("deprecation")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		addPreferencesFromResource(R.xml.preferences);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		
		// Initial values
		for(String key : PreferenceManager.
				getDefaultSharedPreferences(this).getAll().keySet()){
			updatePreferencesProgramatically(key);
		}
		
	      // Set up a listener whenever a key changes
		PreferenceManager.
			getDefaultSharedPreferences(this)
                .registerOnSharedPreferenceChangeListener(this);
	}
	
	@Override
    protected void onPause() {
        super.onPause();
        // Unregister the listener whenever a key changes
        PreferenceManager.
			getDefaultSharedPreferences(this)
                .unregisterOnSharedPreferenceChangeListener(this);
    }
    
	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
			String key) {
		updatePreferencesProgramatically(key);
	}
	
	@SuppressWarnings("deprecation")
	private void updatePreferencesProgramatically(String key){
		Preference preference = getPreferenceScreen().findPreference(key);
		
	    if (key.equals(KEY_PREF_TIME_TO_NEXT_CHECKING)) {
	    	ListPreference timeToNextCheck = (ListPreference) getPreferenceScreen().findPreference(key);
	    	
	    	preference.setSummary(timeToNextCheck.getEntry());
	    }
	    if(key.equals(KEY_PREF_TIMEOUT_TO_PING)){
	    	ListPreference timeoutToPing = (ListPreference) getPreferenceScreen().findPreference(key);
	    	
	    	preference.setSummary(getString(R.string.pref_timeout_to_ping_summary, timeoutToPing.getEntry()));	    	
	    }
	}
	
}
