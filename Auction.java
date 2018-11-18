import java.net.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;


public class Auction implements Runnable {

    // Array of clients	
    private AuctionThread clients[] = new AuctionThread[50];
    private ServerSocket server = null;
    private Thread thread = null;
    private Timer timer;
    private int timeRemaining = 60;

    private boolean auctionIsRunning = false;
    private boolean timeRemainingHasBeenBroadcast = false;

    private ArrayList<Bid> listOfBids = new ArrayList<Bid>();



    private int clientCount = 0;

    //Item to bid on(only one currently)
    public Item item = new Item("Fender Stratocaster", "1959 Vintage Guitar", 100);


    public Auction(int port) {
        auctionIsRunning = true;
        timer = new Timer();
        runTimerTask();


        try {

            System.out.println("Binding to port " + port + ", please wait  ...");
            server = new ServerSocket(port);
            System.out.println("Server started: " + server.getInetAddress());
            start();


        } catch (IOException ioe) {
            System.out.println("Can not bind to port " + port + ": " + ioe.getMessage());

        }
    }


    public void run() {

        while (thread != null) {
            try {

                System.out.println("Waiting for a client ...");
                addThread(server.accept());


                //int pause = (int)(Math.random()*3000);
                //Thread.sleep(pause);

            } catch (IOException ioe) {
                System.out.println("Server accept error: " + ioe);
                stop();
            }
            /*
            catch (InterruptedException e){
                System.out.println(e);
            }
            */
        }
    }

    public void start() {
        if (thread == null) {
            thread = new Thread(this);
            thread.start();
        }
    }

    public void stop() {
        thread = null;

    }

    private int findClient(int ID) {
        for (int i = 0; i < clientCount; i++)
            if (clients[i].getID() == ID)
                return i;
        return -1;
    }

    public synchronized void broadcast(int ID, String input) {
        if (input.equals(".bye")) {
            clients[findClient(ID)].send(".bye");
            remove(ID);
        } else
            for (int i = 0; i < clientCount; i++) {
                if (clients[i].getID() != ID)
                    clients[i].send(ID + ": " + input); // sends messages to clients
            }
        notifyAll();
    }

    public synchronized void broadcastTimeWarning() {
        for (int i = 0; i < clientCount; i++) {
            clients[i].send("Quick! 20 Seconds Left...");
        }

        //clients have seen the time warning
        timeRemainingHasBeenBroadcast = true;
        notifyAll();

    }

    public synchronized void remove(int ID) {
        int pos = findClient(ID);
        if (pos >= 0) {
            AuctionThread toTerminate = clients[pos];
            System.out.println("Removing client thread " + ID + " at " + pos);

            if (pos < clientCount - 1)
                for (int i = pos + 1; i < clientCount; i++)
                    clients[i - 1] = clients[i];
            clientCount--;

            try {
                toTerminate.close();
            } catch (IOException ioe) {
                System.out.println("Error closing thread: " + ioe);
            }
            toTerminate = null;
            System.out.println("Client " + pos + " removed");
            notifyAll();
        }
    }

    private void addThread(Socket socket) {
        String welcomeMessage = "Welcome! You have connected to the auction for: " + this.item.toString() + "\nEnter a bid..\n";
        if (clientCount < clients.length) {

            System.out.println("Client accepted: " + socket);
            clients[clientCount] = new AuctionThread(this, socket);
            try {
                clients[clientCount].open();
                clients[clientCount].start();
                clients[clientCount].send(welcomeMessage);
                clientCount++;
            } catch (IOException ioe) {
                System.out.println("Error opening thread: " + ioe);
            }
        } else
            System.out.println("Client refused: maximum " + clients.length + " reached.");
    }

    //Controls the running of the auction
    public void runTimerTask() {

        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                timeRemaining--;
                System.out.println(timeRemaining);
                if (timeRemaining == 0) {
                    auctionIsRunning = false;
                    timer.cancel();
                    endAuctionAndDetermineWinner();
                }

                //check if warning has been fired already
                if (timeRemaining < 20 && !timeRemainingHasBeenBroadcast) {
                    broadcastTimeWarning();
                }
            }
        }, 0, 1000);
    }

    public int getTimeRemaining() {
        return timeRemaining;
    }

    public void resetTimeRemaining() {
        timeRemaining = 60;
    }

    public void setTimeRemainingHasBeenBroadcastToFalse(){
        //Used to reset the time warning message so it will fire again if another bid has been placed
        timeRemainingHasBeenBroadcast = false;
    }

    public void addBidToBidList(int bidder, Float amount){
        Bid b = new Bid(this.item, bidder, amount);
        listOfBids.add(b);
    }


    public synchronized void endAuctionAndDetermineWinner(){
        double maxValue = -1;
        int indexOfMaxValue = -1;
        for(int i = 0; i < listOfBids.size(); i++) {
            if(listOfBids.get(i).getBidAmount() > maxValue) {
                indexOfMaxValue = i;
            }
        }
        int portOfWinner = listOfBids.get(indexOfMaxValue).getBiddersSocketPort();
        //print on the server
        System.out.println(portOfWinner + " has won!");

        for (int i = 0; i < clientCount; i++) {
            clients[i].send("Auction Over! " + portOfWinner + " has won the item!");
        }
        notifyAll();

    }


    public static void main(String args[]) {
        Auction server = null;
        if (args.length != 1)
            System.out.println("Usage: java Auction port");
        else
            server = new Auction(Integer.parseInt(args[0]));
    }

}