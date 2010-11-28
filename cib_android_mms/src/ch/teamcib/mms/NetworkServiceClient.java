/**
 * 
 */
package ch.teamcib.mms;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import ch.teamcib.mms.gui.Preferences;
import ch.teamcib.mms.service.INetworkService;
import ch.teamcib.mms.service.NetworkServiceImpl;

/**
 * @author Yannic Schneider
 *
 */
public class NetworkServiceClient {
	private volatile static NetworkServiceClient mNetClient = null;
	private static INetworkService mService;
	private static boolean mStarted = false;
	
	private NetworkServiceClient(){		
	}
	
	/**
	 * Method for getting the instance of the NetworkServiceClient
	 * Thread-safe singleton!
	 * 
	 * @return 
	 */
	public static NetworkServiceClient getNetClient(){
		if(mNetClient == null){
			// this is needed if two threads are waiting at the monitor at the
            // time when singleton was getting instantiated
			synchronized(NetworkServiceClient.class){
				if(mNetClient == null){
					mNetClient = new NetworkServiceClient();
				}
			}			
		}
		
		return mNetClient;
	}
	
	
	public static void startSvc(Context c){
		Intent svc = new Intent(c, NetworkServiceImpl.class);
		c.startService(svc);
		mStarted = true;
	}
	
	public static void stopSvc(Context c){
		Intent svc = new Intent(c, NetworkServiceImpl.class);
	    c.stopService(svc);
	    mStarted = false;
	}
	
	
	public static void bindSvc(Context c){
		c.bindService(new Intent(c, NetworkServiceImpl.class),
				mConnection, Context.BIND_AUTO_CREATE);
		
		Log.i("-> NETWORK SERVICE CLIENT", "bindSvc()");
	}
	
	public static void unbindSvc(Context c){
		c.unbindService(mConnection);
//		mConnection = null;
		
		Log.i("-> NETWORK SERVICE CLIENT", "unbindSvc()");
	}
	
	public static INetworkService getService(){
		return mService;
	}
	

	
	/**
	 * 
	 */
	private static ServiceConnection mConnection = new ServiceConnection() {

		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			// TODO Auto-generated method stub
			// This is called when the connection with the service has been
			// established, giving us the service object we can use to
			// interact with the service.  We are communicating with our
			// service through an IDL interface, so get a client-side
			// representation of that from the raw service object.
			mService = INetworkService.Stub.asInterface(service);
			Log.i("-> NETWORK SERVICE CLIENT", "Connected to service.");


		}

		@Override
		public void onServiceDisconnected(ComponentName name) {
			// TODO Auto-generated method stub
			// This is called when the connection with the service has been
			// unexpectedly disconnected -- that is, its process crashed.
			mService = null;
			Log.i("-> NETWORK SERVICE CLIENT", "Disconnected from service");

		}

	};
}
