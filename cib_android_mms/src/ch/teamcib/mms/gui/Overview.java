package ch.teamcib.mms.gui;

import java.io.IOException;
import java.util.ArrayList;

import ch.teamcib.mms.*;
import ch.teamcib.mms.R.*;
import ch.teamcib.mms.service.INetworkService;
import android.app.Activity;
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
import android.widget.ListView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;

/**
 * @author Yannic Schneider
 */
public class Overview extends Activity {
	
	// ===========================================================
    // Final Fields
    // ===========================================================
    protected static final int CONTEXTMENU_DELETEITEM = 0;

    // ===========================================================
    // Fields
    // ===========================================================
    protected ListView mFavList;
    protected ArrayList<Favorite> fakeFavs = new ArrayList<Favorite>();
	
	private INetworkService mNetworkService;
	private RefreshTask mThread;
	private Handler mHandler = new Handler();
	private TextView mTimer;
	private ImageButton mImgBtn1;
	private ImageButton mImgBtn2;
	private boolean mBoolStatus = true;
	
	private Drawable d1;
	private Drawable d2;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.overview); 
		
		
		// Get the TableLayout
//        TableLayout tl = (TableLayout) findViewById(R.id.maintable);
		mImgBtn1 = (ImageButton) findViewById(R.id.btn_status);
		mImgBtn2 = (ImageButton) findViewById(R.id.btn_status2);
		
		d1 = mImgBtn1.getBackground();
		d2 = mImgBtn2.getBackground();
		
        mTimer = (TextView) findViewById(R.id.timer);
        
        mNetworkService = NetworkServiceClient.getService();

		// start service
		NetworkServiceClient.startSvc(this);
//		mThread = new RefreshTask();
//		mThread.setActivity(tl,this);
//		mThread.start();
//		imgBtn = (ImageButton) this.findViewById(R.id.btn_status);
//		Log.i("-> GUI OVERVIEW", "next is changeImg()");

		// NEW //
		
		/* Add some items to the list the listview will be showing. */
        fakeFavs.add(new Favorite("localhost", "online"));
        fakeFavs.add(new Favorite("caffein.ch", "online"));
        fakeFavs.add(new Favorite("micro$oft.com", "offline"));

        this.mFavList = (ListView) this.findViewById(R.id.list_servers);
        // this triggers the ContextMenu of an Item in the List with just a Click 
        mFavList.setOnItemClickListener(new OnItemClickListener() {
        	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
        		arg1.showContextMenu();
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
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		
		// unbind from service
		NetworkServiceClient.unbindSvc(this);
		Log.i("-> OVERVIEW", "onPause()");
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
		boolean bStat;
		
		
		public void run() {
			
			if (reset){
				new CountDownTimer(10000, 200) {

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
				    		 	mImgBtn1.setBackgroundDrawable(d2);
				    		 	mImgBtn1.refreshDrawableState();
				    		 	mBoolStatus = false;
							} else {
								mImgBtn1.setBackgroundDrawable(d1);
								mImgBtn1.refreshDrawableState();
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
	
	private class RefreshTask extends Thread {
		
		Context c;
		ImageButton imgBtn;
		TableLayout tl;
		boolean b = true;
		Drawable d = Drawable.createFromPath("@drawable/emo_im_cool");
		Drawable d2 = Drawable.createFromPath("@drawable/emo_im_crying");
		
		@Override
		public void run() {
			
			// Create a TableRow
	        TableRow tr = new TableRow(c);
	        tr.setLayoutParams(new LayoutParams( LayoutParams.FILL_PARENT, 
	        		LayoutParams.WRAP_CONTENT));
			
//			// Create a TextView 
//	        TextView labelTV = new TextView(c);
//	        labelTV.setText("MIAU");
//	        labelTV.setTextColor(Color.BLUE);
//	        labelTV.setTextSize(12);
//	        //tr.addView(labelTV);
//	        tr.addView(labelTV, 50, 16);
	        
	        ImageButton i = new ImageButton(c);
	        i.setBackgroundDrawable(d);
	        tr.addView(i);

	        // Add the TableRow to the TableLayout
	        tl.addView(tr, new TableLayout.LayoutParams(
	        		LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
			
			while(true){
				if (b){
					i.setBackgroundDrawable(d2);
					b = false;
				} else {
					i.setBackgroundDrawable(d);
					b = true;
				}
				try {
					Thread.sleep(4000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
		}
		
		public void setActivity(TableLayout tl, Context c){
			this.tl = tl;
			this.c = c;
		}
		
	}
	


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
            	menu.setHeaderTitle("ContextMenu");
				menu.add(0, CONTEXTMENU_DELETEITEM, 1, "Delete this server!");
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

		/* Switch on the ID of the item, to get what the user selected. */
		switch (aItem.getItemId()) {
		case CONTEXTMENU_DELETEITEM:
			/* Get the selected item out of the Adapter by its position. */
			Favorite favContexted = (Favorite) mFavList.getAdapter().getItem(info.position);
			/* Remove it from the list.*/
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