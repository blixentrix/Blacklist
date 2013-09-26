package sk.pixel.blacklist;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;


public class MainService extends Service {

	private static final String CONTACTS_ONLY_MODE = "Contacts only mode";
	private static final String TAG = MainActivity.SUPER_TAG + "MainService";
	private static final int ID = 1;
	private static final CharSequence ALLOW_ALL = "Allow all calls";
	private PhoneStateListener listener;
	private TelephonyManager manager;
	private NotificationCompat.Builder builder;

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		builder = new NotificationCompat.Builder(this);
		builder.setContentTitle("Blacklist status:");
		builder.setContentText(CONTACTS_ONLY_MODE);
		builder.setOngoing(true);
		builder.setSmallIcon(R.drawable.ic_launcher);
		builder.setTicker(CONTACTS_ONLY_MODE);
		Intent activityIntent = new Intent(this, MainActivity.class);
		PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, activityIntent, 0);
		builder.setContentIntent(pendingIntent);
		NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		manager.notify(ID, builder.build());
		return super.onStartCommand(intent, flags, startId);
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
