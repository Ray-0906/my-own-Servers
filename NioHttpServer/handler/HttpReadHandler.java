package handler;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;

import http.HttpParser;
import http.HttpResponse;
import state.HttpClientState;

public class HttpReadHandler {

    public static void handle1(SelectionKey key, SocketChannel client)
            throws IOException {

        HttpClientState state = (HttpClientState) key.attachment();

        int n = client.read(state.buffer);
        if (n == -1) {
            client.close();
            return;
        }

        state.buffer.flip();

        if (!state.headersParsed) {
            int headerEnd = HttpParser.findHeaderend(state.buffer);
            if (headerEnd == -1) {
                state.buffer.compact();
                return;
            }

            byte[] headerBytes = new byte[headerEnd];
            state.buffer.get(headerBytes);
            HttpParser.parseHeaders(state, headerBytes);
            state.headersParsed = true;
        }

        if (state.buffer.remaining() < state.contentLength) {
            state.buffer.compact();
            return;
        }

        byte[] body = new byte[state.contentLength];
        state.buffer.get(body);

        // routing
        if ("GET".equals(state.method) && "/ping".equals(state.path)) {
            state.response = HttpResponse.build200("pong");
        } else if ("GET".equals(state.method) && "/time".equals(state.path)) {
            state.response = HttpResponse.build200("Current time: " + System.currentTimeMillis());
        } else {
            state.response = HttpResponse.build404();
        }

        key.interestOps(SelectionKey.OP_WRITE);
    }

    public static void handle(SelectionKey key, SocketChannel client) throws IOException {
        HttpClientState state = (HttpClientState) key.attachment();

        int n = client.read(state.buffer);
        if (n == -1) {
            client.close();
            return;
        }

        // Switch buffer from writing mode to reading mode
        state.buffer.flip();

        // ---------- HEADERS ----------
        if (!state.headersParsed) {
            int headerEnd = HttpParser.findHeaderend(state.buffer);
            if (headerEnd == -1) {
                // If buffer is full and headers still aren't found, headers are too large
                if (state.buffer.limit() == state.buffer.capacity()) {
                    client.close();
                    return;
                }
                state.buffer.compact();
                return;
            }

            // Read exactly the header bytes
            state.buffer.position(0);
            byte[] headerBytes = new byte[headerEnd];
            state.buffer.get(headerBytes);
            HttpParser.parseHeaders(state, headerBytes);
            state.headersParsed = true;
        }

        // ---------- BODY ----------
        if (state.buffer.remaining() < state.contentLength) {
            // Not enough bytes for the body yet, compact and wait for next read
            state.buffer.compact();
            return;
        }

        byte[] bodyBytes = new byte[state.contentLength];
        state.buffer.get(bodyBytes);
        String body = new String(bodyBytes);

        HttpmessageHandler(state, body);

        // Switch interest to OP_WRITE to send the response
        key.interestOps(SelectionKey.OP_WRITE);
    }

    public static void HttpmessageHandler(HttpClientState state, String body) {
        String method = state.method;
        String path = state.path;

        String responseBody;
        String statusLine;

        if (method != null && method.equals("GET") && path.equals("/ping")) {
            responseBody = "pong";
            statusLine = "HTTP/1.1 200 OK\r\n";
        } else if (method != null && method.equals("GET") && path.equals("/time")) {
            responseBody = "Current time: " + System.currentTimeMillis() + "\n";
            statusLine = "HTTP/1.1 200 OK\r\n";
        } else {
            responseBody = "Not Found";
            statusLine = "HTTP/1.1 404 Not Found\r\n";
        }

        byte[] bodyBytes = responseBody.getBytes();
        String response = statusLine +
                "Content-Length: " + bodyBytes.length + "\r\n" +
                "Connection: close\r\n" +
                "\r\n";

        ByteBuffer buffer = ByteBuffer.allocate(response.getBytes().length + bodyBytes.length);
        buffer.put(response.getBytes());
        buffer.put(bodyBytes);
        buffer.flip();

        state.response = buffer;
    }

}