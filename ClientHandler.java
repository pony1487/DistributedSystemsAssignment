import java.io.*;
import java.net.*;
import java.util.*;


public class ClientHandler extends Thread
{
    private Socket client;
    private Scanner input;
    private PrintWriter output;
    private Auction auction;


    public ClientHandler(Socket socket, Auction auction)
    {
        this.auction = auction;


        //Set up reference to associated socket...
        client = socket;

        try
        {
            input = new Scanner(client.getInputStream());
            output = new PrintWriter(
                    client.getOutputStream(),true);



        }
        catch(IOException ioEx)
        {
            ioEx.printStackTrace();
        }
    }

    public void run()
    {

        //send whats on offer straight away from server
        output.println(auction.item.toString());
        String received;
        do
        {

            //Accept message from client on
            //the socket's input stream...
            received = input.nextLine();



            if(received.equals("1")){
                addBid();
            }

            if(received.equals("2")){
                displayCurrentMaxBid();
            }

            if(received.equals("3")){
                displayItem();
            }

            //Echo message back to client on
            //the socket's output stream...
            //output.println("ECHO: " + received);

            //Repeat above until 'QUIT' sent by client...



        }while (!received.equals("QUIT"));

        try
        {
            if (client!=null)
            {
                System.out.println(
                        "Closing down connection...");
                client.close();
            }
        }
        catch(IOException ioEx)
        {
            System.out.println("Unable to disconnect!");
        }
    }


    public void addBid(){

        output.println("Enter name: ");
        String bidderName = input.nextLine();

        output.println("Enter Bid amount: ");
        String bidString = input.nextLine();
        float bidFloat = Float.parseFloat(bidString);

        if(bidFloat < auction.item.getMaxBid()){
            output.println("Invalid: Bid lower than current bid, Press 1 to bid again.");
        }else {

            Bid bid = new Bid(auction.item, bidderName, bidFloat);
            auction.bids.add(bid);
            auction.item.bidOnItem(bidFloat);

            //output.println(auction.item.toString());
            this.auction.notifyClientsOfMaxBid();



        }
    }

    public void displayItem(){
        String itemDetails = auction.item.getName() + auction.item.getDescription();
        output.println(itemDetails);
    }

    public void displayUsers(){
        for(int i = 0; i < auction.users.size();i++){
            output.println(auction.users.get(i));
        }
    }

    public void displayCurrentMaxBid(){
        output.println("New Max Bid: " + auction.item.getMaxBid());
    }
}