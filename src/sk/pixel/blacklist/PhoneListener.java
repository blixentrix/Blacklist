package sk.pixel.blacklist;

import java.lang.reflect.Method;

import com.android.internal.telephony.ITelephony;

import android.content.Context;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;

public class PhoneListener extends PhoneStateListener {
	
	private static final String TAG = MainActivity.SUPER_TAG + "PhoneListener";
	private Context context;
	private ContactSearcher contacts;
	
	public PhoneListener(Context context) {
		this.context = context;
		contacts = new ContactSearcher(context);
	}

	@Override
	public void onCallStateChanged(int state, String incomingNumber) {
		super.onCallStateChanged(state, incomingNumber);
		if (state == TelephonyManager.CALL_STATE_RINGING) {
			if(isForbidden(incomingNumber)) {
				Log.i(TAG, "forbidden number " + incomingNumber);
				endCall(context);
			} else {
				Log.i(TAG, incomingNumber);
			}
		}
	}
	
	private boolean isForbidden(String incoming) {
		String shortForm = incoming.replaceFirst("\\+421", "");
		Log.i(TAG, "number only " + shortForm);
		return !contacts.existsNumber(shortForm);
	}
	
	private void endCall(Context context) {
		try {
			TelephonyManager tm = (TelephonyManager) context
					.getSystemService(Context.TELEPHONY_SERVICE);
			Class<?> c = Class.forName(tm.getClass().getName());
			Method m = c.getDeclaredMethod("getITelephony");
			m.setAccessible(true);
			com.android.internal.telephony.ITelephony telephonyService = (ITelephony) m
					.invoke(tm);
			telephonyService.endCall();
		} catch (Exception e) {
			Log.e(TAG, e.toString());
		}
	}

}
