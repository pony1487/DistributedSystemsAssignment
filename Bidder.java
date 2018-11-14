import java.io.*;
import java.net.*;
import java.util.*;

public class Bidder extends User
{
    private static InetAddress host;
    private final int PORT = 1234;

    public Bidder(String name)
    {
        super(name);

        try
        {
            host = InetAddress.getLocalHost();
        }
        catch(UnknownHostException uhEx)
        {
            System.out.println("Host ID not found!");
            System.exit(1);
        }
        accessServer();
    }

    private void accessServer()
    {
        Socket link = null;						//Step 1.

        try
        {
            link = new Socket(host,PORT);		//Step 1.
            /*
            Scanner input = new Scanner(
                    link.getInputStream());//Step 2.
               */
            PrintWriter output =
                    new PrintWriter(
                            link.getOutputStream(),true);//Step 2.

            BufferedReader input = new BufferedReader(new InputStreamReader(link.getInputStream()));

            //Set up stream for keyboard entry...
            Scanner userEntry = new Scanner(System.in);



            String message, response;

            //Get whats on offer straight away from server
            //response = input.nextLine();
            //response = input.nextLine();
            response = input.readLine();

            System.out.println("\nSERVER> Item for bidding: " + response + "\n");
            System.out.println("\nMenu: 1)Bid 2)Current Max Bid");


            do
            {
                System.out.print(">: ");
                message =  userEntry.nextLine();
                output.println(message); 		//Step 3.
                response = input.readLine();	//Step 3.
                System.out.println("\nMenu: 1)Bid 2)Current Max Bid");
                System.out.println("\nSERVER> " + response + "\n");
            }while (!message.equals("***CLOSE***"));
        }
        catch(IOException ioEx)
        {
            ioEx.printStackTrace();
        }

        finally
        {
            try
            {
                System.out.println(
                        "\n* Closing connection... *");
                link.close();					//Step 4.
            }
            catch(IOException ioEx)
            {
                System.out.println("Unable to disconnect!");
                System.exit(1);
            }
        }
    }

    public static void main(String[] args) {
        Bidder bidder = new Bidder("Tom");
        bidder.accessServer();
    }

}