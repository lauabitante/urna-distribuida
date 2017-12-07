package urna;
import java.io.IOException;
import java.net.ServerSocket;

class ComissarioThread implements Runnable {

    private ServerSocket serverDistribuidor;
    private ServerSocket serverContador;

	@Override
    public void run() {
        try {
        	serverDistribuidor = new ServerSocket(8001);
        	serverContador = new ServerSocket(8002);
            System.out.println("Comissario: Aguardando Distribuidor");
            System.out.println("Comissario: Aguardando Contador");
            while (true) {
                new ComissarioSocketConnection(serverDistribuidor.accept()).start();
                new ContadorSocketConnection(serverContador.accept()).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
