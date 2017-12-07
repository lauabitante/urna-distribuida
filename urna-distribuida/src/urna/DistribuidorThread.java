package urna;
import java.io.IOException;
import java.net.ServerSocket;

class DistribuidorThread implements Runnable {

    private ServerSocket server;

	@Override
    public void run() {
        try {
            server = new ServerSocket(8000);
            System.out.println("Distribuidor: Aguardando Urna");
            while (true) {
                new DistribuidorSocketConnection(server.accept()).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
