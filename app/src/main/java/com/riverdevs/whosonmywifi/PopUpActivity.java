package com.riverdevs.whosonmywifi;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.TextView;

import com.riverdevs.whosinmywifi.R;

public class PopUpActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_pop_up);
		
		// Fill the screen with the layout, must be done just after setting layout
	    LayoutParams params = getWindow().getAttributes();
	    params.width = LayoutParams.MATCH_PARENT;
	    params.height = LayoutParams.MATCH_PARENT;
	    getWindow().setAttributes((android.view.WindowManager.LayoutParams) params);
	    
		String myIP = getIntent().getStringExtra("myIP");
		String gatewayIP = getIntent().getStringExtra("gatewayIP");
		String selectedIP = getIntent().getStringExtra("selectedIP");
		String selectedHostname = getIntent().getStringExtra("selectedHostname");
		String selectedMac = getIntent().getStringExtra("selectedMac");
		String selectedGivenName = getIntent().getStringExtra("selectedGivenName");
		
		TextView selectedIPTextView = (TextView) findViewById(R.id.selectedIPText);
		TextView selectedHostnameTextView = (TextView) findViewById(R.id.selectedHostnameText);
		TextView selectedMacText = (TextView) findViewById(R.id.selectedMacText);
		
		selectedIPTextView.setText(selectedIP);
		selectedHostnameTextView.setText( (TextUtils.isEmpty(selectedHostname) ? getString(R.string.unknown) : selectedHostname) );
		selectedMacText.setText( (TextUtils.isEmpty(selectedMac) ? getString(R.string.unknown) : selectedMac) );
		
		if(myIP.equals(selectedIP)){
			findViewById(R.id.yourIPLayout).setVisibility(View.VISIBLE);
		}
		else{
			if(gatewayIP.equals(selectedIP)){
				findViewById(R.id.gatewayLayout).setVisibility(View.VISIBLE);
			}
			else{
				findViewById(R.id.unknownScrollView).setVisibility(View.VISIBLE);
				
				if(selectedGivenName != null){
					((TextView)findViewById(R.id.unknownText)).setText(getString(R.string.this_is_known_device));
					((TextView)findViewById(R.id.unknownExplanationText)).
						setText(getString(R.string.you_identified_with_name, selectedGivenName));
					findViewById(R.id.unknownExplanationLinkText).setVisibility(View.GONE);
					findViewById(R.id.unknownExplanationMoreText).setVisibility(View.GONE);
				}
//				TextView linkView = (TextView) findViewById(R.id.linkView);
//				linkView.setText("");
//				Linkify.addLinks(linkView, Linkify.ALL);
			}
		}
		
		findViewById(R.id.closeButton).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				finish();
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_pop_up, menu);
		return true;
	}

}
