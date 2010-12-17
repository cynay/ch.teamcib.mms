package ch.teamcib.mms.service;

/**
*  Interface definition for the connection between the GUI-Process and the 
*  Background-Process.
*/
interface INetworkService {

	/**
	 * method for starting the service
	 */
	void startService();
	/**
	 * method for stopping the service
	 */
	void stopService();
	/**
	 * method for invoking a single manual refresh
	 */
	void singleRefresh();
	/**
	 * sets the serverlist in the Background-Service 
	 * 
	 * @param servers array with the server to get the actual data
	 */
	void setServers(in String[] servers);
	
	/**
	 * method for checking if the refresh is done
	 */
	boolean isRefreshed();
	/**
	 * method for getting the newest data
	 */
	String getData();
	/**
	 * method for synchronizing the GUI-Countdowntimer with the Background-
	 * Process timer.
	 */
	long getTimerMillis();
}