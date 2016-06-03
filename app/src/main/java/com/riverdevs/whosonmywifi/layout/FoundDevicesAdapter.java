package com.riverdevs.whosonmywifi.layout;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.riverdevs.whosinmywifi.R;
import com.riverdevs.whosonmywifi.beans.PingResult;

public class FoundDevicesAdapter extends ArrayAdapter<PingResult>{

	private List<PingResult> itemsList = new ArrayList<PingResult>();
	
    public FoundDevicesAdapter(Context context) {
		super(context, R.layout.found_device_list_item);
		
		mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	private LayoutInflater mInflater;

	public List<PingResult> getItemsList(){
		return itemsList;
	}
	
	@Override
	public void add(PingResult object) {
		super.add(object);
		itemsList.add(object);
	}
	
    public View getView(int position, View convertView, ViewGroup parent) {
    	
        PingResult pingResult = getItem(position);

        if(convertView == null){
        	convertView = mInflater.inflate(R.layout.found_device_list_item, null);
        }	
            
        TextView identifierTextView = (TextView) convertView.findViewById(R.id.identifierTextView);
        TextView hostnameTextView = (TextView) convertView.findViewById(R.id.hostnameTextView);
        TextView macTextView = (TextView) convertView.findViewById(R.id.macTextView);
        TextView manufacturerTextView = (TextView) convertView.findViewById(R.id.manufacturerTextView);
        ImageView connectedDeviceImage = (ImageView) convertView.findViewById(R.id.connectedDeviceImage);
        
        if(pingResult.getGivenName() != null){
            identifierTextView.setText(pingResult.getGivenName());	
        }
        else{
        	identifierTextView.setText(pingResult.getIp());
        }
        
        hostnameTextView.setText("Hostname: " + (pingResult.getHostname()==null ? 
				getContext().getString(R.string.unknown) :
				pingResult.getHostname()) );
        macTextView.setText("MAC: " + 
        		(pingResult.getMacAddress()==null ? 
        				getContext().getString(R.string.unknown) :
        				pingResult.getMacAddress()) 
        		);
        manufacturerTextView.setText(getContext().getString(R.string.manufacturer) + ": " +
        		(pingResult.getManufacturer()==null ? 
        				getContext().getString(R.string.unknown) :
        				pingResult.getManufacturer()) 
        		);
        
        if(pingResult.isNotCurrentlyConnected()){
            connectedDeviceImage.setImageResource(R.drawable.disconnected);	
        }
        else{
        	connectedDeviceImage.setImageResource(R.drawable.connected);
        }

        return convertView;
    }
    
}
