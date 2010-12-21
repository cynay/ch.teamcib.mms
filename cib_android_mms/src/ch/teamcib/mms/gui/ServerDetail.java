package ch.teamcib.mms.gui;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import ch.teamcib.mms.DataHelper;
import ch.teamcib.mms.R;

/**
 * The ServerDetail activity shows the detail information of a selected 
 * Server. The shown details are: refresh times, status (online, offline), 
 * process (running, down, ~). 
 * 
 * @author CiB
 *
 */
public class ServerDetail extends Activity {

	// ===========================================================
    // Members
    // ===========================================================
	private TextView hostname; 
	private TextView lastRefresh;
	private TextView time; 
	private TextView status; 
	private TextView process; 
	private DataHelper dh;
	private String sHostname;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.server_detail);

		hostname = (TextView) findViewById(R.id.hostname);
		lastRefresh = (TextView) findViewById(R.id.lastRefresh);
		time = (TextView) findViewById(R.id.time);
		status = (TextView) findViewById(R.id.status);
		process = (TextView) findViewById(R.id.process);
		sHostname = getIntent().getStringExtra("host");
		hostname.setText(sHostname);

		// FIXME hostname check if in db !!!
		// do in next version

		dh = new DataHelper(this);

		time.setText(fillTime());
		status.setText(fillData("status"));
		process.setText(fillData("calc.exe"));
		if(time.getText().length() > 7 )
			lastRefresh.setText(time.getText().subSequence(0, 8));
		btDiagram();	        

	}

	private String fillData(String key){
		StringBuilder sb = new StringBuilder();
		Cursor cursor = dh.selectHostKey(sHostname, key);

		while(cursor.moveToNext()){
			if (key.equals("date")) {
				sb.append(cursor.getString(0).split(" ")[1] +"\n");
			} else {
				sb.append(cursor.getString(3)+ "\n");
			}
		}

		return sb.toString();
	}
	
	private String fillTime(){
		StringBuilder sb = new StringBuilder();
		Cursor cursor = dh.selectHostKey(sHostname, "status");

		while(cursor.moveToNext()){
			sb.append(cursor.getString(0).split(" ")[1] +"\n");
		}
		
		return sb.toString();
	}

	private void btDiagram()
	{
		Button btDiagram = (Button) findViewById(R.id.bt_diagram);
		btDiagram.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				// Create an Intent with the Hostname added to extras
				Intent i = new Intent();
				i.putExtra("host", sHostname );    			
				i.setClass(getBaseContext(), ServerGraph.class);
				startActivity(i);
			}
		});
	}
}