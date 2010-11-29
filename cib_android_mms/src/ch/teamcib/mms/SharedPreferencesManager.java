/**
 * 
 */
package ch.teamcib.mms;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.Map.Entry;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import ch.teamcib.mms.gui.Preferences;
import ch.teamcib.mms.service.INetworkService;
import ch.teamcib.mms.service.NetworkServiceImpl;

/**
 * @author Yannic Schneider
 *
 */
public class SharedPreferencesManager {
	// ===========================================================
    // Finals
    // ===========================================================
	private static final String SERVER_LIST = "hm_servers";
	private static final int NUMBER_OF_SERVERS = 12;
	
	// ===========================================================
    // Members
    // ===========================================================
	private static HashMap<String, String> mServers = new HashMap<String, String>();
	public static String mRefreshRate = "20";

	/**
	 * 
	 * @param c			Context for the SharedPreferences
	 * @param server	the Server to add
	 * @param port		the Port for the connection
	 */
	public static void addServer(Context c, String server, String port){		
		SharedPreferences keyValues = c.getSharedPreferences(SERVER_LIST, 
				Context.MODE_PRIVATE);
		
		SharedPreferences.Editor keyValuesEditor = keyValues.edit();
		keyValuesEditor.putString(server, port);
		
		keyValuesEditor.commit();
	}
	
	/**
	 * 
	 * @param c			Context for the SharedPreferences
	 * @param server	the Server to remove from the list
	 */
	public static void removeServer(Context c, String server){		
		SharedPreferences keyValues = c.getSharedPreferences(SERVER_LIST, 
				Context.MODE_PRIVATE);
		
		SharedPreferences.Editor keyValuesEditor = keyValues.edit();
		keyValuesEditor.remove(server);
		
		keyValuesEditor.commit();
	}
	

	@SuppressWarnings("unchecked")
	public static String[] getServers(Context c){
		String[] servers = new String[NUMBER_OF_SERVERS];
		SharedPreferences keyValues = c.getSharedPreferences(SERVER_LIST, 
				Context.MODE_PRIVATE);
		
		HashMap<String, String> hm = (HashMap<String, String>) 
			keyValues.getAll();
		
		int counter = 0;
		for (String srv : hm.keySet() ) {
			servers[counter] = srv;
			counter++;
		}
		
		return servers;
	}
	
	// ===========================================================
    // Testing stuff ...
    // ===========================================================
	
	/**
	 * Deletes all the servers which are saved.
	 * 
	 * @param c The Context of the Preferences
	 */
	public static void deleteServers(Context c){
		SharedPreferences keyValues = c.getSharedPreferences(SERVER_LIST, 
				Context.MODE_PRIVATE);
		
		SharedPreferences.Editor keyValuesEditor = keyValues.edit();
		keyValuesEditor.clear();
		keyValuesEditor.commit();
	}
	
	public static void setServers(Context c){
		mServers.put("caffein.ch", "1337");
		mServers.put("megapanzer.com", "1337");
		mServers.put("micro$oft.com", "1337");
		mServers.put("omfg.com", "1337");
		
		SharedPreferences keyValues = c.getSharedPreferences(SERVER_LIST, 
				Context.MODE_PRIVATE);
		
		SharedPreferences.Editor keyValuesEditor = keyValues.edit();
		
		for (String srv : mServers.keySet() ) {
			keyValuesEditor.putString(srv, mServers.get(srv));
		}
		
		keyValuesEditor.commit();
	}
}
