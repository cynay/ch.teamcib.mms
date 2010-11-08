package ch.teamcib.mms.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.widget.Toast;


/**
 * NetworkServiceImpl
 *
 * @author Yannic Schneider
 */
public class NetworkServiceImpl extends Service {
	
	
	// TODO add server list etc.

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	
	@Override
	public void onCreate() {
		super.onCreate();

		// init the service here
		startService();

	}

	@Override
	public void onDestroy() {
		super.onDestroy();

		stopService();

	}
	
	private void startService() {
		Toast.makeText(this, "Service started", Toast.LENGTH_LONG).show();
	}

	private void stopService() {
		Toast.makeText(this, "Service stopped", Toast.LENGTH_LONG).show();
	}

}
