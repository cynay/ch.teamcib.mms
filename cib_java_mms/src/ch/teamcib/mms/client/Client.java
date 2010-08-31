package ch.teamcib.mms.client;

import java.io.*;
import java.util.*;

import ch.teamcib.mms.network.TCPSocket;


/**
 *
 * @author Yannic Schneider
 */
public class Client {

    private String name           = "cyn";
    private String srvName        = "127.0.0.1";
    private int    srvPort        = 1337;
    private TCPSocket  tcpSocket  = null;
    private ClientView clientView = new ClientView(this);

    /**
     *
     * @param args
     */
    public static void main(String[] args){
        if(args.length > 0){
            if(args[0].equals("-h") || args[0].equals("help")){
                printHelp();
                return;
            } else if(args.length == 3){    // servername - serverport - username
                new Client().execute(args[0], Integer.valueOf( args[1] ).intValue(), args[2] );
            } else {
                new Client().execute();
            }
        } else {
            printHelp();
        }
    }

    /**
     * prints a simpel help in console
     */
    private static void printHelp(){
        System.out.println("usage: <prog> <servername> <port> <nick>");
    }

    /**
     * Starts the client with a random username an the standart server/port
     */
    public void execute(){
        
        Random randGen = new Random();  // random number for username
        this.execute(this.srvName, this.srvPort, "User_" + randGen.nextInt(100) );
    }

    /**
     * starts the client
     *
     * @param srvName     Serveraddress
     * @param port        Server port
     * @param name        nickname
     */
    public void execute(String srvName, int srvPort, String name){
        this.srvName = srvName;
        this.srvPort = srvPort;
        this.name    = name;

        new ClientViewCLI(clientView);
        ClientViewGUI.execute(clientView);

        try{
            Thread.sleep(200);      // let the ClientViewGUI some time...
            // create socket connection
            System.out.println("[*] Establish connection");
            tcpSocket = new TCPSocket(this.srvName, this.srvPort);

            Listener listener = new Listener(tcpSocket);
            listener.start();

            tcpSocket.sendLine("&cmd&nick&" + this.name);   // send own nick
            
            while(true){

                String input = readLine();
                //TODO check the input if cmd etc.
                tcpSocket.sendLine("&msg&<" + this.name + "> " + input);
            }

        } catch(Exception e){
            System.out.println(e);
        }

        if(tcpSocket != null){
            System.out.println("[*] closing TCP-Connection");
            try{
                tcpSocket.sendLine("&cmd&exit");
                tcpSocket.close();
            }catch(Exception e){
                System.out.println(e);
            }

        }
    }

    /**
     * Sending a command
     *
     * @param command   the command
     */
    public void sendCommand(String command){
        try {
            tcpSocket.sendLine(command);
        } catch(Exception e){
            System.out.println(e);
        }
    }

    /**
     * Sending a message to users
     *
     * @param message   the message
     */
    public void sendMessage(String message){
        this.sendCommand("&msg&<" + this.name + "> " + message);
    }

    /**
     * Reads a Line from the console
     *
     * @return      the readed input from keyboard
     */
    public String readLine(){
        String s = "";
        try {
                InputStreamReader converter = new InputStreamReader(System.in);
                BufferedReader in = new BufferedReader(converter);
                s = in.readLine();
        } catch (Exception e) {
                System.out.println("[*] Error! Exception: "+e);
        }
        return s;
    }

    /**
     * getter for the clientname
     *
     * @return  the nickname
     */
    public String getName(){
        return this.name;
    }


    /**
     * This class/thread waits in an endless loop for incoming messages on
     * the specified socket.
     * When something arrives it informs the ClientView class.
     */
    private class Listener extends Thread {
        
        TCPSocket tcpSocket = null;

        /**
         * Constructor
         *
         * @param tcpSocket     the tcpSocket of the server
         */
        public Listener(TCPSocket tcpSocket ){
            this.tcpSocket = tcpSocket;
        }

        /**
         * this method receives all information from the server
         */
        public void run() {
            try {
                while (true) {
                    clientView.newMessage(tcpSocket.receiveLine());
                }
            } catch (IOException e) {
                    e.printStackTrace();
            }
        }
    }
}
