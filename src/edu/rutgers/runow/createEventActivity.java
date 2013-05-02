package edu.rutgers.runow;
import java.util.Date;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HTTP;
import org.json.JSONObject;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
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
		
		//send event to server
		HttpClient httpclient = new DefaultHttpClient();
		HttpPost httppost = new HttpPost(getString(R.string.url)+"/events");
		httppost.addHeader(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
		HttpResponse response;
		JSONObject json = new JSONObject();
		try{
			JSONObject data = new JSONObject();
			data.put("name",name);
			data.put("when",when.toString());
			data.put("location",location);
			data.put("description",description);
			
			json.put("event", data);
			ByteArrayEntity message = new ByteArrayEntity(json.toString().getBytes("UTF8"));
			httppost.setEntity(message);
			response = httpclient.execute(httppost);
			
			}catch(Exception e){
			Log.e("RUNow Server", e.toString());
			e.printStackTrace();
		}
	}
}
