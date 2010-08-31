package ch.teamcib.mms.client;

import java.util.*;

/**
 * This class stores all the important information for the views.
 * All views are connected to this class and are informed if they have to
 * update themselves.
 *
 * @author cYnaY
 */
public class ClientView {

    private final boolean COMMAND = false;
    private final boolean MESSAGE = true;
    protected Client client  = null;
    private String   message = null;
    private String userString= "";
    private List <String>users              = new LinkedList<String>();
    private List <ClientViewInterface>views = new LinkedList<ClientViewInterface>();
    

    /**
     * Constructor
     *
     * @param client    type Client     The Client of the view
     */
    public ClientView(Client client){
        this.client = client;
    }

    /**
     * Add an view (which implements ClientViewInterface) to the client
     *
     * @param view  type ClientViewInterface    A viewer class for the client
     */
    public void addView(ClientViewInterface view){
        views.add(view);
    }

    /**
     * Informs all connected views that they should update themselves
     */
    private void informViews(boolean isMessage){
        for(ClientViewInterface v : views){
            v.update(isMessage);
        }
    }

    /**
     * New message for the views
     *
     * @param message   type String     a message
     */
    public void newMessage(String message){
        if(message.startsWith("&cmd&")){
            newCommand(message);
        } else if (message.startsWith("&msg&")) {
            this.message = message.split("&")[2];
            informViews(MESSAGE);
        } else if (message.startsWith("&pmsg&")){
            this.message = "&pmsg&" + message.split("&")[2];
            informViews(MESSAGE);
        }
    }

    /**
     * New command for the client
     *
     * @param command   type String     command string
     */
    private void newCommand(String command){
        System.out.println(command);
        String[] cmd =  command.split("&");

        if(cmd[2].equals("clientList")){
            for(String s : cmd){
                if(!users.contains(s) && !s.equals(cmd[1]) && !s.equals(cmd[2]) && !s.isEmpty()){
                    users.add(s);
                    userString += s + "&";
                }
            }
        }

        informViews(COMMAND);
    }

    /**
     * getter for message
     *
     * @return message
     */
    public String getMessage(){
        return message;
    }

    /**
     * getter for users
     *
     * @return users
     */
    public List getUsers(){
        return users;
    }

    /**
     * getter for the Userstring
     *
     * @return      String with all users
     */
    public String getUserString(){
        return userString;
    }
}