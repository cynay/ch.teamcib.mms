package ch.teamcib.mms.service;

interface INetworkService {
	
	boolean startService();
	boolean stopService();
	
	String getData();
}