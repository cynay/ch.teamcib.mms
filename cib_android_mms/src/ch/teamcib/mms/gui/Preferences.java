package ch.teamcib.mms.gui;

import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.os.RemoteException;
import android.preference.CheckBoxPreference;
import android.preference.EditTextPreference;
import android.preference.PreferenceActivity;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.util.Log;
import android.widget.Toast;
import ch.teamcib.mms.*;
import ch.teamcib.mms.service.INetworkService;

/**
 * This is the class for the Preferences. It handles everything on the 
 * Preferencesscreen ...
 * 
 * @author CiB
 * @see <a href=http://stackoverflow.com/questions/531427/>
 * how-do-i-display-the-current-value-of-an-android-preference-in-the-preference-sum</>
 */
public class Preferences extends PreferenceActivity 
implements OnSharedPreferenceChangeListener {

	// ===========================================================
    // Finals 
    // ===========================================================
	public static final String KEY_CKB_STATUS = "ckb_status";
	public static final String KEY_EDT_TIMER = "edt_timer";

	// ===========================================================
    // Members
    // ===========================================================
	private CheckBoxPreference mServiceStatus;
	private EditTextPreference mRefreshRate;
	private INetworkService mNetworkService;
	
	

	@Override
	public void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		addPreferencesFromResource(R.layout.preference);

		// Get a reference to the preferences
		mServiceStatus = (CheckBoxPreference)getPreferenceScreen()
			.findPreference(KEY_CKB_STATUS);
		mRefreshRate = (EditTextPreference)getPreferenceScreen()
			.findPreference(KEY_EDT_TIMER);
		
		
		// on click reset DB		
		Preference rstDB = (Preference) findPreference("prf_resetDB");
		rstDB.setOnPreferenceClickListener(new OnPreferenceClickListener() {

			public boolean onPreferenceClick(Preference preference) {
				DataHelper dh = new DataHelper(getBaseContext());
				dh.deleteAll();
				
				Toast.makeText(getBaseContext(), "Database content deleted!", 
						Toast.LENGTH_SHORT).show();
				
				return true;
			}

		});

	}
	

	@Override
	protected void onResume() {
		super.onResume();

		// set the ServiceActivated-checkbox to the correct state
		mServiceStatus.setChecked(SPManager.getConfigValue(this, 
				SPManager.KEY_SERVICESTATUS));
		
		// Set up a listener whenever a key changes            
		getPreferenceScreen().getSharedPreferences()
			.registerOnSharedPreferenceChangeListener(this);

		// bind to service
		NetworkServiceClient.bindSvc(this);
		Log.i("-> PREFERENCES", "onResume()");
	}

	@Override
	protected void onPause() {
		super.onPause();

		// Unregister the listener whenever a key changes            
		getPreferenceScreen().getSharedPreferences()
			.unregisterOnSharedPreferenceChangeListener(this); 
		
		// unbind from service
		NetworkServiceClient.unbindSvc(this);
		Log.i("-> PREFERENCES", "onPause()");
	}

	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
			String key) {
		String toastMsg = "";

		if (key.equals(KEY_CKB_STATUS)) {
			boolean checked = sharedPreferences.getBoolean(key, false);
			if (checked){
				SPManager.addConfigValue(this, 
						SPManager.KEY_SERVICESTATUS, true);
				
				startService();
				toastMsg = "Service activated";
			} else if (!checked) {
				SPManager.addConfigValue(this, 
						SPManager.KEY_SERVICESTATUS, false);
				
				stopService();
				toastMsg = "Service deactivated";
			}
		} else if (key.equals(KEY_EDT_TIMER)){
			SPManager.addConfigValueLong(this, SPManager.KEY_REFRESHRATE, 
				Long.valueOf(mRefreshRate.getText()).longValue() * 1000);
			stopService();
			startService();
			toastMsg = "Refresh rate changed!";
		}
		Toast.makeText(this, toastMsg, Toast.LENGTH_SHORT).show();
	}
	
	private void startService(){
		try {
			mNetworkService = NetworkServiceClient.getService();
			mNetworkService.startService();
//			Toast.makeText(this, mNetworkService.getData(), Toast.LENGTH_SHORT).show();
		} catch (RemoteException e) {
			Log.i("-> PREFERENCES", "startService() Execption");
			e.printStackTrace();
		}
	}
	
	private void stopService(){
		try {
			mNetworkService = NetworkServiceClient.getService();
			mNetworkService.stopService();
//			Toast.makeText(this, mNetworkService.getData(), Toast.LENGTH_SHORT).show();
		} catch (RemoteException e) {
			Log.i("-> PREFERENCES", "stopService() Execption");
			e.printStackTrace();
		}

	}
	

}