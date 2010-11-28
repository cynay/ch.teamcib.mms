package ch.teamcib.mms.gui;

import java.io.IOException;
import java.util.ArrayList;

import ch.teamcib.mms.*;
import ch.teamcib.mms.R.*;
import ch.teamcib.mms.service.INetworkService;
import android.app.Activity;
import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteException;
import android.os.SystemClock;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.*;
import android.widget.TableLayout.LayoutParams;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View.OnCreateContextMenuListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;

/**
 * @author Yannic Schneider
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
    protected ListView mFavList;
    protected ArrayList<Favorite> fakeFavs = new ArrayList<Favorite>();
	
	private INetworkService mNetworkService;
//	private RefreshTask mThread;
	private Handler mHandler = new Handler();
	private TextView mTimer;

	private boolean mBoolStatus = true;

	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.overview); 
		
		
        mTimer = (TextView) findViewById(R.id.timer);
        
//        mNetworkService = NetworkServiceClient.getService();

		// start service
		NetworkServiceClient.startSvc(this);

		// NEW //
		
//		SharedPreferencesManager.setServers(this); //TODO remove after testing

        this.mFavList = (ListView) this.findViewById(R.id.list_servers);
        
        // this triggers the detail view of an Item in the List with a Click 
        mFavList.setOnItemClickListener(new OnItemClickListener() {
        	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, 
        			long arg3) {
        		startActivity(new Intent(arg1.getContext(), ServerDetail.class));
        	}
        });

        initListView();
		
		
		// /NEW //

		mHandler.removeCallbacks(mUpdateTimeTask);
        mHandler.postDelayed(mUpdateTimeTask, 100);
	
	}
	
	
	@Override
	protected void onResume() {
		super.onResume();
		
		// bind to service
		NetworkServiceClient.bindSvc(this);
		Log.i("-> OVERVIEW", "onResume()");
//		mNetworkService = NetworkServiceClient.getService();
		
		fakeFavs.clear();
		
		/* refresh items for the list the listview  */
		String[] servers = SharedPreferencesManager.getServers(this);
		for (int i = 0; i < servers.length; i++){
			if(servers[i] != null)
				fakeFavs.add(new Favorite(servers[i],"test"));
		}
		
		refreshFavListItems();
		
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		
		// unbind from service
		NetworkServiceClient.unbindSvc(this);
		Log.i("-> OVERVIEW", "onPause()");
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
//		Toast.makeText(this, "TODO: refresh", Toast.LENGTH_SHORT).show();
		updateServers();		
		
	}
	
	private void updateServers(){
		try {
			mNetworkService = NetworkServiceClient.getService();
			mNetworkService.setServers(SharedPreferencesManager.getServers(this));
			mNetworkService.startService();
			Thread.sleep(4000);
			String data = mNetworkService.getData();
			Log.i("-> OVERVIEW", data );
			
			DataHelper dh = new DataHelper(this);
			
			
			
			
			String servers[] = data.split("&");
			
			fakeFavs.clear();
			
			/* refresh items for the list the listview  */
			for (int i = 0; i < servers.length; i++){
				if(servers[i] != null){
					String svr[] = servers[i].split(";");
					if(!svr[1].equalsIgnoreCase("false")){
						String kv[] = svr[1].split("=");
						dh.InsertIntoTable(svr[0], kv[0], kv[1]);
					} else {
						dh.InsertIntoTable(svr[0], "status" , svr[1] );
					}
					fakeFavs.add(new Favorite(svr[0], svr[1]));
				}
			}
			
			refreshFavListItems();
			
			
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void setBoolStatus(boolean status){
		this.mBoolStatus = status;
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

	
	private Runnable mUpdateTimeTask = new Runnable() {	
		private boolean reset = true;		
		
		public void run() {
			if (reset){
				new CountDownTimer(60000, 200) {

				     public void onTick(long millisUntilFinished) {
				    	 mTimer.setText("Next refresh in: " + 
				    			 millisUntilFinished / 1000);
				     }

				     public void onFinish() {
//				    	 try {
//							bStat = ( mNetworkService.getData().equalsIgnoreCase("true") ? true : false);
//							
//						} catch (RemoteException e) {
//							// TODO Auto-generated catch block
//							e.printStackTrace();
//						}
				    	 
				    	 
				    	 if (mBoolStatus){
				    		 	mBoolStatus = false;
							} else {
								mBoolStatus = true;
							}
				         reset = true;
				     }
				  }.start();
				  reset = false;
			}

			mHandler.postDelayed(this, 200);
		}
	};	


	// NEW
	private void refreshFavListItems() {
		mFavList.setAdapter(new ArrayAdapter<Favorite>(this, 
				android.R.layout.simple_list_item_1, fakeFavs));
	}

	private void initListView() {
		/* Loads the items to the ListView. */
		refreshFavListItems();

		/* Add Context-Menu listener to the ListView. */
		mFavList.setOnCreateContextMenuListener(new OnCreateContextMenuListener() {
			 
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
		Favorite favContexted;
			
		/* Switch on the ID of the item, to get what the user selected. */
		switch (aItem.getItemId()) {
		case CONTEXTMENU_EDITITEM:
			/* Get the selected item out of the Adapter by its position. */
			favContexted = (Favorite) mFavList.getAdapter().getItem(info.position);
			
			// TODO open the edit activity
			Intent i = new Intent();
//			Dialog dia = (Dialog) dialog;
//			EditText srvName = (EditText)dia.findViewById(R.id.txt_password);

			Bundle bun = new Bundle();
			bun.putString("key", favContexted.toString());
			
			i.setClass(this, ServerConfig.class);
			i.putExtras(bun);						

			startActivity(i);
			
//			startActivity(new Intent(this, ServerConfig.class));

			refreshFavListItems();
			return true; /* true means: "we handled the event". */
			
		case CONTEXTMENU_DELETEITEM:
			/* Get the selected item out of the Adapter by its position. */
			favContexted = (Favorite) mFavList.getAdapter().getItem(info.position);
			/* Remove it from the list.*/
			SharedPreferencesManager.removeServer(this, favContexted.name);
			fakeFavs.remove(favContexted);

			refreshFavListItems();
			return true; /* true means: "we handled the event". */
		}
		return false;
	}

	// ===========================================================
    // Inner and Anonymous Classes
    // ===========================================================
    /** Small class holding some basic */
	protected class Favorite {

		protected String name;
		protected String status;

		protected Favorite(String name, String status) {
			this.name = name;
			this.status = status;
		}

		/** The ListView is going to display the toString() return-value! */
		public String toString() {
			return name + " [" + status + "]";
		}

		public boolean equals(Object o) {
			return o instanceof Favorite && ((Favorite) o).name.compareTo(name) == 0;
		}
	}
}