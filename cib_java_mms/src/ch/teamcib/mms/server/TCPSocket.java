package ch.teamcib.mms.server;


import java.io.*;
import java.net.*;

/**
 * 
 *
 * @author cYnaY
 */
public class TCPSocket {

//	private final int TIMEOUT = 1000;
	
    private Socket socket;
    private BufferedReader istream;
    private BufferedWriter ostream;
    
    public TCPSocket(String srvAddress, int srvPort)
        throws UnknownHostException, IOException{
        
        socket = new Socket(srvAddress, srvPort);
//        socket.setSoTimeout(TIMEOUT);
        initializeStreams();        
    }
    
    public TCPSocket(Socket socket) throws IOException{
        this.socket = socket;
//        socket.setSoTimeout(TIMEOUT);
        initializeStreams();
    }
    
    public void sendLine(String s) throws IOException{
        ostream.write(s);
        ostream.newLine();
        ostream.flush();
    }
    
    public String receiveLine() throws IOException{
        return istream.readLine();
    }
    
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
