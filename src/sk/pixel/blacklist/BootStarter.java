package sk.pixel.blacklist;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

public class BootStarter extends BroadcastReceiver {

	private static final String AUTO_START_KEY = "pref_auto_start";
	private static final String TAG = MainActivity.SUPER_TAG + "BootStarter";

	@Override
	public void onReceive(Context context, Intent arg1) {
		Log.d(TAG, "onReceive");
		SharedPreferences preferences = PreferenceManager
				.getDefaultSharedPreferences(context);
		if (preferences.getBoolean(AUTO_START_KEY, false)) {
			Intent service = new Intent(context, MainService.class);
			context.startService(service);
			Log.i(TAG, "start after boot");
		} else {
			Log.i(TAG, "dont start after boot");
		}
	}

}
