public class ServerLauncher {

    private int port = 8090;

    public ServerLauncher port(int port) {
        this.port = port;
        return this;
    }

    public void start() {
        try {
            new Server(port).run();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}