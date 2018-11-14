

import java.io.IOException;
import java.io.*;
import java.net.*;
import java.util.*;


public class AuctionServer implements Runnable {
    /*
    One item per auction: Expand it later
     */
    public ArrayList<ClientConnection> listOfConnectedClients = new ArrayList<ClientConnection>();


    private ServerSocket server = null;
    private Thread thread = null;
    private final int PORT = 1234;
    private int clientCount = 0;


    public AuctionServer() {
        try {

            System.out.println("Binding to port " + PORT + ", please wait  ...");
            server = new ServerSocket(PORT);
            System.out.println("Server started: " + server.getInetAddress());
            start();
        } catch (IOException ioe) {
            System.out.println("Can not bind to port " + PORT + ": " + ioe.getMessage());

        }
    }

    public void run() {
        while (thread != null) {
            try {

                System.out.println("Waiting for a client ...");
                addThread(server.accept());

                int pause = (int) (Math.random() * 3000);
                Thread.sleep(pause);

            } catch (IOException ioe) {
                System.out.println("Server accept error: " + ioe);
                stop();
            } catch (InterruptedException e) {
                System.out.println(e);
            }
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

    public synchronized void broadcast(int ID, String input) {

        notifyAll();
    }

    private void addThread(Socket socket) {
        System.out.println("Client accepted: " + socket);
        ClientConnection clientConnection = new ClientConnection(this,socket);
        listOfConnectedClients.add(clientConnection);
        try {
            clientConnection.open();
            clientConnection.start();
            clientCount++;
        } catch (IOException ioe) {
            System.out.println("Error opening thread: " + ioe);
        }
    }


    public static void main(String[] args) {
        AuctionServer auction = new AuctionServer();
    }
}