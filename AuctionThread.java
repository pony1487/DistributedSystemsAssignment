import java.net.*;
import java.io.*;


public class AuctionThread extends Thread {
    private Auction server = null;
    private Socket socket = null;
    private int ID = -1;
    private DataInputStream streamIn = null;
    private DataOutputStream streamOut = null;
    private Thread thread;
    private int indexOfItemBeingBidOn;


    public AuctionThread(Auction _server, Socket _socket) {
        super();
        server = _server;
        socket = _socket;
        ID = socket.getPort();
        indexOfItemBeingBidOn = server.currentItemIndex;

    }

    public void send(String msg) {
        try {
            streamOut.writeUTF(msg);
            streamOut.flush();
        } catch (IOException ioe) {
            System.out.println(ID + " ERROR sending: " + ioe.getMessage());
            server.remove(ID);
            thread = null;
        }
    }

    public int getID() {
        return ID;
    }

    public void run() {

        System.out.println("Server Thread " + ID + " running.");
        thread = new Thread(this);
        while (true) {

            try {

                String inputFromClient = streamIn.readUTF();

                //get the current item index being bid on
                indexOfItemBeingBidOn = server.currentItemIndex;


                //Check if user entered a float
                try {
                    Float inputAsFloat = Float.parseFloat(inputFromClient);

                    System.out.println("Current item index in AuctionThread:" + indexOfItemBeingBidOn);

                    if (inputAsFloat < server.listOfItems.get(indexOfItemBeingBidOn).getMaxBid()) {
                        send("Invalid Bid: Bid Lower than current bid");
                    } else {
                        addBid(inputAsFloat);
                        send("You bid: $" + inputAsFloat);
                        server.broadcast(ID, "Current Max: $" + server.listOfItems.get(indexOfItemBeingBidOn).getMaxBid());

                        //reset timer
                        server.resetTimeRemaining();

                        //reset flag so after another round of bidding they will get a time warning
                        server.setTimeRemainingHasBeenBroadcastToFalse();

                        //Add user bid to bid list(socket port is their id)
                        server.addBidToBidList(ID,inputAsFloat);
                    }
                } catch (NumberFormatException e) {
                    send("Error: Bid must be a float!");
                }

            } catch (IOException ioe) {
                //System.out.println(ID + " ERROR reading: " + ioe.getMessage());
                server.remove(ID);
                thread = null;
            }
        }
    }

    public void open() throws IOException {
        streamIn = new DataInputStream(new
                BufferedInputStream(socket.getInputStream()));
        streamOut = new DataOutputStream(new
                BufferedOutputStream(socket.getOutputStream()));
    }

    public void close() throws IOException {
        if (socket != null)
            socket.close();

        if (streamIn != null)
            streamIn.close();

        if (streamOut != null)
            streamOut.close();
    }


    public void addBid(Float amount) {

        if (amount < server.listOfItems.get(indexOfItemBeingBidOn).getMaxBid()) {
            try {
                streamOut.writeUTF("Invalid: Bid lower than current bid");
            } catch (Exception e) {
                System.out.println(e);
            }
        } else {
            server.listOfItems.get(indexOfItemBeingBidOn).bidOnItem(amount);
        }
    }

}