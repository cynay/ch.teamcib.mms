package ch.teamcib.mms.client;

import java.util.*;

/**
 * Command Line Interface
 *
 * @author Yannic Schneider
 */
public class ClientViewCLI implements ClientViewInterface {

    private final int MAX_MESSAGES = 10;
    private List <String>messages  = new LinkedList<String>();
    private List <String>users     = new LinkedList<String>();
    private ClientView clientView  = null;

    /**
     * Constructor
     *
     * @param clientView    type ClientView     the binding ClientView class
     */
    public ClientViewCLI(ClientView clientView){
        this.clientView = clientView;
        clientView.addView(this);
    }

    /**
     * updates the view
     */
    public void update(boolean isMessage){

        if(isMessage){
            messages.add(clientView.getMessage());
            if(messages.size() > MAX_MESSAGES) {
                messages.remove(0);
            }
        } else {
            users = clientView.getUsers();
        }
        
        printView();
    }

    /**
     * Prints out a new view
     * (25 empty lines an then the chat 'window')
     */
    private void printView(){

        for (int i = 0; i < 25; i++) {  // print 25 empty lines...
            System.out.println("");
        }

        System.out.println("------------------------------------------------");
        System.out.print("|USERS > ");
        for(String s : users){
            System.out.print(s + " ¦ ");
        }
        System.out.print("\n");
        System.out.println("------------------------------------------------");
        for(int i = 0; i < 10; i++){
            if(messages.size() > i){
                System.out.println("| " + messages.get(i));
            } else {
                System.out.println("| ");
            }
        }
        System.out.println("================================================");
        System.out.print(">> ");
    }
}