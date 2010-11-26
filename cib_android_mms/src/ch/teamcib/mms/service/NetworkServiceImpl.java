package ch.teamcib.mms.service;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import ch.teamcib.mms.TCPSocket;
import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.RemoteException;
import android.util.Log;
import android.widget.Toast;


/**
 * NetworkServiceImpl
 *
 * @author Yannic Schneider
 */
public class NetworkServiceImpl extends Service {
	
	// ===========================================================
    // Finals
    // ===========================================================
	private static final int NUMBER_OF_SERVERS = 12;

	private String[] servers = new String[NUMBER_OF_SERVERS];
//	private Task[] mTasks;
	private String[] mValues = new String[NUMBER_OF_SERVERS];

	private String mData;

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return mBinder;
	}

	private INetworkService.Stub mBinder = new INetworkService.Stub() {

		@Override
		public String getData() throws RemoteException {
			// TODO Auto-generated method stub
			mData = "";
			for (String val : mValues){
				if(val != null)
					mData += val;
			}
			return mData;
		}

		@Override
		public void startService() throws RemoteException {
			// TODO Auto-generated method stub
			start();
		}

		@Override
		public void stopService() throws RemoteException {
			// TODO Auto-generated method stub
			stop();
		}

		@Override
		public void setServers(String[] servers) throws RemoteException {
			// TODO Auto-generated method stub
			NetworkServiceImpl.this.servers = servers;
		}		
	};
	
	private void start(){
		Log.i("-> REMOTE SERVICE", "start()");
		
		for (int i = 0; i < servers.length; i++){
			Log.i("-> REMOTE SERVICE", "Value: " + servers[i]);
			if(servers[i] != null)
				new Task(servers[i],1337 ,i).start();
		}
		
//		mThread = new Task();
//		mThread.isDone = false;
//		mThread.start();
	}
	
	private void stop(){
		Log.i("-> REMOTE SERVICE", "stop()");
//		mThread.isDone = true;
		this.stopSelf();
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
	

	/**
	 * 
	 * @author Yannic Schneider
	 *
	 */
	private class Task extends Thread {

		private String mHost = "192.168.66.103";
		private int mPort = 1337;
		private int mNumber;
		private TCPSocket mSocket;
		
		public boolean isDone = false;
		
		public Task(String hostname, int port, int number){
			this.mHost = hostname;
			this.mPort = port;
			this.mNumber = number;
		}
		

		@Override
		public void run() {
			// TODO Auto-generated method stub
			Log.i("-> REMOTE SERVICE THREAD", "run()");
			try {
				mSocket = new TCPSocket(mHost,mPort);
				
				mSocket.sendLine("2&hostname&" + mHost);
				
				String msg = mSocket.receiveLine();
				if (msg != null){
					Log.i("-> SERVER MESSAGE", "Server: \t" + msg);
					
//					String cmd[] = msg.split("&");
//					mValues[mNumber] = cmd[2];
					
				}
				
				mSocket.sendLine("1&null&null");
				
				msg = mSocket.receiveLine();
				if (msg != null){
					Log.i("-> SERVER MESSAGE", "Server: \t" + msg);
					
					String cmd[] = msg.split("&");
					mValues[mNumber] = "&" + mHost + ";" + cmd[2];
					
				}
				
				
				
//				while(!isDone){
//					
//					Log.i("-> REMOTE SERVICE", "Task class while() ");
//					
//					String msg = mSocket.receiveLine();
//					if (msg != null){
//						Log.i("-> SERVER MESSAGE", "Server: \t" + msg);
//						
//						String cmd[] = msg.split("&");
//						mValues[mNumber] = cmd[2];
//						
//					}
//					
//					mSocket.sendLine("1&null&null");
//					isDone = true;
//					Thread.sleep(2000);
//				}
				
				// send the server an exit message
				mSocket.sendLine("2&exit&" + mHost);
				
			} catch (UnknownHostException e) {
				// TODO Auto-generated catch block
				mValues[mNumber] = mHost + ";false";
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				mValues[mNumber] = mHost + ";false";
				e.printStackTrace();
			}
			
			
			Log.i("-> REMOTE SERVICE THREAD", "end of run()");
		}
	}
}
