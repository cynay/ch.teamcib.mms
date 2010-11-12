package ch.teamcib.mms.server;


public class OperatingSystemData 
{
	
	//private static string cpu= "";
	private static double d;
	public OperatingSystemData()
	{}
	
	public static void getData()
	{
		java.lang.management.OperatingSystemMXBean data = java.lang.management.
			ManagementFactory.getOperatingSystemMXBean();
		d = data.getSystemLoadAverage();
		System.out.println(d);
	
	}
	
	public static void sendData()
	{
		GUI g = new GUI();
		g.updateTextArea(Double.toString(d));
	}
}


