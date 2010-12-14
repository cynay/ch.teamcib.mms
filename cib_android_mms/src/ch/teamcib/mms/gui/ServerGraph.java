package ch.teamcib.mms.gui;

import ch.teamcib.mms.DataHelper;
import ch.teamcib.mms.GraphView;
import ch.teamcib.mms.R;
import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;

public class ServerGraph extends Activity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
//		setContentView(R.layout.server_detail); 
		
		String hostname = getIntent().getStringExtra("host");
		
		DataHelper dh = new DataHelper(getBaseContext());
		Cursor cur = dh.selectHostKey(hostname, "mem");
		 
		cur.moveToFirst();
		float[] vals = new float[cur.getCount()];
		int i = 0;
		while ( cur.isAfterLast() == false) {
//			Toast.makeText(this, cur.getString(3).split("/")[0], Toast.LENGTH_SHORT).show();
			if (cur.getString(3).equals("~")){
				vals[i] = 0;
			} else {
				vals[i] = Float.valueOf(cur.getString(3).split("/")[0]).floatValue() / 1000;
			}
			
			i++;
			cur.moveToNext();
		}
		cur.close();
		
//		float[] values = new float[] { 2.0f,1.5f, 2.5f, 1.0f , 3.0f, 6.0f,2.0f };
//		String[] verlabels = new String[] { "4 GB", "3 GB", "2 GB","1 GB", "0 GB" };
		String[] horlabels = new String[] { "", "past", "", "", "", "now", "" };
		GraphView graphView = new GraphView(this, vals, "RAM workload",horlabels, 4, GraphView.LINE);
		setContentView(graphView);

		
	}

}
