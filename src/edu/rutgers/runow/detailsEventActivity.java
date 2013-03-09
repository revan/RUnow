package edu.rutgers.runow;

import java.text.SimpleDateFormat;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

public class detailsEventActivity extends Activity {
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.details_event);
		
		Event event = (Event) getIntent().getSerializableExtra("event");
		
		TextView name = (TextView)findViewById(R.id.detailsEventName);
		TextView date = (TextView)findViewById(R.id.detailsEventDate);
		TextView description = (TextView)findViewById(R.id.detailsDescription);
		
		name.setText(event.getName());
		date.setText(new SimpleDateFormat("MMM d h:mm a").format(event.getDate()));
		description.setText(event.getDescription());
	}
}
