package edu.dnu.androidopac.search;

import java.util.ArrayList;

import edu.dnu.androidopac.Constants;
import edu.dnu.androidopac.EditPreferences;
import edu.dnu.androidopac.R;
import edu.dnu.androidopac.authenticator.AuthenticatorActivity;
import edu.dnu.androidopac.log.LogConfig;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class SearchFormActivity extends Activity implements OnClickListener  {
	static final String TAG = LogConfig.getLogTag(SearchFormActivity.class);
	// whether DEBUG level logging is enabled (whether globally, or explicitly
	// for this log tag)
	static final boolean DEBUG = LogConfig.isDebug(TAG);
	// whether VERBOSE level logging is enabled
	static final boolean VERBOSE = LogConfig.VERBOSE;
	
	private void initiateScan (){
		try {
			Intent intent = new Intent("com.google.zxing.client.android.SCAN");
			intent.putExtra("SCAN_MODE", Constants.SEARCH_SCAN_MODE);
			startActivityForResult(intent, 0);
	    } catch (ActivityNotFoundException e) {
        	Toast.makeText(this, getResources().getString(R.string.scan_not_available), Toast.LENGTH_SHORT).show();
	    }
	}
	

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.search_form);
 
        setUserString();
       
        
        // Set up click handlers for the text field and button
        ((Button) this.findViewById(R.id.btnSearchGo)).setOnClickListener(this);
        
        SharedPreferences mPrefs = PreferenceManager.getDefaultSharedPreferences(this);

        
        String branchname = mPrefs.getString(getResources().getString(R.string.pref_branch_key).toString(), "");
        TextView textViewBranchName = (TextView) findViewById(R.id.defaultlibrary);        
        if(branchname!=null && !branchname.trim().equals("")){
        	textViewBranchName.setText(branchname);
        	textViewBranchName.setVisibility(View.VISIBLE);
        } else {
        	textViewBranchName.setVisibility(View.GONE);
        }

        	
    }
    
    public void setUserString() {
    	
    	String user = AuthenticatorActivity.getUserName();
    	TextView userID = (TextView) this.findViewById(R.id.searchUsername);
    	
    	if (user==null){
    		userID.setText(R.string.user_not_logged);
    	}
    	else {        
    		userID.setText(getResources().getString(R.string.user_logged) + " " + user);
    	}
    }
    

    
    public void addSearch(View v) {
		LinearLayout first = (LinearLayout) this.findViewById(R.id.searchGroup2);
		LinearLayout second = (LinearLayout) this.findViewById(R.id.searchGroup3);
		if (first.getVisibility() == View.VISIBLE) {
			second.setVisibility(View.VISIBLE);
		}
		if (first.getVisibility() == View.GONE) {
			first.setVisibility(View.VISIBLE);
		}
		if (first.getVisibility() == View.VISIBLE && second.getVisibility() == View.VISIBLE) {
			Context context = getApplicationContext();
			CharSequence text = "Maximum Search Terms Reached";
			int duration = Toast.LENGTH_SHORT;
			Toast toast = Toast.makeText(context, text, duration);
			toast.show();
		}
		else {
				Context context = getApplicationContext();
				CharSequence text = "Added Search Term";
				int duration = Toast.LENGTH_SHORT;
				Toast toast = Toast.makeText(context, text, duration);
				toast.show();
		}
	}
    
    public void removeSearch(View v) {
		LinearLayout first = (LinearLayout) this.findViewById(R.id.searchGroup2);
		LinearLayout second = (LinearLayout) this.findViewById(R.id.searchGroup3);
			if (first.getVisibility() == View.VISIBLE) {
				first.setVisibility(View.GONE);
			}
			if (second.getVisibility() == View.VISIBLE) {
			second.setVisibility(View.GONE);
			}
		Context context = getApplicationContext();
		CharSequence text = "Reset Search Terms";
		int duration = Toast.LENGTH_SHORT;
		Toast toast = Toast.makeText(context, text, duration);
		toast.show();
		((Spinner) this.findViewById(R.id.andornot1)).setSelection(0);
		((Spinner) this.findViewById(R.id.andornot2)).setSelection(0);
		((Spinner) this.findViewById(R.id.spinner1)).setSelection(0);
		((Spinner) this.findViewById(R.id.spinner2)).setSelection(0);
		((Spinner) this.findViewById(R.id.spinner3)).setSelection(0);
		((EditText) this.findViewById(R.id.searchTerms1)).setText("");
		((EditText) this.findViewById(R.id.searchTerms2)).setText("");
		((EditText) this.findViewById(R.id.searchTerms3)).setText("");
    }
    public void onClick(View v) {
    	//if (v.getId() == R.id.btnAddGroup) {
    		//LinearLayout first = (LinearLayout) this.findViewById(R.id.searchGroup2);
    		//if (first.getVisibility() == View.GONE) {
    		//first.setVisibility(View.VISIBLE);
    		//}
    	//	Context context = getApplicationContext();
    		//CharSequence text = "Hello toast!";
    		//int duration = Toast.LENGTH_SHORT;

    		//Toast toast = Toast.makeText(context, text, duration);
    		//toast.show();
    	//}
		if (v.getId() == R.id.btnSearchGo) {			
	        EditText mText;
			int pos;

	    	SharedPreferences mPrefs = PreferenceManager.getDefaultSharedPreferences(this);

			String[] av = getResources().getStringArray(R.array.search_options_arrayValues);
			ArrayList<String> idxValues = new ArrayList<String>();
			ArrayList<String> qValues = new ArrayList<String>();
			ArrayList<String> opValues = new ArrayList<String>();
			String pub_date_range;
			
			// allow for 3 fields - maybe make the form dynamic (auto new one if entering in one)
	        //TODO - maybe clean - improve the form element processing (bit cut-n-paste)
			
	        mText = (EditText) this.findViewById(R.id.searchTerms1);
	        if ( mText.getText().toString().trim().length() > 0 ) {
				pos = ((Spinner) this.findViewById(R.id.spinner1)).getSelectedItemPosition();				
				idxValues.add(av[pos]);
				String preParse = mText.getText().toString();
				String delims = "[ ]+";
				String[] tokens = preParse.split(delims);
				for (int i = 0; i < tokens.length; i++) {
					qValues.add(tokens[i]);	
				}
				
	        }
	        mText = (EditText) this.findViewById(R.id.searchTerms2);
	        if ( mText.getText().toString().trim().length() > 0 ) {
				pos = ((Spinner) this.findViewById(R.id.spinner2)).getSelectedItemPosition();
				idxValues.add(av[pos]);
				String preParse = mText.getText().toString();
				String delims = "[ ]+";
				String[] tokens = preParse.split(delims);
				for (int i = 0; i < tokens.length; i++) {
					qValues.add(tokens[i]);	
				}
	        }
	        mText = (EditText) this.findViewById(R.id.searchTerms3);
	        if ( mText.getText().toString().trim().length() > 0 ) {
				pos = ((Spinner) this.findViewById(R.id.spinner3)).getSelectedItemPosition();
				idxValues.add(av[pos]);
				String preParse = mText.getText().toString();
				String delims = "[ ]+";
				String[] tokens = preParse.split(delims);
				for (int i = 0; i < tokens.length; i++) {
					qValues.add(tokens[i]);	
				}
	        }
	        //limit-yr=1999-2000
	        mText = (EditText) this.findViewById(R.id.pub_date_range);
        	pub_date_range = mText.getText().toString().trim();
	        
			// Start the details dialog and pass in the intent containing item details.
	        
        	if ( ! ( idxValues.size() > 0 && qValues.size() > 0 ) ) {
    			Toast.makeText(this, getString(R.string.search_no_search_terms), Toast.LENGTH_SHORT).show();
        	} else {
        		Toast.makeText(this, getString(R.string.search_inprogress), Toast.LENGTH_SHORT).show();
            	
        		// Load up the search results intent
		        Intent d = new Intent(this, SearchResultsActivity.class);
				d.putStringArrayListExtra("idx", idxValues);
				d.putStringArrayListExtra("op", opValues);
				d.putStringArrayListExtra("q", qValues);
				d.putExtra(Constants.SEARCH_PUB_DATE_RANGE_PARAM, pub_date_range);
				
				if ( mPrefs.getBoolean(getResources().getString(R.string.pref_limit_available_key).toString(), false) ) 
					d.putExtra(Constants.LIMIT_AVAILABLE,	"something-non-empty");
				startActivity(d);
        	}}
        	
	}

	public boolean onSearchRequested() {
		initiateScan();
		return true;
	}
	
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		boolean result = super.onCreateOptionsMenu(menu);

		menu.add(Menu.NONE, Constants.SCAN, 1, R.string.menu_scan).setIcon(R.drawable.ic_menu_scan);
		menu.add(Menu.NONE, Constants.PREFERENCES, 2, R.string.menu_preferences).setIcon(android.R.drawable.ic_menu_preferences);
		return result;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		
		switch (item.getItemId()) {
			case Constants.SCAN:
				initiateScan();
				break;				
			case Constants.PREFERENCES:
				startActivity(new Intent(this, EditPreferences.class));
				break;				
			default:
				return super.onOptionsItemSelected(item);
		}
		return true;
	}
	public void onActivityResult(int requestCode, int resultCode, Intent intent) { 
		
        if (resultCode == Activity.RESULT_OK) {
        	String contents = intent.getStringExtra("SCAN_RESULT");
        	String formatName = intent.getStringExtra("SCAN_RESULT_FORMAT");
        	
        	if ( DEBUG ) Log.d(TAG, "scanResult: " + contents + " (" + formatName + ")");
        	
        	EditText mText = (EditText) this.findViewById(R.id.searchTerms1);
        	mText.setText(contents);
        	
        	Spinner mSpinner = (Spinner) this.findViewById(R.id.spinner1);
			String[] ai = getResources().getStringArray(R.array.search_options_array);
			for ( int i=0 ; i < ai.length; i++ ) {
				if ( ai[i].equals(Constants.ISBN) ) 
		        	mSpinner.setSelection(i);
			}
		} 
	}
}
