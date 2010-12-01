package ch.teamcib.mms.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import ch.teamcib.mms.DataHelper;
import ch.teamcib.mms.TCPSocket;
import ch.teamcib.mms.gui.Overview;
import android.app.Service;
import android.content.Context;
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
	private String[] mValues = new String[NUMBER_OF_SERVERS];

	private String mData;
	private int mRefreshedCounter = 0;
	private boolean mIsRefreshed = false;

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
					mData += val + "&";
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

		@Override
		public void singleRefresh() throws RemoteException {
			// TODO Auto-generated method stub
			mIsRefreshed = false;
			updateServerList();
		}

		@Override
		public boolean isRefreshed() throws RemoteException {
			// TODO Auto-generated method stub
			Log.i("-> REMOTE SERVICE", "isRefreshed() returns: " + mIsRefreshed);
			return mIsRefreshed;
		}		
	};
	
	private void updateServerList(){
		Log.i("-> REMOTE SERVICE", "updateServerList()");
		
		for (int i = 0; i < servers.length; i++){
			Log.i("-> REMOTE SERVICE", "Value: " + servers[i]);
			if(servers[i] != null){
				Log.i("-> REMOTE SERVICE", "Create Task for: " + servers[i]);
				mRefreshedCounter++;
				new Task(servers[i],1337 ,i).start();
				
				
//				try {
//					Log.i("-> REMOTE SERVICE", "Next create address :" + servers[i]);
//					InetAddress address = InetAddress.getByName(servers[i]);
//					Log.i("-> REMOTE SERVICE", "Next isReachable? :" + servers[i]);
//					if (address.isReachable(2000)){
//						Log.i("-> REMOTE SERVICE", "Next isReachable = TRUE :" + servers[i]);
//						new Task(servers[i],1337 ,i).start();
//					}
//				} catch (UnknownHostException e) {
//					// TODO Auto-generated catch block
//					mValues[i] = servers[i] + ";false";
//					e.printStackTrace();
//				} catch (IOException e) {
//					// TODO Auto-generated catch block
//					mValues[i] = servers[i] + ";false";
//					e.printStackTrace();
//				}
			}
		}
		
//		mThread = new Task();
//		mThread.isDone = false;
//		mThread.start();
		
	}
	
	private void start(){
		Log.i("-> REMOTE SERVICE", "start()");
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

		private String mHost = "127.0.0.1";
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
			Log.i("-> REMOTE SERVICE THREAD", "run() with -> " + mHost);
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
					mValues[mNumber] = mHost + ";" + cmd[2];
					
					// SAVE TO DB
					DataHelper dh = new DataHelper(getBaseContext());
					String vals[] = cmd[2].split(";");
//					Log.i("-> SERVER MESSAGE", "Server: \t" + vals[0].split("=")[1]);
//					Log.i("-> SERVER MESSAGE", "Server: \t" + vals[1].split("=")[0]);
					dh.InsertIntoTable(mHost, "status", "online");
					dh.InsertIntoTable(mHost, vals[0].split("=")[0], vals[0].split("=")[1]);
					dh.InsertIntoTable(mHost, vals[1].split("=")[0], vals[1].split("=")[1]);
					dh.closeDB();
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
				Log.i("-> REMOTE SERVICE THREAD", "UnknownHostException: " + mHost);
				mValues[mNumber] = mHost + ";offline";
//				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				Log.i("-> REMOTE SERVICE THREAD", "IOException: " + mHost);
				mValues[mNumber] = mHost + ";offline";
				
				// SAVE TO DB
				DataHelper dh = new DataHelper(getBaseContext());
				dh.InsertIntoTable(mHost, "status", "offline");
				dh.InsertIntoTable(mHost, "mem", "~");
				dh.InsertIntoTable(mHost, "calc.exe", "~");
				dh.closeDB();
				
//				e.printStackTrace();
			} finally {
				// set refreshcounter and if its the last thread set flag
				mRefreshedCounter--;
				Log.i("-> REMOTE SERVICE THREAD", "refreshCounter -> " + mRefreshedCounter);
				if(mRefreshedCounter == 0)
					mIsRefreshed = true;

			}
			
			
					
			Log.i("-> REMOTE SERVICE THREAD", "end of run() of -> " + mHost);
		}
	}
	


	private void ping(String hostname) {

		String ip = hostname;
		String pingResult = "";

		String pingCmd = "ping " + ip;

		try {
			Runtime r = Runtime.getRuntime();
			Process p = r.exec(pingCmd);

			BufferedReader in = new BufferedReader(new
					InputStreamReader(p.getInputStream()));
			String inputLine;
			while ((inputLine = in.readLine()) != null) {
				System.out.println(inputLine);
				pingResult += inputLine;
			}
			in.close();

		}//try
		catch (IOException e) {
			System.out.println(e);
		}

	}
}
