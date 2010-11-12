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
	private Task mThread;

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
	};
	
	private void start(){
		Log.i("-> REMOTE SERVICE", "start()");
		mThread = new Task();
		mThread.isDone = false;
		mThread.start();
	}
	
	private void stop(){
		Log.i("-> REMOTE SERVICE", "stop()");
		mThread.isDone = true;
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
				mSocket = new TCPSocket(mHost,mPort);
				
				mSocket.sendLine("2&hostname&myHostname");
				
				while(!isDone){
					Log.i("-> REMOTE SERVICE", "Task class while() ");
					
					String msg = mSocket.receiveLine();
					if (msg != null){
						Log.i("-> SERVER MESSAGE", "Server: \t" + msg);
						
						String cmd[] = msg.split("&");
						mData = cmd[2];
						
					}
					
					
					mSocket.sendLine("1&null&null");
					
					Thread.sleep(4000);
				}
				
				// send the server an exit message
				mSocket.sendLine("2&exit&myHostname");
				
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (UnknownHostException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			
			Log.i("-> REMOTE SERVICE THREAD", "end of run()");
		}
	}
}
