package ch.teamcib.mms;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;
import ch.teamcib.mms.service.INetworkService;
import ch.teamcib.mms.service.NetworkServiceImpl;

/**
 * NetworkServiceClient class is used to handle the connection from the
 * GUI-Process to the Background-Process.
 * It is implemented as Thread-safe singleton so there is only one instance
 * used for binding and unbinding to the Background-Service over the AIDL-
 * Interface. 
 * 
 * @author Yannic Schneider
 *
 */
public class NetworkServiceClient {
	
	// ===========================================================
    // Members
    // ===========================================================
	private volatile static NetworkServiceClient mNetClient = null;
	private static INetworkService mService;
	
	private NetworkServiceClient(){		
	}
	
	/**
	 * Method for getting the instance of the NetworkServiceClient
	 * Thread-safe singleton!
	 * 
	 * @return an Instance of itself 
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
	
	/**
	 * method for starting the Background-Service
	 * 
	 * @param c the Context from the calling object
	 */
	public static void startSvc(Context c){
		Intent svc = new Intent(c, NetworkServiceImpl.class);
		c.startService(svc);
	}
	
	/**
	 * method for stopping the Background-Service
	 * 
	 * @param c the Context from the calling object
	 */
	public static void stopSvc(Context c){
		Intent svc = new Intent(c, NetworkServiceImpl.class);
	    c.stopService(svc);
	}
	
	/**
	 * method for connecting to an application service, creating it if needed. 
	 * This defines a dependency between your application and the service.
	 * 
	 * @param c the Context from the calling object
	 */
	public static void bindSvc(Context c){
		c.bindService(new Intent(c, NetworkServiceImpl.class),
				mConnection, Context.BIND_AUTO_CREATE);
		
		Log.i("-> NETWORK SERVICE CLIENT", "bindSvc()");
	}
	
	/**
	 * method for disconnecting from an application service. The service is now 
	 * allowed to stop at any time.
	 * 
	 * @param c the Context from the calling object
	 */
	public static void unbindSvc(Context c){
		c.unbindService(mConnection);
		
		Log.i("-> NETWORK SERVICE CLIENT", "unbindSvc()");
	}
	
	/**
	 * getter for the Interface for the connection between the GUI-Process and 
	 * the Background- Process.
	 * 
	 * @return an Interface for the connection 
	 */
	public static INetworkService getService(){
		return mService;
	}
	

	
	/**
	 * Interface for monitoring the state of an application service. 
	 */
	private static ServiceConnection mConnection = new ServiceConnection() {

		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
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
			// This is called when the connection with the service has been
			// unexpectedly disconnected -- that is, its process crashed.
			mService = null;
			Log.i("-> NETWORK SERVICE CLIENT", "Disconnected from service");
		}
	};
}
