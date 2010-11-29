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
	private String mOldName = null;
	
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
		
		EditText et = (EditText)findViewById(R.id.edt_hostname);
		Editable txt = et.getText();
		String hostname = txt.toString();
		
		if (mOldName != null){
			SharedPreferencesManager.removeServer(this, mOldName);
		}
		SharedPreferencesManager.addServer(this, hostname, "1337");

		Toast.makeText(this, "Server saved!", Toast.LENGTH_SHORT).show();
		this.finish();
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.server_config); 
		
		try {
//			Toast.makeText(this, savedInstanceState.getString("key") , Toast.LENGTH_SHORT).show();
			Bundle bun = getIntent().getExtras();
			String srvName = bun.getString("key");
//			String srvName = getIntent().getExtras().getString("server");
			EditText edt_hostname = (EditText)findViewById(R.id.edt_hostname);
			edt_hostname.setText(srvName);
			mOldName = srvName;
		
		} catch (Exception e){
			// TODO
			Toast.makeText(this, "ERROR:" + e.getMessage() , Toast.LENGTH_SHORT).show();
		}			
	}
}
