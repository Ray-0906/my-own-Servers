
// package SingleThreaded;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;

public class Server {

    // Runnable r = () -> {
    // System.out.println("Hello");
    // };

    public void run() throws IOException, SocketException {
        int port = 8090;
        ServerSocket server = new ServerSocket(port);
        // server.setSoTimeout(10000);
        while (true) {
            try {
                System.out.println("Waiting for client connection...");
                Socket client = server.accept();

                long sum = 0;
                for (int i = 0; i < 50_000_000; i++)
                    sum += i;

                PrintWriter out = new PrintWriter(client.getOutputStream(), true);
                out.println("OK " + sum);

                client.close();
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