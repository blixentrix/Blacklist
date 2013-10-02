package sk.pixel.blacklist;

import java.util.Calendar;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;

public class MainService extends Service {

	private static final String CHECK_MODE = "sk.pixel.blacklist.action.CHECK_MODE";
	private static final String NOTIFICATION_TITLE = "Blacklist";
	private static final String CONTACTS_ONLY_MODE = "Contacts only mode";
	private static final String TAG = MainActivity.SUPER_TAG + "MainService";
	private static final int ID = 1;
	private static final CharSequence ALLOW_ALL = "Allow all calls";
	private PhoneStateListener listener;
	private TelephonyManager manager;
	private NotificationCompat.Builder builder;
	private PendingIntent pendingIntent;
	private AlarmManager alarmManager;

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.d(TAG, "onStartCommand");
		if (intent.getAction() != null && intent.getAction().equals(CHECK_MODE)) {
			checkForMode();
		} else {
			Log.d(TAG, "first start");
			showStatusNotification();
			testOfDelayedMsg();
		}
		return super.onStartCommand(intent, flags, startId);
	}

	private void checkForMode() {
		Log.i(TAG, "check for mode");
		Calendar calendar = Calendar.getInstance();
		int currentHour = calendar.get(Calendar.HOUR_OF_DAY);
		int currentMinute = calendar.get(Calendar.MINUTE);
		Log.i(TAG, "current time: " + Integer.toString(currentHour) + ":" + Integer.toString(currentMinute));
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
		if (preferences.getBoolean("pref_whitelist_enabled", false)) {
			Log.i(TAG, "checking for whitelist mode");
		}
		if (preferences.getBoolean("pref_contacts_enabled", false)) {
			Log.i(TAG, "checking for contacts only mode");
			checkTime("pref_time_contacts_from", "pref_time_contacts_to");
		}
	}

	private void checkTime(String startTimeKey, String endTimeKey) {
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
		String startTime = preferences.getString(startTimeKey, "00:00");
		String endTime = preferences.getString(endTimeKey, "00:00");
		String[] data = startTime.split(":");
		int starHour = Integer.parseInt(data[0]);
		int startMinute = Integer.parseInt(data[1]);
		data = endTime.split(":");
		int endHour = Integer.parseInt(data[0]);
		int endMinute = Integer.parseInt(data[1]);
		Log.i(TAG, "mode from: " + starHour + ":" + startMinute);
		Log.i(TAG, "mode to: " + endHour + ":" + endMinute);
	}
	
	private void showStatusNotification() {
		builder = new NotificationCompat.Builder(this);
		builder.setContentTitle(NOTIFICATION_TITLE);
		builder.setContentText(CONTACTS_ONLY_MODE);
		builder.setOngoing(true);
		builder.setSmallIcon(R.drawable.ic_launcher);
		builder.setTicker(CONTACTS_ONLY_MODE);
		Intent activityIntent = new Intent(this, MainActivity.class);
		PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
				activityIntent, 0);
		builder.setContentIntent(pendingIntent);
		NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		manager.notify(ID, builder.build());
	}

	private void testOfDelayedMsg() {
		Intent intent = new Intent(this, MainService.class);
		intent.setAction(CHECK_MODE);
		pendingIntent = PendingIntent.getService(this, 0, intent, 0);
		alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.SECOND, 10);
		alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
	}

	@Override
	public void onCreate() {
		super.onCreate();
		initFields();
		manager.listen(listener, PhoneStateListener.LISTEN_CALL_STATE);
	}

	private void initFields() {
		manager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
		listener = new PhoneListener(this);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		manager.listen(listener, PhoneStateListener.LISTEN_NONE);
		notifyAllowAllCalls();
		alarmManager.cancel(pendingIntent);
	}

	private void notifyAllowAllCalls() {
		builder.setOngoing(false);
		builder.setContentText(ALLOW_ALL);
		builder.setTicker(ALLOW_ALL);
		Notification notification = builder.build();
		NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		manager.cancel(ID);
		manager.notify(ID, notification);
	}

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}

}
