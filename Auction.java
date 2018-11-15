

import java.io.IOException;
import java.io.*;
import java.net.*;
import java.util.*;




public class Auction {
    /*
    One item per auction: Expand it later
     */
    private ArrayList<ClientHandler> listOfClientHandlers = new ArrayList<ClientHandler>();
    public ArrayList<User> users = new ArrayList<User>();
    public Item item;
    public ArrayList<Bid>  bids = new ArrayList<Bid>();

    private ServerSocket servSock;
    private final int PORT = 1234;



    private final int ONE_SECOND = 1000;
    private final int MAX_BID_TIME = ONE_SECOND * 60;

    private final long createdMillis;
    private long nowMillis;

    public Auction()
    {
        this.item = new Item("Stratocaster","Fender guitar",999.99f);

        createdMillis = System.currentTimeMillis();

    }

    private void runAuction()
    {
        System.out.println("Opening port...\n");
        try
        {
            servSock = new ServerSocket(PORT);      //Step 1.
        }
        catch(IOException ioEx)
        {
            System.out.println("Unable to attach to port!");
            System.exit(1);
        }


        while (true) {
            //Wait for client..
            try {
                Socket client = servSock.accept();

                System.out.println("\nNew client accepted.\n");

                //Create a thread to handle communication with
                //this client and pass the constructor for this
                //thread a reference to the relevant socket...
                ClientHandler handler = new ClientHandler(client,this);
                listOfClientHandlers.add(handler);

                handler.start();//As usual, this method calls run.

            }catch(IOException ioEx)
            {
                System.out.println("Unable to attach to port!");
                System.exit(1);
            }
        }
    }

    public void notifyClientsOfMaxBid(){
        //This works but requires client to press enter to receive a line from the server to get the update

        for(int i = 0; i < listOfClientHandlers.size();i++){
            listOfClientHandlers.get(i).displayCurrentMaxBid();
        }
        notifyAll();
    }


    public int getSecondCount()
    {
        nowMillis = System.currentTimeMillis();
        return (int)((nowMillis - this.createdMillis) / 1000);
    }

    public void endAuction()
    {

        System.exit(1);
    }


    public static void main(String[] args) {
        Auction auction = new Auction();
        auction.runAuction();
    }
}