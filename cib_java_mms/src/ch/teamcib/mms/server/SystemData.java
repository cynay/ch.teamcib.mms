package ch.teamcib.mms.server;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import javax.management.MBeanServerConnection;

import oshi.*;
import oshi.hardware.HardwareAbstractionLayer;
import oshi.hardware.Processor;
import oshi.software.os.OperatingSystem;
import oshi.util.FormatUtil;

public class SystemData {
	public SystemData() { 
		boolean result = SystemData.isRunning("calc.exe");

		System.out.println("Is Calculator running ?  " + (result ? " Yes" : "No"));
	}

	public static boolean isRunning(String process) {
		boolean found = false;
		try {
			File file = File.createTempFile("realhowto",".vbs");
			file.deleteOnExit();
			FileWriter fw = new java.io.FileWriter(file);

			String vbs = "Set WshShell = WScript.CreateObject(\"WScript.Shell\")\n"
				+ "Set locator = CreateObject(\"WbemScripting.SWbemLocator\")\n"
				+ "Set service = locator.ConnectServer()\n"
				+ "Set processes = service.ExecQuery _\n"
				+ " (\"select * from Win32_Process where name='" + process +"'\")\n"
				+ "For Each process in processes\n"
				+ "wscript.echo process.Name \n"
				+ "Next\n"
				+ "Set WSHShell = Nothing\n";

			fw.write(vbs);
			fw.close();
			Process p = Runtime.getRuntime().exec("cscript //NoLogo " + file.getPath());
			BufferedReader input =
				new BufferedReader
				(new InputStreamReader(p.getInputStream()));
			String line;
			line = input.readLine();
			if (line != null) {
				if (line.equals(process)) {
					found = true;
				}
			}
			input.close();

		}
		catch(Exception e){
			e.printStackTrace();
		}
		return found;
	}

	public static String getMemInfo(){
		SystemInfo si = new SystemInfo();
		HardwareAbstractionLayer hal = si.getHardware();
		
		System.out.println("[->] getMemInfo(): " + 
				(((hal.getMemory().getTotal() - hal.getMemory().getAvailable()) 
				/ 1048576 ) + "/" + (hal.getMemory().getTotal()) / 1048576) );
		
		return (((hal.getMemory().getTotal() - hal.getMemory().getAvailable()) 
				/ 1048576 ) + "/" + (hal.getMemory().getTotal()) / 1048576);
	}


	public static void info(){
		SystemInfo si = new SystemInfo();

		OperatingSystem os = si.getOperatingSystem();
		System.out.println("[->] " + os);

		HardwareAbstractionLayer hal = si.getHardware();
//		System.out.println(hal.getProcessors().length + " CPU(s):");

//		for(Processor cpu : hal.getProcessors()) {
//			System.out.println(" " + cpu);
//		}

		System.out.println("[->] Memory: " + 
				FormatUtil.formatBytes(hal.getMemory().getTotal() - hal.getMemory().getAvailable()) + "/" + 
				FormatUtil.formatBytes(hal.getMemory().getTotal()));
//		System.out.println("Memory: " + 
//				(hal.getMemory().getAvailable()) / 1048576 + "/" + 
//				hal.getMemory().getTotal());
	}




	//  public static void cpu(){
	//	  MBeanServerConnection mbsc = ManagementFactory.getPlatformMBeanServer();
	//
	//	  OperatingSystemMXBean osMBean = ManagementFactory.newPlatformMXBeanProxy(
	//	  mbsc, ManagementFactory.OPERATING_SYSTEM_MXBEAN_NAME, OperatingSystemMXBean.class);
	//
	//	  long nanoBefore = System.nanoTime();
	//	  long cpuBefore = osMBean.getProcessCpuTime();
	//
	//	  // Call an expensive task, or sleep if you are monitoring a remote process
	//
	//	  long cpuAfter = osMBean.getProcessCpuTime();
	//	  long nanoAfter = System.nanoTime();
	//
	//	  long percent;
	//	  if (nanoAfter > nanoBefore)
	//	   percent = ((cpuAfter-cpuBefore)*100L)/
	//	     (nanoAfter-nanoBefore);
	//	  else percent = 0;
	//
	//	  System.out.println("Cpu usage: "+percent+"%");
	//  }

	//  public static void printUsage() {
	//	  OperatingSystemMXBean operatingSystemMXBean = ManagementFactory.getOperatingSystemMXBean();
	//	  for (Method method : operatingSystemMXBean.getClass().getDeclaredMethods()) {
	//	    method.setAccessible(true);
	//	    if (method.getName().startsWith("get") 
	//	        && Modifier.isPublic(method.getModifiers())) {
	//	            Object value;
	//	        try {
	//	            value = method.invoke(operatingSystemMXBean);
	//	        } catch (Exception e) {
	//	            value = e;
	//	        } // try
	//	        System.out.println(method.getName() + " = " + value);
	//	    } // if
	//	  } // for
	//	}

	//  public static void mem(){
	//	  OperatingSystemMXBean operatingSystemMXBean = ManagementFactory.getOperatingSystemMXBean();
	//	  System.out.println(operatingSystemMXBean.getTotalPhysicalMemorySize());
	//	  System.out.println(operatingSystemMXBean.getFreePhysicalMemorySize());
	//  }




}
//
//public class WindowsUtils {
//	  public static List<String> listRunningProcesses() {
//	    List<String> processes = new ArrayList<String>();
//	    try {
//	      String line;
//	      Process p = Runtime.getRuntime().exec("tasklist.exe /fo csv /nh");
//	      BufferedReader input = new BufferedReader
//	          (new InputStreamReader(p.getInputStream()));
//	      while ((line = input.readLine()) != null) {
//	          if (!line.trim().equals("")) {
//	              // keep only the process name
//	              line = line.substring(1);
//	              processes.add(line.substring(0, line.indexOf(""")));
//	          }
//
//	      }
//	      input.close();
//	    }
//	    catch (Exception err) {
//	      err.printStackTrace();
//	    }
//	    return processes;
//	  }
//
//	  public static void main(String[] args){
//	      List<String> processes = listRunningProcesses();
//	      String result = "";
//
//	      // display the result 
//	      Iterator<String> it = processes.iterator();
//	      int i = 0;
//	      while (it.hasNext()) {
//	         result += it.next() +",";
//	         i++;
//	         if (i==10) {
//	             result += "\n";
//	             i = 0;
//	         }
//	      }
//	      msgBox("Running processes : " + result);
//	  }
//
//	  public static void msgBox(String msg) {
//	    javax.swing.JOptionPane.showConfirmDialog((java.awt.Component)
//	       null, msg, "WindowsUtils", javax.swing.JOptionPane.DEFAULT_OPTION);
//	  }
//	}
