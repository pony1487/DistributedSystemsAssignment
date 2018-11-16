import java.net.*;
import java.io.*;


public class Bidder implements Runnable
{  private Socket socket              = null;
    private Thread thread              = null;
    private BufferedReader  console   = null;
    private DataOutputStream streamOut = null;
    private BidderThread client    = null;
    private String chatName;


    public Bidder(String serverName, int serverPort, String name)
    {
        System.out.println("Establishing connection. Please wait ...");

        this.chatName = name;
        try{
            socket = new Socket(serverName, serverPort);
            System.out.println("Connected: " + socket);
            start();
        }
        catch(UnknownHostException uhe){
            System.out.println("Host unknown: " + uhe.getMessage());
        }
        catch(IOException ioe){
            System.out.println("Unexpected exception: " + ioe.getMessage());
        }
    }


    public void run()
    {
        while (thread != null){
            try {
                System.out.print(">: ");
                String message = console.readLine();
                streamOut.writeUTF(message);
                streamOut.flush();
            }
            catch(IOException ioe)
            {  System.out.println("Sending error: " + ioe.getMessage());
                stop();
            }
        }
    }

    public void handle(String msg)
    {  if (msg.equals(".bye"))
    {  System.out.println("Good bye. Press RETURN to exit ...");
        stop();
    }
    else
        System.out.println(msg);
    }

    public void start() throws IOException
    {
        console = new BufferedReader(new InputStreamReader(System.in));

        streamOut = new DataOutputStream(socket.getOutputStream());
        if (thread == null)
        {  client = new BidderThread(this, socket);
            thread = new Thread(this);
            thread.start();
        }
    }

    public void stop()
    {
        try
        {  if (console   != null)  console.close();
            if (streamOut != null)  streamOut.close();
            if (socket    != null)  socket.close();
        }
        catch(IOException ioe)
        {
            System.out.println("Error closing ...");

        }
        client.close();
        thread = null;
    }


    public static void main(String args[])
    {
        Bidder client = null;
        if (args.length != 3)
            System.out.println("Usage: java Bidder host port name");
        else
            client = new Bidder(args[0], Integer.parseInt(args[1]), args[2]);
    }
}
