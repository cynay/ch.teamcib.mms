package ch.teamcib.mms.gui;

import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.PreferenceActivity;
import android.widget.Toast;
import ch.teamcib.mms.*;

/**
 * This is the class for the Preferences. It handles all everything on the Prefscreen...
 * 
 * @author cYnaY
 * URL: http://stackoverflow.com/questions/531427/how-do-i-display-the-current-value-of-an-android-preference-in-the-preference-sum
 */
public class Preferences extends PreferenceActivity implements OnSharedPreferenceChangeListener {
	
	public static final String KEY_CKB_STATUS = "ckb_status";
	
	private CheckBoxPreference mServiceStatus;

	@Override
	public void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		
		this.addPreferencesFromResource(R.layout.preference);
		
		// Get a reference to the preferences
		mServiceStatus = (CheckBoxPreference)getPreferenceScreen().findPreference(KEY_CKB_STATUS);	
		
	}
	
	@Override
    protected void onResume() {
        super.onResume();

        // Set up a listener whenever a key changes            
        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
    }
	
	@Override
    protected void onPause() {
        super.onPause();

        // Unregister the listener whenever a key changes            
        getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);    
    }

	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
			String key) {
		// TODO Auto-generated method stub
		String toastMsg = "";
		
		if (key.equals(KEY_CKB_STATUS)) {
			mServiceStatus.setSummary(sharedPreferences.getBoolean(key, false) ? "Disable this setting" : "Enable this setting");
			toastMsg = sharedPreferences.getBoolean(key, false) ? "Service activated!" : "Service deactivated!" ;
        }
		
	
//		if (mServiceStatus.isChecked())
//			toastMsg = "Service activated";
//		else if (!mServiceStatus.isChecked())
//			toastMsg = "Service deactivated";
		
		Toast.makeText(this, toastMsg, Toast.LENGTH_SHORT).show();

		
	}
	
}