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

	// TODO add server list etc.
	private List<String> mServers = new ArrayList<String>();
	private Thread mThread;

	private int mTEST = 0;

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return mBinder;
	}

	private INetworkService.Stub mBinder = new INetworkService.Stub() {

		@Override
		public String getData() throws RemoteException {
			// TODO Auto-generated method stub
			return Integer.toString(mTEST);
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
	};
	
	private void start(){
		Log.i("-> REMOTE SERVICE", "start()");
		mThread = new Task();
		((Task) mThread).isDone = false;
		mThread.start();
	}
	
	private void stop(){
		Log.i("-> REMOTE SERVICE", "stop()");
		((Task) mThread).isDone = true;
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
		private TCPSocket mSocket;
		
		public boolean isDone = false;

		@Override
		public void run() {
			// TODO Auto-generated method stub
			Log.i("-> REMOTE SERVICE THREAD", "run()");
			try {
//				mSocket = new TCPSocket(mHost,mPort);
				
				while(!isDone){
					++mTEST;
					
					Log.i("-> REMOTE SERVICE", "Task class while() ");
					Thread.sleep(4000);
				}
				
				
//				String msg = mSocket.receiveLine();
//				if (msg != null){
//					Log.i("MESSAGE", "Server: \t" + msg);
//				}


			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			Log.i("-> REMOTE SERVICE THREAD", "end of run()");
		}
	}
}
