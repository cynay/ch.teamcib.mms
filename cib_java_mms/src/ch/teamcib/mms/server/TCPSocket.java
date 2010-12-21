package ch.teamcib.mms.server;

import java.io.*;
import java.net.*;


/**
 * TCPSocket is for handling TCP-Sockets and their buffer. With this class it
 * is possible to create new connections an read/write lines to/from the 
 * buffers.
 *
 * @author CiB
 */
public class TCPSocket {
	// ===========================================================
    // Finals
    // ===========================================================
	private final int TIMEOUT = 2000;

	// ===========================================================
    // Members
    // ===========================================================
    private Socket socket;
    private BufferedReader istream;
    private BufferedWriter ostream;
    
    /**
     * Connect to the specified Server and initialize the input/output streams.
     * 
     * @param srvAddress	Hostname or IP-Address of the Server
     * @param srvPort		The Port for the Socket
     * @throws UnknownHostException
     * @throws IOException
     */
    public TCPSocket(String srvAddress, int srvPort)
        throws UnknownHostException, IOException{
    	
        socket = new Socket();
//        socket.setSoTimeout(TIMEOUT);
        socket.connect(new InetSocketAddress(srvAddress, srvPort), TIMEOUT);
        
        initializeStreams();        
    }
    
    /**
     * Initializes the given Socket input/output streams.
     * 
     * @param socket	The socket to initialize
     * @throws IOException
     */
    public TCPSocket(Socket socket) throws IOException{
        this.socket = socket;
//        socket.setSoTimeout(TIMEOUT);
        initializeStreams();
    }
    
    /**
     * Sends a String to the other side of the connection.
     * 
     * @param s		The message to send as String
     * @throws IOException
     */
    public void sendLine(String s) throws IOException{
        ostream.write(s);
        ostream.newLine();
        ostream.flush();
    }
    
    /**
     * Read a line out of the buffer of the TCP-Socket.
     * 
     * @return Returns the next line of text available. The string does not 
     * 		include the newline sequence.
     * @throws IOException
     */
    public String receiveLine() throws IOException{
        return istream.readLine();
    }
    
    /**
     * Closes the opened connection.
     * @throws IOException
     */
    public void close() throws IOException{
        socket.close();
    }
    
    private void initializeStreams() throws IOException{
        ostream = new BufferedWriter(
                new OutputStreamWriter(socket.getOutputStream()));
        istream = new BufferedReader(
                new InputStreamReader(socket.getInputStream()));
    }
}
