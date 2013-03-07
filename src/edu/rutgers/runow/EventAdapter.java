/** Formats Events to be displayed in ListView */
package edu.rutgers.runow;

import java.text.SimpleDateFormat;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class EventAdapter extends ArrayAdapter<Event> {
	private final Context context;
	private final Event[] events;
	
	public EventAdapter(Context context, Event[] events){
		super(context,R.layout.list_event,events);
		this.context=context;
		this.events=events;
	}
	public View getView(int position, View convertView, ViewGroup parent){
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View eventView = inflater.inflate(R.layout.list_event, parent,false);
		TextView name = (TextView)eventView.findViewById(R.id.listEventText);
		TextView date = (TextView)eventView.findViewById(R.id.listEventDate);
		Log.d(null, "events.length: "+events.length);
		Log.d(null, "position: "+position);
		Event temp = events[position];
		name.setText(temp.getName());
		date.setText(new SimpleDateFormat("MMM d h:mm a").format(events[position].getDate()));
		
		return eventView;
	}

}