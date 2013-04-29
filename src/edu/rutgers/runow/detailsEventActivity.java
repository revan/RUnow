package edu.rutgers.runow;

import java.text.SimpleDateFormat;

import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.widget.TextView;

public class detailsEventActivity extends Activity{
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.details_event);
		
		Event event = (Event) getIntent().getSerializableExtra("event");
		
		TextView name = (TextView)findViewById(R.id.detailsEventName);
		TextView dateAndLocation = (TextView)findViewById(R.id.detailsEventDateLocation);
		TextView description = (TextView)findViewById(R.id.detailsDescription);
		
		name.setText(event.name);
		dateAndLocation.setText(new SimpleDateFormat("h:mm a").format(event.when) + " at " + event.location);
		description.setText(event.description);
		
		final ActionBar actionBar = getActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
		actionBar.setDisplayShowHomeEnabled(true);
		actionBar.setTitle(event.name);
		actionBar.setDisplayShowTitleEnabled(true);

	}
	@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
	private Context getActionBarThemedContextCompat() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
			return getActionBar().getThemedContext();
		} else {
			return this;
		}
	}
}
