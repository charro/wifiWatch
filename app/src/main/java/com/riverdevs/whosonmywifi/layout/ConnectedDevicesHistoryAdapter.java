package com.riverdevs.whosonmywifi.layout;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.riverdevs.whosinmywifi.R;
import com.riverdevs.whosonmywifi.beans.DetectedConnection;
import com.riverdevs.whosonmywifi.beans.Host;
import com.riverdevs.whosonmywifi.dao.DataManager;

public class ConnectedDevicesHistoryAdapter extends ArrayAdapter<DetectedConnection>{

	private List<DetectedConnection> itemsList = new ArrayList<DetectedConnection>();
	
	private List<Host> knownHosts;
	
    public ConnectedDevicesHistoryAdapter(Context context) {
		super(context, R.layout.connected_history_list_item);
		
		mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		knownHosts = DataManager.getHosts(null, null);
	}

	private LayoutInflater mInflater;

	public List<DetectedConnection> getItemsList(){
		return itemsList;
	}
	
	@Override
	public void add(DetectedConnection object) {
		super.add(object);
		itemsList.add(object);
	}
	
    public View getView(int position, View convertView, ViewGroup parent) {
    	
    	DetectedConnection detectedConnection = getItem(position);

        if(convertView == null){
        	convertView = mInflater.inflate(R.layout.connected_history_list_item, null);
        }	
            
        TextView identifierTextView = (TextView) convertView.findViewById(R.id.identifierTextView);
        TextView dateTimeTextView = (TextView) convertView.findViewById(R.id.dateTimeTextView);
        TextView ipTextView = (TextView) convertView.findViewById(R.id.ipTextView);
        TextView macTextView = (TextView) convertView.findViewById(R.id.macTextView);
        TextView manufacturerTextView = (TextView) convertView.findViewById(R.id.manufacturerTextView);
 
        String givenName = null;
        for(Host knownHost: knownHosts){
        	if(knownHost.getMac().equals(detectedConnection.getMac())){
        		givenName = knownHost.getName();
        	}
        }
        
        identifierTextView.setText(givenName == null ? detectedConnection.getIp() : givenName);
        
        // Set date/time
        Date date = new Date(detectedConnection.getTimestamp());
        DateFormat dateFormat = android.text.format.DateFormat.getDateFormat(getContext());
        DateFormat timeFormat = android.text.format.DateFormat.getTimeFormat(getContext());
        String formattedDate = dateFormat.format(date) + " " + timeFormat.format(date);
        dateTimeTextView.setText(formattedDate);
        
        if(givenName == null){
        	ipTextView.setVisibility(View.GONE);
        }
        else{
        	ipTextView.setText("IP: " + detectedConnection.getIp());
        }
        
        macTextView.setText("MAC: " + 
        		(detectedConnection.getMac()==null ? 
        				getContext().getString(R.string.unknown) :
        					detectedConnection.getMac()) 
        		);
        
        manufacturerTextView.setText(getContext().getString(R.string.manufacturer) + ": " +
        		(detectedConnection.getManufacturer()==null ? 
        				getContext().getString(R.string.unknown) :
        				detectedConnection.getManufacturer()) 
        		);

        return convertView;
    }
    
}
