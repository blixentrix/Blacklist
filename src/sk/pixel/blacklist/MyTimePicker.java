package sk.pixel.blacklist;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TimePicker;

public class MyTimePicker extends TimePicker {

	public MyTimePicker(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		setIs24HourView(true);
	}

	public MyTimePicker(Context context, AttributeSet attrs) {
		super(context, attrs);
		setIs24HourView(true);
	}

	public MyTimePicker(Context context) {
		super(context);
		setIs24HourView(true);
	}
	
	

}
