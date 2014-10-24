package com.example.googlecalendartest;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.Date;
import java.util.TimeZone;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.CalendarScopes;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventDateTime;
import com.google.api.services.calendar.model.Events;

public class MainActivity extends Activity {

	GoogleCredential credential;
	JsonFactory JSON_FACTORY;
	HttpTransport httpTransport = null;
	Calendar calenderClientObject;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		Thread t = new Thread() {

			@Override
			public void run() {

				//Service Account Email ID
				String emailAddress = "853727659350@developer.gserviceaccount.com";

				JSON_FACTORY = JacksonFactory.getDefaultInstance();
				httpTransport = AndroidHttp.newCompatibleTransport();

				// Access gcalendar.p12 from /values/raw folder
				int resId = getResources().getIdentifier("raw/" + "gcalendar",
						"raw", getPackageName());
				Uri uri = Uri
						.parse("android.resource://com.example.googlecalendartest/"
								+ resId);
				try {
					resId = getResources().getIdentifier("raw/gcalendar",
							"raw", getPackageName());
					InputStream iStream = getResources().openRawResource(
							getResources().getIdentifier("raw/gcalendar",
									"raw", getPackageName()));
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
								
				// Intentionally stored gcalendar.p12 file in sdcard
				File file = new File("/sdcard/gcalendar.p12");

				//Google Oauth2 with Calendar Scope
				try {
					credential = new GoogleCredential.Builder()
							.setTransport(httpTransport)
							.setJsonFactory(JSON_FACTORY)
							.setServiceAccountId(emailAddress)
							.setServiceAccountPrivateKeyFromP12File(file)
							.setServiceAccountScopes(
									Collections
											.singleton(CalendarScopes.CALENDAR))
							.build();
				} catch (GeneralSecurityException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				//Create Google calendar client object
				calenderClientObject = new com.google.api.services.calendar.Calendar.Builder(
						httpTransport, JSON_FACTORY, credential)
						.setApplicationName("GoogleCalendarTest").build();

				
				// Create event which starts now and ends after 1 hour
				Event newEvent = new Event();
				newEvent.setSummary("Race Gurram");
				Date startDate = new Date();
				Date endDate = new Date(startDate.getTime() + 3600000);
				DateTime s = new DateTime(startDate,
						TimeZone.getTimeZone("UTC"));
				newEvent.setStart(new EventDateTime().setDateTime(s));
				DateTime end2 = new DateTime(endDate,
						TimeZone.getTimeZone("UTC"));
				newEvent.setEnd(new EventDateTime().setDateTime(end2));
				
				//Insert new event into the calendar whose ID is "admin@gujju.com"
				try {
					Event result = calenderClientObject.events()
							.insert("admin@gujju.com", newEvent)
							.execute();
				} catch (IOException e2) {
					// TODO Auto-generated catch block
					e2.printStackTrace();
				}

				// Read all events from the calendar whose ID is "gujju.com_gg487k70i64@group.calendar.google.com"
				try {
					Events feed = calenderClientObject
							.events()
							.list("gujju.com_ggn34vranv870i64@group.calendar.google.com")
							.execute();
					for (Event entry : feed.getItems()) {
						Log.d("ID", entry.getId());
						if (entry.getSummary() != null) {
							Log.d("Summary", entry.getSummary());
						}
						if (entry.getDescription() != null) {
							Log.d("Description:", "" + entry.getDescription());
						}
					}
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		};
		t.start();
	}
}
