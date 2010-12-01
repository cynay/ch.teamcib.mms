package ch.teamcib.mms.service;

interface INetworkService {

	void startService();
	void stopService();
	void singleRefresh();
	void setServers(in String[] servers);
	
	boolean isRefreshed();
	String getData();
}