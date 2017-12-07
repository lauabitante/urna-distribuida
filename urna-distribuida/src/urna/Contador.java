package urna;

public class Contador {
    public static void main(String[] args) {
    	ContadorThread contadorThread = new ContadorThread();
        Thread threadCont = new Thread(contadorThread);
        threadCont.start();
    }
}