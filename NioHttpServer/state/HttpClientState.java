package state;

import java.nio.ByteBuffer;
import java.util.HashMap;

public class HttpClientState {
    public ByteBuffer buffer = ByteBuffer.allocate(8192);
    public boolean headersParsed = false;
    public int contentLength = 0;

    public String method;
    public String path;
    public HashMap<String, String> headers = new HashMap<>();

    public ByteBuffer response;
}