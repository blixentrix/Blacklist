package sk.pixel.blacklist;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MenuItem.OnMenuItemClickListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.ToggleButton;

public class MainActivity extends Activity {

	public static final String SUPER_TAG = "BlackList.";
//	private static final String TAG = SUPER_TAG + "MainActivity";
	private ImageView receiverStatusIcon;
	private ToggleButton receiverSwitch;
	private Intent serviceIntent;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		initFields();
		initViewFields();
		initViews();
	}

	private void initFields() {
		serviceIntent = new Intent(this, MainService.class);
	}

	private void initViews() {
		receiverSwitch.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (receiverSwitch.isChecked()) {
					startService(serviceIntent);
					checkServiceStatus();
				} else {
					stopService(serviceIntent);
					checkServiceStatus();
				}
			}
		});
	}

	protected void checkServiceStatus() {
		if (isServiceRunning()) {
			receiverSwitch.setChecked(true);
			setReceiverStatusIcon(R.drawable.ic_tick);
		} else {
			receiverSwitch.setChecked(false);
			setReceiverStatusIcon(R.drawable.ic_cross);
		}
	}

	private boolean isServiceRunning() {
		ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
		for (RunningServiceInfo service : manager
				.getRunningServices(Integer.MAX_VALUE)) {
			if (MainService.class.getName().equals(
					service.service.getClassName())) {
				return true;
			}
		}
		return false;
	}

	private void initViewFields() {
		receiverStatusIcon = (ImageView) findViewById(R.id.service_icon);
		receiverSwitch = (ToggleButton) findViewById(R.id.receiver_switch);
	}

	private void setReceiverStatusIcon(int res) {
		Drawable drawable = getResources().getDrawable(res);
		receiverStatusIcon.setImageDrawable(drawable);
	}

	@Override
	protected void onResume() {
		super.onResume();
		checkServiceStatus();
	}

	@Override
	protected void onPause() {
		super.onPause();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		menu.getItem(0).setOnMenuItemClickListener(
				new OnMenuItemClickListener() {

					@Override
					public boolean onMenuItemClick(MenuItem item) {
						Intent intent = new Intent(MainActivity.this,
								SettingsActivity.class);
						startActivity(intent);
						return false;
					}
				});
		return true;
	}

	public void testButton(View view) {
//		requestReceiverStatus();
	}

}
