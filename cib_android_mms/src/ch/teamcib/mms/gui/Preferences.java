package ch.teamcib.mms.gui;

import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.PreferenceActivity;
import android.widget.Toast;
import ch.teamcib.mms.*;

/**
 * 
 * @author cYnaY
 *
 */
public class Preferences extends PreferenceActivity implements OnSharedPreferenceChangeListener {
	
	private CheckBoxPreference mServiceStatus;

	@Override
	public void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		
		this.addPreferencesFromResource(R.layout.preference);
		
		getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
	}

	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
			String key) {
		// TODO Auto-generated method stub
		mServiceStatus = (CheckBoxPreference)getPreferenceScreen().findPreference("ckb_status");
		
		if (mServiceStatus.isChecked() == true)
			Toast.makeText(this, "Service activated", Toast.LENGTH_SHORT).show();
		else if (mServiceStatus.isChecked() == false)
			Toast.makeText(this, "Service deactivated", Toast.LENGTH_SHORT).show();
		
	}
	
}