package ch.teamcib.mms.server;

import java.io.*;
import java.net.*;
import java.util.concurrent.*;
import java.util.*;


/**
 * This class is for handling the Clients. For each connected client there is
 * a Task object for storing the socket etc.
 *
 * @author Yannic Schneider
 */
class Task implements Runnable{
    
	private static final String INFO = "0";
	private static final String DATA = "1";
	private static final String RQST = "2";
	
    private TCPSocket       socket;
    private ParallelServer  ps;
    private String          mHostname;

    /**
     * Constructor
     *
     * @param socket    the socket from the client
     * @param ps        the Server-instance
     */
    public Task(TCPSocket socket, ParallelServer ps){
        this.socket  = socket;
        this.ps      = ps;
//        AliveTask at = new AliveTask(this.socket, this.ps);
//        new Thread(at).start();
    }

    /**
     * the main loop waits for new commands and when something arrives
     * it will check the command and do the right thing with it.
     *
     */
    public void run(){
        String request;

        // execute client requests
        try{
            System.out.println("[*] DEBUG: new client connected");

            while(true){
                request = socket.receiveLine();

                if(request != null){
                    System.out.println("[*] DEBUG: new request: " + request);

                    String cmd[] = request.split("&");

//                    System.out.println(cmd[0]);
//                    System.out.println(cmd[1]);
//                    System.out.println(cmd[2]);

                    if(cmd[0].equals(INFO)){
                    	System.out.println("[*] INFO: " + request);
//                        ps.newMessage(request);
                    } else if(cmd[0].equals(DATA)){
                    	System.out.println("[*] DATA: " + request);
//                        socket.sendLine("&pmsg&"+ cmd[3] );
                    	String data = "mem=" + SystemData.getMemInfo();
                        ps.sendData("1&" + mHostname + "&" + data);
                    } else if(cmd[0].equals(RQST)) {
                    	System.out.println("[*] RQST: " + request);
                    	
                        if(cmd[1].equals("hostname")) {
                            mHostname = cmd[2];
                            ps.addHost(mHostname);
                        } else if (cmd[1].equals("exit")) {
                            ps.removeHost(mHostname);
                        } 
                    } else {
                        System.out.println("[*] ERROR: no valid command string");
                    }

                } else {
                    break;
                }
            }
        } catch(Exception e) {
            System.out.println(e);
        }

        try{
            socket.close();
        } catch (IOException e) {
            System.out.println(e);
        }
    }

    /**
     * sending messages
     *
     * @param message
     */
    public void sendMessage(String message){
        try{
            socket.sendLine(message);
        } catch(Exception e) {
            System.out.println(e);
        }
    }

    /**
     * getter for the Hostname
     *
     * @return  the Hostname of this instance
     */
    public String getHostname(){
        return this.mHostname;
    }
}



///**
// * 
// *
// * @author Yannic Schneider
// */
//class AliveTask implements Runnable{
//    
//    private TCPSocket       socket;
//    private ParallelServer  ps;
//    private String          nick;
//
//    /**
//     * Constructor
//     *
//     * @param socket    the socket from the client
//     * @param ps        the Server-instance
//     */
//    public AliveTask(TCPSocket socket, ParallelServer ps){
//        this.socket  = socket;
//        this.ps      = ps;
//    }
//
//    /**
//     * send Alive signal all x seconds
//     *
//     */
//    public void run(){
//        try{
//            System.out.println("[*] connected AliveTask");
//
//            for(int i = 0; i < 10; i++){
//            	socket.sendLine("ALIVE: " + i );
//            	Thread.sleep(1000);
//            }
//           
//        } catch(Exception e) {
//            System.out.println(e);
//        }
//
//        try{
//            socket.close();
//        } catch (IOException e) {
//            System.out.println(e);
//        }
//    }
//}



/**
 * This class is for monitoring the thread-pool and waiting for incoming
 * connections.
 *
 * @author Yannic Schneider
 */
public class ParallelServer {

	private final int  LISTENING_PORT  = 1337;
    private final int  CORE_POOL_SIZE  = 6;
    private final int  MAX_POOL_SIZE   = 8;
    private final long KEEP_ALIVE_TIME = 10;


    private List<Task> clients = new ArrayList<Task>();
    private HashSet<String> clientNames = new HashSet<String>();


    /**
     * main method
     *
     * @param args
     */
    public static void main(String[] args){
    	GUI.mainGUI();
    	try {
			Thread.sleep(500); // So the GUI has time to initialize (If not done the first output goes to the console not the GUI)
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        new ParallelServer().execute();
    }


    /**
     * Method for sending new messages to all the clients
     *
     * @param message   
     */
    public void newMessage(String message){
        //clientNames.add(message.split(">")[0].substring(1));
        for(int i = 0; i < clients.size(); i++){
            Task t = clients.get(i);
            t.sendMessage(message);
        }
    }
    
    /**
     * Method for sending a new messages to the specified client
     *
     * @param message   the message
     */
    public void sendClientMessage(String message){
        String recipient = message.split("&")[1];
        message = "0&" + message.split("&")[2];
        for(int i = 0; i < clients.size(); i++){
            Task t = clients.get(i);
            if(t.getHostname().equals(recipient)){
                t.sendMessage(message);
            }
        }
    }
    
    /**
     * Method for sending data to the specified client
     *
     * @param message   the message
     */
    public void sendData(String data){
        String recipient = data.split("&")[1];
        data = "1&"+ recipient + "&" + data.split("&")[2];
        for(int i = 0; i < clients.size(); i++){
            Task t = clients.get(i);
            if(t.getHostname().equals(recipient)){
                t.sendMessage(data);
            }
        }
    }

    /**
     * Adds a hostname to the hashset
     * Is called when someone new connects
     *
     * @param hostname      The hostname to add
     */
    public void addHost(String hostname){
        clientNames.add(hostname);
    }

    /**
     * removes the specified client from the hashset
     * Is called when the client quits
     *
     * @param hostname      The hostname to remove
     */
    public void removeHost(String hostname){
        clientNames.remove(hostname);
    }
//
//    /**
//     * returns all Clientnames of the clients which are connected to the server
//     * at the moment
//     *
//     * @return      a String with all clientnames separeted by '&'
//     */
//    public String getClientNames(){
//        String allClients = "&cmd&clientList&";
//
//        for(String s : clientNames){
//            allClients += "&" + s;
//        }
//
//        return allClients;
//    }


    /**
     * starts all the server stuff and loops for new connections
     * 
     */
    public void execute(){
        ServerSocket srvSocket = null;
        TCPSocket tcpSocket = null;

        ThreadPoolExecutor pool = new ThreadPoolExecutor(
                CORE_POOL_SIZE, MAX_POOL_SIZE, KEEP_ALIVE_TIME,
                TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>());

        try {
            srvSocket = new ServerSocket(LISTENING_PORT); // create socket
        } catch(Exception e){
            System.out.println("[*] ERROR: creating serversocket failed");
            return;
        }

        while(true){
            try{
            	
//            	SystemData.printUsage();
            	SystemData.info();
                // wait for connection then create streams
                System.out.println("[*] DEBUG: Wait for new connection");
                tcpSocket = new TCPSocket(srvSocket.accept());
                Task task = new Task(tcpSocket,this);
                pool.execute(task);
                clients.add(task);
                task.sendMessage("0&DEBUGINFO: &# welcome");
                Thread.sleep(50);
//                this.newMessage(this.getClientNames());
                        

            }catch(Exception e){
                System.out.println(e);
                if(tcpSocket != null){
                    try{
                        tcpSocket.close();
                    } catch(IOException ex) {
                        System.out.println(ex);
                    }
                }
            }
        }
    }
}
