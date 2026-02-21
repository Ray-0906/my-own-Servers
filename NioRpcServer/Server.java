import java.io.IOException;

import java.net.InetSocketAddress;

import java.nio.ByteBuffer;

import java.nio.channels.*;

import java.util.HashMap;

import java.util.Iterator;

import java.util.concurrent.atomic.AtomicInteger;

class ClientState {

    ByteBuffer header = ByteBuffer.allocate(4);

    ByteBuffer body;

    ByteBuffer response;

}

class Statehandler {

    static void handleRead(SelectionKey key, SocketChannel client) throws IOException {

        ClientState state = (ClientState) key.attachment();

        if (state.header.hasRemaining()) {

            int n = client.read(state.header);

            if (n == -1) {

                client.close();

                return;

            }

            if (state.header.hasRemaining()) {

                return;

            }

            // header is fully read header has 4 bytes representing body length

            state.header.flip();

            int len = state.header.getInt();

            state.body = ByteBuffer.allocate(len);

        } else if (state.body != null && state.body.hasRemaining()) {

            int n = client.read(state.body);

            if (n == -1) {

                client.close();

                return;

            }

            if (state.body.hasRemaining()) {

                return;

            }

            // Body is fully read

            state.body.flip();

            // byte[] data = new byte[state.body.remaining()];

            // state.body.get(data);

            // String message = new String(data);

            // System.err.println("Received message: " + message);

            messageHandler(state);

            // int messageType=state.body.getInt();

            key.interestOps(SelectionKey.OP_WRITE);

        }

    }

    static void handlewrite(SelectionKey key, SocketChannel client) throws IOException {

        ClientState state = (ClientState) key.attachment();

        if (state.response == null) {

            String responseMessage = "Hello from NIO Server\n";

            state.response = ByteBuffer.wrap(responseMessage.getBytes());

        }

        client.write(state.response);

        if (state.response.hasRemaining()) {

            return;

        }

        // response fully sent, close connection

        state.response = null;

        // prepare for next message

        state.header.clear();

        state.body = null;

        client.close();

        // key.interestOps(SelectionKey.OP_READ);

    }

    static void messageHandler(ClientState state) {

        // Process the message and prepare response

        byte[] data = new byte[state.body.remaining()];

        state.body.get(data);

        String message = new String(data);

        // System.err.println("Received message: " + message);

        String responseMessage = "Unknown command\n";

        if (message.equals("ping")) {

            responseMessage = "pong\n";

        } else if (message.equals("time")) {

            responseMessage = "Current time: " + System.currentTimeMillis() + "\n";

        }

        byte[] responseData = responseMessage.getBytes();

        ByteBuffer response = ByteBuffer.allocate(4 + responseData.length);

        response.putInt(responseData.length);

        response.put(responseData);

        response.flip();

        state.response = response;

    }

}

public class Server {

    // ================= WORKER =================

    static class Worker implements Runnable {

        private final Selector selector;

        Worker() throws Exception {

            this.selector = Selector.open();

        }

        Selector selector() {

            return selector;

        }

        @Override

        public void run() {

            try {

                while (true) {

                    selector.select();



                    Iterator<SelectionKey> it = selector.selectedKeys().iterator();



                    while (it.hasNext()) {

                        SelectionKey key = it.next();

                        it.remove();



                        // READ

                        if (key.isReadable()) {

                            SocketChannel client = (SocketChannel) key.channel();



                            Statehandler.handleRead(key, client);

                        }



                        // WRITE

                        else if (key.isWritable()) {

                            SocketChannel client = (SocketChannel) key.channel();

                            Statehandler.handlewrite(key, client);

                        }

                    }

                }

            } catch (Exception e) {

                e.printStackTrace();

            }

        }

    }

    // ================= MAIN =================

    public void run() throws Exception {

        // int cores = Runtime.getRuntime().availableProcessors();

        // Worker[] workers = new Worker[cores];

        // for (int i = 0; i < cores; i++) {

        // workers[i] = new Worker();

        // new Thread(workers[i], "worker-" + i).start();

        // }

        // AtomicInteger idx = new AtomicInteger();

        int cores = Runtime.getRuntime().availableProcessors();

        Worker[] workers = new Worker[cores];

        for (int i = 0; i < cores; i++) {

            workers[i] = new Worker();

            new Thread(workers[i], "worker-" + i).start();

        }

        AtomicInteger idx = new AtomicInteger();

        ServerSocketChannel server = ServerSocketChannel.open();

        server.bind(new InetSocketAddress(8090));

        server.configureBlocking(true); // acceptor blocks

        System.out.println("Multi-reactor NIO server on port 8090");

        // ACCEPTOR LOOP

        while (true) {

            // SocketChannel client = server.accept();

            // client.configureBlocking(false);

            // Worker worker =

            // workers[idx.getAndIncrement() % cores];

            // ByteBuffer buffer = ByteBuffer.allocate(1024);

            // worker.selector().wakeup();

            // client.register(worker.selector(),

            // SelectionKey.OP_READ,

            // buffer);

            SocketChannel client = (SocketChannel) server.accept();

            client.configureBlocking(false);

            Worker worker = workers[idx.getAndIncrement() % cores];

            ClientState state = new ClientState();

            // ByteBuffer buffer= ByteBuffer.allocate(1024);

            worker.selector().wakeup();

            client.register(worker.selector(), SelectionKey.OP_READ, state);

        }

    }

    public static void main(String[] args) throws Exception {

        new Server().run();

    }

}