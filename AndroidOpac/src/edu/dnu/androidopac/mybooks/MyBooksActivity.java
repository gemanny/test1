package edu.dnu.androidopac.mybooks;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import edu.dnu.androidopac.MainActivity;
import edu.dnu.androidopac.R;
import edu.dnu.androidopac.authenticator.AuthenticatorActivity;
import edu.dnu.androidopac.log.LogConfig;
import edu.dnu.androidopac.search.SearchFormActivity;
import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ExpandableListView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


public class MyBooksActivity extends Activity {
	static final String TAG = LogConfig.getLogTag(MainActivity.class);
	// whether DEBUG level logging is enabled (whether globally, or explicitly
	// for this log tag)
	static final boolean DEBUG = LogConfig.isDebug(TAG);
	// whether VERBOSE level logging is enabled
	static final boolean VERBOSE = LogConfig.VERBOSE;
	Button myButton;
	Button btnRem;
	Button btnSave;
	Button btnMail;
	CustomAdapter adapter=null;
	ListView lv;
	Model[] modelItems=null;
	private String uname=null;
	private String FILENAME=null;
	private String body=null;
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.mybooks);
		Log.d(TAG, "MyBooksActivity : ");
		
		lv = (ListView) findViewById(R.id.myBooklistView);
		 
		 
		 setUserString();
		 if(uname != null) FILENAME = uname.toString().trim()+".txt";
		 else FILENAME="nofile.txt";
   	     File f=new File(getFilesDir()+"/"+FILENAME);
   	     StringBuilder total = new StringBuilder();
   	     if(f.exists()){
   		  //doc file
   		  try {   			
     	        FileInputStream inputStream = openFileInput(FILENAME);
     	        BufferedReader r = new BufferedReader(new InputStreamReader(inputStream));
     	        String line;
     	        while ((line = r.readLine()) != null) {
     	            total.append(line);
     	        }
     	        r.close();
     	        inputStream.close();     	        
     	    } catch (Exception e) {
     	    	Log.e(TAG, "Connection error: " + e.getMessage());
     	    } 
   		 
   	    String preParse = total.toString();
   	    body = preParse;
   		String delims = "#";
		String[] tokens = preParse.split(delims);
		modelItems = new Model[tokens.length];
		for (int i = 0; i < tokens.length; i++) {			
			modelItems[i] = new Model(tokens[i], 0);
		}   		  
   	   }else
   	   {
   		 //chua chon sach ua thich
   		 Toast.makeText(getApplicationContext(),
	    	     "No favorite books.\n Please search and choose one...", 
	    	     Toast.LENGTH_LONG).show();
   		modelItems = new Model[1];
   		modelItems[0] = new Model("", 0);
   		adapter = new CustomAdapter(this, modelItems);
		 lv.setAdapter(adapter);
     	// Load up the search books intent
	        Intent d = new Intent(this, MainActivity.class);
			
			startActivity(d);
   		   
   	   }
   	 	 adapter = new CustomAdapter(this, modelItems);
		 lv.setAdapter(adapter);
		
		btnRem = (Button) findViewById(R.id.btnRem);
		btnSave = (Button) findViewById(R.id.btnSave);
		btnMail = (Button) findViewById(R.id.btnMail);  		 
	    checkButtonClick();	
	}
 public void setUserString() {    	
    	String user = AuthenticatorActivity.getUserName();
    	TextView userID = (TextView) this.findViewById(R.id.bookUsername);
    	
    	if (user==null){
    		userID.setText(R.string.user_not_logged);
    	}
    	else {
    		userID.setText(getResources().getString(R.string.user_logged) + " " + user);
    	    uname=user;    	
    	}
 }
 private void checkButtonClick() {    	 
	     
	   	btnRem.setOnClickListener(new OnClickListener() {
	   	@Override
	    public void onClick(View v) {	    	 
	        StringBuffer responseText = new StringBuffer();
	        responseText.append("The following were selected to remove...\n");
	        Model[] Mdl = adapter.modelItems;
	        for(int i=0;i<Mdl.length;i++){	    	    
	    	     String books = Mdl[i].getName();
	    	     if(Mdl[i].getValue() == 1){
	    	      responseText.append("\n" + books);
	    	     }
	        }	        
	        responseText.append("\nClick \"Save\" to Save the Unselected ...\n");
	    	 Toast.makeText(getApplicationContext(),
	    	     responseText, Toast.LENGTH_LONG).show();	    	 
	    }
	    });
	   	
	   	btnSave.setOnClickListener(new OnClickListener() {
		public void onClick(View v) {
			StringBuilder total = new StringBuilder();
			try {				
				Model[] Mdl = adapter.modelItems;
		        for(int i=0;i<Mdl.length;i++){	    	    
		    	     String books = Mdl[i].getName();
		    	     if(Mdl[i].getValue() == 0){
		    	      total.append(books+"#");		    	      
		    	     }
		        }
				//ghi file
		     FileOutputStream fos = openFileOutput(FILENAME,Context.MODE_PRIVATE);
		     fos.write(total.toString().getBytes());
		     fos.close();  		 		 
		     } catch (FileNotFoundException e) {
		 	    Log.e(TAG, "File Not Found error: " + e.getMessage());    		     
		     } catch (IOException e) {
		     	Log.e(TAG, "Connection error: " + e.getMessage());
		     } 
			 Toast.makeText(getApplicationContext(),
		    "Saved...\n"+total, Toast.LENGTH_LONG).show();
			 body=total.toString();
			}
		});
	   	///Toast.makeText(this, getResources().getString(R.string.data_saved_inside_File)+FILENAME, 
			///	Toast.LENGTH_SHORT).show();
	   	
	   	btnMail.setOnClickListener(new 
				View.OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent(MyBooksActivity.this, MailActivity.class);
				intent.putExtra("body", body);
				MyBooksActivity.this.startActivity(intent);
			}
		});
 }
		 
public class CustomAdapter extends ArrayAdapter<Model>{
    	 Model[] modelItems = null;
    	 Context context;
    	 public CustomAdapter(Context context, Model[] resource) {
	    	 super(context,R.layout.mybooks_row,resource);
	    	 // TODO Auto-generated constructor stub
	    	 this.context = context;
	    	 this.modelItems = resource;
    	 }
	    	 @Override
    	 public View getView(int position, View convertView, ViewGroup parent) {
	    	 // TODO Auto-generated method stub
	         final int i=position;
	    	 LayoutInflater inflater = ((Activity)context).getLayoutInflater();
	    	 convertView = inflater.inflate(R.layout.mybooks_row, parent, false); 
	    	 TextView name = (TextView) convertView.findViewById(R.id.mybook);
	    	 CheckBox cb = (CheckBox) convertView.findViewById(R.id.chkmybook);
	    	 cb.setOnClickListener( new View.OnClickListener() {  
			     public void onClick(View v) {  
				      CheckBox cb1 = (CheckBox) v ; 
				      if(cb1.isChecked())modelItems[i].setValue(1);
				      else modelItems[i].setValue(0);			      
				     }  
				    });   	 
	    	 
	    	 name.setText(modelItems[position].getName());
	    	 if(modelItems[position].getValue() == 1)
	    	 cb.setChecked(true);
	    	 else
	    	 cb.setChecked(false);
	    	 return convertView;
	    }
 }	 
	
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_child, menu);
		return true;
	}
	public class Model{
		 String name;
		 int value;  /* 0 -> checkbox disable, 1 -> checkbox enable */

		 Model(String name, int value){
		 this.name = name;
		 this.value = value;
		 }
		 public String getName(){
		 return this.name;
		 }
		 public int getValue(){
		 return this.value;
		 }
		 public void  setValue(int v){
			 this.value=v; return;
			 }

		}
}
