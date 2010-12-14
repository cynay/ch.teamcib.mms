package ch.teamcib.mms;

import java.util.HashMap;
import android.content.Context;
import android.content.SharedPreferences;

/**
 * The SPManager class is for handling the access to the applications 
 * SharedPreferences. It is used to read or write to/from the SharedPreferences.
 * All persistent Data should be saved to the SharedPreferences with this class.
 * 
 * @author Yannic Schneider
 *
 */
public class SPManager {
	// ===========================================================
    // Finals
    // ===========================================================
	private static final String SERVER_LIST = "hm_servers";
	private static final String APP_CONFIG  = "hm_app";
	
	public static final String KEY_REFRESHRATE   = "key_RefreshRate";
	public static final String KEY_FIRSTRUN	     = "key_FirstRun";
	public static final String KEY_SERVICESTATUS = "key_ServiceStatus";
	
	private static final int NUMBER_OF_SERVERS = 12;
	
	// ===========================================================
    // Members
    // ===========================================================
	private static HashMap<String, Integer> mServers = new HashMap<String, Integer>();
	

	/**
	 * adds a Server to the Server HashMap.
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
	 * removes a Server from the saved HashMap.
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
	
	/**
	 * adds a key-value pair to the stored config.
	 * 
	 * @param c			Context for the SharedPreferences
	 * @param key		Identifies the value (one of public finals of this class)
	 * @param value		The value for the specified key
	 */
	public static void addConfigValue(Context c, String key, boolean value){
		SharedPreferences keyValues = c.getSharedPreferences(APP_CONFIG, 
				Context.MODE_PRIVATE);
		
		SharedPreferences.Editor keyValuesEditor = keyValues.edit();
		keyValuesEditor.putBoolean(key, value);
		
		keyValuesEditor.commit();
	}
	
	/**
	 * adds a key-value pair to the stored config.
	 * 
	 * @param c			Context for the SharedPreferences
	 * @param key		Identifies the value (one of public finals of this class)
	 * @param value		The value for the specified key
	 */
	public static void addConfigValueLong(Context c, String key, long value){
		SharedPreferences keyValues = c.getSharedPreferences(APP_CONFIG, 
				Context.MODE_PRIVATE);
		
		SharedPreferences.Editor keyValuesEditor = keyValues.edit();
		keyValuesEditor.putLong(key, value);
		
		keyValuesEditor.commit();
	}
	
	/**
	 * read a value for the given key out of the stored config.
	 * 
	 * @param c			Context for the SharedPreferences
	 * @param key		Identifies the value (one of public finals of this class)
	 * @return	returns the boolean value for the given key
	 */
	public static boolean getConfigValue(Context c, String key){
		SharedPreferences keyValues = c.getSharedPreferences(APP_CONFIG, 
				Context.MODE_PRIVATE);
		
		return keyValues.getBoolean(key, false);
	}
	
	/**
	 * read a value for the given key out of the stored config.
	 * 
	 * @param c			Context for the SharedPreferences
	 * @param key		Identifies the value (one of public finals of this class)
	 * @return	returns the long value for the given key
	 */
	public static long getConfigValueLong(Context c, String key){
		SharedPreferences keyValues = c.getSharedPreferences(APP_CONFIG, 
				Context.MODE_PRIVATE);
		
		return keyValues.getLong(key, 60000);
	}
	
	/**
	 * For initializing the default values of the SharedPreferences and 
	 * checking if the Application runs for the First time. 
	 * 
	 * @param c			Context for the SharedPreferences
	 * @return returns true if the Application runs for the first time.
	 */
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
	
	/**
	 * Retrieve the Port to bind to the Socket for a given Server.
	 * 
	 * @param c			Context for the SharedPreferences
	 * @param server	The Hostname or IP of the Server to lookup the Port.
	 * @return returns the Port to use for the given Server (default: 1337)
	 */
	public static int getServerPort(Context c, String server){
		SharedPreferences keyValues = c.getSharedPreferences(SERVER_LIST, 
				Context.MODE_PRIVATE);
		return keyValues.getInt(server, 1337);
	}

	/**
	 * Retrieve an array of all saved Servers. The array consists of the IP or
	 * Hostname for every stored Server.
	 * 		example: "caffein.ch", "192.168.1.1" 
	 * 
	 * @param c			Context for the SharedPreferences
	 * @return	returns a String-array with all the saved Servers IPs or names.
	 */
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
