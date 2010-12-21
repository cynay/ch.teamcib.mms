package ch.teamcib.mms.gui;

import ch.teamcib.mms.*;
import ch.teamcib.mms.service.INetworkService;
import java.util.ArrayList;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.RemoteException;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.*;
import android.view.ContextMenu;
import android.view.View;
import android.view.View.OnCreateContextMenuListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;

/**
 * @author CiB
 */
public class Overview extends Activity {
	
	// ===========================================================
    // Finals 
    // ===========================================================
	protected static final int CONTEXTMENU_EDITITEM   = 0;
    protected static final int CONTEXTMENU_DELETEITEM = 1;

    // ===========================================================
    // Members
    // ===========================================================
    protected ListView mSrvList;
    protected ArrayList<Server> mServers = new ArrayList<Server>();
	
	private INetworkService mNetworkService;
	private Handler mHandler = new Handler();
	private TextView mTimer;


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.overview); 
		
		mTimer = (TextView) findViewById(R.id.timer);
 
		// Set default data if its firstrun
		if (SPManager.getFirstRun(this) == true)
			Toast.makeText(this, "Enjoy this app!", Toast.LENGTH_LONG).show();
        
		// start service
		NetworkServiceClient.startSvc(this);

        mSrvList = (ListView) findViewById(R.id.list_servers);
        
        // this triggers the detail view of an Item in the List with a Click 
        mSrvList.setOnItemClickListener(new OnItemClickListener() {
        	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, 
        			long arg3) {

        		Server srvContexted;
        		/* Get the selected item out of the Adapter by its position. */
    			srvContexted = (Server) mSrvList.getAdapter().getItem(arg2);
        		
    			// Create an Intent with the Hostname added to extras
    			Intent i = new Intent();
        		i.putExtra("host", srvContexted.getName() );    			
    			i.setClass(getBaseContext(), ServerDetail.class);
    			startActivity(i);
        	}
        });

        initListView();
		
        new UpdateTimerTask().execute((Void)null);

	}
	
	
	@Override
	protected void onResume() {
		super.onResume();
		Log.i("-> OVERVIEW", "onResume()");
		
		// bind to service
		NetworkServiceClient.bindSvc(this);		
		
		// start the timer if service is running 
		if(SPManager.getConfigValue(getBaseContext(),
				SPManager.KEY_SERVICESTATUS)) {
			new UpdateTimerTask().execute((Void)null);
		}
		
		/* refresh items for the list the listview  */
		mServers.clear();
		String[] servers = SPManager.getServers(this);
		for (int i = 0; i < servers.length; i++){
			if(servers[i] != null)
				mServers.add(new Server(servers[i],"~"));
		}
		
		refreshFavListItems();
		
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		Log.i("-> OVERVIEW", "onPause()");
		
		// unbind from service
		NetworkServiceClient.unbindSvc(this);		
	}
	
	/**
	 * called when clickt on button 'refresh'.
	 * 
	 * @see res.layout.editor.xml
	 * 
	 * @param sfNormal
	 *            Button
	 * 
	 * @version Android 1.6 >
	 */
	public void onClickRefresh(final View sfNormal) {
		new ServerUpdater().execute((Void)null);
	}
	
	private class ServerUpdater extends AsyncTask<Void, Void, Void>  {
		private ProgressDialog	pd	= null;
		private final Context	c	= Overview.this;
		
		protected void onPreExecute(){
			pd = ProgressDialog.show(c, "Working", "Refreshing servers status ...", 
					true, false);
		}
		
		protected void onPostExecute(Void result){
			try {
				String data = mNetworkService.getData();
				Log.i("-> OVERVIEW", data );

//				DataHelper dh = new DataHelper(c);

				String servers[] = data.split("&");

				mServers.clear();

				
				/* refresh items for the list the listview  */
				for (int i = 0; i < servers.length; i++){
					if(servers[i] != null){
						String svr[] = servers[i].split(";");
						if(!svr[1].equalsIgnoreCase("offline")){
							String kv[] = svr[1].split("=");
							//	dh.InsertIntoTable(svr[0], kv[0], kv[1]);
							mServers.add(new Server(svr[0], "online"));
						} else {
							//	dh.InsertIntoTable(svr[0], "status" , svr[1] );
							mServers.add(new Server(svr[0], "offline"));
						} 
					}
				}

				refreshFavListItems();

			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
			
			pd.dismiss();
		}
		
		@Override
		protected Void doInBackground(Void... params) {
			// TODO Auto-generated method stub
			try {
				mNetworkService = NetworkServiceClient.getService();
				mNetworkService.setServers(SPManager.getServers(c));
				mNetworkService.singleRefresh();
				
				while(!mNetworkService.isRefreshed()){
					Thread.sleep(1000);
				}

			} catch (Exception e) {
				// TODO Auto-generated catch block
				Log.e("-> ERROR OVERVIEW", e.getMessage());
			} 
			return null;
		}
	}
	

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);

		// Initialize the menu
		getMenuInflater().inflate(R.menu.menu, menu);

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.opt_add:
			if(mServers.size() == 12){
				Toast.makeText(this, "Serverlist is full! (max. 12 Servers)", 
						Toast.LENGTH_LONG).show();
				return true;
			}
			startActivity(new Intent(this, ServerConfig.class));
			return true;
		case R.id.opt_editSettings:
			startActivity(new Intent(this, Preferences.class));
			return true;
		case R.id.opt_help:
			startActivity(new Intent(this, ShowHelp.class));
			return true;
		case R.id.opt_exit:
			finish();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}
	
	private class UpdateTimerTask extends AsyncTask<Void, Void, Void>  {
		long mTimerMillis;
		
		protected void onPostExecute(Void result){
			
			if(SPManager.getConfigValue(getBaseContext(),
					SPManager.KEY_SERVICESTATUS)) {
//				Log.i("-> OVERVIEW", "restart timer");
				mTimer.setText("Next refresh in  " + 
						formatTime( mTimerMillis ));
				new UpdateTimerTask().execute((Void)null);
			} else {
				mTimer.setText("Autorefresh inactive!");
			}
		}
		
		@Override
		protected Void doInBackground(Void... params) {
			try {
				Thread.sleep(500);
				mNetworkService = NetworkServiceClient.getService();
				mTimerMillis = mNetworkService.getTimerMillis();

			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			return null;
		}
		
		private String formatTime(long millis) {
			String output = "00:00:00";
			long seconds = millis / 1000;
			long minutes = seconds / 60;
			long hours = minutes / 60;

			seconds = seconds % 60;
			minutes = minutes % 60;
			hours = hours % 60;

			String secondsD = String.valueOf(seconds);
			String minutesD = String.valueOf(minutes);
			String hoursD = String.valueOf(hours); 

			if (seconds < 10)
				secondsD = "0" + seconds;
			if (minutes < 10)
				minutesD = "0" + minutes;
			if (hours < 10)
				hoursD = "0" + hours;

			output = hoursD + ":" + minutesD + ":" + secondsD;
			return output;
		}
	}


	// NEW
	private void refreshFavListItems() {
		mSrvList.setAdapter(new ArrayAdapter<Server>(this, 
				android.R.layout.simple_list_item_1, mServers));
	}

	private void initListView() {
		/* Loads the items to the ListView. */
		refreshFavListItems();

		/* Add Context-Menu listener to the ListView. */
		mSrvList.setOnCreateContextMenuListener(new OnCreateContextMenuListener() {
			 
            public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
            	menu.setHeaderTitle("Context Menu");
            	menu.add(0, CONTEXTMENU_EDITITEM, 1, "Edit");
				menu.add(0, CONTEXTMENU_DELETEITEM, 2, "Delete!");
				/* Add as many context-menu-options as you want to. */
			}
		});
	}

	// ===========================================================
	// Methods from SuperClass/Interfaces
	// ===========================================================
	@Override
	public boolean onContextItemSelected(MenuItem aItem) {
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) aItem.getMenuInfo();
		Server favContexted;
			
		/* Switch on the ID of the item, to get what the user selected. */
		switch (aItem.getItemId()) {
		case CONTEXTMENU_EDITITEM:
			/* Get the selected item out of the Adapter by its position. */
			favContexted = (Server) mSrvList.getAdapter().getItem(info.position);
			
			// TODO open the edit activity
			Intent i = new Intent();

			Bundle bun = new Bundle();
			bun.putString("key", favContexted.getName());
			
			i.setClass(this, ServerConfig.class);
			i.putExtras(bun);						

			startActivity(i);

			refreshFavListItems();
			return true; /* true means: "we handled the event". */
			
		case CONTEXTMENU_DELETEITEM:
			/* Get the selected item out of the Adapter by its position. */
			favContexted = (Server) mSrvList.getAdapter().getItem(info.position);
			/* Remove it from the list.*/
			SPManager.removeServer(this, favContexted.name);
			mServers.remove(favContexted);

			refreshFavListItems();
			return true; /* true means: "we handled the event". */
		}
		return false;
	}

	// ===========================================================
    // Inner and Anonymous Classes
    // ===========================================================
    /** 
     * Small class holding some basics Serverinfos
     */
	protected class Server {

		protected String name;
		protected String status;

		/**
		 * Creates a Server object 
		 * 
		 * @param name	The Hostname (or IP-address) of the Server
		 * @param status The status of the Server as String
		 */
		protected Server(String name, String status) {
			this.name = name;
			this.status = status;
		}

		/** 
		 * The ListView is going to display the toString() return-value! 
		 * 
		 * @return a String with the Server name and status.
		 */
		public String toString() {
			return name + " [" + status + "]";
		}
		
		/**
		 * Returns the name of the Server
		 * @return name of the Server 
		 */
		public String getName(){
			return name;
		}


//		public boolean equals(Object o) {
//			return o instanceof Server && ((Server) o).name.compareTo(name) == 0;
//		}
	}
}