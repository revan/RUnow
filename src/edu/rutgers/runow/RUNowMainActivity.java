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

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import com.facebook.*;
import com.facebook.model.*;

import edu.rutgers.runow.R.id;

import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class RUNowMainActivity extends FragmentActivity implements
		ActionBar.OnNavigationListener {
	private GraphUser facebookUser;
	
	private static final int SPLASH = 0;
	private static final int LIST = 1;
	private static final int FRAGMENT_COUNT = LIST +1;

	private Fragment[] fragments = new Fragment[FRAGMENT_COUNT];
	
	private boolean isResumed = false;
	
	private UiLifecycleHelper uiHelper;
	private Session.StatusCallback callback = 
	    new Session.StatusCallback() {
	    @Override
	    public void call(Session session, 
	            SessionState state, Exception exception) {
	        onSessionStateChange(session, state, exception);
	    }
	};
	
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
		actionBar.hide();

		// Set up the dropdown list navigation in the action bar.
		actionBar.setListNavigationCallbacks(
		// Specify a SpinnerAdapter to populate the dropdown list.
				new ArrayAdapter<String>(getActionBarThemedContextCompat(),
						android.R.layout.simple_list_item_1,
						android.R.id.text1, new String[] {
								getString(R.string.title_all),
								getString(R.string.title_sports),
								getString(R.string.title_studying), }), this);

		// Set up Universal Image Loader

		// configure caching
		DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder()
			.cacheInMemory().cacheOnDisc().build();

		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
				getApplicationContext()).defaultDisplayImageOptions(
						defaultOptions).build();
		ImageLoader.getInstance().init(config);
		
		
		//Allow non-asynchronous network io -- terrible coding practice, will cause UI lags
		//TODO asynchronous network io
		StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().permitAll().build()); 
		
		
		//hide fragments on start
		 FragmentManager fm = getSupportFragmentManager();
		 fragments[SPLASH] = fm.findFragmentById(R.id.splashFragment);
		 fragments[LIST] = fm.findFragmentById(R.id.selectionFragment);

		 FragmentTransaction transaction = fm.beginTransaction();
		 for(int i = 0; i < fragments.length; i++) {
			 transaction.hide(fragments[i]);
		 }
		 transaction.commit();
		 
		 uiHelper = new UiLifecycleHelper(this, callback);
		 uiHelper.onCreate(savedInstanceState);
	}
	
	private void showFragment(int fragmentIndex, boolean addToBackStack) {
	    FragmentManager fm = getSupportFragmentManager();
	    FragmentTransaction transaction = fm.beginTransaction();
	    for (int i = 0; i < fragments.length; i++) {
	        if (i == fragmentIndex) {
	            transaction.show(fragments[i]);
	        } else {
	            transaction.hide(fragments[i]);
	        }
	    }
	    if (addToBackStack) {
	        transaction.addToBackStack(null);
	    }
	    if(fragmentIndex==LIST){
	    	getActionBar().show();
	    	makeMeRequest(Session.getActiveSession());
	    }
	    else
	    	getActionBar().hide();
	    transaction.commit();
	}
	
	private void makeMeRequest(final Session session) {
        Request request = Request.newMeRequest(session, new Request.GraphUserCallback() {
            @Override
            public void onCompleted(GraphUser user, Response response) {
                if (session == Session.getActiveSession()) {
                    if (user != null) {
                    	facebookUser=user;
                    }
                }
                if (response.getError() != null) {
                    //handleError(response.getError()); //TODO handle io errors
                }
            }
        });
        request.executeAsync();

    }
	
	/*
	private void handleError(FacebookRequestError error) {
        DialogInterface.OnClickListener listener = null;
        String dialogBody = null;

        if (error == null) {
            dialogBody = "An error has occurred.";
        } else {
            switch (error.getCategory()) {
                case AUTHENTICATION_RETRY:
                    // tell the user what happened by getting the message id, and
                    // retry the operation later
                    String userAction = (error.shouldNotifyUser()) ? "" :
                            getString(error.getUserActionMessageId());
                    dialogBody = "Authentication error.";
                    listener = new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://m.facebook.com"));
                            startActivity(intent);
                        }
                    };
                    break;

                case AUTHENTICATION_REOPEN_SESSION:
                    // close the session and reopen it.
                    dialogBody = "Authentication error.";
                    listener = new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Session session = Session.getActiveSession();
                            if (session != null && !session.isClosed()) {
                                session.closeAndClearTokenInformation();
                            }
                        }
                    };
                    break;

                case SERVER:
                case THROTTLING:
                    // this is usually temporary, don't clear the fields, and
                    // ask the user to try again
                    dialogBody = "Connection error, try again.";
                    break;

                case OTHER:
                case CLIENT:
                default:
                    // an unknown issue occurred, this could be a code error, or
                    // a server side issue, log the issue, and either ask the
                    // user to retry, or file a bug
                    dialogBody = "An error has occurred";
                    break;
            }
        }

        new AlertDialog.Builder(getActivity())
                .setPositiveButton("Okay",listener)
                .setTitle("Error")
                .setMessage(dialogBody)
                .show();
    }
	*/

	@Override
	public void onResume() {
	    super.onResume();
	    uiHelper.onResume();
	    isResumed = true;
	}

	@Override
	public void onPause() {
	    super.onPause();
	    uiHelper.onPause();
	    isResumed = false;
	}
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
	    super.onActivityResult(requestCode, resultCode, data);
	    uiHelper.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	public void onDestroy() {
	    super.onDestroy();
	    uiHelper.onDestroy();
	}
	private void onSessionStateChange(Session session, SessionState state, Exception exception) {
	    // Only make changes if the activity is visible
	    if (isResumed) {
	        FragmentManager manager = getSupportFragmentManager();
	        // Get the number of entries in the back stack
	        int backStackSize = manager.getBackStackEntryCount();
	        // Clear the back stack
	        for (int i = 0; i < backStackSize; i++) {
	            manager.popBackStack();
	        }
	        if (state.isOpened()) {
	            // If the session state is open:
	            // Show the authenticated fragment
	            showFragment(LIST, false);
	        } else if (state.isClosed()) {
	            // If the session state is closed:
	            // Show the login fragment
	            showFragment(SPLASH, false);
	        }
	    }
	}
	@Override
	protected void onResumeFragments() {
	    super.onResumeFragments();
	    Session session = Session.getActiveSession();

	    if (session != null && session.isOpened()) {
	        // if the session is already open,
	        // try to show the selection fragment
	        showFragment(LIST, false);
	    } else {
	        // otherwise present the splash screen
	        // and ask the person to login.
	        showFragment(SPLASH, false);
	    }
	}
	
	//End of Facebook Fragment managing
	
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
		super.onRestoreInstanceState(savedInstanceState);
		// Restore the previously serialized current dropdown position.
		if (savedInstanceState.containsKey(STATE_SELECTED_NAVIGATION_ITEM)) {
			getActionBar().setSelectedNavigationItem(
					savedInstanceState.getInt(STATE_SELECTED_NAVIGATION_ITEM));
		}
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		//Serialize the current dropdown position.
		outState.putInt(STATE_SELECTED_NAVIGATION_ITEM, getActionBar()
				.getSelectedNavigationIndex());
		uiHelper.onSaveInstanceState(outState);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_runow_main, menu);
		return true;
	}
	
	public boolean onOptionsItemSelected(MenuItem item){
		switch(item.getItemId()){
		case id.menu_refresh:
			this.onNavigationItemSelected(0, 0);
			return true;
		case id.menu_create:
			Intent intentCreate = new Intent(this, createEventActivity.class);
			startActivity(intentCreate);
			return true;
		case id.menu_logout:
			Session session = Session.getActiveSession();
			if(session!=null)
				session.closeAndClearTokenInformation();
			return true;
		}
		return false;
	}
	//TODO

	@Override
	public boolean onNavigationItemSelected(int position, long id) {
		
		
		String tag=getString(R.string.title_all);
		if(position>0)
			tag = tags[position-1];
		
		new FetchEventsTask(this).execute(tag);
		
		return true;
	}
	
	private class FetchEventsTask extends AsyncTask<String, Integer, Event[]> {
		Context context;
		ProgressDialog waitSpinner;
		
		public FetchEventsTask(Context context){
			this.context=context;
			waitSpinner=new ProgressDialog(this.context);
		}
		@Override
		protected Event[] doInBackground(String... arg0) {
			publishProgress(null);
			
			
			HttpClient httpclient = new DefaultHttpClient();
			HttpGet httpget = new HttpGet(getString(R.string.url) + "/events");
			HttpResponse response;
			try {
				response = httpclient.execute(httpget);
				//Log.i("RUNow Server", response.getStatusLine().toString());
				HttpEntity entity = response.getEntity();
				if(entity!=null){
	                InputStream instream = entity.getContent();
	                String result= convertStreamToString(instream);
	                JSONObject json=new JSONObject(result);
	                JSONArray nameArray=json.names();
	                JSONArray valArray=json.toJSONArray(nameArray);
	                Log.i("RUNow Server",valArray.getString(0));
	                instream.close();        		
				
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
								event.getString("location"),
								event.getString("description"),
								event.getString("url"),
								event.getString("image_url"), 
										"" // tag
						);
						if(isCancelled()) break;
						//publishProgress((int)( (i/values.length) *100));
					}
					return values;
				}

			} catch (Exception e) {
				Log.e("RUNow Server", e.toString());
				e.printStackTrace();
			}
			
			return null;
		}
		protected void onProgressUpdate(Integer... progress){
			//super.onProgressUpdate(progress);
			waitSpinner =ProgressDialog.show(context,"Please wait...", "Fetching events from server",true);
		}


		protected void onPostExecute(final Event[] result){
			final ListView listView = (ListView)findViewById(R.id.listView_events);
			EventAdapter adapter = new EventAdapter(context, result);
			listView.setAdapter(adapter);

			waitSpinner.cancel();
			
			listView.setOnItemClickListener(new OnItemClickListener() {
				public void onItemClick(AdapterView<?> parent, View view,
						int position, long id) {
					// Toast.makeText(getApplicationContext(), "Clicked "+position,
					// Toast.LENGTH_SHORT).show();
					Intent intent = new Intent(RUNowMainActivity.this,
							detailsEventActivity.class);
					intent.putExtra("event", result[position]);
					intent.putExtra("facebookUser",facebookUser.getInnerJSONObject().toString());
					startActivity(intent);
				}
			});
			
			
		}

		
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
