package ch.teamcib.mms;

import ch.teamcib.mms.*;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Spinner;

/**
 * @author 
 */
public class Overview extends Activity {

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.overview);
  }

  
  @Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);

		getMenuInflater().inflate(R.menu.menu, menu);

		return true;
	}
  
  @Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.opt_add:
			return true;
		case R.id.opt_editSettings:
			startActivity(new Intent(this, EditSettings.class));
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