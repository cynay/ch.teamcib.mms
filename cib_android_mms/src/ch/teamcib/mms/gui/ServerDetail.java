package ch.teamcib.mms.gui;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
	 
import java.util.List;

import ch.teamcib.mms.DataHelper;
import ch.teamcib.mms.R;
	 
	public class ServerDetail extends Activity {

		private TextView hostname; 
		private TextView lastRefresh; 
		private TextView output; 
		private TextView time; 
		private TextView status; 
		private TextView process; 
		private DataHelper dh;
		private String sHostname;
//		private String iInfo;
	    
	    @Override
	    public void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        setContentView(R.layout.server_detail);

//	        this.output = (TextView) this.findViewById(R.id);
	        hostname = (TextView) findViewById(R.id.hostname);
	        lastRefresh = (TextView) findViewById(R.id.lastRefresh);
	        time = (TextView) findViewById(R.id.time);
	        status = (TextView) findViewById(R.id.status);
	        process = (TextView) findViewById(R.id.process);
	        sHostname = getIntent().getStringExtra("host");
	        hostname.setText(sHostname);

	        // FIXME hostname check if in db !!!
	        
//	        output.append(getIntent().getStringExtra("host"));
//	        Toast.makeText(this, getIntent().getStringExtra("host"), Toast.LENGTH_SHORT).show();
	        dh = new DataHelper(this);
	        
	        time.setText(testTime());
	        status.setText(testFill("status"));
	        process.setText(testFill("calc.exe"));
	        if(time.getText().length() > 7 )
	        	lastRefresh.setText(time.getText().subSequence(0, 8));
//	        testCursor();
//	        testSelectColum();
	        btDiagram();	        
      
	    }
	    
	    private String testFill(String key){
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
	    private String testTime(){
	    	StringBuilder sb = new StringBuilder();
	    	Cursor cursor = dh.selectHostKey(sHostname, "status");

	    	while(cursor.moveToNext()){
	    		sb.append(cursor.getString(0).split(" ")[1] +"\n");

	    	}
	    	
	    	return sb.toString();
	    	
	    }
	    
	    /**
	     * This Button starts the Diagram
	     */
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
	            	
	            	
//	                Intent myIntent = new Intent(view.getContext(), ServerGraph.class);
//	                startActivityForResult(myIntent, 0);
	            }

	        });
	        
	    }
	    
	    private void testCursor()
	    {
	    		StringBuilder sb = new StringBuilder();
	    		Cursor c = this.dh.selectTable();
		        Log.i("-> SERVER DETAIL","Cursor select table");
		        
		        while(c.moveToNext()){
		        	//zuweisung in SB die nacheher im output ausgegben wird
//		        	sb.append(c.getString(0)+ " ");
//		        	sb.append(c.getString(1) +" ");
		        	sb.append(c.getString(2) + " ");
		        	sb.append(c.getString(3) + "\n");
		        }
		        
		        this.output.setText(sb.toString());
	    	
	    }
	    
	    private void testSelectColum()
	    {
	    	StringBuilder sb = new StringBuilder();
	    	Cursor c = dh.selectCol("HT");
	    	Log.i("-> SERVER DETAIL","Cursor select Tabel");
		        
		        while(c.moveToNext()){
		        	//zuweisung in SB die nacheher im output ausgegben wird
		        	sb.append(c.getString(0)+ " ");
		        	sb.append(c.getString(1) +" ");
		        	sb.append(c.getString(2) + " ");
		        	sb.append(c.getString(3) + "\n");
		        }
		        
		        this.output.setText(sb.toString());
	    }

	  
	}