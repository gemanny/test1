package edu.dnu.androidopac.mybooks;

import edu.dnu.androidopac.MainActivity;
import edu.dnu.androidopac.R;
import edu.dnu.androidopac.authenticator.AuthenticatorActivity;
import edu.dnu.androidopac.log.LogConfig;
import android.net.Uri;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MailActivity extends Activity {
	static final String TAG = LogConfig.getLogTag(MainActivity.class);
	// whether DEBUG level logging is enabled (whether globally, or explicitly
	// for this log tag)
	static final boolean DEBUG = LogConfig.isDebug(TAG);
	// whether VERBOSE level logging is enabled
	static final boolean VERBOSE = LogConfig.VERBOSE;
	private EditText recipient;
	private String subject="Favorite Books from Koha Library";
    private String body=null;
    
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.mail);
		recipient = (EditText) findViewById(R.id.txtmail);
		
        Bundle b = getIntent().getExtras();
        if(b!=null)
        {
            body =(String) b.get("body");            
        }
        setUserString();
        if ( DEBUG ) Log.d(TAG, "MailActivity: ");
		Button btnBack= (Button) 
				findViewById(R.id.btnSend);
		btnBack.setOnClickListener(new 
				View.OnClickListener() {
			public void onClick(View v) {
				sendEmail();
		//	 Intent d = new Intent(MailActivity.this, MainActivity.class);				
			//		startActivity(d);
			}
		});
	}
	public void setUserString() {    	
    	String user = AuthenticatorActivity.getUserName();
    	TextView userID = (TextView) this.findViewById(R.id.txtuserLogged);    	
    	if (user==null){
    		userID.setText(R.string.user_not_logged);
    	}
    	else {
    		userID.setText(getResources().getString(R.string.user_logged) + " " + user);    	       	
    	}
 }
	
	protected void sendEmail() {
	      String[] recipients = {recipient.getText().toString()};
	      Intent email = new Intent(Intent.ACTION_SEND, Uri.parse("mailto:"));
	      // prompts email clients only
	      email.setType("message/rfc822");
	      email.putExtra(Intent.EXTRA_EMAIL, recipients);
	      email.putExtra(Intent.EXTRA_SUBJECT, subject);
	      email.putExtra(Intent.EXTRA_TEXT, body);
	      email.addFlags(Intent.FLAG_ACTIVITY_FORWARD_RESULT);
	 
	      try {
	        // the user can choose the email client
	         startActivity(Intent.createChooser(email, "Choose an email client from..."));
	      
	      } catch (android.content.ActivityNotFoundException ex) {
	         Toast.makeText(MailActivity.this, "No email client installed.",
	                 Toast.LENGTH_LONG).show();
	      }
	   }
	
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_child, menu);
		return true;
	}
}