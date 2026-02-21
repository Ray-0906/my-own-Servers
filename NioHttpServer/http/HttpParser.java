package http;

import java.nio.ByteBuffer;
import state.HttpClientState;

public class HttpParser {

     public static int findHeaderend(ByteBuffer buffer) {
        // Fix: Start searching from position() up to limit() - 4
        int limit = buffer.limit();
        for (int i = buffer.position(); i <= limit - 4; i++) {
            if (buffer.get(i) == '\r' && buffer.get(i + 1) == '\n' && 
                buffer.get(i + 2) == '\r' && buffer.get(i + 3) == '\n') {
                return i + 4;
            }
        }
        return -1;
    }

    public static void parseHeaders(HttpClientState state, byte[] headerBytes) {
        String header = new String(headerBytes);
        String[] lines = header.split("\r\n");

        if (lines.length == 0) return;
        
        String[] requestLine = lines[0].split(" ");
        if (requestLine.length >= 2) {
            state.method = requestLine[0];
            state.path = requestLine[1];
        }
        
        for (int i = 1; i < lines.length; i++) {
            String line = lines[i];
            if (line.isEmpty()) continue;

            String[] kv = line.split(": ", 2);
            if (kv.length == 2) {
                state.headers.put(kv[0], kv[1]);
            }
        }

        state.contentLength = Integer.parseInt(state.headers.getOrDefault("Content-Length", "0"));
    }
}