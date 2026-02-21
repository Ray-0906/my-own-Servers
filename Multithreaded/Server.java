
// package SingleThreaded;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;

public class Server {
      
//     Runnable r = () -> {
//     System.out.println("Hello");
// };

    public void run() throws IOException, SocketException {
        int port = 8090;
        ServerSocket server = new ServerSocket(port);
        // server.setSoTimeout(10000);
        while (true) {
            try {
                System.out.println("Waiting for client connection...");
                Socket client = server.accept();

                Thread thread = new Thread(() -> {
                    try {
                          System.out.println("Client connected from : " + client.getRemoteSocketAddress());
                    PrintWriter out = new PrintWriter(client.getOutputStream());
                    BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
                    out.println("Welcome to server ! ! ");
                    out.flush();
                    System.out.println("Message from client : " + in.readLine());

                    out.close();
                    in.close();
                    client.close();
                    } catch (Exception e) {
                        // TODO: handle exception
                        e.printStackTrace();
                    }
                  

                });
                thread.start();

                // Handle client connection
            } catch (Exception e) {
                // TODO: handle exception
                e.printStackTrace();
            }

        }
    }
    
    public static void main(String[] args) {

        Server server = new Server();
        try {

            server.run();

        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }

        // Server logic goes here
    }
}