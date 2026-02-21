package http;

import java.nio.ByteBuffer;

public class HttpResponse {

    public static ByteBuffer build200(String body) {
        byte[] bodyBytes = body.getBytes();
        System.out.println("building resp for "+body);
        String headers =
                "HTTP/1.1 200 OK\r\n" +
                "Content-Length: " + bodyBytes.length + "\r\n" +
                "Connection: close\r\n\r\n";

        ByteBuffer buffer =
                ByteBuffer.allocate(headers.getBytes().length + bodyBytes.length);
        buffer.put(headers.getBytes());
        buffer.put(bodyBytes);
        buffer.flip();
        return buffer;
    }

    public static ByteBuffer build404() {
        return build200("Not Found");
    }
}