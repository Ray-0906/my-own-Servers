import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.concurrent.atomic.AtomicInteger;

import state.HttpClientState;

public class Server {
   int port=8090;
   Server(int port){
       this.port=port;}

    public  void run() throws Exception {
        int cores = Runtime.getRuntime().availableProcessors();
        Worker[] workers = new Worker[cores];

        for (int i = 0; i < cores; i++) {
            workers[i] = new Worker();
            new Thread(workers[i], "worker-" + i).start();
        }

        AtomicInteger idx = new AtomicInteger();

        ServerSocketChannel server = ServerSocketChannel.open();
        server.bind(new InetSocketAddress(this.port));
        server.configureBlocking(true);

        System.out.println("NIO HTTP server on port " + port);

        while (true) {
            SocketChannel client = server.accept();
            client.configureBlocking(false);
            
            Worker worker = workers[idx.getAndIncrement() % cores];
            worker.selector().wakeup();
            client.register(worker.selector(),
                    SelectionKey.OP_READ,
                    new HttpClientState());
        }
    }
}