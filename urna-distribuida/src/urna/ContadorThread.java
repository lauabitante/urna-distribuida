package urna;
import java.io.IOException;
import java.net.ServerSocket;

class ContadorThread implements Runnable {

    private ServerSocket server;

	@Override
    public void run() {
        try {
            server = new ServerSocket(8004);
            System.out.println("Contador: Aguardando Urna");
            while (true) {
                new ContadorSocketConnection(server.accept()).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
