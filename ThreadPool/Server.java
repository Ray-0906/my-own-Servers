

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {
    ExecutorService executorService;

    Server(int poolSize) {
        executorService = Executors.newFixedThreadPool(poolSize);
    }

    void run() throws IOException {
        System.out.println("Server is running...");
        ServerSocket server = new ServerSocket(8090);

        while (true) {
            Socket client = server.accept();
            System.out.println("Client connected from : " + client.getRemoteSocketAddress());
            executorService.execute(()->{handleClient(client);});

        }

    }

    void handleClient(Socket client) {
    try {
        // simulate CPU work
        long sum = 0;
        for (int i = 0; i < 50_000_000; i++) sum += i;

        PrintWriter out =
            new PrintWriter(client.getOutputStream(), true);
        out.println("OK " + sum);

        client.close();
    } catch (Exception e) {
        e.printStackTrace();
    }
}



    public static void main(String[] args) {
        Server server = new Server(2000);

        try {
            server.run();
        } catch (Exception e) {
            e.printStackTrace();
            // TODO: handle exception
        } // Create server with a pool size of 10

    }
}
