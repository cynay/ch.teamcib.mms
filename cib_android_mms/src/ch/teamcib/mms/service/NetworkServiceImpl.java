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
	private Handler mServiceHandler;
	private Task mTask = new Task();

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
		mServiceHandler = new Handler();
		mServiceHandler.postDelayed(mTask, 4000L);
	}
	
	private void stop(){
		Log.i("-> REMOTE SERVICE", "stop()");
		android.os.Process.killProcess(android.os.Process.myPid());
	}


	@Override
	public void onCreate() {
		super.onCreate();
		Log.i("-> REMOTE SERVICE", "onCreate()");
		
		// init the service here
		//startService();

	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		Log.i("-> REMOTE SERVICE", "onDestroy()");
		//stopService();

	}

	@Override
	public void onStart(Intent intent, int startId){
		super.onStart(intent, startId);
//		mServiceHandler = new Handler();
//		mServiceHandler.postDelayed(mTask, 1000L);
		Log.i("-> REMOTE SERVICE", "onStart()");
	}

	//	private void startService() {
	//		Toast.makeText(this, "Service started", Toast.LENGTH_LONG).show();
	//		
	//		try {
	//	    	TCPSocket tcpSocket = new TCPSocket("192.168.66.103", 1337);
	//
	//	    	mListener = new Listener(tcpSocket);
	//	    	mListener.start();
	//	        Log.i("INFO", "--------------B");
	//			tcpSocket.sendLine("&cmd&nick&" + "cyn");
	//		} catch (IOException e) {
	//			// TODO Auto-generated catch block
	//			e.printStackTrace();
	//		}
	//	}
	//
	//	private void stopService() {
	//		Toast.makeText(this, "Service stopped", Toast.LENGTH_LONG).show();
	//	}



	private class Task implements Runnable {

		private String mHost = "192.168.66.103";
		private int mPort = 1337;
		private TCPSocket mSocket;

		@Override
		public void run() {
			// TODO Auto-generated method stub
			try {
				mSocket = new TCPSocket(mHost,mPort);

				++mTEST;
				mServiceHandler.postDelayed(this,4000L);
				Log.i("-> REMOTE SERVICE", "Task class ");
//				String msg = mSocket.receiveLine();
//				if (msg != null){
//					Log.i("MESSAGE", "Server: \t" + msg);
//				}


			} catch (UnknownHostException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}	
	}

//	/**
//	 * This class/thread waits in an endless loop for incoming messages on
//	 * the specified socket.
//	 * When something arrives it informs the ClientView class.
//	 */
//	private class Listener extends Thread {
//
//		TCPSocket tcpSocket = null;
//
//		/**
//		 * Constructor
//		 *
//		 * @param tcpSocket     the tcpSocket of the server
//		 */
//		public Listener(TCPSocket tcpSocket ){
//			this.tcpSocket = tcpSocket;
//		}
//
//		/**
//		 * this method receives all information from the server
//		 */
//		public void run() {
//			try {
//				while (true) {
//					String msg = tcpSocket.receiveLine();
//					if (msg != null){
//						Log.i("MESSAGE", "Server: \t" + msg);
//					}
//					Thread.sleep(2000);
//					//	clientView.newMessage(tcpSocket.receiveLine());
//				}
//			} catch (IOException e) {
//				e.printStackTrace();
//			} catch (InterruptedException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//		}
//	}
}
