package ch.cynay.book1;


import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

/**
 * @author Arno Becker, 2010 visionera gmbh
 */
public class ErgebnisActivity extends Activity {

  @Override
  public void onCreate(Bundle icicle) {
    super.onCreate(icicle);
    setContentView(R.layout.ergebnis_anzeigen);

    final Bundle extras = getIntent().getExtras();
    
    if (extras != null) {
    	final Ergebnis ergebnis = new Ergebnis();
    	
    	ergebnis.betrag = 
    	  extras.getFloat(FormActivity.BETRAG_KEY);
    	ergebnis.isNetto = 
    	  extras.getBoolean(FormActivity.BETRAG_ART, 
    	      true);
    	ergebnis.ustProzent = 
    	  extras.getInt(FormActivity.UST_PROZENT);
    	
    	zeigeErgebnis(ergebnis);
    }
  }
  
  /**
   * 
   * @param ergebnis 
   */
  private void zeigeErgebnis(Ergebnis ergebnis) {
    setTitle("Ergebnis");
    
    ergebnis.berechneErgebnis();

    final TextView txtNettobetrag = 
      (TextView) findViewById(R.id.txt_nettobetrag);
    txtNettobetrag.setText(String.valueOf(
        ergebnis.betragNetto));

    final TextView txtUmsatzsteuer = 
      (TextView) findViewById(R.id.txt_umsatzsteuer);
    txtUmsatzsteuer.setText(
        String.valueOf(ergebnis.betragUst));

    final TextView txtBruttobetrag = 
      (TextView) findViewById(R.id.txt_bruttobetrag);
    txtBruttobetrag.setText(
        String.valueOf(ergebnis.betragBrutto));
  }
}
