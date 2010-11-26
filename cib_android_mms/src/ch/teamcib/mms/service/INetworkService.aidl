package ch.teamcib.mms.service;

interface INetworkService {

	void startService();
	void stopService();
	void setServers(in String[] servers);
	
	String getData();
}