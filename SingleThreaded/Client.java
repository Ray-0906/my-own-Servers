// package SingleThreaded;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
public class Client {
    public void run() throws  UnknownHostException,IOException {
        // Client logic goes here
        
        int port = 8090;
        String host = "localhost";
        InetAddress address = InetAddress.getAllByName(host)[0];
        System.out.println("Connecting to server at " + address + ":" + port);  
        Socket socket = new Socket(address, port);
        PrintWriter tosocket= new PrintWriter(socket.getOutputStream());
        BufferedReader fromsocket = new BufferedReader( new InputStreamReader(socket.getInputStream()));
        tosocket.println("Hello Server ! ! ");
        tosocket.flush();
        System.out.println("Message from server : "+fromsocket.readLine());
        tosocket.close();
        fromsocket.close();
        socket.close();
        // tosocket.flush();
        
    }
    public static void main(String[] args) {
        System.out.println("Client is running...");
        Client client = new Client();
        try {
            client.run();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        // Client logic goes here
    }
}
