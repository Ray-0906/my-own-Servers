import java.nio.channels.*;
import java.util.Iterator;

import handler.HttpReadHandler;
import handler.HttpWriteHandler;

public class Worker implements Runnable {

    private final Selector selector;

    public Worker() throws Exception {
        this.selector = Selector.open();
    }

    public Selector selector() {
        return selector;
    }

    @Override
    public void run() {
        try {
            while (true) {
                selector.select();

                Iterator<SelectionKey> it =
                        selector.selectedKeys().iterator();

                while (it.hasNext()) {
                    SelectionKey key = it.next();
                    it.remove();

                    SocketChannel client = (SocketChannel) key.channel();

                    if (key.isReadable()) {
                        HttpReadHandler.handle(key, client);
                    } else if (key.isWritable()) {
                        HttpWriteHandler.handle(key, client);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}