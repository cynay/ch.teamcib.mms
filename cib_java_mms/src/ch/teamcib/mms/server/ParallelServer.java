package ch.teamcib.mms.server;

import java.io.*;
import java.net.*;
import java.util.concurrent.*;
import java.util.*;

import ch.teamcib.mms.network.TCPSocket;


/**
 * This class is for handling the Clients. For each connected client there is
 * a Task object for storing the socket,nick etc.
 *
 * @author Yannic Schneider
 */
class Task implements Runnable{
    
    private TCPSocket       socket;
    private ParallelServer  ps;
    private String          nick;

    /**
     * Constructor
     *
     * @param socket    the socket from the client
     * @param ps        the Server-instance
     */
    public Task(TCPSocket socket, ParallelServer ps){
        this.socket  = socket;
        this.ps      = ps;
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
            System.out.println("[*] connected");

            while(true){
                request = socket.receiveLine();

                if(request != null){
                    System.out.println("[*] new request: " + request);

                    String cmd[] = request.split("&");

                    if(cmd[1].equals("msg")){
                        ps.newMessage(request);
                    } else if(cmd[1].equals("pmsg")){
                        socket.sendLine("&pmsg&"+ cmd[3] );
                        ps.newPrivMessage(request);
                    } else if(cmd[1].equals("cmd")) {
                        if(cmd[2].equals("nick")) {
                            this.nick = cmd[3];
                            ps.addClientName(nick);
                        } else if (cmd[2].equals("exit")) {
                            ps.removeClientName(nick);
                        }
                    } else {
                        System.out.println("[*] error: no valid command string");
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
     * getter for the nickname
     *
     * @return  The nickname
     */
    public String getNick(){
        return this.nick;
    }
}

/**
 * This class is for monitoring the thread-pool and waiting for incoming
 * connections.
 *
 * @author cYnaY
 */
public class ParallelServer {

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
     * Method for sending a new private messages to the specified client
     *
     * @param message   the message
     */
    public void newPrivMessage(String message){
        String recipient = message.split("&")[2];
        message = "&pmsg&" + message.split("&")[3];
        for(int i = 0; i < clients.size(); i++){
            Task t = clients.get(i);
            if(t.getNick().equals(recipient)){
                t.sendMessage(message);
            }
        }
    }

    /**
     * Adds a clientname to the hashset
     * Is called when someone new connects
     *
     * @param nick      The nick to add
     */
    public void addClientName(String nick){
        clientNames.add(nick);
    }

    /**
     * removes the specified client from the hashset
     * Is called when the client quits
     *
     * @param nick      The nick to remove
     */
    public void removeClientName(String nick){
        clientNames.remove(nick);
        this.newMessage(this.getClientNames());
    }

    /**
     * returns all Clientnames of the clients which are connected to the server
     * at the moment
     *
     * @return      a String with all clientnames separeted by '&'
     */
    public String getClientNames(){
        String allClients = "&cmd&clientList&";

        for(String s : clientNames){
            allClients += "&" + s;
        }

        return allClients;
    }


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
            srvSocket = new ServerSocket(1337); // create socket
        } catch(Exception e){
            System.out.println("[*] error while creating serversocket");
            return;
        }

        while(true){
            try{
                // wait for connectin then create streams
                System.out.println("[*] Wait for connection");
                tcpSocket = new TCPSocket(srvSocket.accept());
                Task task = new Task(tcpSocket,this);
                pool.execute(task);
                clients.add(task);
                task.sendMessage("&msg&# welcome");
                Thread.sleep(50);
                this.newMessage(this.getClientNames());
                        

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
