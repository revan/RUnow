package edu.rutgers.runow;

import java.text.SimpleDateFormat;

import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

public class detailsEventActivity extends Activity{
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.details_event);
		
		Event event = (Event) getIntent().getSerializableExtra("event");
		
		TextView name = (TextView)findViewById(R.id.detailsEventName);
		TextView date = (TextView)findViewById(R.id.detailsEventDate);
		TextView location = (TextView)findViewById(R.id.detailsLocation);
		TextView description = (TextView)findViewById(R.id.detailsDescription);
		
		name.setText(event.name);
		date.setText(new SimpleDateFormat("MMM d h:mm a").format(event.when));
		location.setText(event.location);
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
