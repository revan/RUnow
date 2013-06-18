package edu.rutgers.runow;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HTTP;
import org.json.JSONObject;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.MemoryCacheUtil;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.text.format.DateFormat;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TimePicker;

public class createEventActivity extends FragmentActivity {
	
	private ImageView mImageView;
    private String mCurrentPhotoPath;
    private AlbumStorageDirFactory mAlbumStorageDirFactory = null;
    
    private Button takePhoto;
    private Button useOurs;
    private static Button dateButton;
    private static Button timeButton;
    
    private static Calendar c;
    static int hour, minute;
    static int year, month, day;
    private static DecimalFormat mFormat;
    
	private static final String BITMAP_STORAGE_KEY = "viewbitmap";
	private static final String IMAGEVIEW_VISIBILITY_STORAGE_KEY = "imageviewvisibility";
	
	DisplayImageOptions options;
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.create_event);
		
		dateButton = (Button) findViewById(R.id.dateButton);
		timeButton = (Button) findViewById(R.id.timeButton);
		useOurs = (Button) findViewById(R.id.ourPhotoButton);
		takePhoto = (Button) findViewById(R.id.takePhotoButton);
		mImageView = (ImageView) findViewById(R.id.eventPhotoPreview);
		useOurs.setEnabled(false);
		
		c = Calendar.getInstance();
		hour = c.get(Calendar.HOUR_OF_DAY) + 1;
		minute = 0;
		year = c.get(Calendar.YEAR);
		month = c.get(Calendar.MONTH);
		day = c.get(Calendar.DAY_OF_MONTH);
		
		mFormat = new DecimalFormat("00"); 
		
		if (hour > 12) {
			timeButton.setText((hour - 12) + ":" + mFormat.format(minute) + " PM");
		} else if (hour == 0) {
			timeButton.setText("12:" + mFormat.format(minute) + " AM");
		} else if (hour == 12) {
			timeButton.setText("12:" + mFormat.format(minute) + " PM");
		} else {
			timeButton.setText(hour + ":" + mFormat.format(minute) + " AM");
		}
		
		dateButton.setText(month + "/" + day + "/" + year);
		
		DisplayImageOptions options = new DisplayImageOptions.Builder()
				.bitmapConfig(Bitmap.Config.RGB_565)
				.build();
		
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO) {
			mAlbumStorageDirFactory = new FroyoAlbumDirFactory();
		} else {
			mAlbumStorageDirFactory = new BaseAlbumDirFactory();
		}
	}

	public static class TimePickerFragment extends DialogFragment implements
			TimePickerDialog.OnTimeSetListener {

		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			// Create a new instance of TimePickerDialog and return it
			return new TimePickerDialog(getActivity(), this, hour, minute,
					DateFormat.is24HourFormat(getActivity()));
		}

		public void onTimeSet(TimePicker view, int selectedHour, int selectedMinute) {
			hour = selectedHour;
			minute = selectedMinute;
			
			//TODO actually calculate when event will become visible and display it!
			
			if (hour > 12) {
				timeButton.setText((hour - 12) + ":" + mFormat.format(minute) + " PM");
			} else if (hour == 0) {
				timeButton.setText("12:" + mFormat.format(minute) + " AM");
			} else if (hour == 12) {
				timeButton.setText("12:" + mFormat.format(minute) + " PM");
			} else {
				timeButton.setText(hour + ":" + mFormat.format(minute) + " AM");
			}
			
			
		}
	}

	public static class DatePickerFragment extends DialogFragment implements
			DatePickerDialog.OnDateSetListener {

		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {

			// Create a new instance of DatePickerDialog and return it
			return new DatePickerDialog(getActivity(), this, year, month, day);
		}

		public void onDateSet(DatePicker view, int selectedYear, int selectedMonth, int selectedDay) {
			year = selectedYear;
			month = selectedMonth;
			day = selectedDay;
			
			dateButton.setText(month + "/" + day + "/" + year);
		}
	}

	public void showTimePickerDialog(View v) {
		DialogFragment newFragment = new TimePickerFragment();
		newFragment.show(getSupportFragmentManager(), "timePicker");
	}

	public void showDatePickerDialog(View v) {
		DialogFragment newFragment = new DatePickerFragment();
		newFragment.show(getSupportFragmentManager(), "datePicker");
	}
	
	public static boolean isIntentAvailable(Context context, String action) {
	    final PackageManager packageManager = context.getPackageManager();
	    final Intent intent = new Intent(action);
	    List<ResolveInfo> list =
	            packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
	    return list.size() > 0;
	}
	
	private String getAlbumName() {
		return getString(R.string.album_name);
	}
	
	private File getAlbumDir() {
		File storageDir = null;

		if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
			
			storageDir = mAlbumStorageDirFactory.getAlbumStorageDir(getAlbumName());

			if (storageDir != null) {
				if (! storageDir.mkdirs()) {
					if (! storageDir.exists()){
						Log.d("CameraSample", "failed to create directory");
						return null;
					}
				}
			}
			
		} else {
			Log.v(getString(R.string.app_name), "External storage is not mounted READ/WRITE.");
		}
		
		return storageDir;
	}
	
	private File createImageFile() throws IOException {
		// Create an image file name
		String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
		String imageFileName = "IMG_" + timeStamp + "_";
		File albumF = getAlbumDir();
		File imageF = File.createTempFile(imageFileName, ".jpg", albumF);
		return imageF;
	}

	
	public void dispatchTakePictureIntent(View v) {
	    Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
	    File f;
	    
	    try {
	    	f = createImageFile();
	    	mCurrentPhotoPath = f.getAbsolutePath();
	    	savePreferences();
	    	takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
	    } catch (IOException e) {
	    	e.printStackTrace();
	    	f = null;
	    	mCurrentPhotoPath = null;
	    }
	    
	    startActivityForResult(takePictureIntent, 1);
	    
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == 1) {
			if (resultCode == RESULT_OK) {
				if(mCurrentPhotoPath == null)
			        restorePreferences();
				
				ImageLoader.getInstance().displayImage("file://" + mCurrentPhotoPath, mImageView, options);
				takePhoto.setText("Take\nAnother");
			}
		} // ACTION_TAKE_PHOTO_B	
	
	}
	
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		outState.putString(BITMAP_STORAGE_KEY, mCurrentPhotoPath);
		outState.putBoolean(IMAGEVIEW_VISIBILITY_STORAGE_KEY, (mCurrentPhotoPath != null) );
		super.onSaveInstanceState(outState);
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		mCurrentPhotoPath = savedInstanceState.getString(BITMAP_STORAGE_KEY);
		ImageLoader.getInstance().displayImage("file://" + mCurrentPhotoPath, mImageView, options);
		mImageView.setVisibility(
				savedInstanceState.getBoolean(IMAGEVIEW_VISIBILITY_STORAGE_KEY) ? 
						ImageView.VISIBLE : ImageView.INVISIBLE
		);
	}
	
	private void savePreferences(){
	    // We need an Editor object to make preference changes.
	    SharedPreferences.Editor editor = getPreferences(MODE_PRIVATE).edit();
	    editor.putString("mCurrentPhotoPath", mCurrentPhotoPath);

	    // Commit the edits!
	    editor.commit();
	}

	private void restorePreferences() {
	    SharedPreferences settings = getPreferences(MODE_PRIVATE);
	    mCurrentPhotoPath = settings.getString("mCurrentPhotoPath", "");
	}

	public void createEvent(View button) throws IOException {
		final EditText nameField = (EditText) findViewById(R.id.FieldEventName);
		final EditText locationField = (EditText) findViewById(R.id.FieldEventLocation);
		final EditText descriptionsField = (EditText) findViewById(R.id.FieldEventDescription);
		
		String name = nameField.getText().toString();
		String location = locationField.getText().toString();
		String description = descriptionsField.getText().toString();
		Date when = new Date(year, month, day, hour, minute);
		String tag = "no tags";
		if (tag.equals("no tags"))
			tag = "";
		
		// encode image at max width 1080 pixels
		
	    int targetW = 1080;
	    int targetH = 608;
	  
	    // Get the dimensions of the bitmap
	    BitmapFactory.Options bmOptions = new BitmapFactory.Options();
	    bmOptions.inJustDecodeBounds = true;
	    BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
	    int photoW = bmOptions.outWidth;
	    int photoH = bmOptions.outHeight;
	  
	    // Determine how much to scale down the image
		int scaleFactor = 1;
		if ((targetW > 0) || (targetH > 0)) {
			scaleFactor = Math.min(photoW/targetW, photoH/targetH);	
		}
	  
	    // Decode the image file into a Bitmap sized to fill the View
	    bmOptions.inJustDecodeBounds = false;
	    bmOptions.inSampleSize = scaleFactor;
	    bmOptions.inPurgeable = true;
	  
	    Bitmap bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);

		ByteArrayOutputStream baos = new ByteArrayOutputStream();  
		bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
		
		byte[] b = baos.toByteArray(); 
		
		String encodedImage = Base64.encodeToString(b, Base64.DEFAULT);

		// send event to server
		HttpClient httpclient = new DefaultHttpClient();
		HttpPost httppost = new HttpPost(getString(R.string.url) + "/events");
		httppost.addHeader(new BasicHeader(HTTP.CONTENT_TYPE,
				"application/json"));
		HttpResponse response;
		
		JSONObject json = new JSONObject();
		try {
			JSONObject data = new JSONObject();
			data.put("name", name);
			data.put("when", when.toString());
			data.put("location", location);
			data.put("description", description);

			json.put("event", data);
			ByteArrayEntity message = new ByteArrayEntity(json.toString()
					.getBytes("UTF8"));
			httppost.setEntity(message);
			response = httpclient.execute(httppost);

		} catch (Exception e) {
			Log.e("RUNow Server", e.toString());
			e.printStackTrace();
		}
	}
}
