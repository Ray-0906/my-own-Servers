package handler;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;

import state.HttpClientState;

public class HttpWriteHandler {

    public static void handle(SelectionKey key, SocketChannel client)
            throws IOException {

        HttpClientState state = (HttpClientState) key.attachment();

        client.write(state.response);
        if (state.response.hasRemaining()) {
            return;
        }

        key.cancel();
        client.close();
    }
}