package edu.dnu.androidopac;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.View;
import android.widget.Button;

public class ChildActivity extends Activity {
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_child);
		Button btnBack= (Button) 
				findViewById(R.id.btnBacktoMainActivity);
		btnBack.setOnClickListener(new 
				View.OnClickListener() {
			public void onClick(View v) {
				finish();
			}
		});
	}
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_child, menu);
		return true;
	}
}