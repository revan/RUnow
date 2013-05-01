package edu.rutgers.runow;
import java.util.Date;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;


public class createEventActivity extends Activity {
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.create_event);
	}
	public void createEvent(View button){
		final EditText nameField = (EditText)findViewById(R.id.FieldEventName);
		final EditText descriptionsField = (EditText)findViewById(R.id.FieldEventDescription);
		final DatePicker dateField= (DatePicker)findViewById(R.id.FieldDate);
		final TimePicker timeField= (TimePicker)findViewById(R.id.FieldTime);
		//final Spinner spinnerTags = (Spinner)findViewById(R.id.SpinnerTags);
		
		String name = nameField.getText().toString();
		String description = descriptionsField.getText().toString();
		Date when = new Date(dateField.getYear(),dateField.getMonth(),dateField.getDayOfMonth(),
				timeField.getCurrentHour(),timeField.getCurrentMinute());
		//String tag = spinnerTags.getSelectedItem().toString();
		String tag ="no tags";
		if(tag.equals("no tags"))
			tag="";
		
		String location =""; //TODO implement location
		
		// adjusted Event to have id and url members, but these are set by the server
		// maybe overloaded constructor?
		
		Event created = new Event(null, name, null, when, location, description, null, tag);
		
		//TODO send event to server
	}
}
