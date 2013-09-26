package sk.pixel.blacklist;


import android.content.Context;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

public class TimePickerPreference extends DialogPreference {

	private static final String DEF_TIME = "00:00";
	private static final String TAG = MainActivity.SUPER_TAG
			+ "TimePickerPreference";
	private MyTimePicker timePicker;
	private String currentTime = "";

	public TimePickerPreference(Context context, AttributeSet attrs) {
		super(context, attrs);
		setDialogLayoutResource(R.layout.time_picker_dialog);
		setPositiveButtonText(android.R.string.ok);
		setNegativeButtonText(android.R.string.cancel);
		setPersistent(true);
		setDialogIcon(null);
	}

	@Override
	protected void onBindDialogView(View view) {
		super.onBindDialogView(view);
		timePicker = (MyTimePicker) view.findViewById(R.id.time_picker);
		String[] data = currentTime.split(":");
		Integer hour = 0, minute = 0;
		try {
			hour = Integer.parseInt(data[0]);
			minute = Integer.parseInt(data[1]);
		} catch (ArrayIndexOutOfBoundsException e) {
			Log.e(TAG, e.toString());
		} catch (NumberFormatException e) {
			Log.e(TAG, e.toString());
		}
		timePicker.setCurrentHour(hour);
		timePicker.setCurrentMinute(minute);
	}

	@Override
	protected void onDialogClosed(boolean positiveResult) {
		if (positiveResult) {
			Integer hour = timePicker.getCurrentHour();
			Integer minute = timePicker.getCurrentMinute();
			String persistedTime = hour + ":" + minute;
			persistString(persistedTime);
			currentTime = getPersistedString(DEF_TIME);
		}
	}

	@Override
	protected void onSetInitialValue(boolean restorePersistedValue,
			Object defaultValue) {
		if (restorePersistedValue) {
			currentTime = getPersistedString(DEF_TIME);
		} else {
			currentTime = (String) defaultValue;
			persistString(currentTime);
		}
	}

}
