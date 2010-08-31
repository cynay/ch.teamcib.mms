package ch.cynay.book1;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Spinner;

/**
 * @author Arno Becker, 2010 visionera gmbh
 */
public class FormActivity extends Activity {
  
  /** Id des Menüeintrags. */
  public static final int AUSRECHNEN_ID = Menu.FIRST;
  
  /** Schlüssel für den Betragswert. */
  public static final String BETRAG_KEY = "betrag";
  
  /** Schlüssel für die Betragsart (Brutto, Netto). */
  public static final String BETRAG_ART = "art";
  
  /** Schlüssel für den Prozentwert der Umsatzsteuer. */
  public static final String UST_PROZENT = "ust";

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.main);
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    menu.add(0, AUSRECHNEN_ID, Menu.NONE, "Umrechnen");
    return super.onCreateOptionsMenu(menu);
  }
  
  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      case AUSRECHNEN_ID:
        // Betrag:
        final EditText txtBetrag = 
          (EditText) findViewById(R.id.edt_betrag);       
        final float betrag = 
          Float.parseFloat(txtBetrag.getText().toString());
        
        // Art des Betrags (Brutto, Netto):
        boolean isNetto = true;
        final RadioGroup rg = 
          (RadioGroup) findViewById(R.id.rg_art);
        switch (rg.getCheckedRadioButtonId()) {
          case R.id.rb_art_netto:
            isNetto = true;
            break;
          case R.id.rb_art_brutto:
            isNetto = false;
            break;
          default:            
        }
       
        // Prozentwert Umsatzsteuer:
        final Spinner spinner =
          (Spinner) findViewById(R.id.sp_umsatzsteuer);
        final int pos = spinner.getSelectedItemPosition();
        final int[] prozentwerte = 
          getResources().getIntArray(R.array.ust_werte);
        final int prozentwert = prozentwerte[pos];
        
        final Intent intent = new Intent(this,
            ErgebnisActivity.class);
        
        intent.putExtra(BETRAG_KEY, betrag);
        intent.putExtra(BETRAG_ART, isNetto);
        intent.putExtra(UST_PROZENT, prozentwert);
        
        startActivity(intent);        
        
       default:          
    }

    return super.onOptionsItemSelected(item);
  }  
}