package ch.teamcib.mms.service;

import java.io.IOException;
import java.net.UnknownHostException;
import ch.teamcib.mms.DataHelper;
import ch.teamcib.mms.SPManager;
import ch.teamcib.mms.TCPSocket;
import android.app.Service;
import android.content.Intent;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.widget.Toast;


/**
 * NetworkServiceImpl is our Implementation of our Background-Service.
 * 
 * This Service can be activated an when it is it does a Refresh of all servers
 * saved in the serverslist in a specified interval. 
 *
 * @author Yannic Schneider
 */
public class NetworkServiceImpl extends Service {
	// ===========================================================
    // Finals
    // ===========================================================
	private static final int NUMBER_OF_SERVERS = 12;

	// ===========================================================
    // Members
    // ===========================================================
	private String[] mServers = new String[NUMBER_OF_SERVERS];
	private String[] mValues = new String[NUMBER_OF_SERVERS];
	
	private Handler mHandler = new Handler();
	private String mData;
	private int mRefreshedCounter = 0;
	private long mTimerMillis = 0;
	private boolean mIsRefreshed = false;

	
	@Override
	public IBinder onBind(Intent intent) {
		return mBinder;
	}

	private void updateServerList(){
		Log.i("-> REMOTE SERVICE", "updateServerList()");
		mIsRefreshed = false;
		
		for (int i = 0; i < mServers.length; i++){
			Log.i("-> REMOTE SERVICE", "Value: " + mServers[i]);
			if(mServers[i] != null){
				Log.i("-> REMOTE SERVICE", "Create Task for: " + mServers[i]);
				mRefreshedCounter++;
				
				new Task(mServers[i],1337 ,i).start();
			}
		}		
	}

	@Override
	public void onCreate() {
		super.onCreate();
		Log.i("-> REMOTE SERVICE", "onCreate()");
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		Log.i("-> REMOTE SERVICE", "onDestroy()");
	}

	@Override
	public void onStart(Intent intent, int startId){
		super.onStart(intent, startId);
		Log.i("-> REMOTE SERVICE", "onStart()");
	}
	
	private INetworkService.Stub mBinder = new INetworkService.Stub() {

		@Override
		public String getData() throws RemoteException {
			mData = "";
			for (String val : mValues){
				if(val != null)
					mData += val + "&";
			}
			return mData;
		}

		@Override
		public void startService() throws RemoteException {
			mHandler.removeCallbacks(mRefreshTask);
	        mHandler.postDelayed(mRefreshTask, 100);
		}

		@Override
		public void stopService() throws RemoteException {
			mHandler.removeCallbacks(mRefreshTask);
		}

		@Override
		public void setServers(String[] servers) throws RemoteException {
			NetworkServiceImpl.this.mServers = servers;
		}

		@Override
		public void singleRefresh() throws RemoteException {
			updateServerList();
		}

		@Override
		public boolean isRefreshed() throws RemoteException {
			Log.i("-> REMOTE SERVICE", "isRefreshed() returns: " + mIsRefreshed);
			return mIsRefreshed;
		}

		@Override
		public long getTimerMillis() throws RemoteException {
			return mTimerMillis;
		}		
	};
	
	/**
	 * This is used for the periodic refresh of the servers list.
	 */
	private Runnable mRefreshTask = new Runnable() {

		public void run() {
			new CountDownTimer(SPManager.getConfigValueLong(
					getBaseContext(), SPManager.KEY_REFRESHRATE), 500) {

				@Override
				public void onTick(long millisUntilFinished) {
					mTimerMillis = millisUntilFinished;

					// check if bg-service is deactivated in the meantime	
					if(!SPManager.getConfigValue(getBaseContext(),
							SPManager.KEY_SERVICESTATUS)) {
						Log.i("-> REMOTE SERVICE THREAD", "timer cancel()...");
						this.cancel();
					}
				}

				@Override
				public void onFinish() {
					// Refresh date and restart Timer
					Log.i("-> REMOTE SERVICE THREAD", "timer onFinish()");
					mTimerMillis = -1;

					// do a refresh if not deactivated in the meantime
					if(SPManager.getConfigValue(getBaseContext(),
							SPManager.KEY_SERVICESTATUS)) {

						Toast.makeText(getBaseContext(), "MMS: refreshing data!", 
								Toast.LENGTH_SHORT).show();
						updateServerList();

						mHandler.postDelayed(mRefreshTask, 200);
					}
				}
			}.start();
		}
	};
	

	/**
	 * Each server in the servers list is updated in his own thread. Task
	 * handles the connection and communicates with the server. 
	 * The gathered data is written into the SQLite-DB. 
	 *
	 */
	private class Task extends Thread {

		private String mHost = "127.0.0.1";
		private int mPort = 1337;
		private int mNumber;
		private TCPSocket mSocket;
		
		public Task(String hostname, int port, int number){
			this.mHost = hostname;
			this.mPort = port;
			this.mNumber = number;
		}
		

		@Override
		public void run() {
			Log.i("-> REMOTE SERVICE THREAD", "run() with -> " + mHost);
			try {
				mSocket = new TCPSocket(mHost,mPort);
				mSocket.sendLine("2&hostname&" + mHost);
				
				String msg = mSocket.receiveLine();
				if (msg != null){
					Log.i("-> SERVER MESSAGE", "Server: \t" + msg);		
				}
				
				mSocket.sendLine("1&null&null");
				
				msg = mSocket.receiveLine();
				if (msg != null){
					Log.i("-> SERVER MESSAGE", "Server: \t" + msg);
					
					String cmd[] = msg.split("&");
					mValues[mNumber] = mHost + ";" + cmd[2];
					
					// saving data into DB
					DataHelper dh = new DataHelper(getBaseContext());
					String vals[] = cmd[2].split(";");
					dh.InsertIntoTable(mHost, "status", "online");
					dh.InsertIntoTable(mHost, vals[0].split("=")[0], 
							vals[0].split("=")[1]);
					dh.InsertIntoTable(mHost, vals[1].split("=")[0], 
							vals[1].split("=")[1]);
					dh.closeDB();
				}

				// send the server an exit message to abort the connection
				mSocket.sendLine("2&exit&" + mHost);
				
			} catch (UnknownHostException e) {
				Log.i("-> REMOTE SERVICE THREAD", "UnknownHostException: " + mHost);
				mValues[mNumber] = mHost + ";offline";
			} catch (IOException e) {
				Log.i("-> REMOTE SERVICE THREAD", "IOException: " + mHost);
				mValues[mNumber] = mHost + ";offline";
				
				// writing an entry into the DB that the server is not reachable
				DataHelper dh = new DataHelper(getBaseContext());
				dh.InsertIntoTable(mHost, "status", "offline");
				dh.InsertIntoTable(mHost, "mem", "~");
				dh.InsertIntoTable(mHost, "calc.exe", "~");
				dh.closeDB();
				
			} finally {
				// decrement the refresh counter
				// if its the last thread set the refreshed flag to true
				mRefreshedCounter--;
				Log.i("-> REMOTE SERVICE THREAD", "refreshCounter -> " + mRefreshedCounter);
				if(mRefreshedCounter == 0)
					mIsRefreshed = true;
			}
			
			Log.i("-> REMOTE SERVICE THREAD", "end of run() of -> " + mHost);
		}
	}
}