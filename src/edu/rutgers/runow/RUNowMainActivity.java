package edu.rutgers.runow;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.LinkedList;
import java.util.PriorityQueue;

import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.Activity;
import android.os.Bundle;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

public class RUNowMainActivity extends Activity implements
		ActionBar.OnNavigationListener {
	
	String[] tags = new String[]{"sports","studying"};
	
	
	/**
	 * The serialization (saved instance state) Bundle key representing the
	 * current dropdown position.
	 */
	private static final String STATE_SELECTED_NAVIGATION_ITEM = "selected_navigation_item";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_runow_main);

		// Set up the action bar to show a dropdown list.
		final ActionBar actionBar = getActionBar();
		actionBar.setDisplayShowTitleEnabled(false);
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);

		// Set up the dropdown list navigation in the action bar.
		actionBar.setListNavigationCallbacks(
		// Specify a SpinnerAdapter to populate the dropdown list.
				new ArrayAdapter<String>(getActionBarThemedContextCompat(),
						android.R.layout.simple_list_item_1,
						android.R.id.text1, new String[] {
								getString(R.string.title_all),
								getString(R.string.title_sports),
								getString(R.string.title_studying), }), this);
	}

	/**
	 * Backward-compatible version of {@link ActionBar#getThemedContext()} that
	 * simply returns the {@link android.app.Activity} if
	 * <code>getThemedContext</code> is unavailable.
	 */
	@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
	private Context getActionBarThemedContextCompat() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
			return getActionBar().getThemedContext();
		} else {
			return this;
		}
	}

	@Override
	public void onRestoreInstanceState(Bundle savedInstanceState) {
		// Restore the previously serialized current dropdown position.
		if (savedInstanceState.containsKey(STATE_SELECTED_NAVIGATION_ITEM)) {
			getActionBar().setSelectedNavigationItem(
					savedInstanceState.getInt(STATE_SELECTED_NAVIGATION_ITEM));
		}
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		// Serialize the current dropdown position.
		outState.putInt(STATE_SELECTED_NAVIGATION_ITEM, getActionBar()
				.getSelectedNavigationIndex());
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_runow_main, menu);
		return true;
	}

	@Override
	public boolean onNavigationItemSelected(int position, long id) {
		final ListView listView = (ListView)findViewById(R.id.listView_events);
		
		String tag=null;
		if(position>0)
			tag = tags[position-1];
		final Event[] events = getEvents(tag);
		EventAdapter adapter = new EventAdapter(this, events);
		listView.setAdapter(adapter);
		
		listView.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view, int position, long id){
				//Toast.makeText(getApplicationContext(), "Clicked "+position, Toast.LENGTH_SHORT).show();
				Intent intent = new Intent(RUNowMainActivity.this, detailsEventActivity.class);
				intent.putExtra("event", events[position]);
				startActivity(intent);
			}
		});
		listView.setOnLongClickListener(new OnLongClickListener() {
			
			@Override
			public boolean onLongClick(View v) {
				//TODO open new screen displaying details
				//Toast.makeText(getApplicationContext(), "long clicked", Toast.LENGTH_SHORT).show();
				
				return false;
			}
		});
		return true;
	}
	private Event[] getEvents(String tag){
		//will eventually fetch events from server, using dummy events for now.
		
		/*fabricating dummy values*
		Event[] values = new Event[50];
		String[] tags = new String[]{"one","two"};
		for(int i=0;i<values.length;i++)
			values[i]=new Event(Integer.toString(position)+" "+Integer.toString(i),new Date(0),tags);
		/**/
		/* premade dummy values */
		Event[] values = new Event[3];
		values[0]=new Event("Soccer",new GregorianCalendar(2013, 3, 8, 15, 0).getTime(), new String[]{"sports"},"default description default description default description default description default description default description");
		values[1]=new Event("Board Games",new GregorianCalendar(2013, 3, 8, 19, 20).getTime(), new String[]{},"default description default description default description default description default description default description");
		values[2]=new Event("Chemistry Review",new GregorianCalendar(2013, 3, 8, 20,15).getTime(), new String[]{"studying"},"default description default description default description default description default description default description");
		//values[3]=new Event("Basketball",new GregorianCalendar(2013, 3, 8, 20, 45).getTime(), new String[]{"sports"});
		//TODO fix null pointer when trying to view a tag with more than one event 
		
		if(tag!=null){
			//iterate through array of events, build PriorityQueue of matching events ordering by time
			//then create array from PriorityQueue
			
			PriorityQueue<Event> matches = new PriorityQueue<Event>();
			for(Event event : values){
				for(String tempTag : event.getTags())
					if(tag.equals(tempTag)){
						matches.add(event);
						break;
					}
			}
			Event[] toReturn = new Event[matches.size()];
			
			for(int i=0; i<matches.size();i++){
				toReturn[i]=matches.remove();
			}
			return toReturn;
		}
		return values;
	}
}
