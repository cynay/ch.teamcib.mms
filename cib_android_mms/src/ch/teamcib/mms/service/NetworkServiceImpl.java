package ch.teamcib.mms.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;
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
		return networkService;
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
	
	private INetworkService.Stub networkService = new INetworkService.Stub() {
		
		
		@Override
		public boolean stopService() throws RemoteException {
			// TODO Auto-generated method stub
			return false;
		}
		
		@Override
		public boolean startService() throws RemoteException {
			// TODO Auto-generated method stub
			return false;
		}
		
		@Override
		public String getData() throws RemoteException {
			// TODO Auto-generated method stub
			return null;
		}
	};

}
