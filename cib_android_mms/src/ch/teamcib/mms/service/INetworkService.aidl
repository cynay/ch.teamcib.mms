package ch.teamcib.mms.service;

/**
*  Interface for the connection between the GUI-Process and the Background-
*  Process.
*/
interface INetworkService {

	void startService();
	void stopService();
	void singleRefresh();
	void setServers(in String[] servers);
	
	boolean isRefreshed();
	String getData();
	long getTimerMillis();
}