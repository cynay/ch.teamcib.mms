package ch.cynay.book1;

/** 
 * Hilfsklasse zur Umrechnung von Brutto- in Nettobeträge
 * und umgekehrt.
 * 
 * @author Arno Becker, 2010 visionera gmbh
 *
 */
public class Ergebnis {
  
  /** Eingabebetrag in Euro. */
  public float betrag; 
  
  /** 
   * true, falls betrag ein Nettobetrag ist, 
   * sonst false. 
   */
  public boolean isNetto;  
  
  /** Umsatzsteuer in Prozent, z.B. 19 Prozent. */
  public int ustProzent;
  
  /** Ergebniswert, wenn Netto errechnet wurde. */
  public float betragNetto;  
  
  /** Ergebniswert, wenn Brutto errechnet wurde. */
  public float betragBrutto; 
  
  /** Ergebniswert Umsatzsteuer in Euro. */
  public float betragUst;
  
  /**
   * Berechnet Bruttobetrag aus Nettobetrag, falls isNetto
   * true ist. Ansonsten den Nettobetrag aus dem 
   * Bruttobetrag.
   * Die anteilige Umsatzsteuer wird in Euro errechnet.
   */
  public void berechneErgebnis() {
    // Berechne Bruttobetrag aus Nettobetrag:
    if (isNetto) {
      betragNetto = betrag;
      betragUst = betrag * ustProzent / 100;
      betragBrutto = betrag + betragUst;
    } else {
      // Berechne Nettobetrag aus Bruttobetrag:
      betragBrutto = betrag;
      betragUst = betrag * ustProzent /
          (100 + ustProzent);
      betragNetto = betrag - betragUst;
    }
  }

}
