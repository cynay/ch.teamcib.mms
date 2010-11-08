package ch.teamcib.mms.gui;

import java.io.IOException;

import ch.teamcib.mms.*;
import ch.teamcib.mms.R.*;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.*;

/**
 * @author 
 */
public class Overview extends Activity {

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.overview); 
    try {
    	TCPSocket tcpSocket = new TCPSocket("192.168.66.103", 1337);

        Listener listener = new Listener(tcpSocket);
        listener.start();
        Log.i("INFO", "--------------B");
		tcpSocket.sendLine("&cmd&nick&" + "cyn");
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
    
//    Thread myThread = new Thread(new TCPClient());
//    myThread.start();
    
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
			startActivity(new Intent(this, Preferences.class));
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
   
  
  
  /**
   * This class/thread waits in an endless loop for incoming messages on
   * the specified socket.
   * When something arrives it informs the ClientView class.
   */
  private class Listener extends Thread {
      
      TCPSocket tcpSocket = null;

      /**
       * Constructor
       *
       * @param tcpSocket     the tcpSocket of the server
       */
      public Listener(TCPSocket tcpSocket ){
          this.tcpSocket = tcpSocket;
      }

      /**
       * this method receives all information from the server
       */
      public void run() {
          try {
              while (true) {
            	  String msg = tcpSocket.receiveLine();
            	  if (msg != null){
            		  Log.i("MESSAGE", "Server: \t" + msg);
            	  }
            	  Thread.sleep(2000);
//                  clientView.newMessage(tcpSocket.receiveLine());
              }
          } catch (IOException e) {
                  e.printStackTrace();
          } catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
      }
  }
}