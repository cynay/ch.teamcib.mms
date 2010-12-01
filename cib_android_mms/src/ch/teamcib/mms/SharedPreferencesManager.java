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
	private static final String APP_CONFIG  = "hm_app";
	
	public static final String KEY_REFRESHRATE   = "key_RefreshRate";
	public static final String KEY_FIRSTRUN	  = "key_FirstRun";
	public static final String KEY_SERVICESTATUS = "key_ServiceStatus";
	
	private static final int NUMBER_OF_SERVERS = 12;
	
	// ===========================================================
    // Members
    // ===========================================================
	private static HashMap<String, Integer> mServers = new HashMap<String, Integer>();
	
	public static String mRefreshRate = "20";

	/**
	 * 
	 * @param c			Context for the SharedPreferences
	 * @param server	the Server to add
	 * @param port		the Port for the connection
	 */
	public static void addServer(Context c, String server, int port){		
		SharedPreferences keyValues = c.getSharedPreferences(SERVER_LIST, 
				Context.MODE_PRIVATE);
		
		SharedPreferences.Editor keyValuesEditor = keyValues.edit();
		keyValuesEditor.putInt(server, port);
		
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
	
	public static void addConfigValue(Context c, String key, boolean value){
		SharedPreferences keyValues = c.getSharedPreferences(APP_CONFIG, 
				Context.MODE_PRIVATE);
		
		SharedPreferences.Editor keyValuesEditor = keyValues.edit();
		keyValuesEditor.putBoolean(key, value);
		
		keyValuesEditor.commit();
	}
	
	public static void addConfigValueLong(Context c, String key, long value){
		SharedPreferences keyValues = c.getSharedPreferences(APP_CONFIG, 
				Context.MODE_PRIVATE);
		
		SharedPreferences.Editor keyValuesEditor = keyValues.edit();
		keyValuesEditor.putLong(key, value);
		
		keyValuesEditor.commit();
	}
	
	public static boolean getConfigValue(Context c, String key){
		SharedPreferences keyValues = c.getSharedPreferences(APP_CONFIG, 
				Context.MODE_PRIVATE);
		
		return keyValues.getBoolean(key, false);
	}
	
	public static long getConfigValueLong(Context c, String key){
		SharedPreferences keyValues = c.getSharedPreferences(APP_CONFIG, 
				Context.MODE_PRIVATE);
		
		return keyValues.getLong(key, 60000);
	}
	
	public static boolean getFirstRun(Context c){
		boolean firstRun = false;
		SharedPreferences keyValues = c.getSharedPreferences(APP_CONFIG, 
				Context.MODE_PRIVATE);
		
		if (keyValues.getBoolean(KEY_FIRSTRUN, true) == true ){
			SharedPreferences.Editor keyValuesEditor = keyValues.edit();
			
			// Set the default values for first run
			keyValuesEditor.putBoolean(KEY_FIRSTRUN, false);
			keyValuesEditor.putLong(KEY_REFRESHRATE, 60000);
			keyValuesEditor.putBoolean(KEY_SERVICESTATUS, false);
			
			keyValuesEditor.commit();
			firstRun = true;
		}
		
		return firstRun;
	}
	
	public static int getServerPort(Context c, String server){
		SharedPreferences keyValues = c.getSharedPreferences(SERVER_LIST, 
				Context.MODE_PRIVATE);
		return keyValues.getInt(server, 1337);
	}

	@SuppressWarnings("unchecked")
	public static String[] getServers(Context c){
		String[] servers = new String[NUMBER_OF_SERVERS];
		SharedPreferences keyValues = c.getSharedPreferences(SERVER_LIST, 
				Context.MODE_PRIVATE);
		
		HashMap<String, Integer> hm = (HashMap<String, Integer>) 
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
		mServers.put("caffein.ch", 1337);
		mServers.put("megapanzer.com", 1337);
		mServers.put("micro$oft.com", 1337);
		
		SharedPreferences keyValues = c.getSharedPreferences(SERVER_LIST, 
				Context.MODE_PRIVATE);
		
		SharedPreferences.Editor keyValuesEditor = keyValues.edit();
		
		for (String srv : mServers.keySet() ) {
			keyValuesEditor.putInt(srv, mServers.get(srv));
		}
		
		keyValuesEditor.commit();
	}
}
