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
	   
	   private TextView output; 
	   private DataHelper dh;
	   private String iInfo;
	    
	    @Override
	    public void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        setContentView(R.layout.server_detail);

	        this.output = (TextView) this.findViewById(R.id.out_text);

	        output.append(getIntent().getStringExtra("host"));
	        Toast.makeText(this, getIntent().getStringExtra("host"), Toast.LENGTH_SHORT).show();
	        this.dh = new DataHelper(this);

	        testCursor();
//	        testSelectColum();
	        btDiagram();
	        
      
	    }
	    
	    /**
	     * This Button starts the Diagram
	     */
	    private void btDiagram()
	    {
	        Button btDiagram = (Button) findViewById(R.id.bt_diagram);
	        btDiagram.setOnClickListener(new View.OnClickListener() {
	            public void onClick(View view) {
	                Intent myIntent = new Intent(view.getContext(), ServerGraph.class);
	                startActivityForResult(myIntent, 0);
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