package edu.rutgers.runow;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.PriorityQueue;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import edu.rutgers.runow.R.id;

import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.Activity;
import android.os.Bundle;
import android.os.StrictMode;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class RUNowMainActivity extends Activity implements
		ActionBar.OnNavigationListener {

	String[] tags = new String[] { "sports", "studying" };

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

		// Allow non-asynchronous network io -- terrible coding practice, will
		// cause UI lags
		StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
				.permitAll().build());

		// Set up Universal Image Loader
		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(getApplicationContext())
        .build();
		ImageLoader.getInstance().init(config);
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

	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case id.menu_create:
			Intent intentCreate = new Intent(this, createEventActivity.class);
			startActivity(intentCreate);
			return true;
		}
		return false;
	}

	@Override
	public boolean onNavigationItemSelected(int position, long id) {
		final ListView listView = (ListView) findViewById(R.id.listView_events);

		String tag = null;
		if (position > 0)
			tag = tags[position - 1];
		final Event[] events = getEvents(tag);
		EventAdapter adapter = new EventAdapter(this, events);
		listView.setAdapter(adapter);

		listView.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// Toast.makeText(getApplicationContext(), "Clicked "+position,
				// Toast.LENGTH_SHORT).show();
				Intent intent = new Intent(RUNowMainActivity.this,
						detailsEventActivity.class);
				intent.putExtra("event", events[position]);
				startActivity(intent);
			}
		});
		return true;
	}

	private Event[] getEvents(String tag) {
		// fetch events from server
		HttpClient httpclient = new DefaultHttpClient();
		HttpGet httpget = new HttpGet(getString(R.string.url) + "/events");
		HttpResponse response;
		try {
			response = httpclient.execute(httpget);
			Log.i("RUNow Server", response.getStatusLine().toString());
			HttpEntity entity = response.getEntity();
			if (entity != null) {
				InputStream instream = entity.getContent();
				String result = convertStreamToString(instream);
				JSONObject json = new JSONObject(result);
				JSONArray nameArray = json.names();
				JSONArray valArray = json.toJSONArray(nameArray);
				Log.i("RUNow Server", valArray.getString(0));
				instream.close();

				// TODO parse data to create array of Event
				JSONArray events = (JSONArray) valArray.get(0);
				Event[] values = new Event[events.length()];
				// Log.i("JSON",events.toString());
				for (int i = 0; i < values.length; i++) {
					Log.i("JSON", events.get(i).toString());
					JSONObject event = (JSONObject) events.get(i);
					DateFormat df = new SimpleDateFormat(
							"yyyy-MM-dd'T'HH:mm:ss'Z'");
					String when = event.getString("when");
					values[i] = new Event(
							event.getInt("id"),
							event.getString("name"),
							null,
							(when.equals("null") ? new Date() : df.parse(when)),
							event.getString("location"), event
									.getString("description"), event
									.getString("url"), ""// tag
					);
				}
				return values;
			}

		} catch (Exception e) {
			Log.e("RUNow Server", e.toString());
			e.printStackTrace();
		}

		/*
		 * fabricating dummy values* Event[] values = new Event[50]; String[]
		 * tags = new String[]{"one","two"}; for(int i=0;i<values.length;i++)
		 * values[i]=new
		 * Event(Integer.toString(position)+" "+Integer.toString(i),new
		 * Date(0),tags); /*
		 */
		/* premade dummy values */

		// updated to include new class members
		Event[] values = new Event[3];
		String defaultDescription = "default description default description default description default description default description default description";
		String defaultLocation = "default location";
		values[0] = new Event(10, "Soccer", null, new GregorianCalendar(2013,
				3, 8, 15, 0).getTime(), defaultLocation, "sports", "",
				defaultDescription);
		values[1] = new Event(11, "Board Games", null, new GregorianCalendar(
				2013, 3, 8, 19, 20).getTime(), defaultLocation, "", "",
				defaultDescription);
		values[2] = new Event(12, "Chemistry Review", null,
				new GregorianCalendar(2013, 3, 8, 20, 15).getTime(),
				defaultLocation, "studying", "", defaultDescription);
		// values[3]=new Event("Basketball",new GregorianCalendar(2013, 3, 8,
		// 20, 45).getTime(), new String[]{"sports"});
		// TODO fix null pointer when trying to view a tag with more than one
		// event

		if (tag != null) {
			// iterate through array of events, build PriorityQueue of matching
			// events ordering by time
			// then create array from PriorityQueue

			PriorityQueue<Event> matches = new PriorityQueue<Event>();
			for (Event event : values) {
				if (tag.equals(event.tag))
					matches.add(event);
			}
			Event[] toReturn = new Event[matches.size()];

			for (int i = 0; i < matches.size(); i++) {
				toReturn[i] = matches.remove();
			}
			return toReturn;
		}
		return values;
		/**/
	}

	// from
	// http://senior.ceng.metu.edu.tr/2009/praeda/2009/01/11/a-simple-restful-client-at-android/
	private static String convertStreamToString(InputStream is) {
		/*
		 * To convert the InputStream to String we use the
		 * BufferedReader.readLine() method. We iterate until the BufferedReader
		 * return null which means there's no more data to read. Each line will
		 * appended to a StringBuilder and returned as String.
		 */
		BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		StringBuilder sb = new StringBuilder();

		String line = null;
		try {
			while ((line = reader.readLine()) != null) {
				sb.append(line + "\n");
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				is.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return sb.toString();
	}
}
