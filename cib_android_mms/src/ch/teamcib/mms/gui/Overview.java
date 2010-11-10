package ch.teamcib.mms.gui;

import java.io.IOException;

import ch.teamcib.mms.*;
import ch.teamcib.mms.R.*;
import ch.teamcib.mms.service.INetworkService;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.*;

/**
 * @author Yannic Schneider
 */
public class Overview extends Activity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.overview); 
		
		// start service
		NetworkServiceClient.startSvc(this);
		

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

}