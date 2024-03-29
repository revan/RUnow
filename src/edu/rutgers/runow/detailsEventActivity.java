package edu.rutgers.runow;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HTTP;
import org.json.JSONArray;
import org.json.JSONObject;

import com.nostra13.universalimageloader.core.ImageLoader;

import com.facebook.model.GraphObject;
import com.facebook.model.GraphUser;

import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class detailsEventActivity extends Activity{
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.details_event);
		
		GraphUser user=null;
		try {
			user = GraphObject.Factory.create(new JSONObject(getIntent().getStringExtra("facebookUser")), GraphUser.class);
		} catch (Exception e) {
			e.printStackTrace();
		}
		final String userName;
		final String userId;
		if(user==null){
			userName="";
			userId="";
		}
		else{
			userName = user.getName();
			userId = user.getId();
		}
		
		final Event event = (Event) getIntent().getSerializableExtra("event");
		
		final TextView name = (TextView)findViewById(R.id.detailsEventName);
		final TextView dateAndLocation = (TextView)findViewById(R.id.detailsEventDateLocation);
		final TextView description = (TextView)findViewById(R.id.detailsDescription);
		final ImageView image = (ImageView) findViewById(R.id.detailsEventImage);
		
		final EditText commentField = (EditText)findViewById(R.id.detailsCommentField);
		
		name.setText(event.name);
		dateAndLocation.setText(new SimpleDateFormat("h:mm a").format(event.when) + " at " + event.location);
		description.setText(event.description);
		ImageLoader.getInstance().displayImage(getString(R.string.url) + "/" + event.photo_url, image);
		
		final ActionBar actionBar = getActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
		actionBar.setDisplayShowHomeEnabled(true);
		actionBar.setTitle(event.name);
		actionBar.setDisplayShowTitleEnabled(true);

		final LinearLayout commentsLayout = (LinearLayout)findViewById(R.id.list_comments);
		
		final Comment[] comments = getComments(event.url);
		
		for(Comment temp : comments){
			LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			View commentView = inflater.inflate(R.layout.list_comment, null, false);
			((TextView) commentView.findViewById(R.id.listCommentUser)).setText(temp.user);
			((TextView) commentView.findViewById(R.id.listCommentContent)).setText(temp.content);
			commentsLayout.addView(commentView);
		}
		
		final Button post = (Button)findViewById(R.id.detailsCommentButton);
		
		post.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Log.v("RUNow Server", event.id.toString());
				//send event to server
				HttpClient httpclient = new DefaultHttpClient();
				HttpPost httppost = new HttpPost(getString(R.string.url)+"/events/"+event.id+"/comments");
				httppost.addHeader(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
				HttpResponse response;
				JSONObject json = new JSONObject();
				try{
					JSONObject data = new JSONObject();
					data.put("user",userName);
					data.put("content",commentField.getText());
					//data.put("id",userId);
					
					json.put("comment", data);
					ByteArrayEntity message = new ByteArrayEntity(json.toString().getBytes("UTF8"));
					httppost.setEntity(message);
					response = httpclient.execute(httppost);
					
					}catch(Exception e){
					Log.e("RUNow Server", e.toString());
					e.printStackTrace();
				}
			}
		});
	}
	
	private Comment[] getComments(String thisEventURL) {
		HttpClient httpclient = new DefaultHttpClient();
		HttpGet httpget = new HttpGet(thisEventURL + "/comments");
		HttpResponse response;
		
		try {
			response = httpclient.execute(httpget);
			
			Log.i("RUNow Server",response.getStatusLine().toString());
			
			HttpEntity entity = response.getEntity();
			if(entity != null) {
				InputStream instream = entity.getContent();
				String result = convertStreamToString(instream);
				JSONObject json = new JSONObject(result);
				JSONArray nameArray = json.names();
				JSONArray valArray = json.toJSONArray(nameArray);
				instream.close();
				
				JSONArray comments = (JSONArray) valArray.get(0);
				Comment[] values = new Comment[comments.length()];
				
				for(int i = 0; i < values.length; i++) {
					JSONObject comment = (JSONObject) comments.get(i);
					values[i] = new Comment(comment.getString("user"), comment.getString("content"));					
				}
				
				return values;
			}
			
		} catch(Exception e) {
			Log.e("RUNow Server", e.toString());
			e.printStackTrace();
		}
		
		return new Comment[0];
			
	}
	
	private static String convertStreamToString(InputStream is) {
		/*
		 * To convert the InputStream to String we use the BufferedReader.readLine()
		 * method. We iterate until the BufferedReader return null which means
		 * there's no more data to read. Each line will appended to a StringBuilder
		 * and returned as String.
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
	
	@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
	private Context getActionBarThemedContextCompat() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
			return getActionBar().getThemedContext();
		} else {
			return this;
		}
	}
}
