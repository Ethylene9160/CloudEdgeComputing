import com.edge.EdgeServer;

public class Main {
    public static void main(String[] args) {
        EdgeServer edgeServer = new EdgeServer(8956);
        edgeServer.startServer();
    }
}