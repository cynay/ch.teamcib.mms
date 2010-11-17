package ch.teamcib.mms.gui;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import ch.teamcib.mms.*;

/**
 * 
 * @author Yannic Schneider
 *
 */
public class ServerConfig extends Activity {
	
	/**
	 * called when clickt on button 'cancel'.
	 * 
	 * @see res.layout.editor.xml
	 * 
	 * @param sfNormal
	 *            Button
	 * 
	 * @version Android 1.6 >
	 */
	public void onClickCancel(final View sfNormal) {
		finish();
	}
	
	/**
	 * called when clickt on button 'save'.
	 * 
	 * @see res.layout.editor.xml
	 * 
	 * @param sfNormal
	 *            Button
	 * 
	 * @version Android 1.6 >
	 */
	public void onClickSave(final View sfNormal) {
		// Get the EditText and Button References
		
		Toast.makeText(this, "TODO", Toast.LENGTH_SHORT).show();
		
//		txt_editor = (EditText)findViewById(R.id.txt_editor);
//		Editable txt = txt_editor.getText();
//		boolean what = FileOperations.saveCipherFile(txt.toString(),this.PASSWORD);
//		
//		Context context = getApplicationContext();
//		CharSequence text = "Failed to save!";
//		int duration = Toast.LENGTH_SHORT;
//		if (what == true)
//			text = "Saved!";		
//
//		Toast toast = Toast.makeText(context, text, duration);
//		toast.show();
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.server_config); 
		
		Context context = getApplicationContext();
		CharSequence text = "TODO!";
		int duration = Toast.LENGTH_SHORT;

		Toast toast = Toast.makeText(context, text, duration);
		toast.show();
			
	}
}
