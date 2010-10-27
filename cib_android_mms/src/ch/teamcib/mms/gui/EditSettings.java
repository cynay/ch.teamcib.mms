package ch.teamcib.mms.gui;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.widget.*;

public class EditSettings extends Activity{
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		Context context = getApplicationContext();
		CharSequence text = "TODO!";
		int duration = Toast.LENGTH_SHORT;

		Toast toast = Toast.makeText(context, text, duration);
		toast.show();
		
		finish();	
	}
}
