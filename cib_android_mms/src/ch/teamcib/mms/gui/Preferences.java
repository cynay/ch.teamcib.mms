package ch.teamcib.mms.gui;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.preference.CheckBoxPreference;
import android.preference.PreferenceActivity;
import android.util.Log;
import android.widget.Toast;
import ch.teamcib.mms.*;
import ch.teamcib.mms.service.INetworkService;
import ch.teamcib.mms.service.NetworkServiceImpl;

/**
 * This is the class for the Preferences. It handles everything on the 
 * Preferencesscreen ...
 * 
 * @author Yannic Schneider
 * URL: http://stackoverflow.com/questions/531427/how-do-i-display-the-current-value-of-an-android-preference-in-the-preference-sum
 */
public class Preferences extends PreferenceActivity 
implements OnSharedPreferenceChangeListener {

	public static final String KEY_CKB_STATUS = "ckb_status";

	private INetworkService mService;
	private boolean mStarted = false;
	private CheckBoxPreference mServiceStatus;

	@Override
	public void onCreate(Bundle bundle) {
		super.onCreate(bundle);

		this.addPreferencesFromResource(R.layout.preference);

		// Get a reference to the preferences
		mServiceStatus = (CheckBoxPreference)getPreferenceScreen()
			.findPreference(KEY_CKB_STATUS);
		
		this.bindService(new Intent(Preferences.this, NetworkServiceImpl.class),
				mConnection, Context.BIND_AUTO_CREATE);

	}

	@Override
	protected void onResume() {
		super.onResume();

		// Set up a listener whenever a key changes            
		getPreferenceScreen().getSharedPreferences()
		.registerOnSharedPreferenceChangeListener(this);
	}

	@Override
	protected void onPause() {
		super.onPause();

		// Unregister the listener whenever a key changes            
		getPreferenceScreen().getSharedPreferences()
		.unregisterOnSharedPreferenceChangeListener(this);    
	}

	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
			String key) {
		// TODO Auto-generated method stub
		String toastMsg = "";

		if (key.equals(KEY_CKB_STATUS)) {
			boolean checked = sharedPreferences.getBoolean(key, false);
			if (checked){
				toastMsg = "Service activated";
				startService();
			} else if (!checked) {
				toastMsg = "Service deactivated";
				stopService();
			}
		}


		//		mServiceStatus.setSummary(sharedPreferences.getBoolean(key, false) 
		//				? "Disable this setting" : "Enable this setting");
		//		toastMsg = sharedPreferences.getBoolean(key, false) 
		//		? "Service activated!" : "Service deactivated!" ;



		Toast.makeText(this, toastMsg, Toast.LENGTH_SHORT).show();

	}
	
	private void startService(){
		try {
			Toast.makeText(this, mService.getData(), Toast.LENGTH_SHORT).show();
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
//		if (mStarted) {
//			Toast.makeText(Preferences.this, "Service already started", 
//					Toast.LENGTH_SHORT).show();
//		} else {
//			Intent i = new Intent();
//			i.setClassName("ch.teamcib.mms.service", 
//					"ch.teamcib.mms.service.NetworkServiceImpl");
//			startService(i);
//			mStarted = true;
//		}
	}
	
	private void stopService(){
		try {
			Toast.makeText(this, mService.getData(), Toast.LENGTH_SHORT).show();
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
//		if (!mStarted) {
//			Toast.makeText(Preferences.this, "Service not yet started", 
//					Toast.LENGTH_SHORT).show();
//		} else {
//			Intent i = new Intent();
//			i.setClassName("ch.teamcib.mms.service", 
//					"ch.teamcib.mms.service.NetworkServiceImpl");
//			stopService(i);
//			mStarted = false;
//		}
	}



	private ServiceConnection mConnection = new ServiceConnection() {

		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			// TODO Auto-generated method stub
			// This is called when the connection with the service has been
			// established, giving us the service object we can use to
			// interact with the service.  We are communicating with our
			// service through an IDL interface, so get a client-side
			// representation of that from the raw service object.
			mService = INetworkService.Stub.asInterface(service);
			Log.i("-> REMOTE SERVICE", "Attached.");


		}

		@Override
		public void onServiceDisconnected(ComponentName name) {
			// TODO Auto-generated method stub
			// This is called when the connection with the service has been
			// unexpectedly disconnected -- that is, its process crashed.
			mService = null;
			Log.i("-> REMOTE SERVICE", "Disconnected.");

		}

	};
}